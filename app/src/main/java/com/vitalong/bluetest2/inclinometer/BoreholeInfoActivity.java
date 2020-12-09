package com.vitalong.bluetest2.inclinometer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.views.CompanySelectDialog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class BoreholeInfoActivity extends AppCompatActivity {

    private EditText edtConstructionSite;
    private EditText edtHoleNumber;
    private EditText edtAoDes;
    private TextView tvMeasurePoints;
    private TextView tvInterval;
    private EditText edtTopDepth;
    private EditText edtBottomDepth;
    private Toolbar toolbar;
    private TextView tvTopHit;
    private ImageButton imgbConstructionSite;
    private CompanySelectDialog companySelectDialog;
    private Button btnOk;
    private List<File> ConstructionSiteFiles;//工地文件夹名称

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
                    float topValue = Float.valueOf(edtTopDepth.getText().toString());
                    if (topValue <= 0) {
                        tvTopHit.setVisibility(View.VISIBLE);
                    } else {
                        if (tvTopHit.getVisibility() == View.VISIBLE) {
                            tvTopHit.setVisibility(View.GONE);
                        }

                        if (s.toString().isEmpty()) {
                            tvMeasurePoints.setText("");
                        } else {
                            float bottomValue = Float.valueOf(s.toString());
                            //计算点数
                            float pointsValue = (topValue - bottomValue) * 2;
                            int pointsNums = (int) pointsValue;
                            tvMeasurePoints.setText(String.valueOf(pointsNums));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verify()) {

                    Intent i = new Intent(BoreholeInfoActivity.this, Survey2Activity.class);
                    startActivity(i);
                }
            }
        });
    }

    private boolean verify() {

        if (edtConstructionSite.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請填寫工地名稱", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtHoleNumber.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請填寫孔號", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtAoDes.getText().toString().isEmpty()) {
            Toast.makeText(BoreholeInfoActivity.this, "請填寫A0描述", Toast.LENGTH_SHORT).show();
            return false;
        }

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
