package com.vitalong.inclinometer.inclinometer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.inclinometer.MyApplication;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.bean.BoreholeInfoTable;
import com.vitalong.inclinometer.bean.SurveyDataTable;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;
import com.vitalong.inclinometer.greendaodb.SurveyDataTableDao;
import com.vitalong.inclinometer.views.CompanySelectDialog;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BoreholeInfoActivity extends AppCompatActivity {

    private EditText edtConstructionSite;
    private EditText edtHoleNumber;
    private EditText edtAoDes;
    private TextView tvMeasurePoints;
    private TextView tvInterval;
    private EditText edtTopDepth;
    private EditText edtBottomDepth;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private Toolbar toolbar;
    private TextView tvTopHit;
    private ImageButton imgbConstructionSite;
    private CompanySelectDialog companySelectDialog;
    private Button btnOk;
    private List<File> ConstructionSiteFiles;//工地文件夹名称
    private BoreholeInfoTableDao boreholeInfoTableDao;
    private SurveyDataTableDao surveyDataTableDao;
    private float interval = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borehole_info);
        initData();
        initView();
        bindToolBar();
        makeStatusBar(R.color.white);
    }

    private void initData() {
        ConstructionSiteFiles = new ArrayList<File>();
        List<File> list = FileUtils.getFileListByDirPath(Constants.PRO_ROOT_PATH, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
        ConstructionSiteFiles.addAll(list);
    }

    private void initView() {

        imgbConstructionSite = findViewById(R.id.imgbConstructionSite);
        edtConstructionSite = findViewById(R.id.edtConstructionSite);
        edtHoleNumber = findViewById(R.id.edtHoleNumber);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        edtAoDes = findViewById(R.id.edtAoDes);
        toolbar = findViewById(R.id.toolbar);
        tvMeasurePoints = findViewById(R.id.tvMeasurePoints);
        tvInterval = findViewById(R.id.tvInterval);
        edtTopDepth = findViewById(R.id.edtTopDepth);
        edtBottomDepth = findViewById(R.id.edtBottomDepth);
        tvTopHit = findViewById(R.id.tvTopHit);
        btnOk = findViewById(R.id.btnOk);
        initListener();
    }

    private void initListener() {

        imgbConstructionSite.setOnClickListener(v -> {

            if (companySelectDialog == null) {
                companySelectDialog = new CompanySelectDialog(BoreholeInfoActivity.this, ConstructionSiteFiles, new CompanySelectDialog.ChangeComapngeListener() {
                    @Override
                    public void changeComapny(File file) {

                        edtConstructionSite.setText(file.getName());
                    }
                });
            }
            companySelectDialog.show();
        });

        edtTopDepth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtBottomDepth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtTopDepth.getText().toString().isEmpty()) {

                    tvTopHit.setVisibility(View.VISIBLE);
                } else {
                    float topValue = Float.parseFloat(edtTopDepth.getText().toString());
                    if (topValue > 500) {
                        tvTopHit.setVisibility(View.VISIBLE);
                    } else {

                        if (tvTopHit.getVisibility() == View.VISIBLE) {
                            tvTopHit.setVisibility(View.GONE);
                        }

                        if (s.toString().isEmpty()) {
                            tvMeasurePoints.setText("");
                        } else {
                            float bottomValue = Float.parseFloat(s.toString());
                            //计算点数
//                            float pointsValue = (bottomValue - topValue) * 2;
//                            float pointsValue = (bottomValue - topValue) / interval;
//                            int pointsNums = (int) pointsValue + 2;
                            int pointsNums = (int) (bottomValue / interval * 2);
                            tvMeasurePoints.setText(String.valueOf(pointsNums));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //0.5m
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interval = 0.5f;
                    edtTopDepth.setText("" + interval);
                    if (!edtTopDepth.getText().toString().trim().isEmpty() && !edtBottomDepth.getText().toString().trim().isEmpty()) {
                        float topValue = Float.parseFloat(edtTopDepth.getText().toString());
                        float bottomValue = Float.parseFloat(edtBottomDepth.getText().toString());
                        float pointsValue = (bottomValue - topValue) / interval;
                        Log.d("chenliang", "bottomValue - topValue1111->" + (bottomValue - topValue) + "   " + interval);
//                        int pointsNums = (int) pointsValue + 2;
                        int pointsNums = (int) (bottomValue / interval * 2);
                        tvMeasurePoints.setText(String.valueOf(pointsNums));
                    }
                }
            }
        });

        //1.0m
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    interval = 1.0f;
                    edtTopDepth.setText("" + interval);
                    if (!edtTopDepth.getText().toString().trim().isEmpty() && !edtBottomDepth.getText().toString().trim().isEmpty()) {
                        float topValue = Float.parseFloat(edtTopDepth.getText().toString().trim());
                        float bottomValue = Float.parseFloat(edtBottomDepth.getText().toString().trim());
                        float pointsValue = (bottomValue - topValue) / interval;
//                        int pointsNums = (int) pointsValue + 2;
                        int pointsNums = (int) (bottomValue / interval * 2);
                        tvMeasurePoints.setText(String.valueOf(pointsNums));
                    }
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTopDepth.getText().toString().trim().isEmpty()) {

                    Toast.makeText(BoreholeInfoActivity.this, "頂部距離不能為空", Toast.LENGTH_SHORT).show();
                    return;
                }
                float topValue = Float.parseFloat(edtTopDepth.getText().toString());
