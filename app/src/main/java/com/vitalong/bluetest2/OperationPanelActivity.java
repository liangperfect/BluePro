package com.vitalong.bluetest2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.VerifyDataBean;

import butterknife.Bind;

public class OperationPanelActivity extends MyBaseActivity2 implements View.OnClickListener {

    @Bind(R.id.imageButton)
    ImageButton backBtn;

    int delayTime = 300;
    private OperationPanelHandler operationPanelHandler;
    int count = 0;

    private VerifyDataBean verifyDataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_panel);
        bindToolBar();
        //发送数据
        makeStatusBar(R.color.red);
        operationPanelHandler = new OperationPanelHandler();
        operationPanelHandler.sendEmptyMessageDelayed(1, delayTime);
        initListener();
        verifyDataBean = new VerifyDataBean();
    }

    private void initListener() {
        backBtn.setOnClickListener(this);

    }

    @Override
    void registerBc() {
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    void receiveDataFromBlue(byte[] array) {

        //进行数据解析
        System.out.println("接收到的数据:" + formatMsgContent(array));
        parseVerifyData(formatMsgContent(array).substring(6, 20));
    }

    @Override
    void disconnectBlue() {
        System.out.println("蓝牙断开了连接");
        isShowingDialog = true;
        showStateDialog(getString(R.string.conn_disconnected_home), OperationPanelActivity.this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageButton:
                OperationPanelActivity.this.finish();
                break;
            default:
                break;
        }
    }


    class OperationPanelHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            sendCmdGetVerifyCode(msg.what);
        }
    }

    //需要连续发送8次获取数据
    private void sendCmdGetVerifyCode(int No) {

        switch (No) {
            case 1:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO1);
                operationPanelHandler.sendEmptyMessageDelayed(2, delayTime);
                break;
            case 2:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO2);
                operationPanelHandler.sendEmptyMessageDelayed(3, delayTime);
                break;
            case 3:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO3);
                operationPanelHandler.sendEmptyMessageDelayed(4, delayTime);
                break;
            case 4:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO4);
                operationPanelHandler.sendEmptyMessageDelayed(5, delayTime);
                break;
            case 5:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO5);
                operationPanelHandler.sendEmptyMessageDelayed(6, delayTime);
                break;
            case 6:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO6);
                operationPanelHandler.sendEmptyMessageDelayed(7, delayTime);
                break;
            case 7:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO7);
                operationPanelHandler.sendEmptyMessageDelayed(8, delayTime);
                break;
            case 8:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO8);
                break;
            default:
                System.out.println("获取矫正参数命令发送完成");
                break;
        }
    }

    private void parseVerifyData(String codeStr) {

        String d = Utils.getVerifyDatas(codeStr);
        count++;
        switch (count) {
            case 1:
                verifyDataBean.setAaxisA(d);
                break;
            case 2:
                verifyDataBean.setAaxisB(d);
                break;
            case 3:
                verifyDataBean.setAaxisC(d);
                break;
            case 4:
                verifyDataBean.setAaxisD(d);
                break;
            case 5:
                verifyDataBean.setBaxisA(d);
                break;
            case 6:
                verifyDataBean.setBaxisB(d);
                break;
            case 7:
                verifyDataBean.setBaxisC(d);
                break;
            case 8:
                verifyDataBean.setBaxisD(d);
                count = 0;
                break;
            default:

                break;
        }

    }
}
