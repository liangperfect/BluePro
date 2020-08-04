package com.vitalong.bluetest2.bluepro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Utils;

import butterknife.Bind;

public class SettingActivity extends MyBaseActivity2 {

    @Bind(R.id.btnCoefficients)
    public Button btnCoefficients;
    @Bind(R.id.tvSNValue)
    public TextView tvSNValue;

    private SettingHandler settingHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindToolBar();
        //发送数据
        makeStatusBar(R.color.white);
        settingHandler = new SettingHandler();
        initListener();
        settingHandler.sendEmptyMessageDelayed(0, 200);
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
        String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
        //进行数据解析
        System.out.println("SettingActivity接收到的数据:" + formatMsgContent(array));
//        System.out.println("SN数据->" + Integer.parseInt(hexStr.substring(6, 14), 16));
        tvSNValue.setText(String.valueOf(Integer.parseInt(hexStr.substring(6, 14), 16)));
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

    class SettingHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //获取SN的命令
            sendCmdCodeByHex("01 03 00 05 00 02 d4 0a");
        }
    }
}
