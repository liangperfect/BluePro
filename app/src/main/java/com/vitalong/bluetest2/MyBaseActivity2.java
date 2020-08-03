package com.vitalong.bluetest2;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.bluetest2.BlueToothLeService.BluetoothLeService;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.GattAttributes;
import com.vitalong.bluetest2.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public abstract class MyBaseActivity2 extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic indicateCharacteristic;
    protected MyApplication myApplication;
    protected boolean isShowingDialog = false;
    protected MaterialDialog alarmDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication) getApplication();
        initCharacteristics();
        registerBc();
    }

    /**
     * 注册发送和接收的广播
     */
    protected abstract void registerBc();

    protected abstract void receiveDataFromBlue(byte[] array);

    protected abstract void disconnectBlue();

    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_search_white_36dp);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
    }

    //声明绑定广播的类
    protected final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");
                //搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
//                hander.removeCallbacks(dismssDialogRunnable);
//                progressDialog.dismiss();
//                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
//                progressDialog.dismiss();
                //connect break (连接断开)
//                showDialog(getString(R.string.conn_disconnected_home));
                disconnectBlue();
            }

            //接收返回的数据
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                System.out.println("GattDetailActivity-------------->onReceive");
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            BluetoothGattCharacteristic requiredCharacteristic = myApplication.getCharacteristic();
                            String uuidRequired = requiredCharacteristic.getUuid().toString();
                            String receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);
                            byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                            receiveDataFromBlue(array);
//                            com.vitalong.bluetest2.bean.Message msg1 = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array));
//                            System.out.println("返回的数据是:" + msg1.getContent());
                            //                            if (isDebugMode) {
//                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
//                                com.vitalong.bluetest2.bean.Message msg = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array));
//                                notifyAdapter(msg);
//                            } else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
//                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
//                                com.vitalong.bluetest2.bean.Message msg = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array, MyApplication.serviceType));
//                                notifyAdapter(msg);
//                            }
                        }
                    }
                }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        BluetoothGattCharacteristic requiredCharacteristic = myApplication.
                                getCharacteristic();
                        String uuidRequired = requiredCharacteristic.getUuid().toString();
                        String receivedUUID = intent.getStringExtra(
                                Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);

                        byte[] array = intent
                                .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);

//                        System.out.println("GattDetailActivity---------------------->descriptor:" + Utils.ByteArraytoHex(array));
//                        if (isDebugMode) {
//                            updateButtonStatus(array);
//                        } else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
//                            updateButtonStatus(array);
//                        }

                    }
                }
            }

        }
    };

    protected void showStateDialog(String info, Context context) {
        if (!isShowingDialog)
            return;
        if (alarmDialog != null)
            return;
        alarmDialog = new MaterialDialog(context);
        alarmDialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        isShowingDialog = false;
                        alarmDialog = null;
                    }
                });
        alarmDialog.show();
    }

    /**
     * 向蓝牙发送数据
     */
    protected void sendCmdCodeByAscii(String codeStr) {

        try {
            byte[] array = codeStr.getBytes("US-ASCII");
            writeCharacteristic(writeCharacteristic, array);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向蓝牙发送数据
     *
     * @param codeStr
     */
    protected void sendCmdCodeByHex(String codeStr) {
        String cmdStr = codeStr.replace(" ", "");
        System.out.println("sendCmdCodeByHex发送的数据是:" + cmdStr);
        byte[] array = Utils.hexStringToByteArray(cmdStr);
        writeCharacteristic(writeCharacteristic, array);
    }

    /**
     * 向蓝牙发送数据
     *
     * @param characteristic
     * @param bytes
     */
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic,
                    bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initCharacteristics() {
        BluetoothGattCharacteristic characteristic = ((MyApplication) getApplication()).getCharacteristic();
        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)) {
            List<BluetoothGattCharacteristic> characteristics = ((MyApplication) getApplication()).getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                if (Utils.getPorperties(this, c).equals("Notify")) {
                    notifyCharacteristic = c;
                    continue;
                }

                if (Utils.getPorperties(this, c).equals("Write")) {
                    writeCharacteristic = c;
                    continue;
                }
            }
        }
    }

    /**
     * 格式化收到的字节数组
     *
     * @param data
     * @return
     */
    protected String formatMsgContent(byte[] data) {
        return "HEX:" + Utils.ByteArraytoHex(data) + "  (ASSCII:" + Utils.byteToASCII(data) + ")";
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
