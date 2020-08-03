package com.vitalong.bluetest2.bluepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Utils;

import butterknife.Bind;

public class SettingActivity extends MyBaseActivity2 {

    @Bind(R.id.btnCoefficients)
    public Button btnCoefficients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindToolBar();
        //发送数据
        makeStatusBar(R.color.white);
//        operationPanelHandler = new OperationPanelActivity.OperationPanelHandler();
//        operationPanelHandler.sendEmptyMessageDelayed(1, delayTime);
        initListener();
//        verifyDataBean = new VerifyDataBean();
    }

    private void initListener() {

        btnCoefficients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingActivity.this, CoefficientsActivity.class));
            }
        });
    }

    @Override
    protected void registerBc() {
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {

        //进行数据解析
        System.out.println("SettingActivity接收到的数据:" + formatMsgContent(array));
//        parseVerifyData(formatMsgContent(array).substring(6, 20));
    }

    @Override
    protected void disconnectBlue() {
        System.out.println("蓝牙断开了连接");
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), SettingActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SettingActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
