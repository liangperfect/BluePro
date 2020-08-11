package com.vitalong.bluetest2.bluepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.Utils.Utils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        bindToolBar();
        makeStatusBar(R.color.white);
        initSp();
        initView();
    }

    @Override
    protected void registerBc() {

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {
        System.out.println("SurveyActivity:" + formatMsgContent(array));
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
    }

    private void initView() {
        tvUnitValue.setText(ctype3[unitValue]);
        tvSurveyModeValue.setText(sfMode[sensorModeValue]);
        tvSNValue.setText(currSnValue);
        if (unitValue == 0) {
            tvDecimalVaule.setText(decimalValue + "");
        } else {
            tvDecimalVaule.setText("0");
        }

        if (sensorModeValue == 0) {
            constraGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SurveyActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
