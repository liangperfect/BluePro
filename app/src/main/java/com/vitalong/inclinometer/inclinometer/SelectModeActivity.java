package com.vitalong.inclinometer.inclinometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.inclinometer.MyApplication;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Constants;
import com.vitalong.inclinometer.bean.BoreholeInfoTable;
import com.vitalong.inclinometer.bean.SurveyDataTable;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;
import com.vitalong.inclinometer.greendaodb.SurveyDataTableDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelectModeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnHistory;
    private Button btnNew;
    private BoreholeInfoTableDao boreholeInfoTableDao;
    private SurveyDataTableDao surveyDataTableDao;
    private CsvUtil csvUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        initView();
        bindToolBar();
        makeStatusBar(R.color.white);
        boreholeInfoTableDao = ((MyApplication) getApplication()).boreholeInfoTableDao;
        surveyDataTableDao = ((MyApplication) getApplication()).surveyDataTableDao;
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        btnHistory = findViewById(R.id.btnHistory);
        btnNew = findViewById(R.id.btnNew);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LFilePicker()
                        .withActivity(SelectModeActivity.this)
                        .withRequestCode(123)
                        .withMutilyMode(false)
                        .withStartPath(Constants.PRO_ROOT_PATH)
                        .withIsGreater(false)
                        .withSelectorMode(Constants.SELECTOR_MODE_2)
                        .withFileSize(500 * 1024)
                        .start();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SelectModeActivity.this, BoreholeInfoActivity.class));
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SelectModeActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == FILE_SELECTOR_SHARE) {
//            List<String> list = data.getStringArrayListExtra("paths");
//            Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
//            //如果是文件夹选择模式，需要获取选择的文件夹路径
//            String path = data.getStringExtra("path");
//            Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
//        }
        if (resultCode == Activity.RESULT_OK && requestCode == 123) {
            assert data != null;
            if (data.getIntExtra("selectModel", 0) == Constant.SELECTOR_MODE_2_1) {
                //在已有的工地号和孔号里面创建新的csv进行测量
                String siteName = data.getStringExtra("siteName");
                String holeName = data.getStringExtra("holeName");
                Log.d("chenliang", "onActivityResult : siteName:" + siteName + "   holeName:" + holeName)
                ;
                String sdPath = FileUtils.getSDCardPath();
                String sitePath = sdPath + Constants.PRO_ROOT_DIR_PATH + "/" + siteName;
                //给孔洞的文件夹加Namber_前缀是为了在选择文件时候点击的时候判断是否是孔号的文件夹
                String holePath = sitePath + "/" + FileUtils.fetchHoleName(holeName);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
                String de = sdf.format(date);
                String csvFileName = siteName + "_" + holeName + "_" + de + ".csv";
                String csvPath = holePath + "/" + csvFileName;
                Log.d("chenliang", "onActivityResult:" + sitePath + "  holePath:" + holePath
                        + "  csvFileName:" + csvFileName
                        + "  csvPath:" + csvPath);
                //进行测试
                FileUtils.createFile(csvPath);
                //根据间隔点位来初实话数据库表，根据csv的文件名为唯一键值对
                initTableByCsvName(siteName, FileUtils.fetchHoleName(holeName), csvFileName);
                //需要传递的参数
                Intent intent1 = new Intent(SelectModeActivity.this, Survey2Activity.class);
                //传递参数
                intent1.putExtra("constructionSiteName", siteName);
                intent1.putExtra("holeName", FileUtils.fetchHoleName(holeName));
                intent1.putExtra("fromWhich", Constants.FROM_HAS_SITE_HOLE_CREATE_CSV);
                intent1.putExtra("csvFileName", csvFileName);
                intent1.putExtra("csvFilePath", csvPath);
                startActivity(intent1);
            } else {
                //这里进行参数传递
                //对原有的csv文件进行修盖
                try {
                    List<String> list = data.getStringArrayListExtra("paths");
                    String csvFileName = data.getStringExtra("csvFileName");
                    Intent intent2 = new Intent(SelectModeActivity.this, Survey2Activity.class);
                    //从csv文件中拆分出工地名称及孔号
                    String[] splitArr = csvFileName.split("_");
                    for (int i = 0; i < splitArr.length; i++) {
                        Log.d("chenliang", "splitArr:" + splitArr[i]);
                    }
                    intent2.putExtra("constructionSiteName", splitArr[0]);
                    intent2.putExtra("holeName", FileUtils.fetchHoleName(splitArr[1]));
                    intent2.putExtra("fromWhich", Constants.FROM_MODIFY_CSV);
                    intent2.putExtra("csvFileName", csvFileName);
                    assert list != null;
                    intent2.putExtra("csvFilePath", list.get(0));
                    startActivity(intent2);
                } catch (Exception e) {

                    Toast.makeText(SelectModeActivity.this, "SelectModeActivity:文件解析错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 创建文件就直接生成对应的数据库表数据
     */
    private void initTableByCsvName(String siteName, String holeName, String csvFileName) {
        float topValue = 0;
        float bottomValue = 0;
        try {
            BoreholeInfoTable boreholeInfoTable = boreholeInfoTableDao.queryBuilder()
                    .where(BoreholeInfoTableDao.Properties.ConstructionSite.eq(siteName),
                            BoreholeInfoTableDao.Properties.HoleName.eq(holeName)).build().list().get(0); //只可能匹配一个
            topValue = boreholeInfoTable.getTopValue();
            bottomValue = boreholeInfoTable.getBottomValue();
//            //初始化相关view
//            tvDepthNum.setText(String.valueOf(bottomValue));
//            tvBottom.setText("Bottom:" + bottomValue + "m");
//            tvTop.setText("Top:" + topValue + "m");
//            csvUtil = new CsvUtil(SelectModeActivity.this, siteName, holeName, topValue, bottomValue);
        } catch (Exception exception) {

            Toast.makeText(SelectModeActivity.this, "沒找到孔的信息", Toast.LENGTH_SHORT).show();
        }

        while (bottomValue >= topValue) {
            String topStr = String.valueOf(topValue);
            surveyDataTableDao.insert(new SurveyDataTable(csvFileName, topStr, "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
            topValue += 0.5f;
        }
    }
}
