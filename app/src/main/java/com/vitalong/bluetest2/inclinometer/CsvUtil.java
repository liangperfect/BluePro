package com.vitalong.bluetest2.inclinometer;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.bean.SurveyDataTable;
import com.vitalong.bluetest2.bean.TableRowBean2;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.SurveyDataTableDao;

import net.ozaydin.serkan.easy_csv.EasyCsv;
import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.File;
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
    private EasyCsv easyCsv;
    private Activity activity;
    private VerifyDataBean verifyDataBean;
    private SurveyDataTableDao surveyDataTableDao;

    public CsvUtil(Activity activity) {
        //获取snValue
        this.activity = activity;
        snValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        verifyDataBean = ((MyApplication) activity.getApplication()).getVerifyDataBean();
        surveyDataTableDao = ((MyApplication) activity.getApplication()).surveyDataTableDao;
        easyCsv = new EasyCsv(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveData(String csvFilePath, String csvFileName) {
        try {
            easyCsv.setSeparatorColumn(",");//列分隔符
            easyCsv.setSeperatorLine("/n");//行分隔符
            List<String> headerList = new ArrayList<>();//为空的
            List<String> dataList = createTableData(csvFileName);
            //截取文件适配csv存储器的文件名称
            String csvAdapterPath = csvFilePath.substring(20, csvFilePath.length() - 4);
            easyCsv.createCsvFile(csvAdapterPath, headerList, dataList, 1, new FileCallback() {

                @Override
                public void onSuccess(File file) {
                    Log.d("chenliang", "存储成功file:" + file.getPath());
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
        collection.add(new TableRowBean2("Type:", "Digital Inclinometer", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Model:", "7000BT", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Serial Number:", snValue, "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Range:", "30 Deg", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Communication:", "Bluetooth 4.2", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Firmware:", "V2.58", "Software:", "V1.37.2", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Units:", "Raw / Deg", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Gage Factor(A):", "A1=" + verifyDataBean.getAaxisA(), "A2=" + verifyDataBean.getAaxisB(), "A3=" + verifyDataBean.getAaxisC(), "A4=" + verifyDataBean.getAaxisD(), "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Gage Factor(B):", "B1=" + verifyDataBean.getBaxisA(), "B2=" + verifyDataBean.getBaxisB(), "B3=" + verifyDataBean.getBaxisC(), "B4=" + verifyDataBean.getBaxisD(), "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Site#:", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Hole#:", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Depth(m):", "", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Interval(mm):", "500", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("Date/Time:", "2020/10/25  22:29:03", "", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean2("", "Raw", "Raw", "Raw", "Raw", "Displacement(mm)=500*SIN(RADIANS)θ", "", "", "", "A0+A180", "B0+B180").toSaveString());
        collection.add(new TableRowBean2("", "A0", "A180", "B0", "B180", "A0(mm)", "A180(mm)", "B0(mm)", "B180(mm)", "CheckSumA", "CheckSumB").toSaveString());

        List<SurveyDataTable> surveyDatas = surveyDataTableDao.queryBuilder().where(SurveyDataTableDao.Properties.CsvFileName.eq(csvFileName)).build().list();
        for (SurveyDataTable item : surveyDatas
        ) {

            collection.add(new TableRowBean2(item.getDepth(), item.getRawA0(), item.getRawA180(), item.getRawA0(), item.getRawA180()
                    , item.getA0mm(), item.getA180mm(), item.getB0mm(), item.getB180mm(), item.getCheckSumA(), item.getCheckSumB()).toSaveString());
        }
        //        for (int i = 0; i < 5000; i++) {
//            collection.add(new TableRowBean2("", "A0", "A180", "B0", "B180", "A0(mm)", "A180(mm)", "B0(mm)", "B180(mm)", "CheckSumA", "CheckSumB").toSaveString());
//        }
        //        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Compare:", "", "", "", "", "", "", "", "").toSaveString());
        //获取数据库中的数据并添加到列表数据容器中
//        List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(selectDir + "_" + selectFileName)).build().list();
//        double d1Temp = Double.valueOf(deg1);
//        double d2Temp = Double.valueOf(deg2);
//        double d3Temp = Double.valueOf(deg3);
//        double d4Temp = Double.valueOf(deg4);
//        double include1 = (d1Temp - d3Temp) / 2 * 3600;
//        double include2 = (d2Temp - d4Temp) / 2 * 3600;
//        int intIncline1 = (int) include1;
//        int intIncline2 = (int) include2;
//        if (listDatas.isEmpty()) {
//
//            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, "0", "", "", "", "").toSaveString());
//            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, "0", "", "", "", "").toSaveString());
//            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, "0", String.valueOf(intIncline1)));
//            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, "0", String.valueOf(intIncline2)));
//        } else {
//
//            int firstInclude = Integer.valueOf(listDatas.get(0).getRealIncline());
//            int secondInclude = Integer.valueOf(listDatas.get(1).getRealIncline());
//            for (int i = 0; i < listDatas.size(); i++) {
//                RealDataCached realDataCached = listDatas.get(i);
//                collection.add(new TableRowBean(realDataCached.getTime(), realDataCached.getDirection(), realDataCached.getRawFirst(),
//                        realDataCached.getRawSecond(), realDataCached.getInclude(), "", "", "", "").toSaveString());
//            }
//
//            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, String.valueOf(intIncline1 - firstInclude), "", "", "", "").toSaveString());
//            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, String.valueOf(intIncline2 - secondInclude), "", "", "", "").toSaveString());
////            //插入到数据库中去
//            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, String.valueOf(intIncline1 - firstInclude), String.valueOf(intIncline1 - firstInclude)));
//            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, String.valueOf(intIncline2 - secondInclude), String.valueOf(intIncline2 - secondInclude)));
//        }
        return collection;
    }
}
