package com.vitalong.bluetest2.bluepro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.bluetest2.MyBaseActivity2;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.Utils.cmdAnalyze;
import com.vitalong.bluetest2.Utils.cmdClass;
import com.vitalong.bluetest2.bean.VerifyDataBean;

public class CoefficientsActivity extends MyBaseActivity2 {

    //    @Bind(R.id.AaxisA)
    public EditText edtAxiasA;
    //    @Bind(R.id.AaxisB)
    public EditText edtAxiasB;
    //    @Bind(R.id.AaxisC)
    public EditText edtAxiasC;
    //    @Bind(R.id.AaxisD)
    public EditText edtAxiasD;
    //    @Bind(R.id.BaxisA)
    public EditText edtBxiasA;
    //    @Bind(R.id.BaxisB)
    public EditText edtBxiasB;
    //    @Bind(R.id.BaxisC)
    public EditText edtBxiasC;
    //    @Bind(R.id.BaxisD)
    public EditText edtBxiasD;
    //    @Bind(R.id.btnSet)
    public Button btnSet;
    //    @Bind(R.id.imgAxisA)
    public ImageView imgAxisA;
    //    @Bind(R.id.imgAxisB)
    public ImageView imgAxisB;
    //    @Bind(R.id.imgAxisC)
    public ImageView imgAxisC;
    //    @Bind(R.id.imgAxisD)
    public ImageView imgAxisD;
    //    @Bind(R.id.imgBxisA)
    public ImageView imgBxisA;
    //    @Bind(R.id.imgBxisB)
    public ImageView imgBxisB;
    //    @Bind(R.id.imgBxisC)
    public ImageView imgBxisC;
    //    @Bind(R.id.imgBxisD)
    public ImageView imgBxisD;
    public Button btnRefresh;
    private VerifyDataBean verifyDataBean;
    private CoefficientsHandler coefficientsHandler;
    private int count = 0;
    private int refreshCount = 0;
    private long sendTime = 0;
    private long receiveTime = 0;
    private long DELAYTIME = 500; //设置矫正参数超时时间
    private long SEND_DURATION = 500;//发送设置命令的时间间隔为500ms
    int delayTime = 400;
    private RefreshHandler refreshHandler;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coefficients);
        bindToolBar();
        makeStatusBar(R.color.white);
        initView();
        verifyDataBean = myApplication.getVerifyDataBean();
        coefficientsHandler = new CoefficientsHandler(new DelaySendHandler());
        refreshHandler = new RefreshHandler();
        initData();
        initListener();
    }

    private void initView() {
        edtAxiasA = findViewById(R.id.AaxisA);
        edtAxiasB = findViewById(R.id.AaxisB);
        edtAxiasC = findViewById(R.id.AaxisC);
        edtAxiasD = findViewById(R.id.AaxisD);
        edtBxiasA = findViewById(R.id.BaxisA);
        edtBxiasB = findViewById(R.id.BaxisB);
        edtBxiasC = findViewById(R.id.BaxisC);
        edtBxiasD = findViewById(R.id.BaxisD);
        btnSet = findViewById(R.id.btnSet);
        btnRefresh = findViewById(R.id.btnRefresh);
        imgAxisA = findViewById(R.id.imgAxisA);
        imgAxisB = findViewById(R.id.imgAxisB);
        imgAxisC = findViewById(R.id.imgAxisC);
        imgAxisD = findViewById(R.id.imgAxisD);
        imgBxisA = findViewById(R.id.imgBxisA);
        imgBxisB = findViewById(R.id.imgBxisB);
        imgBxisC = findViewById(R.id.imgBxisC);
        imgBxisD = findViewById(R.id.imgBxisD);
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
//              sendCmdCodeByHex("010300000002C40B");
                if (verify()) {
                    clearUIState();
                    //这个1不是时间
                    isRefresh = true;
                    coefficientsHandler.sendEmptyMessage(1);
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isRefresh = false;
                clearEdtData();
                refreshHandler.sendEmptyMessageDelayed(1, delayTime);
            }
        });
    }

    private void clearUIState() {

        imgAxisA.setVisibility(View.GONE);
        imgAxisB.setVisibility(View.GONE);
        imgAxisC.setVisibility(View.GONE);
        imgAxisD.setVisibility(View.GONE);
        imgBxisA.setVisibility(View.GONE);
        imgBxisB.setVisibility(View.GONE);
        imgBxisC.setVisibility(View.GONE);
        imgBxisD.setVisibility(View.GONE);
    }

    private void clearEdtData() {
        edtAxiasA.setText("");
        edtAxiasB.setText("");
        edtAxiasC.setText("");
        edtAxiasD.setText("");
        edtBxiasA.setText("");
        edtBxiasB.setText("");
        edtBxiasC.setText("");
        edtBxiasD.setText("");
    }

    /**
     * 发送命令且将浮点字符串转成16进制
     *
     * @param No 发送次数
     */
    private void sendConvertCmd(int No) {
        btnSet.setEnabled(false);
        String cmd = "";
        switch (No) {
            case 1:
                cmd = edtAxiasA.getText().toString().trim();
                break;
            case 2:
                cmd = edtAxiasB.getText().toString().trim();
                break;
            case 3:
                cmd = edtAxiasC.getText().toString().trim();
                break;
            case 4:
                cmd = edtAxiasD.getText().toString().trim();
                break;
            case 5:
                cmd = edtBxiasA.getText().toString().trim();
                break;
            case 6:
                cmd = edtBxiasB.getText().toString().trim();
                break;
            case 7:
                cmd = edtBxiasC.getText().toString().trim();
                break;
            case 8:
                cmd = edtBxiasD.getText().toString().trim();
                break;
            default:
                System.out.println("设置矫正参数完毕");
                break;
        }
        cmd = cmdAnalyze.getParamToCmd(cmd);
        cmd = cmdClass.getParamsCmd(cmd, String.valueOf(No - 1));//getParamsCmd
        sendCmdCodeByHex(cmd);
        coefficientsHandler.getDelaySendHandler().sendEmptyMessageDelayed(No + 1, DELAYTIME);
    }

    private boolean verify() {

        if (edtAxiasA.getText().toString().trim().isEmpty()) {

            Toast.makeText(CoefficientsActivity.this, "AxiasA不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtAxiasB.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "AxiasB不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtAxiasC.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "AxiasC不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtAxiasD.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "AxiasD不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtBxiasA.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "BxiasA不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtBxiasB.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "BxiasB不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtBxiasC.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "BxiasC不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtBxiasD.getText().toString().trim().isEmpty()) {
            Toast.makeText(CoefficientsActivity.this, "BxiasC不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void registerBc() {

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {

        System.out.println("CoefficientsActivity接收到的数据:" + formatMsgContent(array));
        //返回的数据就删除延迟handle里面的消息
        if (isRefresh) {
            //设置
            coefficientsHandler.getDelaySendHandler().removeCallbacksAndMessages(null);
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            parseVerifyData(hexStr);
        } else {
            //刷新数据
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            if (hexStr.length() == 24) {
                //加判断是为了避免其它命令接收数据造成这里解析出错
                refreshVerifyData(hexStr.substring(6, 20));
            }
        }
    }

    private void parseVerifyData(String codeStr) {
        count++;
        System.out.println("parseVerifyData codeStr:" + codeStr);
        switch (count) {
            case 1:
                try {
                    if (codeStr.substring(8, 10).equals("00")) {
                        verifyDataBean.setAaxisA(edtAxiasA.getText().toString());
                    } else {
                        imgAxisA.setImageResource(R.drawable.error);
                    }

                    coefficientsHandler.sendEmptyMessageDelayed(2, SEND_DURATION);
                } catch (Exception e) {
                    imgAxisA.setImageResource(R.drawable.error);
                }
                imgAxisA.setVisibility(View.VISIBLE);
                break;
            case 2:
                try {
                    if (codeStr.substring(8, 10).equals("01")) {

                        verifyDataBean.setAaxisB(edtAxiasB.getText().toString());
                    } else {
                        imgAxisB.setImageResource(R.drawable.error);
                    }
                    coefficientsHandler.sendEmptyMessageDelayed(3, SEND_DURATION);
                } catch (Exception e) {
                    imgAxisB.setImageResource(R.drawable.error);
                }
                imgAxisB.setVisibility(View.VISIBLE);
                break;
            case 3:
                try {
                    if (codeStr.substring(8, 10).equals("02")) {

                        verifyDataBean.setAaxisC(edtAxiasC.getText().toString());
                    } else {
                        imgAxisC.setImageResource(R.drawable.error);
                    }
                    coefficientsHandler.sendEmptyMessageDelayed(4, SEND_DURATION);
                } catch (Exception e) {
                    imgAxisC.setImageResource(R.drawable.error);
                }
                imgAxisC.setVisibility(View.VISIBLE);
                break;
            case 4:

                try {
                    if (codeStr.substring(8, 10).equals("03")) {
                        verifyDataBean.setAaxisD(edtAxiasD.getText().toString());
                    } else {
                        imgAxisD.setImageResource(R.drawable.error);
                    }
                    coefficientsHandler.sendEmptyMessageDelayed(5, SEND_DURATION);
                } catch (Exception e) {
                    imgAxisD.setImageResource(R.drawable.error);
                }
                imgAxisD.setVisibility(View.VISIBLE);

                break;
            case 5:

                try {
                    if (codeStr.substring(8, 10).equals("04")) {
                        verifyDataBean.setBaxisA(edtBxiasA.getText().toString());
                    } else {
                        imgBxisA.setImageResource(R.drawable.error);
                    }

                    coefficientsHandler.sendEmptyMessageDelayed(6, SEND_DURATION);
                } catch (Exception e) {
                    imgBxisA.setImageResource(R.drawable.error);
                }
                imgBxisA.setVisibility(View.VISIBLE);
                break;
            case 6:
                try {
                    if (codeStr.substring(8, 10).equals("05")) {

                        verifyDataBean.setBaxisB(edtBxiasB.getText().toString());
                    } else {
                        imgBxisB.setImageResource(R.drawable.error);
                    }

                    coefficientsHandler.sendEmptyMessageDelayed(7, SEND_DURATION);
                } catch (Exception e) {
                    imgBxisB.setImageResource(R.drawable.error);
                }
                imgBxisB.setVisibility(View.VISIBLE);
                break;
            case 7:

                try {
                    if (codeStr.substring(8, 10).equals("06")) {
                        verifyDataBean.setBaxisC(edtBxiasC.getText().toString());
                    } else {
                        imgBxisC.setImageResource(R.drawable.error);
                    }

                    coefficientsHandler.sendEmptyMessageDelayed(8, SEND_DURATION);
                } catch (Exception e) {
                    imgBxisC.setImageResource(R.drawable.error);
                }
                imgBxisC.setVisibility(View.VISIBLE);
                break;
            case 8:

                try {
                    if (codeStr.substring(8, 10).equals("07")) {

                        verifyDataBean.setBaxisD(edtBxiasD.getText().toString());
                    } else {
                        imgBxisD.setImageResource(R.drawable.error);
                    }
                    myApplication.setVerifyDataBean(verifyDataBean);
                } catch (Exception e) {
                    imgBxisD.setImageResource(R.drawable.error);
                }
                imgBxisD.setVisibility(View.VISIBLE);
                count = 0;
                btnSet.setEnabled(true);
                Toast.makeText(CoefficientsActivity.this, "参数设置完毕", Toast.LENGTH_SHORT).show();
                break;
            default:

                break;
        }
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

    class CoefficientsHandler extends Handler {

        private DelaySendHandler delaySendHandler;

        public CoefficientsHandler(DelaySendHandler d) {
            delaySendHandler = d;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            sendConvertCmd(msg.what);
            //伴随着有个500ms的超时发送
        }

        public DelaySendHandler getDelaySendHandler() {
            return delaySendHandler;
        }
    }

    class DelaySendHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            sendConvertCmd(msg.what);
            count++; //超时也算完成了一次数据
            //todo 要进行操作错误的判断
        }
    }

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 9) {
//                verifyLoadDialog.dismiss();
                Toast.makeText(CoefficientsActivity.this, "Refresh Success", Toast.LENGTH_SHORT).show();
            } else {
                sendCmdGetVerifyCode(msg.what);
            }
        }
    }

    private void refreshVerifyData(String codeStr) {
        String d = Utils.getVerifyDatas(codeStr);
        refreshCount++;
        switch (refreshCount) {
            case 1:
                verifyDataBean.setAaxisA(d);
                edtAxiasA.setText(d);
                break;
            case 2:
                verifyDataBean.setAaxisB(d);
                edtAxiasB.setText(d);
                break;
            case 3:
                verifyDataBean.setAaxisC(d);
                edtAxiasC.setText(d);
                break;
            case 4:
                verifyDataBean.setAaxisD(d);
                edtAxiasD.setText(d);
                break;
            case 5:
                verifyDataBean.setBaxisA(d);
                edtBxiasA.setText(d);
                break;
            case 6:
                verifyDataBean.setBaxisB(d);
                edtBxiasB.setText(d);
                break;
            case 7:
                verifyDataBean.setBaxisC(d);
                edtBxiasC.setText(d);
                break;
            case 8:
                verifyDataBean.setBaxisD(d);
                edtBxiasD.setText(d);
                myApplication.setVerifyDataBean(verifyDataBean);
                refreshCount = 0;
                Toast.makeText(CoefficientsActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                break;
            default:
                refreshCount = 0;
                refreshHandler.removeCallbacksAndMessages(null);
                Toast.makeText(CoefficientsActivity.this, "刷新出錯，請重新點擊刷新！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //需要连续发送8次获取数据
    private void sendCmdGetVerifyCode(int No) {

        switch (No) {
            case 1:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO1);
                refreshHandler.sendEmptyMessageDelayed(2, delayTime);
                break;
            case 2:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO2);
                refreshHandler.sendEmptyMessageDelayed(3, delayTime);
                break;
            case 3:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO3);
                refreshHandler.sendEmptyMessageDelayed(4, delayTime);
                break;
            case 4:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO4);
                refreshHandler.sendEmptyMessageDelayed(5, delayTime);
                break;
            case 5:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO5);
                refreshHandler.sendEmptyMessageDelayed(6, delayTime);
                break;
            case 6:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO6);
                refreshHandler.sendEmptyMessageDelayed(7, delayTime);
                break;
            case 7:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO7);
                refreshHandler.sendEmptyMessageDelayed(8, delayTime);
                break;
            case 8:
                sendCmdCodeByHex(Constants.DATA_VERIFY_NO8);
                break;
            default:
                System.out.println("获取矫正参数命令发送完成");
                break;
        }
    }
}
