package com.vitalong.bluetest2.inclinometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.vitalong.bluetest2.OperationPanelActivity;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;

import java.util.List;

public class SelectModeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnHistory;
    private Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        initView();
        bindToolBar();
        makeStatusBar(R.color.white);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        btnHistory = findViewById(R.id.btnHistory);
        btnNew = findViewById(R.id.btnNew);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SelectModeActivity.this, Survey2Activity.class));
                new LFilePicker()
                        .withActivity(SelectModeActivity.this)
                        .withRequestCode(123)
                        .withMutilyMode(false)
                        .withStartPath("/storage/emulated/0/tiltmeter")
                        .withIsGreater(false)
                        .withSelectorMode(Constants.SELECTOR_MODE_2)
                        .withFileSize(500 * 1024)
                        .start();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                startActivity(new Intent(SelectModeActivity.this, BoreholeInfoActivity.class));
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
                //选择
                Intent intent1 = new Intent(SelectModeActivity.this, Survey2Activity.class);
                startActivity(intent1);
            } else {
                List<String> list = data.getStringArrayListExtra("paths");
                Intent intent2 = new Intent(SelectModeActivity.this, Survey2Activity.class);
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "选中文件路径:" + list.get(0), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
