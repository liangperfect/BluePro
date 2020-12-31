package com.vitalong.bluetest2.inclinometer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.ByteTransformUtil;
import com.vitalong.bluetest2.Utils.CRC16CheckUtil;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.BoreholeInfoTable;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.BoreholeInfoTableDao;

import java.io.IOException;
import java.text.DecimalFormat;

public class Survey2Activity extends MyBaseActivity2 {
    final String TAG = "Survey2Activity";
    Toolbar toolbar;
    private ImageView imgPlay;
    private String currSnValue = "";
    private Survey2Handler survey2Handler;
    private Button btnSave;
    private TextView tvTitle2;
    private TextView tvA;
    private TextView tvB;
    private TextView tvDepthNum;
    private ImageView imgUp;
    private ImageView imgDown;
    private RadioButton rb0degree;
    private RadioButton rb180degree;
    private double dAxisA = 0;
    private double dAxisB = 0;
    private double dAxisC = 0;
    private double dAxisD = 0;
    private double dBxisA = 0;
    private double dBxisB = 0;
    private double dBxisC = 0;
    private double dBxisD = 0;
    private int sensorModeValue = 0;//单轴还是双轴
    private int sensitivityValue = 0;//1 fASTER
    private int beepValue = 0;//声音
    private int unitValue = 0;//单位
    private int decimalValue = 3;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    String[] ctype = new String[]{"1(Faster)", "2(Default)", "3(Slower)", "4(Degree)", "5(Degree)", "6(Degree)", "7(Degree)", "8(Degree)", "9(Degree)"};
    String[] beeps = new String[]{"Mute", "TypeA", "TypeB", "TypeC", "TypeD", "TypeE"};
    String[] ctype3 = new String[]{"Deg", "Raw"};
    String[] ctype4ByDeg = new String[]{"3", "4"};
    String[] ctype4ByRaw = new String[]{"1"};
    private int sendDuration = 200;//循环发送命令间隔时间

