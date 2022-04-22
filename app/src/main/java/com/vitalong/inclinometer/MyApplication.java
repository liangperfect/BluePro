package com.vitalong.inclinometer;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.database.sqlite.SQLiteDatabase;

import com.tencent.bugly.crashreport.CrashReport;
import com.vitalong.inclinometer.Utils.SharedPreferencesUtil;
import com.vitalong.inclinometer.bean.MService;
import com.vitalong.inclinometer.bean.VerifyDataBean;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;
import com.vitalong.inclinometer.greendaodb.DaoMaster;
import com.vitalong.inclinometer.greendaodb.DaoSession;
import com.vitalong.inclinometer.greendaodb.RealDataCachedDao;
import com.vitalong.inclinometer.greendaodb.SurveyDataTableDao;

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

    public RealDataCachedDao realDataCachedDao;

    public BoreholeInfoTableDao boreholeInfoTableDao;
    public SurveyDataTableDao surveyDataTableDao;

    public boolean isConnectBlue = true;//是否连接了蓝牙进入的主界面

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
        boreholeInfoTableDao = daoSession.getBoreholeInfoTableDao();
        surveyDataTableDao = daoSession.getSurveyDataTableDao();
    }

    private void initBugly() {

        CrashReport.initCrashReport(getApplicationContext(), "77f2a83f4e", true);
//        CrashReport.enableBugly(false);
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
