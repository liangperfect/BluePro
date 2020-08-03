package com.vitalong.bluetest2.bluepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.VerifyDataBean;

import butterknife.Bind;

public class CoefficientsActivity extends MyBaseActivity2 {

    @Bind(R.id.AaxisA)
    public EditText edtAxiasA;
    @Bind(R.id.AaxisB)
    public EditText edtAxiasB;
    @Bind(R.id.AaxisC)
    public EditText edtAxiasC;
    @Bind(R.id.AaxisD)
    public EditText edtAxiasD;
    @Bind(R.id.BaxisA)
    public EditText edtBxiasA;
    @Bind(R.id.BaxisB)
    public EditText edtBxiasB;
    @Bind(R.id.BaxisC)
    public EditText edtBxiasC;
    @Bind(R.id.BaxisD)
    public EditText edtBxiasD;
    @Bind(R.id.btnSet)
    public Button btnSet;
    private VerifyDataBean verifyDataBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coefficients);
        bindToolBar();
        makeStatusBar(R.color.white);
        verifyDataBean = myApplication.getVerifyDataBean();
        initData();
        initListener();
    }

    private void initData() {

        if (verifyDataBean == null)
            return;
        edtAxiasA.setText(verifyDataBean.getAaxisA());
        edtAxiasB.setText(verifyDataBean.getAaxisB());
        edtAxiasC.setText(verifyDataBean.getAaxisC());
        edtAxiasD.setText(verifyDataBean.getAaxisD());
        edtBxiasA.setText(verifyDataBean.getBaxisA());
        edtBxiasB.setText(verifyDataBean.getBaxisB());
        edtBxiasC.setText(verifyDataBean.getBaxisC());
        edtBxiasD.setText(verifyDataBean.getBaxisD());
    }

    private void initListener() {

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendCmdCodeByHex("010300000002C40B");
            }
        });
    }

    @Override
    protected void registerBc() {

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {

        System.out.println("CoefficientsActivity接收到的数据:" + formatMsgContent(array));
    }

    @Override
    protected void disconnectBlue() {
        System.out.println("蓝牙断开了连接");
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), CoefficientsActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            CoefficientsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
