package com.vitalong.bluetest2.bluepro;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.ByteTransformUtil;
import com.vitalong.bluetest2.Utils.CRC16CheckUtil;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.SaveSuerveyBean;
import com.vitalong.bluetest2.bean.VerifyDataBean;

import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.Bind;

public class SurveyActivity extends MyBaseActivity2 {
    final String TAG = "SurveyActivity";

    @Bind(R.id.tvUnitValue)
    public TextView tvUnitValue;
    @Bind(R.id.tvSNValue)
    public TextView tvSNValue;
    @Bind(R.id.tvSurveyModeValue)
    public TextView tvSurveyModeValue;
    @Bind(R.id.tvDecimalVaule)
    public TextView tvDecimalVaule;
    @Bind(R.id.constraGroup)
    public Group constraGroup;
    @Bind(R.id.tvAxis1Value)
    public TextView tvAxis1Value;
    @Bind(R.id.tvAxis2Value)
    public TextView tvAxis2Value;
    @Bind(R.id.imgAxis1)
    public ImageView imgAxis1;
    @Bind(R.id.imgAxisTest2)
    public ImageView imgAxis2;
    @Bind(R.id.btnSave)
    public Button btnSave;
    @Bind(R.id.img1)
    public ImageView image1;
    @Bind(R.id.img2)
    public ImageView image2;
    @Bind(R.id.tvNo1)
    public TextView tvNo1;
    @Bind(R.id.tvNo2)
    public TextView tvNo2;
    public TextView tvTestData;
    public TextView tvTestData2;
    String[] sfMode = new String[]{"1 Axis", "2 Axis"};
    String filePath = "/geostar/tiltmeter/";
    String[] ctype = new String[]{"1(Faster)", "2(Default)", "3(Slower)", "4(Degree)", "5(Degree)", "6(Degree)", "7(Degree)", "8(Degree)", "9(Degree)"};
    String[] beeps = new String[]{"Mute", "TypeA", "TypeB", "TypeC", "TypeD", "TypeE"};
    String[] ctype3 = new String[]{"Deg", "Raw"};
    String[] ctype4ByDeg = new String[]{"3", "4"};
    String[] ctype4ByRaw = new String[]{"1"};

    private int sensorModeValue = 0;//单轴还是双轴
    private int sensitivityValue = 0;//1 fASTER
    private int beepValue = 0;//声音
    private int unitValue = 0;//单位
    private int decimalValue = 3;
    private String currSnValue = "";
    private SurveyHandler surveyHandler;
    private double dAxisA = 0;
    private double dAxisB = 0;
    private double dAxisC = 0;
    private double dAxisD = 0;
    private double dBxisA = 0;
    private double dBxisB = 0;
    private double dBxisC = 0;
    private double dBxisD = 0;
    private DecimalFormat deg2Format = new DecimalFormat("0.00");
    private DecimalFormat deg3Format = new DecimalFormat("0.000");
    private DecimalFormat deg4Format = new DecimalFormat("0.0000");
    private AxisLink oneAxisLink;//用于判断第一个角度是否有连续值的
    private AxisLink twoAxisLink;//用于判断第二行角度是否有连续值的
    private double THRESHOLD = 0.002;//阈值
    boolean isSave = false;
    int clNumb = 0;//用于计算点击次数
    private SaveSuerveyBean saveSuerveyBean;//传递给保存界面保存的数据
    private boolean isBack = false; //是否双轴点击保存后的返回
    MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean isFirstPlay = true;

