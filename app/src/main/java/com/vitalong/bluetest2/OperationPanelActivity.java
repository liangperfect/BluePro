package com.vitalong.bluetest2;

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

import com.leon.lfilepickerlibrary.LFilePicker;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.bluepro.CompareActivity;
import com.vitalong.bluetest2.bluepro.MeregParametersActivity;
import com.vitalong.bluetest2.bluepro.SettingActivity;
import com.vitalong.bluetest2.bluepro.SurveyActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.drakeet.materialdialog.MaterialDialog;
import sakura.bottommenulibrary.bottompopfragmentmenu.BottomMenuFragment;

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
    private int FILE_SELECTOR_SHARE = 111;//选择文件进行分享
    boolean isPause = false;
    int delayTime = 600;
    private OperationPanelHandler operationPanelHandler;
    int count = 0;

    private VerifyDataBean verifyDataBean;
    private MaterialDialog verifyLoadDialog;
    private List<String> hasArrayStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_panel);
        bindToolBar();
        //发送数据
        makeStatusBar(R.color.red);
        hasArrayStr = new ArrayList<String>();
        myApplication = (MyApplication) getApplication();
        verifyDataBean = new VerifyDataBean();
        operationPanelHandler = new OperationPanelHandler();
        operationPanelHandler.sendEmptyMessageDelayed(1, delayTime);
        initListener();
        //显示获取verifydata的加载框
        showLoadVeriftDataDialog();
    }

    private void initListener() {

        backBtn.setOnClickListener(this);
        imageButton1.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
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
//        if (hexStr.length() == 24) {
            //加判断是为了避免其它命令接收数据造成这里解析出错
//            parseVerifyData(hexStr.substring(6, 20));
            try {
//                parseVerifyData(hexStr);
                if (hasArrayStr.contains(hexStr)) {
                    return;
                }
                hasArrayStr.add(hexStr);
                parseVerifyData2(hexStr);
            } catch (Exception e) {
                clearVerifyDataBean();
                Toast.makeText(OperationPanelActivity.this, "數據解析錯誤，請推送重新鏈接", Toast.LENGTH_SHORT).show();
            }
        }
//        }
    }

    private void clearVerifyDataBean() {
        verifyDataBean.setAaxisA("");
        verifyDataBean.setAaxisB("");
        verifyDataBean.setAaxisC("");
        verifyDataBean.setAaxisD("");
        verifyDataBean.setBaxisA("");
        verifyDataBean.setBaxisB("");
        verifyDataBean.setBaxisC("");
        verifyDataBean.setBaxisD("");
        myApplication.setVerifyDataBean(verifyDataBean);
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
                startActivity(new Intent(OperationPanelActivity.this, SurveyActivity.class));
                break;
            case R.id.imageButton2:
                startActivity(new Intent(OperationPanelActivity.this, CompareActivity.class));
                break;
            case R.id.imageButton3:
                startActivity(new Intent(OperationPanelActivity.this, SettingActivity.class));
                break;
            case R.id.imageButton4:
//                startActivity(new Intent(OperationPanelActivity.this, ShareFileActivity.class));
                selectDialogShow();
                break;
            default:
                break;
        }
    }

    private void selectDialogShow() {

        new BottomMenuFragment(OperationPanelActivity.this)
                .addMenuItems(new sakura.bottommenulibrary.bottompopfragmentmenu.MenuItem("Share"))
                .addMenuItems(new sakura.bottommenulibrary.bottompopfragmentmenu.MenuItem("Merge"))
                .setOnItemClickListener((textView, i) -> {

                    if (i == 0) {
                        //前往分享界面
                        new LFilePicker()
                                .withActivity(OperationPanelActivity.this)
                                .withRequestCode(FILE_SELECTOR_SHARE)
                                .withMutilyMode(true)
                                .withStartPath(Constants.PRO_ROOT_PATH)
                                .withIsGreater(false)
                                .withFileSize(500 * 1024)
                                .withTitleColor("#000000")
                                .withBackgroundColor("#FFFFFF")
                                .start();
                    } else {
                        //前往数据合并界面
                        startActivity(new Intent(OperationPanelActivity.this, MeregParametersActivity.class));
                    }
                }).show();
    }

    class OperationPanelHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                verifyLoadDialog.dismiss();
            } else {
                sendCmdGetVerifyCodeTwice(msg.what);
            }
        }
    }

    //发送两次，第一次获取5个数字，第二次获取3个数字
    private void sendCmdGetVerifyCodeTwice(int No) {

        switch (No) {
            case 1:
//                sendCmdCodeByHex(Constants.DATA_VERIFY_NO1);
                sendCmdCodeByHex(Constants.DATA_VERIFY_SEND_5);
                operationPanelHandler.sendEmptyMessageDelayed(2, delayTime);
                break;
            case 2:
//                sendCmdCodeByHex(Constants.DATA_VERIFY_NO2);
                sendCmdCodeByHex(Constants.DATA_VERIFY_SEND_3);
                operationPanelHandler.sendEmptyMessageDelayed(3, delayTime);
                break;
            case 3:
                //获取SN参数
                sendCmdCodeByHex("01 03 00 05 00 02 d4 0a");
                break;
            default:
                Log.d("chenliang", "获取矫正参数命令发送完成");
                break;
        }
    }

    private void parseVerifyData2(String codeStr) {

        count++;
        switch (count) {
            case 1:
                String d1 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                String d2 = Utils.getVerifyDatas(codeStr.substring(20, 34));
                String d3 = Utils.getVerifyDatas(codeStr.substring(34, 48));
                String d4 = Utils.getVerifyDatas(codeStr.substring(48, 62));
                String d5 = Utils.getVerifyDatas(codeStr.substring(62, 76));
                verifyDataBean.setAaxisA(d1);
                verifyDataBean.setAaxisB(d2);
                verifyDataBean.setAaxisC(d3);
                verifyDataBean.setAaxisD(d4);
                verifyDataBean.setBaxisA(d5);
                break;
            case 2:
                String d6 = Utils.getVerifyDatas(codeStr.substring(6, 20));
                String d7 = Utils.getVerifyDatas(codeStr.substring(20, 34));
                String d8 = Utils.getVerifyDatas(codeStr.substring(34, 48));
                verifyDataBean.setBaxisB(d6);
                verifyDataBean.setBaxisC(d7);
                verifyDataBean.setBaxisD(d8);
                myApplication.setVerifyDataBean(verifyDataBean);
                break;
            case 3:
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
        Log.d("chenliang", "Operation onDestroy");
        unregisterReceiver(mGattUpdateReceiver);
        operationPanelHandler = null;
        super.onDestroy();

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
        //大概4秒左右数据发送完毕之后，就将窗口给关闭了
        operationPanelHandler.sendEmptyMessageDelayed(10, 2000);
    }
}
