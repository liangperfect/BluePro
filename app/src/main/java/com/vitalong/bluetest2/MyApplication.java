package com.vitalong.bluetest2;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.database.sqlite.SQLiteDatabase;

import com.tencent.bugly.crashreport.CrashReport;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.bean.MService;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.DaoMaster;
import com.vitalong.bluetest2.greendaodb.DaoSession;
import com.vitalong.bluetest2.greendaodb.RealDataCachedDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USR_LJQ on 2015-11-17.
 */
public class MyApplication extends Application {

    private boolean clearflag;
    public boolean isConnectBlue = true;//是否连接了蓝牙进入的主界面
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

    public RealDataCachedDao realDataCachedDao;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtil.getInstance(getApplicationContext(), "blue");
        verifyDataBean = new VerifyDataBean();
        initDB();
        //初始化Bugly
        initBugly();
    }

    private void initDB() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "user,db");
        SQLiteDatabase sqLiteDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        DaoSession daoSession = daoMaster.newSession();
        realDataCachedDao = daoSession.getRealDataCachedDao();
    }

    private void initBugly() {

        CrashReport.initCrashReport(getApplicationContext(),"77f2a83f4e",true);

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
