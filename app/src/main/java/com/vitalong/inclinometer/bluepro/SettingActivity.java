package com.vitalong.inclinometer.bluepro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.inclinometer.MyBaseActivity2;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.Utils.SharedPreferencesUtil;
import com.vitalong.inclinometer.Utils.Utils;

import butterknife.Bind;

public class SettingActivity extends MyBaseActivity2 {

    @Bind(R.id.btnCoefficients)
    public Button btnCoefficients;
    @Bind(R.id.tvSNValue)
    public TextView tvSNValue;
    @Bind(R.id.SpSensorModeValue)
    public Spinner SpSensorModeValue;
    @Bind(R.id.spSensitivityValue)
    public Spinner spSensitivityValue;
    @Bind(R.id.spBeepValue)
    public Spinner spBeepValue;
    @Bind(R.id.spUnitValue)
    public Spinner spUnitValue;
    @Bind(R.id.btnUpdate)
    public Button btnUpdate;
    @Bind(R.id.radioGroup)
    public RadioGroup radioGroup;
    @Bind(R.id.spDecimalValue)
    public TextView spDecimalValue;
    @Bind(R.id.radio1)
    public RadioButton radio1Button;
    @Bind(R.id.radio2)
    public RadioButton radio2Button;
    @Bind(R.id.edtSurveyDuration)
    public EditText edtSurveyDuration;
    @Bind(R.id.btnSite)
    public Button btnSite;
    @Bind(R.id.spTime)
    public Spinner spTime;
    @Bind(R.id.tvStabilityTime)
    public TextView tvStabilityTime;
    @Bind(R.id.spStabilityTime)
    public Spinner spStabilityTime;
    private int sensorModeValue = 0;
    private int sensitivityValue = 0;
    private int beepValue = 0;
    private int unitValue = 0;
    private int decimalValue = 0;
    private int stabilityTimeValue = 0;
    private int currTimeOut = 3;//自动模式下菱形稳定的最小时间区间