    private float currOneChannelAngle;//记录轴1的原始角度值
    private float currtwoChannelAngle;//记录轴2的原始角度值
    private double THRESHOLD = 0.02;//阈值
    private DecimalFormat deg2Format = new DecimalFormat("0.00");
    private DecimalFormat deg3Format = new DecimalFormat("0.000");
    private DecimalFormat deg4Format = new DecimalFormat("0.0000");
    private AxisLink2 oneAxisLink;//用于判断第一个角度是否有连续值的
    private AxisLink2 twoAxisLink;//用于判断第二行角度是否有连续值的
    boolean isSave = false;
    private boolean isFirstPlay = true;
    private int clickNum = 0;//点击次数
    private Handler handler = new Handler(); //事件点击
    //初始化測量的數據
    private int fromWhich = 0;
    private String constructionSiteName;
    private String holeName;
    private String csvFileName;
    private String csvFilePath;
    private BoreholeInfoTableDao boreholeInfoTableDao;
    private float topValue;
    private float bottomValue;
    private CsvUtil csvUtil;
    private boolean isZero = true; //当前模式是不是0模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey2);
        initView();
        fetchIntentData();
        bindToolBar();
        makeStatusBar(R.color.white);
        survey2Handler = new Survey2Handler();
        initSp();
        initPlay(beeps[beepValue]);
        if (initVerifyData()) {
            startCmdRepeat();
        }
    }

    @Override
    protected void registerBc() {

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {

        //获取到解析的数据
        parseData(array);
    }

    /**
     * 开始重复发送数据
     */
    private void startCmdRepeat() {
        //开始发送数据
        survey2Handler.sendEmptyMessageDelayed(0, 100);
    }


    @Override
    protected void disconnectBlue() {

        // TODO: 2020/12/3 蓝牙断开了链接，然后需要判断数据是不是要重新使用
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), Survey2Activity.this);
    }

    private void fetchIntentData() {
        fromWhich = getIntent().getIntExtra("fromWhich", 0);
        constructionSiteName = getIntent().getStringExtra("constructionSiteName");
        holeName = getIntent().getStringExtra("holeName");
        csvFileName = getIntent().getStringExtra("csvFileName");
        csvFilePath = getIntent().getStringExtra("csvFilePath");

        //初始化数据
        boreholeInfoTableDao = ((MyApplication) getApplication()).boreholeInfoTableDao;
        try {
            BoreholeInfoTable boreholeInfoTable = boreholeInfoTableDao.queryBuilder()
                    .where(BoreholeInfoTableDao.Properties.ConstructionSite.eq(constructionSiteName),
                            BoreholeInfoTableDao.Properties.HoleName.eq(holeName)).build().list().get(0); //只可能匹配一个
            topValue = boreholeInfoTable.getTopValue();
            bottomValue = boreholeInfoTable.getBottomValue();
            //初始化相关view
            tvDepthNum.setText(String.valueOf(bottomValue));
            csvUtil = new CsvUtil(Survey2Activity.this,topValue,bottomValue);
        } catch (Exception exception) {

            Toast.makeText(Survey2Activity.this, "沒找到孔的信息", Toast.LENGTH_SHORT).show();
        }
    }

    protected void bindToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void initView() {

        tvTitle2 = findViewById(R.id.tvTitle2);
        toolbar = findViewById(R.id.toolbar);
        imgPlay = findViewById(R.id.imgPlay);
        btnSave = findViewById(R.id.btnSave);
        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        tvDepthNum = findViewById(R.id.tvDepthNum);
        imgUp = findViewById(R.id.imgUp);
        imgDown = findViewById(R.id.imgDown);
        rb0degree = findViewById(R.id.rb0degree);
        rb180degree = findViewById(R.id.rb180degree);
        initListener();
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initListener() {

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgPlay.setImageResource(R.drawable.pause);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNum++;
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if (clickNum == 1) {

                        } else if (clickNum == 2) {
                            //将数据存储起来
                            saveCsvData();
                            changeLogicUI();
                        }
                        handler.removeCallbacksAndMessages(null);//防止handler引起的内存泄漏
                        clickNum = 0;
                    }
                }, 200);
            }
        });

        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeDepthNumber(true);
            }
        });

        imgDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeDepthNumber(false);
            }
        });

        rb0degree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isZero = true;
            }
        });

        rb180degree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isZero = false;
            }
        });

    }

    /**
     * 数据存储成功后，改变UI及相关阈值
     */
    private void changeLogicUI() {

        //数字先减去0.5
        changeDepthNumber(false);
    }

    /**
     * 井口深度的上升还是下降
     *
     * @param isUp
     */
    private void changeDepthNumber(boolean isUp) {

        float depthValue = Float.parseFloat(tvDepthNum.getText().toString());
        if (isUp) {
            depthValue = depthValue + 0.5f;
            if (depthValue == 90) {
                //达到最大的阈值之后，就弹框询问是否跳转到180继续进行测量
                new AlertDialog.Builder(Survey2Activity.this).setTitle("是否跳轉到180繼續進行測量")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Survey2Activity.this.finish();
                            }
                        }).setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rb180degree.setChecked(true);
                    }
                }).show();

            }
        } else {
            depthValue = depthValue - 0.5f;
        }
        tvDepthNum.setText(String.valueOf(depthValue));
    }

    /**
     * 存储CSV的数据
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveCsvData() {
        //计算出表格需要的值,根据csvfilename和高度去更新对应的的值
        String depthStr = tvDepthNum.getText().toString();
        //哪种模式下
        csvUtil.saveDatasInDB(csvFileName, depthStr, currOneChannelAngle, currtwoChannelAngle, isZero);
        //将数据存入到csv文件中去
        csvUtil.saveData(csvFilePath, csvFileName);
    }


    private Boolean initVerifyData() {

        try {
            VerifyDataBean verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
            dAxisA = Double.parseDouble(verifyDataBean.getAaxisA());
            dAxisB = Double.parseDouble(verifyDataBean.getAaxisB());
            dAxisC = Double.parseDouble(verifyDataBean.getAaxisC());
            dAxisD = Double.parseDouble(verifyDataBean.getAaxisD());
            dBxisA = Double.parseDouble(verifyDataBean.getBaxisA());
            dBxisB = Double.parseDouble(verifyDataBean.getBaxisB());
            dBxisC = Double.parseDouble(verifyDataBean.getBaxisC());
            dBxisD = Double.parseDouble(verifyDataBean.getBaxisD());
        } catch (Exception e) {
            Toast.makeText(Survey2Activity.this, "verify data is error,please update verify data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initSp() {

        sensorModeValue = (int) SharedPreferencesUtil.getData(Constants.SENSORMODE_KEY, 0);
        sensitivityValue = (int) SharedPreferencesUtil.getData(Constants.SENSITIVITY_KEY, 0);
        beepValue = (int) SharedPreferencesUtil.getData(Constants.BEEP_KEY, 0);
        unitValue = (int) SharedPreferencesUtil.getData(Constants.UNIT_KEY, 0);
        decimalValue = (int) SharedPreferencesUtil.getData(Constants.DECIMAL, 0);
//        sendDuration = (int) SharedPreferencesUtil.getData(Constants.SURVEY_DURATION, 150);
        currSnValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
//        if (unitValue == Constants.UNIT_DEG) {
//
//            image1.setImageResource(R.drawable.new_deg);
////            image2.setImageResource(R.mipmap.b6);
//        } else {
//            image1.setImageResource(R.drawable.new_raw1);
////            image2.setImageResource(R.mipmap.b1);
//        }
        int sensitivityMode = 3;
        if (sensitivityValue == Constants.SENSITVITY_1_FASTER) {
            sensitivityMode = 3;
        } else if (sensitivityValue == Constants.SENSITVITY_2_DEFAULT) {
            sensitivityMode = 4;
        } else if (sensitivityValue == Constants.SENSITVITY_3_SLOWER) {
            sensitivityMode = 5;
        } else if (sensitivityValue == Constants.SENSITVITY_4_DEGREE) {
            sensitivityMode = 6;
        } else if (sensitivityValue == Constants.SENSITVITY_5_DEGREE) {
            sensitivityMode = 7;
        } else if (sensitivityValue == Constants.SENSITVITY_6_DEGREE) {
            sensitivityMode = 8;
        } else if (sensitivityValue == Constants.SENSITVITY_7_DEGREE) {
            sensitivityMode = 9;
        } else if (sensitivityValue == Constants.SENSITVITY_8_DEGREE) {
            sensitivityMode = 10;
        } else if (sensitivityValue == Constants.SENSITVITY_9_DEGREE) {
            sensitivityMode = 11;
        }
        oneAxisLink = new AxisLink2(sensitivityMode);
        twoAxisLink = new AxisLink2(sensitivityMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        survey2Handler.removeCallbacksAndMessages(null);
        unregisterReceiver(mGattUpdateReceiver);
    }

    void initPlay(String beep) {
        try {
            if (beep.equals("TypeA"))
                playRd("TypeA.mp3");
            else if (beep.equals("TypeB"))
                playRd("TypeB.mp3");
            else if (beep.equals("TypeC"))
                playRd("TypeC.mp3");
            else if (beep.equals("TypeD"))
                playRd("TypeD.mp3");
            else if (beep.equals("TypeE"))
                playRd("TypeE.mp3");
        } catch (IOException err) {
            Toast.makeText(Survey2Activity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void playRd(String fileName) throws IOException {
        AssetManager assetManager = getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
        mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mMediaPlayer.prepare();
        //mMediaPlayer.setVolume(1,1);
        //mMediaPlayer.setLooping(false);
    }

    /**
     * 解析数据
     *
     * @param data 接收到的字节数组
     */
    private void parseData(byte[] data) {

        try {
            String dtSt = ByteTransformUtil.bytesToHex(data);
//            boolean canShow = false;
            if (dtSt != null && dtSt.length() > 4) {
                String _dts = dtSt.substring(0, dtSt.length() - 4);//截取后面位数
                if (_dts.length() > 0) _dts = CRC16CheckUtil.getCrcString(_dts);//crc校验
                if (!_dts.equals(dtSt)) {
                    //校验不通过
                    return;
                }
            }
            // 获取板子的原始值
            float oneChannelAngle = ByteTransformUtil.byte2float(new byte[]{data[5], data[6], data[3], data[4]});
            float oneChannelTemperature = ByteTransformUtil.byte2float(new byte[]{data[9], data[10], data[7], data[8]});
            float twoChannelAngle = ByteTransformUtil.byte2float(new byte[]{data[13], data[14], data[11], data[12]});
            float twoChannelTemperature = ByteTransformUtil.byte2float(new byte[]{data[17], data[18], data[15], data[16]});
            float voltage = ByteTransformUtil.byte2float(new byte[]{data[21], data[22], data[19], data[20]});
            Log.d("chenliang", "SurveyActivity1解析的浮点数是:" + oneChannelAngle +
                    " : " + oneChannelTemperature +
                    " : " + twoChannelAngle +
                    " : " + twoChannelTemperature
                    + " : " + voltage
            );
            currOneChannelAngle = oneChannelAngle; //记录当前原始角度值，用于保存到表格当中去
            currtwoChannelAngle = twoChannelAngle;
            //获取电量
            float battValue = Utils.getBattValue(voltage);
            tvTitle2.setText(battValue + "%");
            isSave = showAxisValue(oneChannelAngle, oneAxisLink);
            isSave = showAxisValue(twoChannelAngle, twoAxisLink) && isSave;
            //显示mm的值
            double deg1 = getDeg(oneChannelAngle, Constants.SFMODE_1AXIS);
            double deg2 = getDeg(twoChannelAngle, Constants.SFMODE_2AXIS);

            double radians1 = Math.toRadians(deg1);
            double radians2 = Math.toRadians(deg2);
            double mm1 = Math.sin(radians1) * 500;
            double mm2 = Math.sin(radians2) * 500;
            tvA.setText(deg2Format.format(mm1));
            tvB.setText(deg2Format.format(mm2));
            if (isSave) {
                btnSave.setText("Stable");
                btnSave.setBackgroundResource(R.drawable.btn_start_bg);
                toPlay();
            } else {
                btnSave.setText("Wait");
                btnSave.setBackgroundResource(R.drawable.btn_pause_bg);
                isFirstPlay = true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("chenliang", "数据解析出问题:" + formatMsgContent(data));
        }
    }

    /**
     * 获取单位模式为Deg下的值
     *
     * @param angle
     * @return
     */
    private double getDeg(double angle, int axisMode) {
        double f = angle * 7.2;
        if (axisMode == Constants.SFMODE_1AXIS) {
            return dAxisA * (f * f * f) + dAxisB * (f * f) + (dAxisC * f) + dAxisD;
        }

        return dBxisA * (f * f * f) + dBxisB * (f * f) + (dBxisC * f) + dBxisD;
    }

    /**
     * 根据raw获取Raw
     *
     * @param deg
     * @return
     */
    private double getRaw(double deg) {

        try {
            double raw = Math.sin(deg * Math.PI / 180) * 25000;
            return raw;
        } catch (Exception err) {
            return 100000.0;
        }
    }

    /**
     * 展示计算过后的值
     *
     * @param tv        展示数据TextView
     * @param imgDimond 展示图形的ImageView
     * @param textValue 需要展示的值
     * @param axisValue 从板子获取到的原始值
     * @param axisLink  是否几次值连续的判断类
     *                  true :显示菱形   false:不显示菱形且数据实时变动
     */
    private boolean showAxisValue(TextView tv, ImageView imgDimond, double textValue, double axisValue, AxisLink2 axisLink) {
        if (!axisLink.verifyData((float) axisValue)) {
            if (unitValue == Constants.UNIT_DEG) {
                String v = "";
                if (decimalValue == 3) {
                    v = deg3Format.format(textValue);
                } else {
                    v = deg4Format.format(textValue);
                }
                tv.setText(v);
            } else if (unitValue == Constants.UNIT_RAW) {
                int v1 = (int) textValue;
                tv.setText(String.valueOf(v1));
            }
            imgDimond.setVisibility(View.GONE);
            return false;
        } else {
            imgDimond.setVisibility(View.VISIBLE);
            return true;
        }
    }

    /**
     * 看值的变动
     *
     * @param axisValue 从板子获取到的原始值
     * @param axisLink  是否几次值连续的判断类
     *                  true :显示菱形，数据稳定了   false:不显示菱形且数据实时变动
     */
    private boolean showAxisValue(double axisValue, AxisLink2 axisLink) {
        if (!axisLink.verifyData((float) axisValue)) {
//            if (unitValue == Constants.UNIT_DEG) {
//                String v = "";
//                if (decimalValue == 3) {
//                    v = deg3Format.format(textValue);
//                } else {
//                    v = deg4Format.format(textValue);
//                }
//            } else if (unitValue == Constants.UNIT_RAW) {
//                int v1 = (int) textValue;
//            }
            //计算出mm的值然后，进行显示
            return false;
        } else {
            return true;
        }
    }


    private void toPlay() {
        if (!mMediaPlayer.isPlaying() && isFirstPlay) {
            isFirstPlay = false;
            mMediaPlayer.start();
        }
    }

    class Survey2Handler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //进行handle处理
            sendCmdCodeByHex(Constants.REAL_DATA_CMD2);
            survey2Handler.sendEmptyMessageDelayed(0, sendDuration);
        }
    }

    /**
     * 用于判断数据是否相等
     */
    class AxisLink2 {

        public AxisLink2(int sersitivity) {
            this.sersitivityMode = sersitivity;
        }

        private int sersitivityMode; //精度

        private int count;//计算连续第几次

        private float preValue = 0;

        public boolean verifyData(float axiaValue) {
            float changeValue = Float.parseFloat(deg3Format.format(axiaValue));
            Log.d(TAG, "sersitivityMode000:" + changeValue);
            if (preValue == changeValue) {
                count = count + 1;
                if (count > sersitivityMode) {
                    count = sersitivityMode;
                }
                return count == sersitivityMode;
            }
            Log.d(TAG, "Math.abs(changeValue - preValue):" + Math.abs(changeValue - preValue));
            if (count == sersitivityMode) {
                if (Math.abs(changeValue - preValue) > THRESHOLD) {
                    count = 0;
                    preValue = changeValue;
                    Log.d(TAG, "sersitivityMode111:" + sersitivityMode);
                    return false;
                }
                Log.d(TAG, "sersitivityMode222:" + sersitivityMode);
                return true;
            }
            Log.d(TAG, "sersitivityMode333:" + sersitivityMode);
            preValue = changeValue;
            count = 0; //当在sersitivityMode次数内没有连续，则重新计算
            return false;
        }
    }

}
