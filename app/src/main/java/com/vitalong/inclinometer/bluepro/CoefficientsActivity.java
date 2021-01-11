package com.vitalong.inclinometer.bluepro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vitalong.inclinometer.MyBaseActivity2;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.Utils.Utils;
import com.vitalong.inclinometer.Utils.cmdAnalyze;
import com.vitalong.inclinometer.Utils.cmdClass;
import com.vitalong.inclinometer.bean.VerifyDataBean;

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
    public TextView tvAaxisA;
    public TextView tvAaxisB;
    public TextView tvAaxisC;
    public TextView tvAaxisD;
    public TextView tvBaxisA;
    public TextView tvBaxisB;
    public TextView tvBaxisC;
    public TextView tvBaxisD;
    public Button btnRefresh;
    public TextView tvCheck;
    private VerifyDataBean verifyDataBean;
    private CoefficientsHandler coefficientsHandler;
    private int count = 0;
    private int refreshCount = 0;
    private int singleCount = 0;
    private long sendTime = 0;
    private long receiveTime = 0;
    private long DELAYTIME = 500; //设置矫正参数超时时间
    private long SEND_DURATION = 500;//发送设置命令的时间间隔为500ms
    int delayTime = 400;
    private RefreshHandler refreshHandler;
    private int isRefresh = 0; //0是刷新 1是全部重新設置  2是单个设置
    private String orginal1Str = "";
    private String orginal2Str = "";
    private String orginal3Str = "";
    private String orginal4Str = "";
    private String orginal5Str = "";
    private String orginal6Str = "";
    private String orginal7Str = "";
    private String orginal8Str = "";

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
        tvCheck = findViewById(R.id.check);
        tvAaxisA = findViewById(R.id.tvAaxisA);
        tvAaxisB = findViewById(R.id.tvAaxisB);
        tvAaxisC = findViewById(R.id.tvAaxisC);
        tvAaxisD = findViewById(R.id.tvAaxisD);
        tvBaxisA = findViewById(R.id.tvBaxisA);
        tvBaxisB = findViewById(R.id.tvBaxisB);
        tvBaxisC = findViewById(R.id.tvBaxisC);
        tvBaxisD = findViewById(R.id.tvBaxisD);
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
                    btnSet.setEnabled(false);
                    btnRefresh.setEnabled(false);
                    isRefresh = 1;
                    coefficientsHandler.sendEmptyMessage(1);
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isRefresh = 0;
                clearEdtData();
                btnRefresh.setEnabled(false);
                refreshHandler.sendEmptyMessageDelayed(1, delayTime);
            }
        });

        tvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CoefficientsActivity.this, VerifyResultActivity.class);
                i.putExtra("orginal1Str", orginal1Str);
                i.putExtra("orginal2Str", orginal2Str);
                i.putExtra("orginal3Str", orginal3Str);
                i.putExtra("orginal4Str", orginal4Str);
                i.putExtra("orginal5Str", orginal5Str);
                i.putExtra("orginal6Str", orginal6Str);
                i.putExtra("orginal7Str", orginal7Str);
                i.putExtra("orginal8Str", orginal8Str);
                startActivity(i);
            }
        });

        tvAaxisA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAxiasA.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 1;
                    String cmd = edtAxiasA.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(0));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvAaxisB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAxiasB.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 2;
                    String cmd = edtAxiasB.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(1));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvAaxisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAxiasC.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 3;
                    String cmd = edtAxiasC.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(2));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvAaxisD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAxiasD.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 4;
                    String cmd = edtAxiasD.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(3));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvBaxisA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBxiasA.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 5;
                    String cmd = edtBxiasA.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(4));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvBaxisB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBxiasB.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 6;
                    String cmd = edtBxiasB.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(5));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvBaxisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBxiasC.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 7;
                    String cmd = edtBxiasC.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(6));//getParamsCmd
                    sendCmdCodeByHex(cmd);
                }
            }
        });

        tvBaxisD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBxiasD.getText().toString().isEmpty()) {
                    Toast.makeText(CoefficientsActivity.this, "AaxisA的值不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    isRefresh = 2;
                    singleCount = 8;
                    String cmd = edtBxiasD.getText().toString();
                    cmd = cmdAnalyze.getParamToCmd(cmd);
                    cmd = cmdClass.getParamsCmd(cmd, String.valueOf(7));//getParamsCmd
                    sendCmdCodeByHex(cmd);
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

    private void clearEdtData() {
        edtAxiasA.setText("");
        edtAxiasB.setText("");
        edtAxiasC.setText("");
        edtAxiasD.setText("");
        edtBxiasA.setText("");
        edtBxiasB.setText("");
        edtBxiasC.setText("");
        edtBxiasD.setText("");
        clearUIState();
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
        Log.d("chenliang111", "String.valueOf(No - 1):" + String.valueOf(No - 1));
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
        if (isRefresh == 1) {
            //设置
            coefficientsHandler.getDelaySendHandler().removeCallbacksAndMessages(null);
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            parseVerifyData(hexStr);
        } else if (isRefresh == 0) {
            //刷新数据
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
//            if (hexStr.length() == 24) {
//                //加判断是为了避免其它命令接收数据造成这里解析出错
//            refreshVerifyData(hexStr.substring(6, 20), hexStr);
//            }
            parseAllVerifyData(hexStr);
        } else if (isRefresh == 2) {
            coefficientsHandler.getDelaySendHandler().removeCallbacksAndMessages(null);
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            parseVerifyDataSingle(hexStr);
        } else {
            //nothing todo
        }
    }

    private void parseAllVerifyData(String codeStr) {
        String AaxisAStr = codeStr.substring(6, 20);
        String AaxisBStr = codeStr.substring(20, 34);
        String AaxisCStr = codeStr.substring(34, 48);
        String AaxisDStr = codeStr.substring(48, 62);
        String BaxisAStr = codeStr.substring(62, 76);
        String BaxisBStr = codeStr.substring(76, 90);
        String BaxisCStr = codeStr.substring(90, 104);
        String BaxisDStr = codeStr.substring(104, 118);
        Log.d("chenliang", "解析出的数据是->AaxisAStr:" + AaxisAStr + "   AaxisBStr:" + AaxisBStr +
                "  AaxisCStr:" + AaxisCStr + "  AaxisDStr:" + AaxisDStr + "  BaxisAStr:" + BaxisAStr + "  BaxisBStr:" + BaxisBStr +
                "  BaxisCStr:" + BaxisCStr + "  BaxisDStr:" + BaxisDStr);
        String d1 = Utils.getVerifyDatas(AaxisAStr);
        String d2 = Utils.getVerifyDatas(AaxisBStr);
        String d3 = Utils.getVerifyDatas(AaxisCStr);
        String d4 = Utils.getVerifyDatas(AaxisDStr);
        String d5 = Utils.getVerifyDatas(BaxisAStr);
        String d6 = Utils.getVerifyDatas(BaxisBStr);
        String d7 = Utils.getVerifyDatas(BaxisCStr);
        String d8 = Utils.getVerifyDatas(BaxisDStr);
        orginal1Str = AaxisAStr;
        edtAxiasA.setText(d1);
        verifyDataBean.setAaxisA(d1);
        orginal2Str = AaxisBStr;
        edtAxiasB.setText(d2);
        verifyDataBean.setAaxisB(d2);
        orginal3Str = AaxisCStr;
        edtAxiasC.setText(d3);
        verifyDataBean.setAaxisC(d3);
        orginal4Str = AaxisDStr;
        edtAxiasD.setText(d4);
        verifyDataBean.setAaxisD(d4);
        orginal5Str = BaxisAStr;
        edtBxiasA.setText(d5);
        verifyDataBean.setBaxisA(d5);
        orginal6Str = BaxisBStr;
        edtBxiasB.setText(d6);
        verifyDataBean.setBaxisB(d6);
        orginal7Str = BaxisCStr;
        edtBxiasC.setText(d7);
        verifyDataBean.setBaxisC(d7);
        orginal8Str = BaxisDStr;
        edtBxiasD.setText(d8);
        verifyDataBean.setBaxisD(d8);
        myApplication.setVerifyDataBean(verifyDataBean);
        btnRefresh.setEnabled(true);
        Toast.makeText(CoefficientsActivity.this, "Refresh Success", Toast.LENGTH_SHORT).show();
    }

    private void parseVerifyDataSingle(String codeStr) {

        switch (singleCount) {
            case 1:
                try {
                    if (codeStr.substring(8, 10).equals("00")) {
                        verifyDataBean.setAaxisA(edtAxiasA.getText().toString());
                    } else {
                        imgAxisA.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgAxisA.setImageResource(R.drawable.error);
                }
                imgAxisA.setImageResource(R.drawable.tick);
                imgAxisA.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "A axis A設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                try {
                    if (codeStr.substring(8, 10).equals("01")) {

                        verifyDataBean.setAaxisB(edtAxiasB.getText().toString());
                    } else {
                        imgAxisB.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgAxisB.setImageResource(R.drawable.error);
                }
                imgAxisB.setImageResource(R.drawable.tick);
                imgAxisB.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "A axis B設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                try {
                    if (codeStr.substring(8, 10).equals("02")) {

                        verifyDataBean.setAaxisC(edtAxiasC.getText().toString());
                    } else {
                        imgAxisC.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgAxisC.setImageResource(R.drawable.error);
                }
                imgAxisC.setImageResource(R.drawable.tick);
                imgAxisC.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "A axis C設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 4:

                try {
                    if (codeStr.substring(8, 10).equals("03")) {
                        verifyDataBean.setAaxisD(edtAxiasD.getText().toString());
                    } else {
                        imgAxisD.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgAxisD.setImageResource(R.drawable.error);
                }
                imgAxisD.setImageResource(R.drawable.tick);
                imgAxisD.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "A axis D設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 5:

                try {
                    if (codeStr.substring(8, 10).equals("04")) {
                        verifyDataBean.setBaxisA(edtBxiasA.getText().toString());
                    } else {
                        imgBxisA.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgBxisA.setImageResource(R.drawable.error);
                }
                imgBxisA.setImageResource(R.drawable.tick);
                imgBxisA.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "B axis A設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                try {
                    if (codeStr.substring(8, 10).equals("05")) {

                        verifyDataBean.setBaxisB(edtBxiasB.getText().toString());
                    } else {
                        imgBxisB.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgBxisB.setImageResource(R.drawable.error);
                }
                imgBxisB.setImageResource(R.drawable.tick);
                imgBxisB.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "B axis B設置完畢", Toast.LENGTH_SHORT).show();
                break;
            case 7:

                try {
                    if (codeStr.substring(8, 10).equals("06")) {
                        verifyDataBean.setBaxisC(edtBxiasC.getText().toString());
                    } else {
                        imgBxisC.setImageResource(R.drawable.error);
                    }
                } catch (Exception e) {
                    imgBxisC.setImageResource(R.drawable.error);
                }
                imgBxisC.setImageResource(R.drawable.tick);
                imgBxisC.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "B axis C設置完畢", Toast.LENGTH_SHORT).show();
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
                imgBxisD.setImageResource(R.drawable.tick);
                imgBxisD.setVisibility(View.VISIBLE);
                Toast.makeText(CoefficientsActivity.this, "B axis D設置完畢", Toast.LENGTH_SHORT).show();
                break;
            default:

                break;
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
                btnRefresh.setEnabled(true);
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
            if (msg.what == 9) {
//                verifyLoadDialog.dismiss();
                btnSet.setEnabled(true);
                btnRefresh.setEnabled(true);
                Toast.makeText(CoefficientsActivity.this, "全部設置完畢", Toast.LENGTH_SHORT).show();
            } else {
                sendConvertCmd(msg.what);
            }
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
                btnRefresh.setEnabled(true);
                Toast.makeText(CoefficientsActivity.this, "Refresh Success", Toast.LENGTH_SHORT).show();
            } else {
                //sendCmdGetVerifyCode(msg.what);
                //发送命令，直接获取到所有的矫正系数
                sendCmdCodeByHex(Constants.DATA_ALL_VERIFY);
            }
        }
    }

    private void refreshVerifyData(String codeStr, String originalStr) {
        String d = Utils.getVerifyDatas(codeStr);
        refreshCount++;
        switch (refreshCount) {
            case 1:
                orginal1Str = originalStr;
                verifyDataBean.setAaxisA(d);
                edtAxiasA.setText(d);
                break;
            case 2:
                orginal2Str = originalStr;
                verifyDataBean.setAaxisB(d);
                edtAxiasB.setText(d);
                break;
            case 3:
                orginal3Str = originalStr;
                verifyDataBean.setAaxisC(d);
                edtAxiasC.setText(d);
                break;
            case 4:
                orginal4Str = originalStr;
                verifyDataBean.setAaxisD(d);
                edtAxiasD.setText(d);
                break;
            case 5:
                orginal5Str = originalStr;
                verifyDataBean.setBaxisA(d);
                edtBxiasA.setText(d);
                break;
            case 6:
                orginal6Str = originalStr;
                verifyDataBean.setBaxisB(d);
                edtBxiasB.setText(d);
                break;
            case 7:
                orginal7Str = originalStr;
                verifyDataBean.setBaxisC(d);
                edtBxiasC.setText(d);
                break;
            case 8:
                orginal8Str = originalStr;
                verifyDataBean.setBaxisD(d);
                edtBxiasD.setText(d);
                myApplication.setVerifyDataBean(verifyDataBean);
                refreshCount = 0;
                btnRefresh.setEnabled(true);
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
