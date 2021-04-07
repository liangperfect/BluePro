package com.vitalong.inclinometer.inclinometer;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.vitalong.inclinometer.MyApplication;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.Utils.EasyCsvCopy;
import com.vitalong.inclinometer.Utils.SharedPreferencesUtil;
import com.vitalong.inclinometer.Utils.Utils;
import com.vitalong.inclinometer.bean.SurveyDataTable;
import com.vitalong.inclinometer.bean.TableRowBean2;
import com.vitalong.inclinometer.bean.VerifyDataBean;
import com.vitalong.inclinometer.greendaodb.SurveyDataTableDao;

import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Package: com.vitalong.bluetest2.inclinometer
 * @Description:
 * @Author: 亮
 * @CreateDate: 2020/12/15 9:37
 * @UpdateUser: 更新者
 */
public class CsvUtil {
    private String snValue = "";
    private EasyCsvCopy easyCsv;
    private Activity activity;
    private SurveyDataTableDao surveyDataTableDao;
    private String siteStr = "";
    private String holeStr = "";
    private String Depth = "";
    private String Interval = "";
    private String Date = "";
    private float topValue;
    private float bottomValue;
    private double dAxisA = 0;
    private double dAxisB = 0;
    private double dAxisC = 0;
    private double dAxisD = 0;
    private double dBxisA = 0;
    private double dBxisB = 0;
    private double dBxisC = 0;
    private double dBxisD = 0;
    //原始科学技术法对string
    private String dAxisAStr = "";
    private String dAxisBStr = "";
    private String dAxisCStr = "";
    private String dAxisDStr = "";
    private String dBxisAStr = "";
    private String dBxisBStr = "";
    private String dBxisCStr = "";
    private String dBxisDStr = "";

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Vibrator vibrator;

    public CsvUtil(Activity activity) {
        this.activity = activity;
//        initVerifyData();
        snValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        surveyDataTableDao = ((MyApplication) activity.getApplication()).surveyDataTableDao;
        easyCsv = new EasyCsvCopy(activity);
    }

