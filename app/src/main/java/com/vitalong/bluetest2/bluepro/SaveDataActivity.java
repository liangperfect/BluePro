package com.vitalong.bluetest2.bluepro;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.bean.RealDataCached;
import com.vitalong.bluetest2.bean.SaveSuerveyBean;
import com.vitalong.bluetest2.bean.TableRowBean;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.RealDataCachedDao;

import net.ozaydin.serkan.easy_csv.EasyCsv;
import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SaveDataActivity extends AppCompatActivity {
    @Bind(R.id.spSite)
    Spinner spSite;
    @Bind(R.id.spTiltmeter)
    Spinner spTiltmeter;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvNo1Text)
    TextView tv1;
    @Bind(R.id.tvNo2Text)
    TextView tv2;
    @Bind(R.id.tvNo3Text)
    TextView tv3;
    @Bind(R.id.tvNo4Text)
    TextView tv4;
    @Bind(R.id.btnSave)
    Button btnSave;
    private double dAxisA = 0;
    private double dAxisB = 0;
    private double dAxisC = 0;
    private double dAxisD = 0;
    private double dBxisA = 0;
    private double dBxisB = 0;
    private double dBxisC = 0;
    private double dBxisD = 0;
    private SaveSuerveyBean saveSuerveyBean;
    private String selectDir = "A001";
    private String selectFileName = "T01";
    private VerifyDataBean verifyDataBean;//矫正参数
    private boolean isSingleAxis = false; //判断数据是单轴还是双轴的 true:单轴   false:双轴

    private String deg1;
    private String deg2;
    private String deg3;
    private String deg4;
    private String raw1;
    private String raw2;
    private String raw3;
    private String raw4;
    private double raw1Andraw3 = 0;
    private double raw2Andraw4 = 0;
    private boolean canSave = false; //当前传过来的值是否可以保存了
    private RealDataCachedDao realDataCachedDao;
    private EasyCsv easyCsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);
        verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
        saveSuerveyBean = (SaveSuerveyBean) Objects.requireNonNull(getIntent().getExtras()).get("saveData");
        isSingleAxis = (boolean) getIntent().getExtras().get("isSingleAxis");
        canSave = (boolean) getIntent().getExtras().get("canSave");
        bindToolBar();
        makeStatusBar(R.color.white);
        initView();
        easyCsv = new EasyCsv(SaveDataActivity.this);
        if (savedInstanceState == null) {
            initVerifyData();
        }
    }

    private void initView() {

        tv1.setText(saveSuerveyBean.getShow1());
        tv2.setText(saveSuerveyBean.getShow2());
        tv3.setText(saveSuerveyBean.getShow3());
        tv4.setText(saveSuerveyBean.getShow4());

        boolean isS = isSingleAxis;
        if (isS) {
            //单轴
            double deg1Double = getDeg(saveSuerveyBean.getOneChannelAngle1(), Constants.SFMODE_1AXIS);
            double raw1Double = getRaw(deg1Double);
            deg1 = String.valueOf(deg1Double);
            raw1 = String.valueOf(raw1Double);
            double deg2Double = getDeg(saveSuerveyBean.getOneChannelAngle2(), Constants.SFMODE_1AXIS);
            double raw2Double = getRaw(deg2Double);
            deg2 = String.valueOf(deg2Double);
            raw2 = String.valueOf(raw2Double);
            double deg3Double = getDeg(saveSuerveyBean.getOneChannelAngle3(), Constants.SFMODE_1AXIS);
            double raw3Double = getRaw(deg3Double);
            deg3 = String.valueOf(deg3Double);
            raw3 = String.valueOf(raw3Double);
            double deg4Double = getDeg(saveSuerveyBean.getOneChannelAngle4(), Constants.SFMODE_1AXIS);
            double raw4Double = getRaw(deg4Double);
            deg4 = String.valueOf(deg4Double);
            raw4 = String.valueOf(raw4Double);

            raw1Andraw3 = raw1Double + raw3Double;
            raw2Andraw4 = raw2Double + raw4Double;
        } else {
            //双轴
            double deg1Double = getDeg(saveSuerveyBean.getOneChannelAngle1(), Constants.SFMODE_1AXIS);
            double raw1Double = getRaw(deg1Double);
            deg1 = String.valueOf(deg1Double);
            raw1 = String.valueOf(raw1Double);
            double deg2Double = getDeg(saveSuerveyBean.getTwoChannelAngle1(), Constants.SFMODE_2AXIS);
            double raw2Double = getRaw(deg2Double);
            deg2 = String.valueOf(deg2Double);
            raw2 = String.valueOf(raw2Double);
            double deg3Double = getDeg(saveSuerveyBean.getOneChannelAngle2(), Constants.SFMODE_1AXIS);
            double raw3Double = getRaw(deg3Double);
            deg3 = String.valueOf(deg3Double);
            raw3 = String.valueOf(raw3Double);
            double deg4Double = getDeg(saveSuerveyBean.getTwoChannelAngle2(), Constants.SFMODE_2AXIS);
            double raw4Double = getRaw(deg4Double);
            deg4 = String.valueOf(deg4Double);
            raw4 = String.valueOf(raw4Double);

            raw1Andraw3 = raw1Double + raw3Double;
            raw2Andraw4 = raw2Double + raw4Double;
        }
        initSpnr(spSite, dirs);
        initSpnr(spTiltmeter, fileNames);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (canSave) {
                    saveData();
                    //保存了数据就前往列表展示页面
                    Intent i = new Intent(SaveDataActivity.this, SurveyListActivity.class);
                    i.putExtra("tableName", selectDir + "_" + selectFileName);
                    i.putExtra("fromWhich", true);
                    startActivity(i);
                }
                setResult(Activity.RESULT_OK);
                SaveDataActivity.this.finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveData() {
        try {
            File file = createDirAndFile();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String de = sdf.format(date);
            String saveFileStr = "tiltmeter/" + selectDir + "/" + selectDir + "_" + selectFileName + "_" + de;
            easyCsv.setSeparatorColumn(",");//列分隔符
            easyCsv.setSeperatorLine("/n");//行分隔符
            List<String> headerList = new ArrayList<>();
            List<String> dataList = createTableData2();
            easyCsv.createCsvFile(saveFileStr, headerList, dataList, 1, new FileCallback() {

                @Override
                public void onSuccess(File file) {

                    Log.d("chenliang", "file:" + file.getPath());
                }

                @Override
                public void onFail(String s) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Collection<String[]> createTableData() {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = sdf.format(d);
        final Collection<String[]> collection = new ArrayList<>();
        collection.add(new TableRowBean("Type:", "Digital Tiltmeter", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Model:", "6600D", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Serial Number:", "1233", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Range:", "30 Deg", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Communication:", "Bluetooth 4.2", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Firmware:", "v1.2", "Software:", "v2.0", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Units:", "Raw / Deg", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Gage Factor(A):", "A1=" + verifyDataBean.getAaxisA(), "A2=" + verifyDataBean.getAaxisB(), "A3=" + verifyDataBean.getAaxisC(), "A4=" + verifyDataBean.getAaxisD(), "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Gage Factor(B):", "B1=" + verifyDataBean.getBaxisA(), "B2=" + verifyDataBean.getBaxisB(), "B3=" + verifyDataBean.getBaxisC(), "B4=" + verifyDataBean.getBaxisD(), "", "", "", "").toStrArray());
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Date/Time", "Site No.", "Instrument No.", "Direction", "Raw", "Raw", "Deg", "Deg", "CheckSum").toStrArray());
        collection.add(new TableRowBean(currTime, selectDir, selectFileName, "(1 - 3)", raw1, raw3, deg1, deg3, String.valueOf(raw1Andraw3)).toStrArray());
        collection.add(new TableRowBean(currTime, selectDir, selectFileName, "(2 - 4)", raw2, raw4, deg2, deg4, String.valueOf(raw2Andraw4)).toStrArray());
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean("Compare:", "", "", "", "", "", "", "", "").toStrArray());
        collection.add(new TableRowBean(selectDir + "_" + selectFileName, "Direction", "Raw", "Raw", "Include()", "", "", "", "").toStrArray());
        //获取数据库中的数据并添加到列表数据容器中
        List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(selectDir + "_" + selectFileName)).build().list();
        double d1Temp = Double.valueOf(deg1);
        double d2Temp = Double.valueOf(deg2);
        double d3Temp = Double.valueOf(deg3);
        double d4Temp = Double.valueOf(deg4);
        double include1 = (d1Temp - d3Temp) / 2 * 3600;
        double include2 = (d2Temp - d4Temp) / 2 * 3600;
        if (listDatas.isEmpty()) {

            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, String.valueOf(include1), "", "", "", "").toStrArray());
            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, String.valueOf(include2), "", "", "", "").toStrArray());
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, String.valueOf(include1)));
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, String.valueOf(include2)));
        } else {

            double firstInclude = Double.valueOf(listDatas.get(0).getInclude());
            double secondInclude = Double.valueOf(listDatas.get(1).getInclude());
//            listDatas.forEach(new Consumer<RealDataCached>() {
//                @Override
//                public void accept(RealDataCached realDataCached) {
//
//                    collection.add(realDataCached.toStrArray());
//                }
//            });
            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, String.valueOf(include1 - firstInclude), "", "", "", "").toStrArray());
            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, String.valueOf(include2 - secondInclude), "", "", "", "").toStrArray());
            //插入到数据库中去
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, String.valueOf(include1 - firstInclude)));
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, String.valueOf(include2 - secondInclude)));
        }
        return collection;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> createTableData2() {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = sdf.format(d);
        final List<String> collection = new ArrayList<>();
        collection.add(new TableRowBean("Type:", "Digital Tiltmeter", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Model:", "6600D", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Serial Number:", "1233", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Range:", "30 Deg", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Communication:", "Bluetooth 4.2", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Firmware:", "v1.2", "Software:", "v2.0", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Units:", "Raw / Deg", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Gage Factor(A):", "A1=" + verifyDataBean.getAaxisA(), "A2=" + verifyDataBean.getAaxisB(), "A3=" + verifyDataBean.getAaxisC(), "A4=" + verifyDataBean.getAaxisD(), "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Gage Factor(B):", "B1=" + verifyDataBean.getBaxisA(), "B2=" + verifyDataBean.getBaxisB(), "B3=" + verifyDataBean.getBaxisC(), "B4=" + verifyDataBean.getBaxisD(), "", "", "", "").toSaveString());
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Date/Time", "Site No.", "Instrument No.", "Direction", "Raw", "Raw", "Deg", "Deg", "CheckSum").toSaveString());
        collection.add(new TableRowBean(currTime, selectDir, selectFileName, "(1 - 3)", raw1, raw3, deg1, deg3, String.valueOf(raw1Andraw3)).toSaveString());
        collection.add(new TableRowBean(currTime, selectDir, selectFileName, "(2 - 4)", raw2, raw4, deg2, deg4, String.valueOf(raw2Andraw4)).toSaveString());
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Compare:", "", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean(selectDir + "_" + selectFileName, "Direction", "Raw", "Raw", "Include()", "", "", "", "").toSaveString());
        //获取数据库中的数据并添加到列表数据容器中
        List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(selectDir + "_" + selectFileName)).build().list();
        double d1Temp = Double.valueOf(deg1);
        double d2Temp = Double.valueOf(deg2);
        double d3Temp = Double.valueOf(deg3);
        double d4Temp = Double.valueOf(deg4);
        double include1 = (d1Temp - d3Temp) / 2 * 3600;
        double include2 = (d2Temp - d4Temp) / 2 * 3600;
        if (listDatas.isEmpty()) {

            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, String.valueOf(include1), "", "", "", "").toSaveString());
            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, String.valueOf(include2), "", "", "", "").toSaveString());
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, String.valueOf(include1)));
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, String.valueOf(include2)));
        } else {

            double firstInclude = Double.valueOf(listDatas.get(0).getInclude());
            double secondInclude = Double.valueOf(listDatas.get(1).getInclude());
//            listDatas.forEach(new Consumer<RealDataCached>() {
//                @Override
//                public void accept(RealDataCached realDataCached) {
//                    collection.add(new TableRowBean(realDataCached.getTime(), realDataCached.getDirection(), realDataCached.getRawFirst(),
//                            realDataCached.getRawSecond(), realDataCached.getInclude(), "", "", "", "").toSaveString());
//                }
//            });
            for (int i = 0; i < listDatas.size(); i++) {
                RealDataCached realDataCached = listDatas.get(i);
                collection.add(new TableRowBean(realDataCached.getTime(), realDataCached.getDirection(), realDataCached.getRawFirst(),
                        realDataCached.getRawSecond(), realDataCached.getInclude(), "", "", "", "").toSaveString());
            }

            collection.add(new TableRowBean(currTime, "(1-3)", raw1, raw3, String.valueOf(include1 - firstInclude), "", "", "", "").toSaveString());
            collection.add(new TableRowBean(currTime, "(2-4)", raw2, raw4, String.valueOf(include2 - secondInclude), "", "", "", "").toSaveString());
//            //插入到数据库中去
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(1-3)", raw1, raw3, String.valueOf(include1 - firstInclude)));
            realDataCachedDao.insert(new RealDataCached(selectDir + "_" + selectFileName, currTime, "(2-4)", raw2, raw4, String.valueOf(include2 - secondInclude)));
        }
        return collection;
    }

    /**
     * 创建目录及文件
     *
     * @return
     */
    private File createDirAndFile() throws IOException {

        String dir = "sdcard/tiltmeter/" + selectDir + "/";
        Utils.createDir(dir);
        File dirFile = new File(dir);
        //删除之前的文件
        if (dirFile != null && dirFile.list() != null && dirFile.list().length > 0) {
            for (String s : dirFile.list()) {
                new File(dir + s).delete();
            }
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String de = sdf.format(date);
        String fp = dir + "/" + selectDir + "_" + selectFileName + "_" + de + ".csv";
        File dataFile = new File(fp);
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        return dataFile;
    }

    public void initSpnr(Spinner spinner, String[] ctype) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text, ctype);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemSelected(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void itemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.spSite:

                selectDir = dirs[position];
                break;
            case R.id.spTiltmeter:

                selectFileName = fileNames[position];
                break;
            default:
                break;
        }
    }

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

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SaveDataActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    String[] dirs = new String[]{"A001", "A002", "A003", "A004", "A005", "A006", "A007", "A008", "A009", "A010", "A011", "A012", "A013", "A014", "A015", "A016", "A017", "A018", "A019"
            , "A020", "A021", "A022", "A023", "A024", "A025", "A026", "A027", "A028", "A029", "A030", "A031", "A032", "A033", "A034", "A035", "A036", "A037", "A038", "A039"
            , "A040", "A041", "A042", "A043", "A044", "A045", "A046", "A047", "A048", "A049", "A050", "A051", "A052", "A053", "A054", "A055", "A056", "A057", "A058", "A059"
            , "A060", "A061", "A062", "A063", "A064", "A065", "A066", "A067", "A068", "A069", "A070", "A071", "A072", "A073", "A074", "A075", "A076", "A077", "A078", "A079"
            , "A080", "A081", "A082", "A083", "A084", "A085", "A086", "A087", "A088", "A089", "A090", "A091", "A092", "A093", "A094", "A095", "A096", "A097", "A098", "A099"};//2 Axis
    String[] fileNames = new String[]{"T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10", "T11", "T12", "T13", "T14", "T15", "T16", "T17", "T18", "T19"
            , "T20", "T21", "T22", "T23", "T24", "T25", "T26", "T27", "T28", "T29", "T30", "T31", "T32", "T33", "T34", "T35", "T36", "T37", "T38", "T39"
            , "T40", "T41", "T42", "T43", "T44", "T45", "T46", "T47", "T48", "T49", "T50", "T51", "T52", "T53", "T54", "T55", "T56", "T57", "T58", "T59"
            , "T60", "T61", "T62", "T63", "T64", "T65", "T66", "T67", "T68", "T69", "T70", "T71", "T72", "T73", "T74", "T75", "T76", "T77", "T78", "T79"
            , "T80", "T81", "T82", "T83", "T84", "T85", "T86", "T87", "T88", "T89", "T90", "T91", "T92", "T93", "T94", "T95", "T96", "T97", "T98", "T99"};

    private Boolean initVerifyData() {

        realDataCachedDao = ((MyApplication) getApplication()).realDataCachedDao;

        try {
            VerifyDataBean verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
            dAxisA = Double.parseDouble(verifyDataBean.getAaxisA());
            dAxisB = Double.parseDouble(verifyDataBean.getAaxisB());
            dAxisC = Double.parseDouble(verifyDataBean.getAaxisC());
            dAxisD = Double.parseDouble(verifyDataBean.getAaxisD());
            dBxisA = Double.parseDouble(verifyDataBean.getBaxisA());
            dBxisB = Double.parseDouble(verifyDataBean.getBaxisB());
            dBxisC = Double.parseDouble(verifyDataBean.getBaxisC());
            dBxisD = Double.parseDouble(verifyDataBean.getBaxisD());
        } catch (Exception e) {
            Toast.makeText(SaveDataActivity.this, "verify data is error,please update verify data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private double getDeg(double angle, int axisMode) {
        double f = angle * 7.2;
        if (axisMode == Constants.SFMODE_1AXIS) {
            return dAxisA * (f * f * f) + dAxisB * (f * f) + (dAxisC + f) + dAxisD;
        }

        return dBxisA * (f * f * f) + dBxisB * (f * f) + (dBxisC + f) + dBxisD;
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

}