//                if (topValue < 0.5f) {
//                    edtTopDepth.setText("0.5");
//                }
                if (topValue < interval) {
                    edtTopDepth.setText("" + interval);
                }


                if (verify()) {
                    //将新建的工地，孔的属性添加到数据库当中去
                    addDataSource();
                    //创建对应的文件夹
                    HashMap<String, String> csvMap = addDirAndFile();
                    Intent i = new Intent(BoreholeInfoActivity.this, Survey2Activity.class);
                    i.putExtra("constructionSiteName", edtConstructionSite.getText().toString().trim());
                    i.putExtra("holeName", FileUtils.fetchHoleName(edtHoleNumber.getText().toString().trim()));
                    i.putExtra("fromWhich", Constants.FROM_CREATE_NEW_SITE_HOLE);
                    assert csvMap != null;
                    i.putExtra("csvFileName", csvMap.get("csvFileName"));
                    i.putExtra("csvFilePath", csvMap.get("csvFilePath"));
                    startActivity(i);
                    BoreholeInfoActivity.this.finish();
                }
            }
        });
    }

    /**
     * 添加对应的工地名称文件夹，孔号名称文件夹，再创建对应的csv的文件
     * 返回创建的csv文件名称
     */
    private HashMap<String, String> addDirAndFile() {
        if (FileUtils.isSDCardState()) {
            String sdPath = FileUtils.getSDCardPath();
            String SiteStr = edtConstructionSite.getText().toString();
            String holeStr = edtHoleNumber.getText().toString();
            String sitePath = sdPath + Constants.PRO_ROOT_DIR_PATH + "/" + SiteStr;
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
            String de = sdf.format(date);
            //给孔洞的文件夹加Namber_前缀是为了在选择文件时候点击的时候判断是否是孔号的文件夹
            String holePath = sitePath + "/" + FileUtils.fetchHoleName(holeStr);
//            String csvFileName = SiteStr + "_" + holeStr + "_" + de + ".csv";
            String csvFileName = SiteStr + "_" + holeStr + "_" + de + ".csv";
            String csvPath = holePath + "/" + csvFileName;
            if (!FileUtils.isFileExits(sdPath + Constants.PRO_ROOT_DIR_PATH)) {
                //tilmeter文件夹不存在，进行创建
                FileUtils.createSDDirection(sdPath + Constants.PRO_ROOT_DIR_PATH);
            }

            if (!FileUtils.isFileExits(sitePath)) {
                //创建工程名称的文件夹
                FileUtils.createSDDirection(sitePath);
            }

            if (!FileUtils.isFileExits(holePath)) {
                //创建孔号的文件夹
                FileUtils.createSDDirection(holePath);
            }
            //创建csv文件
            FileUtils.createFile(csvPath);
            //根据间隔点位来初实话数据库表，根据csv的文件名为唯一键值对
            initTableByCsvName(csvFileName);
            HashMap<String, String> map = new HashMap<>();
            map.put("csvFileName", csvFileName);
            map.put("csvFilePath", csvPath);
            return map;
        } else {
            //SD卡不存在
            Toast.makeText(BoreholeInfoActivity.this, "請插入SD卡", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 根据csv的文件名初始化从底部到顶部的空数据，文件名不包含Namber_关键字
     */
    private void initTableByCsvName(String csvFileName) {

        float top = Float.parseFloat(edtTopDepth.getText().toString());
        float bottom = Float.parseFloat(edtBottomDepth.getText().toString());

        while (bottom >= top) {
            String topStr = String.valueOf(top);
            surveyDataTableDao.insert(new SurveyDataTable(csvFileName, topStr, "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
//            top += 0.5f;
            top += interval;
        }
    }

    private void addDataSource() {
        boreholeInfoTableDao = ((MyApplication) getApplication()).boreholeInfoTableDao;
        surveyDataTableDao = ((MyApplication) getApplication()).surveyDataTableDao;
        float topValue = Float.parseFloat(edtTopDepth.getText().toString());
        float bottomValue = Float.parseFloat(edtBottomDepth.getText().toString());
        int pointsNumbers = Integer.parseInt(tvMeasurePoints.getText().toString());
        BoreholeInfoTable boreholeInfoTable = new BoreholeInfoTable();
        boreholeInfoTable.setConstructionSite(edtConstructionSite.getText().toString());
        boreholeInfoTable.setHoleName(FileUtils.fetchHoleName(edtHoleNumber.getText().toString()));
        //保存到孔号那些
        boreholeInfoTable.setA0Des(edtAoDes.getText().toString());
        boreholeInfoTable.setTopValue(topValue);
        boreholeInfoTable.setBottomValue(bottomValue);
        boreholeInfoTable.setPointsNumber(pointsNumbers);
//        boreholeInfoTable.setDuration(0.5f);
        boreholeInfoTable.setDuration(interval);
        boreholeInfoTableDao.insert(boreholeInfoTable);
    }

    private boolean verify() {

        if (edtConstructionSite.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請填寫工地名稱", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtConstructionSite.getText().toString().startsWith("#")) {
            Toast.makeText(BoreholeInfoActivity.this, "工地名稱不能以#开头", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtHoleNumber.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請填寫孔號", Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (edtAoDes.getText().toString().isEmpty()) {
//            Toast.makeText(BoreholeInfoActivity.this, "請填寫A0描述", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (tvMeasurePoints.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請計算測量點數", Toast.LENGTH_SHORT).show();
            return false;
        }

        //这里判断是几个点，是否测试底部数据
        return true;
    }

    private void bindToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= 23) {
//            toolbar.setTitleTextColor(getColor(android.R.color.white));
            toolbar.setTitleTextColor(getColor(android.R.color.black));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
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
            BoreholeInfoActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