    private SettingHandler settingHandler;
    String[] sfMode = new String[]{"1 Axis", "2 Axis"};
    String filePath = "/geostar/tiltmeter/";
    //    String[] ctype = new String[]{"1(Faster)", "2(Default)", "3(Slower)", "4(Degree)", "5(Degree)", "6(Degree)", "7(Degree)", "8(Degree)", "9(Degree)"};
    String[] ctype = new String[]{"1(Faster)", "2(Default)", "3(Slower)"};
    String[] ctype2 = new String[]{"Default", "TypeA", "TypeB", "TypeC", "TypeD", "TypeE", "TypeF"};
    String[] ctype3 = new String[]{"Deg", "Raw"};
    String[] times = new String[]{"level 1", "level 2", "level 3", "level 4", "level 5", "level 6", "level 7", "level 8", "level 9", "level 10",
            "level 11", "level 12", "level 13", "level 14", "level 15", "level 16", "level 17", "level 18", "level 19", "level 20"};//稳定的时间数组
    //    String[] ctype4ByDeg = new String[]{"3", "4"};
//    String[] ctype4ByRaw = new String[]{"0"};
    boolean isPause = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindToolBar();
        makeStatusBar(R.color.white);
        settingHandler = new SettingHandler();
        initViewAndDatas();
        initListener();
        settingHandler.sendEmptyMessageDelayed(0, 400);
    }

    private void initViewAndDatas() {
        initSpiners(SpSensorModeValue, sfMode);
        initSpiners(spSensitivityValue, ctype);
        initSpiners(spBeepValue, ctype2);
        initSpiners(spUnitValue, ctype3);
        initSpiners(spStabilityTime, times);
        sensorModeValue = (int) SharedPreferencesUtil.getData(Constants.SENSORMODE_KEY, 0);
        sensitivityValue = (int) SharedPreferencesUtil.getData(Constants.SENSITIVITY_KEY, 0);
        beepValue = (int) SharedPreferencesUtil.getData(Constants.BEEP_KEY, 0);
        unitValue = (int) SharedPreferencesUtil.getData(Constants.UNIT_KEY, 0);
        decimalValue = (int) SharedPreferencesUtil.getData(Constants.DECIMAL, 3);
        stabilityTimeValue = (int) SharedPreferencesUtil.getData(Constants.STABLITY_TIME, 9);
        currTimeOut = (int) SharedPreferencesUtil.getData(Constants.TIME_OUT, 3);
        int duration = (int) SharedPreferencesUtil.getData(Constants.SURVEY_DURATION, 150);
        edtSurveyDuration.setText(String.valueOf(duration));
        SpSensorModeValue.setSelection(sensorModeValue);
        spSensitivityValue.setSelection(sensitivityValue);
        spBeepValue.setSelection(beepValue);
        spUnitValue.setSelection(unitValue);
        spStabilityTime.setSelection(stabilityTimeValue);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取Survey界面发送命令的时间间隔
                String duration = edtSurveyDuration.getText().toString();
                SharedPreferencesUtil.putData(Constants.SENSORMODE_KEY, sensorModeValue);
                SharedPreferencesUtil.putData(Constants.SENSITIVITY_KEY, sensitivityValue);
                SharedPreferencesUtil.putData(Constants.BEEP_KEY, beepValue);
                SharedPreferencesUtil.putData(Constants.UNIT_KEY, unitValue);
                SharedPreferencesUtil.putData(Constants.STABLITY_TIME, stabilityTimeValue);
                SharedPreferencesUtil.putData(Constants.DECIMAL, decimalValue);
                SharedPreferencesUtil.putData(Constants.SURVEY_DURATION, Integer.valueOf(duration));
                SharedPreferencesUtil.putData(Constants.TIME_OUT, currTimeOut);
                SettingActivity.this.finish();
                Toast.makeText(SettingActivity.this, "Setting success", Toast.LENGTH_SHORT).show();
            }
        });

        spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currTimeOut = position + 3;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStabilityTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                stabilityTimeValue = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private <T> void initSpiners(final Spinner sp, T[] datas) {
        //init
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datas);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemSelected(sp.getId(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void ItemSelected(int parentId, int mode) {

        switch (parentId) {

            case R.id.SpSensorModeValue:

                sensorModeValue = mode;
                break;
            case R.id.spSensitivityValue:

                sensitivityValue = mode;
                break;
            case R.id.spBeepValue:

                beepValue = mode;
                break;
            case R.id.spUnitValue:

                unitValue = mode;
                if (unitValue == 0) {
                    radioGroup.setVisibility(View.VISIBLE);
                    spDecimalValue.setVisibility(View.GONE);
                } else {
                    radioGroup.setVisibility(View.GONE);
                    spDecimalValue.setVisibility(View.VISIBLE);
                }
                break;
            default:
                //nothing to do
                break;
        }
    }

    private void initListener() {

        btnSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse("https://drive.google.com/file/d/1uJ6p5G9bKu1p4TYKo55zPrGyxOJu8gMq/view");
                Uri uri = Uri.parse("https://drive.google.com/file/d/1SAnS_4UEruJJTLn-MFzK5sGKi0Bl3B12/view?usp=sharing");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        btnCoefficients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingActivity.this, CoefficientsActivity.class));
            }
        });
        if (decimalValue == 3) {

            radio1Button.setChecked(true);
        }

        if (decimalValue == 4) {
            radio2Button.setChecked(true);
        }

        radio1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    decimalValue = 3;
                }
            }
        });

        radio2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    decimalValue = 4;
                }
            }
        });
    }

    @Override
    protected void registerBc() {
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {
        if (!isPause) {
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            //进行数据解析
            try {
                String snValueStr = String.valueOf(Integer.parseInt(hexStr.substring(6, 14), 16));
                tvSNValue.setText(snValueStr);
                SharedPreferencesUtil.putData("SNVaule", snValueStr);
            } catch (Exception ex) {
                tvSNValue.setText("error");
            }
        }
    }

    @Override
    protected void disconnectBlue() {
        System.out.println("蓝牙断开了连接");
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), SettingActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPause = true;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SettingActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class SettingHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //获取SN的命令
            sendCmdCodeByHex("01 03 00 05 00 02 d4 0a");
        }
    }
}
