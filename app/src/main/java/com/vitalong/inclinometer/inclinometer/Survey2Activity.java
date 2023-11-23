package com.vitalong.inclinometer.inclinometer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
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

import com.vitalong.inclinometer.MyApplication;
import com.vitalong.inclinometer.MyBaseActivity2;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.ByteTransformUtil;
import com.vitalong.inclinometer.Utils.CRC16CheckUtil;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.Utils.SharedPreferencesUtil;
import com.vitalong.inclinometer.Utils.Utils;
import com.vitalong.inclinometer.bean.BoreholeInfoTable;
import com.vitalong.inclinometer.bean.SurveyDataTable;
import com.vitalong.inclinometer.bean.VerifyDataBean;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;
import com.vitalong.inclinometer.views.NumberSelectDialog;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private TextView tvRaw;
    private ImageView imgUp;
    private ImageView imgDown;
    private RadioButton rb0degree;
    private RadioButton rb180degree;
    private TextView tvBottomTitle;
    private TextView tvBottom;
    private TextView tvTop;
    private TextView tvAOldmm;
    private TextView tvBOldmm;
    private TextView tvPreDepth;
    private TextView tvPreAValue;
    private TextView tvPreBValue;
    private TextView tvCompareA0;//当是180模式当时候
    private TextView tvCompareB0;
    private double dAxisA = 0;
    private double dAxisB = 0;
    private double dAxisC = 0;
    private double dAxisD = 0;
    private double dBxisA = 0;
    private double dBxisB = 0;
    private double dBxisC = 0;
    private double dBxisD = 0;
    //    private int sensorModeValue = 0;//单轴还是双轴
    private int sensitivityValue = 0;//1 fASTER
    private int beepValue = 0;//声音
    //    private int unitValue = 0;//单位
