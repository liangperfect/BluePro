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
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.Utils.cmdAnalyze;
import com.vitalong.bluetest2.Utils.cmdClass;
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
    @Bind(R.id.imgAxisA)
    public ImageView imgAxisA;
    @Bind(R.id.imgAxisB)
    public ImageView imgAxisB;
    @Bind(R.id.imgAxisC)
    public ImageView imgAxisC;
    @Bind(R.id.imgAxisD)
    public ImageView imgAxisD;
    @Bind(R.id.imgBxisA)
    public ImageView imgBxisA;
    @Bind(R.id.imgBxisB)
    public ImageView imgBxisB;
    @Bind(R.id.imgBxisC)
    public ImageView imgBxisC;
    @Bind(R.id.imgBxisD)
    public ImageView imgBxisD;
    private VerifyDataBean verifyDataBean;
    private CoefficientsHandler coefficientsHandler;
    private int count = 0;
    private long sendTime = 0;
    private long receiveTime = 0;
    private long DELAYTIME = 500; //设置矫正参数超时时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coefficients);
        bindToolBar();
        makeStatusBar(R.color.white);
        verifyDataBean = myApplication.getVerifyDataBean();
        coefficientsHandler = new CoefficientsHandler(new DelaySendHandler());
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
//              sendCmdCodeByHex("010300000002C40B");
                if (verify()) {
                    clearUIState();
                    coefficientsHandler.sendEmptyMessage(200);
                }
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
        coefficientsHandler.getDelaySendHandler().removeCallbacksAndMessages(null);
        String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
        parseVerifyData(hexStr);
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

                    coefficientsHandler.sendEmptyMessageDelayed(2, 50);
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
                    coefficientsHandler.sendEmptyMessageDelayed(3, 50);
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
                    coefficientsHandler.sendEmptyMessageDelayed(4, 50);
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
                    coefficientsHandler.sendEmptyMessageDelayed(5, 50);
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

                    coefficientsHandler.sendEmptyMessageDelayed(6, 50);
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

                    coefficientsHandler.sendEmptyMessageDelayed(7, 50);
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

                    coefficientsHandler.sendEmptyMessageDelayed(8, 50);
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
}