    public CsvUtil(Activity activity, String siteStr, String holeStr, float topValue, float bottomValue) {
        //获取snValue
        this.activity = activity;
        this.topValue = topValue;
        this.bottomValue = bottomValue;
        this.siteStr = siteStr;
        this.holeStr = holeStr;
        initVerifyData();
        snValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        surveyDataTableDao = ((MyApplication) activity.getApplication()).surveyDataTableDao;
        easyCsv = new EasyCsvCopy(activity);
        try {
            playRd("Default.mp3");
            vibrator = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initVerifyData() {

        try {
            VerifyDataBean verifyDataBean = ((MyApplication) activity.getApplication()).getVerifyDataBean();
            dAxisA = Double.parseDouble(verifyDataBean.getAaxisA());
            dAxisB = Double.parseDouble(verifyDataBean.getAaxisB());
            dAxisC = Double.parseDouble(verifyDataBean.getAaxisC());
            dAxisD = Double.parseDouble(verifyDataBean.getAaxisD());
            dBxisA = Double.parseDouble(verifyDataBean.getBaxisA());
            dBxisB = Double.parseDouble(verifyDataBean.getBaxisB());
            dBxisC = Double.parseDouble(verifyDataBean.getBaxisC());
            dBxisD = Double.parseDouble(verifyDataBean.getBaxisD());
            dAxisAStr = verifyDataBean.getAaxisA();
            dAxisBStr = verifyDataBean.getAaxisB();
            dAxisCStr = verifyDataBean.getAaxisC();
            dAxisDStr = verifyDataBean.getAaxisD();
            dBxisAStr = verifyDataBean.getBaxisA();
            dBxisBStr = verifyDataBean.getBaxisB();
            dBxisCStr = verifyDataBean.getBaxisC();
            dBxisDStr = verifyDataBean.getBaxisD();
        } catch (Exception e) {
            Toast.makeText(activity, "verify data is error,please update verify data", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveData(String csvFilePath, String csvFileName) {
        try {
            easyCsv.setSeparatorColumn(",");//列分隔符
            easyCsv.setSeperatorLine("/n");//行分隔符
            List<String> headerList = new ArrayList<>();//为空的
            //生成需要存储的数据
            List<String> dataList = createTableData(csvFileName);
            //截取文件适配csv存储器的文件名称
            String csvAdapterPath = csvFilePath.substring(20, csvFilePath.length() - 4);
            easyCsv.createCsvFile(csvAdapterPath, headerList, dataList, 1, new FileCallback() {

                @Override
                public void onSuccess(File file) {


                    Log.d("chenliang", "存储成功file:" + file.getPath());

                    //存储成功进行震动和播放提示声音
                    toPlayAndVibrator();
                }

                @Override
                public void onFail(String s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据csv文件名称获取存储在数据库中对应的名称
     *
     * @param csvFileName
     * @return
     */
    private List<String> createTableData(String csvFileName) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currTime = sdf.format(d);
        final List<String> collection = new ArrayList<>();
        //将Factory参数采用科学技术法去表示
        collection.add(new TableRowBean2("Type:", "Digital Inclinometer", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Model:", "7000BT", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Serial Number:", snValue, "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Range:", "30 Deg", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Communication:", "Bluetooth 4.2", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Firmware:", "V2.58", "Software:", "V1.37.2", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Units:", "mm / Deg / Raw", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean2("Gage Factor(A):", "A1=" + dAxisA, "A2=" + dAxisB, "A3=" + dAxisC, "A4=" + dAxisD, "", "", "", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean2("Gage Factor(B):", "B1=" + dBxisA, "B2=" + dBxisB, "B3=" + dBxisC, "B4=" + dBxisD, "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Gage Factor(A):", "A1=" + dAxisAStr, "A2=" + dAxisBStr, "A3=" + dAxisCStr, "A4=" + dAxisDStr, "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Gage Factor(B):", "B1=" + dBxisAStr, "B2=" + dBxisBStr, "B3=" + dBxisCStr, "B4=" + dBxisDStr, "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Site#:", siteStr, "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Hole#:", holeStr, "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Depth(m):", "Top=" + topValue, "End=" + bottomValue, "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Interval(mm):", "500", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Date/Time:", "2020/10/25  22:29:03", "", "", "", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("", "Displacement(mm)=500*SIN(RADIANS(Deg))", "", "", "", "", "", "", "", "", "", "", "", "A0(mm)+A180(mm)", "B0(mm)+B180(mm)").toSaveString());
        collection.add(new TableRowBean2("", "A0(mm)", "A180(mm)", "B0(mm)", "B180(mm)", "A0(Deg)", "A180(Deg)", "B0(Deg)", "B180(Deg)", "A0(Raw)", "A180(Raw)", "B0(Raw)", "B180(Raw)", "CheckSumA", "CheckSumB").toSaveString());

        List<SurveyDataTable> surveyDatas = surveyDataTableDao.queryBuilder().where(SurveyDataTableDao.Properties.CsvFileName.eq(csvFileName)).build().list();
        for (SurveyDataTable item : surveyDatas
        ) {

            //将小数点位数做处理
//            handleDecimalPoint(item);
            collection.add(new TableRowBean2(item.getDepth(), item.getA0mm(), item.getA180mm(), item.getB0mm(), item.getB180mm(), item.getA0Deg()
                    , item.getA180Deg(), item.getB0Deg(), item.getB180Deg(), item.getA0Raw(), item.getA180Raw(), item.getB0Raw(), item.getB180Raw(),
                    item.getCheckSumA(), item.getCheckSumB()).toSaveString());
        }
        return collection;
    }

    /**
     * 对double进行格式化,默认是保留2位
     * @param number 格式化对数字
     * @param num 小数点后面保留几位
     */
    private String handleDecimalPoint(double number,int num){

//        java.text.DecimalFormat   df4   =new   java.text.DecimalFormat("#.0000");
        java.text.DecimalFormat   df;
        if (num == 2){
            df   =new   java.text.DecimalFormat("#.00");
        }else {
            df   =new   java.text.DecimalFormat("#.0000");
        }

        return df.format(number);
    }
    /**
     * true是0度, false是180度
     * 将实时数据存储到数据库中去
     */
    public String saveDatasInDB(String csvFileName, String depth, float angleA, float angleB, boolean isZero) {
        double deg1 = getDeg(angleA, Constants.SFMODE_1AXIS);
        double deg2 = getDeg(angleB, Constants.SFMODE_2AXIS);
        double raw1 = getRaw(deg1);
        double raw2 = getRaw(deg2);
        double radians1 = Math.toRadians(deg1);
        double radians2 = Math.toRadians(deg2);
        double mm1 =  (Math.sin(radians1) * 500);
        double mm2 = (Math.sin(radians2) * 500);
        SurveyDataTable surveyDataTable = surveyDataTableDao.queryBuilder().where(SurveyDataTableDao.Properties.CsvFileName.eq(csvFileName), SurveyDataTableDao.Properties.Depth.eq(depth)).build().list().get(0);

        if (surveyDataTable != null) {
            if (isZero) {
                //值要重新计算
//                surveyDataTable.setA0mm(String.valueOf(mm1));
//                surveyDataTable.setB0mm(String.valueOf(mm2));
//                surveyDataTable.setA0Deg(String.valueOf(deg1));
//                surveyDataTable.setB0Deg(String.valueOf(deg2));
//                surveyDataTable.setA0Raw(String.valueOf(raw1));
//                surveyDataTable.setB0Raw(String.valueOf(raw2));

                surveyDataTable.setA0mm(handleDecimalPoint(mm1,2));
                surveyDataTable.setB0mm(handleDecimalPoint(mm2,2));
                surveyDataTable.setA0Deg(handleDecimalPoint(deg1,4));
                surveyDataTable.setB0Deg(handleDecimalPoint(deg2,4));
                surveyDataTable.setA0Raw(String.valueOf((int)raw1));
                surveyDataTable.setB0Raw(String.valueOf((int) raw2));
//                Log.d("chenliang","数据验证" + handleDecimalPoint(mm1,2));
//                Log.d("chenliang","数据验证" + handleDecimalPoint(deg1,4));
//                Log.d("chenliang","数据验证" + (int) raw1);
                if (surveyDataTable.getA180mm().equals("")) {
                    surveyDataTable.setCheckSumA(handleDecimalPoint(mm1,2));
                } else {
                    surveyDataTable.setCheckSumA(handleDecimalPoint((mm1 + Double.parseDouble(surveyDataTable.getA180mm())),2));
                }

                if (surveyDataTable.getB180mm().equals("")) {
                    surveyDataTable.setCheckSumB(handleDecimalPoint(mm2,2));
                } else {
                    surveyDataTable.setCheckSumB(handleDecimalPoint((mm2 + Double.parseDouble(surveyDataTable.getB180mm())),2));
                }
            } else {

                surveyDataTable.setA180mm(handleDecimalPoint(mm1,2));
                surveyDataTable.setB180mm(handleDecimalPoint(mm2,2));
                surveyDataTable.setA180Deg(handleDecimalPoint(deg1,4));
                surveyDataTable.setB180Deg(handleDecimalPoint(deg2,4));
                surveyDataTable.setA180Raw(String.valueOf((int) raw1));
                surveyDataTable.setB180Raw(String.valueOf((int) raw2));
                if (surveyDataTable.getA0mm().equals("")) {
                    surveyDataTable.setCheckSumA(handleDecimalPoint(mm1,2));
                } else {
                    surveyDataTable.setCheckSumA(handleDecimalPoint((mm1 + Double.parseDouble(surveyDataTable.getA0mm())),2));
                }

                if (surveyDataTable.getB0mm().equals("")) {
                    surveyDataTable.setCheckSumB(handleDecimalPoint(mm2,2));
                } else {
                    surveyDataTable.setCheckSumB(handleDecimalPoint((mm2 + Double.parseDouble(surveyDataTable.getB0mm())),2));
                }
            }
        }
        surveyDataTableDao.update(surveyDataTable);
        return mm1 + "," + mm2;
    }

    /**
     * 根据csv文件名主键和高度来获取对应数据库记录
     *
     * @param csvFileName csv文件名称
     * @param depth       对应的高度
     * @return
     */
    public SurveyDataTable getSurveyByDepth(String csvFileName, String depth) {
        return surveyDataTableDao.queryBuilder().where(SurveyDataTableDao.Properties.CsvFileName.eq(csvFileName), SurveyDataTableDao.Properties.Depth.eq(depth)).build().list().get(0);
    }

    /**
     * 根据csv文件名来获取到改csv文件的所存储的实时数据
     *
     * @param csvFileName
     * @return 返回查询到的列表
     */
    public List<SurveyDataTable> getSurveyListByCsvName(String csvFileName) {

        return surveyDataTableDao.queryBuilder().where(SurveyDataTableDao.Properties.CsvFileName.eq(csvFileName)).list();
    }

    /**
     * 获取单位模式为Deg下的值
     *
     * @param angle
     * @return
     */
    private double getDeg(double angle, int axisMode) {
        double f = angle * 7.2;
        if (axisMode == Constants.SFMODE_1AXIS) {
            return dAxisA * (f * f * f) + dAxisB * (f * f) + (dAxisC * f) + dAxisD;
        }

        return dBxisA * (f * f * f) + dBxisB * (f * f) + (dBxisC * f) + dBxisD;
    }

    /**
     * 根据raw获取Raw
     *
     * @param deg
     * @return
     */
    private double getRaw(double deg) {

        try {
            double raw = Math.sin(deg * Math.PI / 180) * 25000;
            return raw;
        } catch (Exception err) {
            return 100000.0;
        }
    }

    /**
     * 初始化存储成功声音
     * @param fileName
     * @throws IOException
     */
    void playRd(String fileName) throws IOException {
        AssetManager assetManager = activity.getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
        mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mMediaPlayer.prepare();
    }

    private void toPlayAndVibrator() {
        if (!mMediaPlayer.isPlaying() ) {
            mMediaPlayer.start();
            vibrator.vibrate(100);
        }
    }
}
