package com.vitalong.bluetest2.bluepro;

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
import com.vitalong.bluetest2.bean.VerifyDataBean;

import java.text.DecimalFormat;

import butterknife.Bind;

public class SurveyActivity extends MyBaseActivity2 {

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
    @Bind(R.id.imgAxis2)
    public ImageView imgAxis2;
    @Bind(R.id.btnSave)
    public Button btnSave;
    String[] sfMode = new String[]{"1 Axis", "2 Axis"};
    String filePath = "/geostar/tiltmeter/";
    String[] ctype = new String[]{"1(Faster)", "2(Default)", "3(Slower)"};
    String[] ctype2 = new String[]{"Mute", "TypeA", "TypeB", "TypeC", "TypeD", "TypeE"};
    String[] ctype3 = new String[]{"Deg", "Raw"};
    String[] ctype4ByDeg = new String[]{"3", "4"};
    String[] ctype4ByRaw = new String[]{"1"};

    private int sensorModeValue = 0;
    private int sensitivityValue = 0;
    private int beepValue = 0;
    private int unitValue = 0;
    private int decimalValue = 0;
    private String currSnValue = "";
    private SurveyHandler surveyHandler;
    private VerifyDataBean verifyDataBean;
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
    private double THRESHOLD = 0.002;
    boolean isSave = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        bindToolBar();
        makeStatusBar(R.color.white);
        surveyHandler = new SurveyHandler();
        initSp();
        initView();
        if (initVerifyData()) {
            startCmdRepeat();
        }
    }

    private Boolean initVerifyData() {

        try {
            verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
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
            if (unitValue == Constants.UNIT_DEG) {
                isSave = showAxisValue(tvAxis1Value, imgAxis1, getDeg(oneChannelAngle, Constants.SFMODE_1AXIS), oneChannelAngle, oneAxisLink);
                if (sensorModeValue == Constants.SFMODE_2AXIS) {
                    isSave = isSave && showAxisValue(tvAxis2Value, imgAxis2, getDeg(twoChannelAngle, Constants.SFMODE_2AXIS), twoChannelAngle, twoAxisLink);
                }
            } else if (unitValue == Constants.UNIT_RAW) {
                isSave = showAxisValue(tvAxis1Value, imgAxis1, getRaw(getDeg(oneChannelAngle, Constants.SFMODE_1AXIS)), oneChannelAngle, oneAxisLink);
                if (sensorModeValue == Constants.SFMODE_2AXIS) {
                    isSave = isSave && showAxisValue(tvAxis2Value, imgAxis2, getRaw(getDeg(twoChannelAngle, Constants.SFMODE_2AXIS)), twoChannelAngle, twoAxisLink);
                }
            }

            if (isSave) {
                btnSave.setText("SAVE");
                btnSave.setBackgroundColor(0xFF35548B);
                btnSave.setTextColor(0xFFffffFF);
            } else {
                btnSave.setText("LADING...");
                btnSave.setBackgroundColor(0xFFEE7600);
                btnSave.setTextColor(0xFFffffFF);
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
                if (decimalValue == 0) {
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
        currSnValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        int sensitivityMode = 3;
        if (sensitivityValue == Constants.SENSITVITY_1_FASTER) {
            sensitivityMode = 3;
        } else if (sensitivityValue == Constants.SENSITVITY_2_DEFAULT) {
            sensitivityMode = 4;
        } else if (sensitivityValue == Constants.SENSITVITY_3_SLOWER) {
            sensitivityMode = 5;
        }
        oneAxisLink = new AxisLink(sensitivityMode);
        twoAxisLink = new AxisLink(sensitivityMode);
    }

    private void initView() {
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
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提交数据



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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surveyHandler.removeCallbacksAndMessages(null);
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
            return dAxisA * (f * f * f) + dAxisB * (f * f) + (dAxisC + f) + dAxisD;
        }

        return dBxisA * (f * f * f) + dBxisB * (f * f) + (dBxisC + f) + dBxisD;
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

    class SurveyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //一直重复发送
            sendCmdCodeByHex(Constants.REAL_DATA_CMD);
            surveyHandler.sendEmptyMessageDelayed(0, 200);
        }
    }

    /**
     * 用于判断数据是否相等
     */
    class AxisLink {

        public AxisLink(int sersitivity) {
            this.sersitivityMode = sersitivity;
        }

        private int sersitivityMode;

        private int count;//

        private float preValue = 0;

        public boolean verifyData(float axiaValue) {
            float changeValue = Float.parseFloat(deg4Format.format(axiaValue));
            if (preValue == changeValue) {
                count = count + 1;
                if (count > sersitivityMode) {
                    count = sersitivityMode;
                }
                return count == sersitivityMode;
            }

            if (Math.abs(changeValue - preValue) > THRESHOLD) {
                preValue = changeValue;
                count = 1;
                return false;
            }
            return true;
        }
    }
}