    private float currOneChannelAngle;//记录轴1的原始角度值
    private float currtwoChannelAngle;//记录轴2的原始角度值
    private int sendDuration = 150;//循环发送命令的时间间隔

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        bindToolBar();
        makeStatusBar(R.color.white);
        surveyHandler = new SurveyHandler();
        saveSuerveyBean = new SaveSuerveyBean();
        initSp();
        initPlay(beeps[beepValue]);
        initView();
        if (initVerifyData()) {
            startCmdRepeat();
        }
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
            Toast.makeText(SurveyActivity.this, "verify data is error,please update verify data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 开始重复发送数据
     */
    private void startCmdRepeat() {
        surveyHandler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    protected void registerBc() {

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {
//        System.out.println("SurveyActivity:" + formatMsgContent(array));
        //get数据
        //解析数据
        parseData(array);
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
            Log.d("chenliang", "SurveyActivity1解析的浮点数是:" + oneChannelAngle +
                    " : " + oneChannelTemperature +
                    " : " + twoChannelAngle +
                    " : " + twoChannelTemperature);
            currOneChannelAngle = oneChannelAngle; //记录当前原始角度值，用于保存到表格当中去
            currtwoChannelAngle = twoChannelAngle;
            tvTestData.setText(oneChannelAngle + "");
            tvTestData2.setText(twoChannelAngle + "");
            if (unitValue == Constants.UNIT_DEG) {
                isSave = showAxisValue(tvAxis1Value, imgAxis1, getDeg(oneChannelAngle, Constants.SFMODE_1AXIS), oneChannelAngle, oneAxisLink);
                if (sensorModeValue == Constants.SFMODE_2AXIS) {
                    isSave = showAxisValue(tvAxis2Value, imgAxis2, getDeg(twoChannelAngle, Constants.SFMODE_2AXIS), twoChannelAngle, twoAxisLink) && isSave;
                }
            } else if (unitValue == Constants.UNIT_RAW) {
                isSave = showAxisValue(tvAxis1Value, imgAxis1, getRaw(getDeg(oneChannelAngle, Constants.SFMODE_1AXIS)), oneChannelAngle, oneAxisLink);
                if (sensorModeValue == Constants.SFMODE_2AXIS) {
                    isSave = showAxisValue(tvAxis2Value, imgAxis2, getRaw(getDeg(twoChannelAngle, Constants.SFMODE_2AXIS)), twoChannelAngle, twoAxisLink) && isSave;
                }
            }

            if (isSave) {
                btnSave.setText("SAVE");
                btnSave.setBackgroundColor(0xFF35548B);
                btnSave.setTextColor(0xFFffffFF);
                toPlay();
            } else {
                btnSave.setText("LOADING...");
                btnSave.setBackgroundColor(0xFFEE7600);
                btnSave.setTextColor(0xFFffffFF);
                isFirstPlay = true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("chenliang", "数据解析出问题:" + formatMsgContent(data));
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
    private boolean showAxisValue(TextView tv, ImageView imgDimond, double textValue, double axisValue, AxisLink axisLink) {
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

    @Override
    protected void disconnectBlue() {
        System.out.println("蓝牙断开了连接");
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), SurveyActivity.this);
    }

    private void initSp() {

        sensorModeValue = (int) SharedPreferencesUtil.getData(Constants.SENSORMODE_KEY, 0);
        sensitivityValue = (int) SharedPreferencesUtil.getData(Constants.SENSITIVITY_KEY, 0);
        beepValue = (int) SharedPreferencesUtil.getData(Constants.BEEP_KEY, 0);
        unitValue = (int) SharedPreferencesUtil.getData(Constants.UNIT_KEY, 0);
        decimalValue = (int) SharedPreferencesUtil.getData(Constants.DECIMAL, 0);
        sendDuration = (int) SharedPreferencesUtil.getData(Constants.SURVEY_DURATION, 150);
        currSnValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        if (unitValue == Constants.UNIT_DEG) {

            image1.setImageResource(R.drawable.new_deg);
//            image2.setImageResource(R.mipmap.b6);
        } else {
            image1.setImageResource(R.drawable.new_raw1);
//            image2.setImageResource(R.mipmap.b1);
        }
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
        oneAxisLink = new AxisLink(sensitivityMode);
        twoAxisLink = new AxisLink(sensitivityMode);
    }

    private void initView() {
        tvTestData = findViewById(R.id.tvTestData1);
        tvTestData2 = findViewById(R.id.tvTestData2);
        tvUnitValue.setText(ctype3[unitValue]);
        tvSurveyModeValue.setText(sfMode[sensorModeValue]);
        tvSNValue.setText(currSnValue);
        if (unitValue == Constants.UNIT_DEG) {
            tvDecimalVaule.setText(decimalValue + "");
        } else {
            tvDecimalVaule.setText("0");
        }

        if (sensorModeValue == Constants.SFMODE_1AXIS) {
            constraGroup.setVisibility(View.GONE);
            imgAxis2.setVisibility(View.GONE);
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

//                mMediaPlayer.stop();
//                isFirstPlay = true;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提交数据
                if (sensorModeValue == Constants.SFMODE_1AXIS) {
                    //单轴模式
                    clNumb++;
                    if (clNumb == 1) {
                        saveSuerveyBean.setOneChannelAngle1(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle1(currtwoChannelAngle);
                        saveSuerveyBean.setShow1(tvAxis1Value.getText().toString());
                        Log.d("chenliang", "打印原始值的数据->currOneChannelAngle" + currOneChannelAngle + "    getDeg:" +
                                getDeg(currOneChannelAngle, Constants.SFMODE_1AXIS) + "   getRaw:" +
                                getRaw(getDeg(currOneChannelAngle, Constants.SFMODE_1AXIS)));

                        if (unitValue == Constants.UNIT_RAW) {
                            image1.setImageResource(R.drawable.new_raw2);
//                            image2.setImageResource(R.mipmap.b2);
                        }
                        tvNo1.setText(clNumb + 1 + "");
                    } else if (clNumb == 2) {
                        saveSuerveyBean.setOneChannelAngle2(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle2(currtwoChannelAngle);
                        saveSuerveyBean.setShow2(tvAxis1Value.getText().toString());
                        if (unitValue == Constants.UNIT_RAW) {
                            image1.setImageResource(R.drawable.new_raw3);
//                            image2.setImageResource(R.mipmap.b3);
                        }
                        tvNo1.setText(clNumb + 1 + "");
                    } else if (clNumb == 3) {
                        saveSuerveyBean.setOneChannelAngle3(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle3(currtwoChannelAngle);
                        saveSuerveyBean.setShow3(tvAxis1Value.getText().toString());
                        if (unitValue == Constants.UNIT_RAW) {
                            image1.setImageResource(R.drawable.new_raw4);
//                            image2.setImageResource(R.mipmap.b4);
                        }
                        tvNo1.setText(clNumb + 1 + "");
                    } else {
                        saveSuerveyBean.setOneChannelAngle4(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle4(currtwoChannelAngle);
                        saveSuerveyBean.setShow4(tvAxis1Value.getText().toString());
                        Intent i = new Intent(SurveyActivity.this, SaveDataActivity.class);
                        i.putExtra("saveData", saveSuerveyBean);
                        i.putExtra("isSingleAxis", true);
                        i.putExtra("canSave", true);
                        startActivity(i);
                        SurveyActivity.this.finish();
                        Toast.makeText(SurveyActivity.this, "进行数据保存", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //双轴模式
                    Intent i = new Intent(SurveyActivity.this, SaveDataActivity.class);
                    if (!isBack) {
                        saveSuerveyBean.setOneChannelAngle1(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle1(currtwoChannelAngle);
                        saveSuerveyBean.setShow1(tvAxis1Value.getText().toString());
                        saveSuerveyBean.setShow2(tvAxis2Value.getText().toString());
                        if (unitValue == Constants.UNIT_RAW) {
                            image1.setImageResource(R.drawable.new_raw3);
//                            image2.setImageResource(R.mipmap.b3);
                        }
                        i.putExtra("isSingleAxis", false);
                        i.putExtra("saveData", saveSuerveyBean);
                        i.putExtra("canSave", false);
                        startActivityForResult(i, 1);
                    } else {
                        saveSuerveyBean.setOneChannelAngle2(currOneChannelAngle);
                        saveSuerveyBean.setTwoChannelAngle2(currtwoChannelAngle);
                        saveSuerveyBean.setShow3(tvAxis1Value.getText().toString());
                        saveSuerveyBean.setShow4(tvAxis2Value.getText().toString());
                        i.putExtra("isSingleAxis", false);
                        i.putExtra("saveData", saveSuerveyBean);
                        i.putExtra("canSave", true);
                        startActivity(i);
                        SurveyActivity.this.finish();
                    }
                    Toast.makeText(SurveyActivity.this, "进行数据保存", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SurveyActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void toPlay() {
        if (!mMediaPlayer.isPlaying() && isFirstPlay) {
            isFirstPlay = false;
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        deInit();
        super.onDestroy();
    }

    private void deInit() {
        surveyHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mGattUpdateReceiver);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            tvNo1.setText("3");
            tvNo2.setText("4");
            isBack = true;
        }
    }

    class SurveyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //一直重复发送
            sendCmdCodeByHex(Constants.REAL_DATA_CMD);
            surveyHandler.sendEmptyMessageDelayed(0, sendDuration);
        }
    }

    void initPlay(String beep) {
        try {
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
        } catch (IOException err) {
            Log.d("chenliang","错误信息->"+err.getMessage());
            Toast.makeText(SurveyActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
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
     * 用于判断数据是否相等
     */
    class AxisLink {

        public AxisLink(int sersitivity) {
            this.sersitivityMode = sersitivity;
        }

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
            Log.d(TAG, "Math.abs(changeValue - preValue):" + Math.abs(changeValue - preValue));
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
}
