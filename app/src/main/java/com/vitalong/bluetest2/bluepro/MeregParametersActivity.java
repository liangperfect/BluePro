package com.vitalong.bluetest2.bluepro;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.bluetest2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MeregParametersActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mereg_parameters);
        bindToolBar();
        makeStatusBar(R.color.white);
        initData();
        initListener();
    }


    private void initData() {

    }
    private void initListener() {

    }


    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationIcon(R.mipmap.ic_search_white_36dp);
        if (Build.VERSION.SDK_INT >= 23) {
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
            onBackPressed();
            MeregParametersActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}