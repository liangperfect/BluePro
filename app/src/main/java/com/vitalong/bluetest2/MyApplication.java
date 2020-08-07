package com.vitalong.bluetest2;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;

import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.bean.MService;
import com.vitalong.bluetest2.bean.VerifyDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USR_LJQ on 2015-11-17.
 */
public class MyApplication extends Application {

    private boolean clearflag;

    public boolean isClearflag() {
        return clearflag;
    }

    public void setClearflag(boolean clearflag) {
        this.clearflag = clearflag;
    }

    public enum SERVICE_TYPE {
        TYPE_USR_DEBUG, TYPE_NUMBER, TYPE_STR, TYPE_OTHER;
    }

    private final List<MService> services = new ArrayList<>();
    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

    private BluetoothGattCharacteristic characteristic;

    //板子的校验数据
    private VerifyDataBean verifyDataBean;

    public List<MService> getServices() {
        return services;
    }

    public static SERVICE_TYPE serviceType;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtil.getInstance(getApplicationContext(), "blue");
        verifyDataBean = new VerifyDataBean();
    }

    public void setServices(List<MService> services) {
        this.services.clear();
        this.services.addAll(services);
    }


    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<BluetoothGattCharacteristic> characteristics) {
        this.characteristics.clear();
        this.characteristics.addAll(characteristics);
    }


    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public VerifyDataBean getVerifyDataBean() {
        return verifyDataBean;
    }

    public void setVerifyDataBean(VerifyDataBean verifyDataBean) {
        this.verifyDataBean = verifyDataBean;
    }
}