//    private int decimalValue = 3;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    String[] beeps = new String[]{"Default", "TypeA", "TypeB", "TypeC", "TypeD", "TypeE", "TypeF"};
    double[] stabityTimeFloats = new double[]{0.02, 0.019, 0.018, 0.017, 0.016, 0.015, 0.014, 0.013, 0.012, 0.011, 0.01, 0.009, 0.008, 0.007, 0.006, 0.005, 0.004, 0.003, 0.002, 0.001};
    private int sendDuration = 200;//循环发送命令间隔时间

    private float currOneChannelAngle;//记录轴1的原始角度值
    private float currtwoChannelAngle;//记录轴2的原始角度值
    private double THRESHOLD = 0.01;//阈值
    private DecimalFormat deg2Format = new DecimalFormat("0.00");
    private DecimalFormat deg3Format = new DecimalFormat("0.000");
    private DecimalFormat deg4Format = new DecimalFormat("0.0000");
    private AxisLink2 oneAxisLink;//用于判断第一个角度是否有连续值的
    private AxisLink2 twoAxisLink;//用于判断第二行角度是否有连续值的
    private boolean isPreSave = false;//用于记录isSave的前一个状态
    private boolean isSave = false;
    private boolean isAutoSaveData = false; //在自动模式下是否在稳定状态下已经存储了数据
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
    private float interval = 0.5f;
    private CsvUtil csvUtil;
    private boolean isZero = true; //当前模式是不是0模式
    private boolean isAuto = false;//true:自动  false:手动  记录当前是什么模式
    private int autoTimeOut = 1;//自动模式下稳定的时间单位秒
    private int autoNums = 3;//自动模式下稳定的次数，由时间转换过来
    private int currAutoIndex = 0;//用于技术当前自动模式下再稳定后又稳定的次数
    private long lastClickTime = 0;//防止按钮被连续点击造成存储问题
    private int surveySelectModeValue = 0;//0:mm 1:raw
    private final int SURVEY_MODE_MM = 0;
    private final int SURVEY_MODE_RAW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            interval = boreholeInfoTable.getDuration();
            //初始化相关view
            tvDepthNum.setText(String.valueOf(bottomValue));
            tvBottom.setText("Bottom:" + bottomValue + "m");
            tvTop.setText("Top:" + topValue + "m");
            csvUtil = new CsvUtil(Survey2Activity.this, constructionSiteName, holeName, topValue, bottomValue);
            showOldMMValue(String.valueOf(bottomValue), isZero);
        } catch (Exception exception) {
            Toast.makeText(Survey2Activity.this, "沒找到孔的信息", Toast.LENGTH_SHORT).show();
//            btnSave.setEnabled(false);
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
        tvRaw = findViewById(R.id.tvRaw);
        imgUp = findViewById(R.id.imgUp);
        imgDown = findViewById(R.id.imgDown);
        rb0degree = findViewById(R.id.rb0degree);
        rb180degree = findViewById(R.id.rb180degree);
        tvBottomTitle = findViewById(R.id.tvBottomTitle);
        tvBottom = findViewById(R.id.tvBottom);
        tvTop = findViewById(R.id.tvTop);
        tvAOldmm = findViewById(R.id.tvAOld);
        tvBOldmm = findViewById(R.id.tvBOld);
        tvPreDepth = findViewById(R.id.tvPreDepth);
        tvPreAValue = findViewById(R.id.tvPreAValue);
        tvPreBValue = findViewById(R.id.tvPreBValue);
        tvCompareA0 = findViewById(R.id.tvCompareA);
        tvCompareB0 = findViewById(R.id.tvCompareB);
        initListener();
    }

    private void initListener() {

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imgId = isAuto ? R.drawable.start : R.drawable.pause;
                imgPlay.setImageResource(imgId);
                isAuto = !isAuto;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (isAuto) {

                    Toast.makeText(Survey2Activity.this, "請切換成手動模式再進行存儲", Toast.LENGTH_SHORT).show();
                } else {
                    //双击存储
//                    clickNum++;
//                    handler.postDelayed(new Runnable() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void run() {
//                            if (clickNum == 1) {
//
//                            } else if (clickNum >= 2) {
//                                //将数据存储起来
//                                saveCsvData();
//                                changeLogicUI();
//                            }
//                            handler.removeCallbacksAndMessages(null);//防止handler引起的内存泄漏
//                            clickNum = 0;
//                        }
//                    }, 200);
//                  将数据存储起来
                    long now = System.currentTimeMillis();
                    if (now - lastClickTime > 1200) {
                        lastClickTime = now;
                        String preAB = saveCsvData();
                        changeLogicUI(preAB);
                    }
                }
            }
        });

        imgUp.setOnClickListener(v -> {
            if (isAuto) {
                Toast.makeText(Survey2Activity.this, "請切換成手動模式再進行存儲", Toast.LENGTH_SHORT).show();
            } else {
                changeDepthNumber(false);
            }
        });

        imgDown.setOnClickListener(v -> {
            if (isAuto) {
                Toast.makeText(Survey2Activity.this, "請切換成手動模式再進行存儲", Toast.LENGTH_SHORT).show();
            } else {
                changeDepthNumber(true);
            }
        });

        rb0degree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    isZero = true;
                    isShowCompareTv(isZero);
                }
            }
        });

        rb180degree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isZero = false;
                    isShowCompareTv(isZero);
                }
            }
        });

        tvDepthNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> numbers = new ArrayList<>();
                float topTemp = topValue;
                float bottomTemp = bottomValue;
                while (topTemp <= bottomTemp) {
                    numbers.add(String.valueOf(topTemp));
//                    topTemp += 0.5;
                    topTemp += interval;
                }
                NumberSelectDialog selectDialog = new NumberSelectDialog(Survey2Activity.this, numbers, new NumberSelectDialog.ChangeNumberListener() {
                    @Override
                    public void onSelectWhichNum(String numberStr) {

                        tvDepthNum.setText(numberStr);
                    }
                });
                selectDialog.show();
            }
        });
    }

    /**
     * 是否展示A0 B0的值，当模式是180会展示，模式是0当时候隐藏掉
     *
     * @param isZero
     */
    private void isShowCompareTv(boolean isZero) {

        int v = isZero ? View.GONE : View.VISIBLE;
        tvCompareA0.setVisibility(v);
        tvCompareB0.setVisibility(v);
        String depStr = String.valueOf(tvDepthNum.getText());
        if (!isZero)
            showCompareMMValue(depStr);
    }

    /**
     * 数据存储成功后，改变UI及相关阈值
     */
    private void changeLogicUI(String preAB) {

        try {
            //数字先减去0.5
            float pre = changeDepthNumber(false);
            String preAmmStr = preAB.split(",")[0];
            String preBmmStr = preAB.split(",")[1];
            String preARawStr = preAB.split(",")[2];
            String preBRawStr = preAB.split(",")[3];
            showPreABValue(pre, preAmmStr, preBmmStr, preARawStr, preBRawStr);
        } catch (Exception e) {

            showPreABValue(0, "0", "0", "0", "0");
        }
    }

    private void showPreABValue(float preDepth, String preA, String preB, String preARaw, String preBRaw) {

        tvPreDepth.setText(preDepth + "m");

        String prefixA = "A0: ";
        String prefixB = "B0: ";
        if (isZero) {
            prefixA = "A0: ";
            prefixB = "B0: ";
        } else {
            prefixA = "A180: ";
            prefixB = "B180: ";
        }
        if (surveySelectModeValue == SURVEY_MODE_MM) {
            double preADouble = Double.parseDouble(preA);
            double preBDouble = Double.parseDouble(preB);
            tvPreAValue.setText(prefixA + deg2Format.format(preADouble));
            tvPreBValue.setText(prefixB + deg2Format.format(preBDouble));
        } else {
            tvPreAValue.setText(prefixA + preARaw);
            tvPreBValue.setText(prefixB + preBRaw);
        }
    }

    /**
     * 井口深度的上升还是下降
     *
     * @param isUp true:上升  false:下降
     */
    public float changeDepthNumber(boolean isUp) {

        float depthValue = Float.parseFloat(tvDepthNum.getText().toString());
        float returnValue = depthValue;
        if (isUp) {

            if (depthValue > bottomValue)
                depthValue = bottomValue;
            else
                depthValue = depthValue + interval;
//                depthValue = depthValue + 0.5f;

        } else {

            if (depthValue < topValue)
                depthValue = topValue;
            else
                depthValue = depthValue - interval;
//                depthValue = depthValue - 0.5f;
        }
        if (depthValue < topValue || depthValue > bottomValue) {
            if (isZero) {
                showRevertDegree();
            } else {
                completeSurvey();
            }
        } else {
            String depStr = String.valueOf(depthValue);
            tvDepthNum.setText(depStr);
//            showOldMMValue(depStr, isZero);
            showCompareMMValue(depStr);
        }
        return returnValue;
    }

    /**
     * @param depthStr
     */
    private void showOldMMValue(String depthStr, boolean isZero) {

        //获取对应坐标的mm值进行展示
        //
        if (surveySelectModeValue == SURVEY_MODE_MM) {
            SurveyDataTable surveyDataTable = csvUtil.getSurveyByDepth(csvFileName, String.valueOf(depthStr));
            String oldAmm = "";
            String oldBmm = "";
            oldAmm = isZero ? surveyDataTable.getA0mm() : surveyDataTable.getA180mm();
            oldBmm = isZero ? surveyDataTable.getB0mm() : surveyDataTable.getB180mm();
            if (oldAmm.length() > 7) {
                int endIndex = oldAmm.contains("-") ? 7 : 6;
                oldAmm = oldAmm.substring(0, endIndex);
            }

            if (oldBmm.length() > 7) {
                int endIndex = oldBmm.contains("-") ? 7 : 6;
                oldBmm = oldBmm.substring(0, endIndex);
            }
            tvAOldmm.setText(oldAmm);
            tvBOldmm.setText(oldBmm);
        } else {
            //raw模型
            SurveyDataTable surveyDataTable = csvUtil.getSurveyByDepth(csvFileName, String.valueOf(depthStr));
            Log.d("chenliang surveyDataTable->", surveyDataTable.getA0Raw() + "," + surveyDataTable.getA180Raw() + "," +
                    surveyDataTable.getA0mm() + "," + surveyDataTable.getA180mm());
            String oldARaw = "";
            String oldBRaw = "";
            oldARaw = isZero ? surveyDataTable.getA0Raw() : surveyDataTable.getA180Raw();
            oldBRaw = isZero ? surveyDataTable.getB0Raw() : surveyDataTable.getB180Raw();
            if (oldARaw.length() > 7) {
                int endIndex = oldARaw.contains("-") ? 7 : 6;
                oldARaw = oldARaw.substring(0, endIndex);
            }

            if (oldBRaw.length() > 7) {
                int endIndex = oldBRaw.contains("-") ? 7 : 6;
                oldBRaw = oldBRaw.substring(0, endIndex);
            }
            tvAOldmm.setText(oldARaw);
            tvBOldmm.setText(oldBRaw);
        }

    }

    /**
     * 当模式180的时候，展示对应深度0模式的值
     *
     * @param depthStr
     */
    private void showCompareMMValue(String depthStr) {
        //获取对应坐标的mm值进行展示
        SurveyDataTable surveyDataTable = csvUtil.getSurveyByDepth(csvFileName, String.valueOf(depthStr));
//        String oldAmm = "";
//        String oldBmm = "";
//        oldAmm = isZero ? surveyDataTable.getA0mm() : surveyDataTable.getA180mm();
//        oldBmm = isZero ? surveyDataTable.getB0mm() : surveyDataTable.getB180mm();
        if (surveySelectModeValue == SURVEY_MODE_MM) {
            String compareA0mm = "";
            String compareB0mm = "";
            compareA0mm = surveyDataTable.getA0mm();
            compareB0mm = surveyDataTable.getB0mm();
            if (compareA0mm.length() > 7) {
                int endIndex = compareA0mm.contains("-") ? 7 : 6;
                compareA0mm = compareA0mm.substring(0, endIndex);
            }

            if (compareB0mm.length() > 7) {
                int endIndex = compareB0mm.contains("-") ? 7 : 6;
                compareB0mm = compareB0mm.substring(0, endIndex);
            }
//        tvAOldmm.setText(oldAmm);
//        tvBOldmm.setText(oldBmm);
            compareA0mm = "A0:" + compareA0mm;
            compareB0mm = "B0:" + compareB0mm;
            tvCompareA0.setText(compareA0mm);
            tvCompareB0.setText(compareB0mm);
        } else {

            String compareA0Raw = surveyDataTable.getA0Raw();
            String compareB0Raw = surveyDataTable.getB0Raw();
            compareA0Raw = "A0:" + compareA0Raw;
            compareB0Raw = "B0:" + compareB0Raw;
            tvCompareA0.setText(compareA0Raw);
            tvCompareB0.setText(compareB0Raw);

        }

    }

    /**
     * 完成测量
     */
    private void completeSurvey() {
        Toast.makeText(Survey2Activity.this, "測量完成", Toast.LENGTH_SHORT).show();
        Intent i;
        i = new Intent(Survey2Activity.this, InclinometerSurveyListActivity.class);
        i.putExtra("csvFileName", csvFileName);
        i.putExtra("csvFilePath", csvFilePath);
        startActivity(i);
        Survey2Activity.this.finish();
    }

    private void showRevertDegree() {
        new AlertDialog.Builder(Survey2Activity.this).setTitle("是否跳轉到180繼續進行測量")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Survey2Activity.this.finish();
                    }
                }).setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //顺序不能变，不然显示赌赢的A0值会取到之前没修改的值，显示是top的值获取的却是bottom的值
                        tvDepthNum.setText(String.valueOf(bottomValue));
                        rb180degree.setChecked(true);
                    }
                }).show();
    }

    /**
     * 存储CSV的数据
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String saveCsvData() {
        //计算出表格需要的值,根据csvfilename和高度去更新对应的的值
        String depthStr = tvDepthNum.getText().toString();
        //哪种模式下
        String saveAB = "";
//      if (csvUtil !=null){
        saveAB = csvUtil.saveDatasInDB(csvFileName, depthStr, currOneChannelAngle, currtwoChannelAngle, interval, isZero);
        //将数据存入到csv文件中去
        csvUtil.saveData(csvFilePath, csvFileName, interval);
//        }
        return saveAB;
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

//        sensorModeValue = (int) SharedPreferencesUtil.getData(Constants.SENSORMODE_KEY, 0);
        sensitivityValue = (int) SharedPreferencesUtil.getData(Constants.SENSITIVITY_KEY, 0);
        beepValue = (int) SharedPreferencesUtil.getData(Constants.BEEP_KEY, 0);
//        unitValue = (int) SharedPreferencesUtil.getData(Constants.UNIT_KEY, 0);
//        decimalValue = (int) SharedPreferencesUtil.getData(Constants.DECIMAL, 0);
//        sendDuration = (int) SharedPreferencesUtil.getData(Constants.SURVEY_DURATION, 150);
        currSnValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        autoTimeOut = (int) SharedPreferencesUtil.getData(Constants.TIME_OUT, autoTimeOut);
        autoNums = autoTimeOut * 1000 / sendDuration;
//        if (unitValue == Constants.UNIT_DEG) {
//
//            image1.setImageResource(R.drawable.new_deg);
////            image2.setImageResource(R.mipmap.b6);
//        } else {
//            image1.setImageResource(R.drawable.new_raw1);
////            image2.setImageResource(R.mipmap.b1);
//        }
        int sensitivityMode = 2;
        if (sensitivityValue == Constants.SENSITVITY_1_FASTER) {
            sensitivityMode = 2;
        } else if (sensitivityValue == Constants.SENSITVITY_2_DEFAULT) {
            sensitivityMode = 3;
        } else if (sensitivityValue == Constants.SENSITVITY_3_SLOWER) {
            sensitivityMode = 4;
        } else if (sensitivityValue == Constants.SENSITVITY_4_DEGREE) {
            sensitivityMode = 5;
        } else if (sensitivityValue == Constants.SENSITVITY_5_DEGREE) {
            sensitivityMode = 6;
        } else if (sensitivityValue == Constants.SENSITVITY_6_DEGREE) {
            sensitivityMode = 7;
        } else if (sensitivityValue == Constants.SENSITVITY_7_DEGREE) {
            sensitivityMode = 8;
        } else if (sensitivityValue == Constants.SENSITVITY_8_DEGREE) {
            sensitivityMode = 9;
        } else if (sensitivityValue == Constants.SENSITVITY_9_DEGREE) {
            sensitivityMode = 10;
        }
        oneAxisLink = new AxisLink2(sensitivityMode, "AAAAA");
        twoAxisLink = new AxisLink2(sensitivityMode, "BBBBB");
        tvBottomTitle.setText(csvFileName);
        //稳定阀值
        int stabltyTimeIndex = (int) SharedPreferencesUtil.getData(Constants.STABLITY_TIME, 10);
        THRESHOLD = stabityTimeFloats[stabltyTimeIndex];
        //数据展示模式
        surveySelectModeValue = (int) SharedPreferencesUtil.getData(Constants.SURVEY_SELECT_MODE, 0);
        if (surveySelectModeValue == SURVEY_MODE_RAW) {
            tvRaw.setText("raw");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        survey2Handler.removeCallbacksAndMessages(null);
        unregisterReceiver(mGattUpdateReceiver);
    }

    void initPlay(String beep) {
        try {
            if (beep.equals("Default"))
                playRd("save_csv.mp3");
            if (beep.equals("TypeA"))
                playRd("TypeA.wav");
            else if (beep.equals("TypeB"))
                playRd("TypeB.wav");
            else if (beep.equals("TypeC"))
                playRd("TypeC.wav");
            else if (beep.equals("TypeD"))
                playRd("TypeD.wav");
            else if (beep.equals("TypeE"))
                playRd("TypeE.wav");
            else if (beep.equals("TypeF")) {
                playRd("TypeF.wav");
            }
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
        //mMediaPlayer.setLooping(false)
    }

    /**
     * 解析数据
     *
     * @param data 接收到的字节数组
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
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
            currOneChannelAngle = oneChannelAngle; //记录当前原始角度值，用于保存到表格当中去
            currtwoChannelAngle = twoChannelAngle;
            //获取电量
            float battValue = Utils.getBattValue(voltage);
            tvTitle2.setText((int) battValue + "%");
            boolean isSave1 = showAxisValue(oneChannelAngle, oneAxisLink);
            boolean isSave2 = showAxisValue(twoChannelAngle, twoAxisLink);
            boolean isSave3 = isSave1 && isSave2;
//            Log.d("chenliang", "isSave1->" + isSave1 + "   isSave2->" + isSave2 + "   isSave3->" + isSave3);
//            isSave = showAxisValue(oneChannelAngle, oneAxisLink);
//            isSave = showAxisValue(twoChannelAngle, twoAxisLink)&& isSave;
//            boolean isSave3 = isSave1 && isSave2;
            //显示mm的值
            double deg1 = getDeg(oneChannelAngle, Constants.SFMODE_1AXIS);
            double deg2 = getDeg(twoChannelAngle, Constants.SFMODE_2AXIS);
            //获取mm数据
            double radians1 = Math.toRadians(deg1);
            double radians2 = Math.toRadians(deg2);
//            double mm1 = Math.sin(radians1) * 500;
//            double mm2 = Math.sin(radians2) * 500;
            double mm1 = (Math.sin(radians1) * interval * 1000);
            double mm2 = (Math.sin(radians2) * interval * 1000);

            //展示数据
            double showA = mm1;
            double showB = mm2;
            if (surveySelectModeValue == SURVEY_MODE_RAW) {
                //raw数据展示且获取raw数据
                showA = getRaw(deg1);
                showB = getRaw(deg2);
                String showAInt = (int) showA + "";
                String showBInt = (int) showB + "";
//                tvA.setText(deg2Format.format(showA));
//                tvB.setText(deg2Format.format(showB));
                tvA.setText(showAInt);
                tvB.setText(showBInt);
            } else {
                //mm
                String showAInt = (int) showA + "";
                String showBInt = (int) showB + "";
//                tvA.setText(showAInt);
//                tvB.setText(showBInt);
                tvA.setText(deg2Format.format(showA));
                tvB.setText(deg2Format.format(showB));
            }
//            tvA.setText(currOneChannelAngle + "");
//            tvB.setText(currtwoChannelAngle + "");
            if (isSave3) {
                btnSave.setText("Stable");
//                btnSave.setBackgroundResource(R.drawable.btn_start_bg);
                btnSave.setBackgroundResource(R.drawable.btn_save_selector);
                toPlay();
                if (isAuto && !isAutoSaveData) {
                    //在自动模式稳定情况下，在该点没有存储过数据就进行存储
                    ++currAutoIndex;
                    if (currAutoIndex == autoNums) {
                        String preAB = saveCsvData();
                        changeLogicUI(preAB);
                        isAutoSaveData = true;
                    }
                }
            } else {
                currAutoIndex = 0;//重置索引
                btnSave.setText("Wait");
                btnSave.setBackgroundResource(R.drawable.btn_pause_bg);
                isFirstPlay = true;
                if (isAuto) {
                    //自动模式下没有稳定的话就将是否在该店数据重置为false
                    isAutoSaveData = false;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("chenliang", "數據解析出問題:" + formatMsgContent(data));
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

        public AxisLink2(int sersitivity, String axis) {
            this.sersitivityMode = sersitivity;
            this.axis = axis;
        }

        private String axis;

        private int sersitivityMode; //精度

        private int count;//计算连续第几次

        private float preValue = 0;

        public boolean verifyData(float axiaValue) {
            float changeValue = Float.parseFloat(deg3Format.format(axiaValue));
            if (preValue == changeValue) {
                count = count + 1;
                if (count > sersitivityMode) {
                    count = sersitivityMode;
                }
                return count == sersitivityMode;
            }
            if (count == sersitivityMode) {
                if (Math.abs(changeValue - preValue) > THRESHOLD) {
                    count = 0;
                    preValue = changeValue;
                    return false;
                }
                return true;
            }

            preValue = changeValue;
            count = 0; //当在sersitivityMode次数内没有连续，则重新计算
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Survey2Activity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
}
