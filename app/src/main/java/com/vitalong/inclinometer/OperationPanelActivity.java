package com.vitalong.inclinometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.Utils.SharedPreferencesUtil;
import com.vitalong.inclinometer.Utils.Utils;
import com.vitalong.inclinometer.bean.VerifyDataBean;
import com.vitalong.inclinometer.bluepro.CompareActivity;
import com.vitalong.inclinometer.bluepro.SettingActivity;
import com.vitalong.inclinometer.inclinometer.AboutUsActivity;
import com.vitalong.inclinometer.inclinometer.SelectModeActivity;

import java.util.List;

import butterknife.Bind;
import me.drakeet.materialdialog.MaterialDialog;

public class OperationPanelActivity extends MyBaseActivity2 implements View.OnClickListener {

    @Bind(R.id.imageButton)
    ImageButton backBtn;
    @Bind(R.id.imageButton1)
    ImageButton imageButton1;
    @Bind(R.id.imageButton2)
    ImageButton imageButton2;
    @Bind(R.id.imageButton3)
    ImageButton imageButton3;
    @Bind(R.id.imageButton4)
    ImageButton imageButton4;
    @Bind(R.id.imageButton5)
    ImageButton imageButton5;

    private boolean isPause = false;
    private int delayTime = 400;
    private OperationPanelHandler operationPanelHandler;
    private int count = 0;
    private boolean parseVerify = true; //true解析矫正系数  false是解析sn的值
    private VerifyDataBean verifyDataBean;
    private MaterialDialog verifyLoadDialog;
    private int FILE_SELECTOR_SHARE = 111;//选择文件进行分享

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
        myApplication = (MyApplication) getApplication();
        verifyDataBean = new VerifyDataBean();
        //显示获取verifydata的加载框
        showLoadVeriftDataDialog();
    }

    private void initListener() {

        backBtn.setOnClickListener(this);
        imageButton1.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        imageButton5.setOnClickListener(this);
    }

    @Override
    protected void registerBc() {
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    protected void receiveDataFromBlue(byte[] array) {
        //进行数据解析
        if (!isPause) {
            String hexStr = Utils.ByteArraytoHex(array).replace(" ", "");
            Log.d("chenliang", "获取到的矫正系数" + hexStr);
            if (parseVerify){
                parseAllVerifyData(hexStr);
            }else{
                parseSnValue(hexStr);
            }
//        if (hexStr.length() == 24) {
            //加判断是为了避免其它命令接收数据造成这里解析出错
//            parseVerifyData(hexStr.substring(6, 20));
//            try {
//                parseVerifyData(hexStr);
//            } catch (Exception e) {
//                Toast.makeText(OperationPanelActivity.this, "數據解析錯誤，請推送重新鏈接", Toast.LENGTH_SHORT).show();
//            }
        }
//        }
    }

    @Override
    protected void disconnectBlue() {
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
            case R.id.imageButton1:
//                startActivity(new Intent(OperationPanelActivity.this, SurveyActivity.class));
                startActivity(new Intent(OperationPanelActivity.this, SelectModeActivity.class));
                break;
            case R.id.imageButton2:
                startActivity(new Intent(OperationPanelActivity.this, CompareActivity.class));
//                startActivity(new Intent(OperationPanelActivity.this, Graph2Activity.class));
                break;
            case R.id.imageButton3:
                startActivity(new Intent(OperationPanelActivity.this, SettingActivity.class));
                break;
            case R.id.imageButton4:
//                startActivity(new Intent(OperationPanelActivity.this, ShareFileActivity.class));
                new LFilePicker()
                        .withActivity(OperationPanelActivity.this)
                        .withRequestCode(FILE_SELECTOR_SHARE)
                        .withMutilyMode(true)
                        .withStartPath(Constants.PRO_ROOT_PATH)
                        .withIsGreater(false)
                        .withFileSize(500 * 1024)
                        .start();
                break;
            case R.id.imageButton5:
//                startActivity(new Intent(OperationPanelActivity.this,WebViewActivity.class));
                startActivity(new Intent(OperationPanelActivity.this, AboutUsActivity.class));
//                int REQUESTCODE_FROM_ACTIVITY = 1000;
                break;
            default:
                break;
        }
    }

    class OperationPanelHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                verifyLoadDialog.dismiss();
            } else {
//                sendCmdGetVerifyCode(msg.what);
                sendCmdCodeByHex(Constants.DATA_ALL_VERIFY);
            }
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
                operationPanelHandler.sendEmptyMessageDelayed(9, delayTime);
                break;
            case 9:
                //获取SN参数
                sendCmdCodeByHex("01 03 00 05 00 02 d4 0a");
                break;
            default:
                System.out.println("获取矫正参数命令发送完成");
                break;
        }
    }

    /**
     * 解析sn的数值
     * @param codeStr
     */
    private void parseSnValue(String codeStr){
        try {
            String snValueStr = String.valueOf(Integer.parseInt(codeStr.substring(6, 14), 16));
            Log.d("chenliang", "获取到的sn:" + snValueStr);
            SharedPreferencesUtil.putData("SNVaule", snValueStr);
        } catch (Exception ex) {
            Log.e("chenliang", "解析sn出错");
            Toast.makeText(OperationPanelActivity.this, "SN解析出错", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 解析所有的矫正系数
     * @param codeStr
     */
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
        verifyDataBean.setAaxisA(d1);
        verifyDataBean.setAaxisB(d2);
        verifyDataBean.setAaxisC(d3);
        verifyDataBean.setAaxisD(d4);
        verifyDataBean.setBaxisA(d5);
        verifyDataBean.setBaxisB(d6);
        verifyDataBean.setBaxisC(d7);
        verifyDataBean.setBaxisD(d8);
        Log.d("chenliang", "数据结果是->d1:" + d1 + "  d2:" + d2 + "  d3:" + d3 + "  d4:" + d4 + "  d5:" + d5 + "   d6:" + d6 + "  d7:" + d7 + "  d8:" + d8);
        myApplication.setVerifyDataBean(verifyDataBean);
        //发送sn参数
        parseVerify = false;
        sendCmdCodeByHex("01 03 00 05 00 02 d4 0a");
    }

    private void parseVerifyData(String codeStr) {

        count++;
        switch (count) {
            case 1:
                String d1 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setAaxisA(d1);
                break;
            case 2:
                String d2 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setAaxisB(d2);
                break;
            case 3:
                String d3 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setAaxisC(d3);
                break;
            case 4:
                String d4 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setAaxisD(d4);
                break;
            case 5:
                String d5 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setBaxisA(d5);
                break;
            case 6:
                String d6 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setBaxisB(d6);
                break;
            case 7:
                String d7 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setBaxisC(d7);
                break;
            case 8:
                String d8 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                verifyDataBean.setBaxisD(d8);
                myApplication.setVerifyDataBean(verifyDataBean);
                break;
            case 9:
                //进行数据解析
                try {
                    String snValueStr = String.valueOf(Integer.parseInt(codeStr.substring(6, 14), 16));
                    Log.d("chenliang", "获取到的sn:" + snValueStr);
                    SharedPreferencesUtil.putData("SNVaule", snValueStr);
                } catch (Exception ex) {
                    Log.e("chenliang", "解析sn出错");
                    Toast.makeText(OperationPanelActivity.this, "SN解析出错", Toast.LENGTH_SHORT).show();
                }
                count = 0;
                break;
            default:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    private void showLoadVeriftDataDialog() {
        ProgressBar progressBar = new ProgressBar(OperationPanelActivity.this);
        verifyLoadDialog = new MaterialDialog(OperationPanelActivity.this);
        verifyLoadDialog.setTitle("Load verify data,Please wait for 3s")
                .setContentView(progressBar);
        verifyLoadDialog.show();
        operationPanelHandler.sendEmptyMessageDelayed(10, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == FILE_SELECTOR_SHARE) {
            List<String> list = data.getStringArrayListExtra("paths");
            Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
            //如果是文件夹选择模式，需要获取选择的文件夹路径
            String path = data.getStringExtra("path");
            Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
        }

//        if (resultCode == Activity.RESULT_OK && requestCode == 123) {
//            assert data != null;
//            if (data.getIntExtra("selectModel", 0) == Constant.SELECTOR_MODE_2_1) {
//                //选择
//                Intent intent1 = new Intent(OperationPanelActivity.this, Survey2Activity.class);
//                startActivity(intent1);
//            } else {
//                List<String> list = data.getStringArrayListExtra("paths");
//                Intent intent2 = new Intent(OperationPanelActivity.this, Survey2Activity.class);
//                startActivity(intent2);
//                Toast.makeText(getApplicationContext(), "选中文件路径:" + list.get(0), Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}
