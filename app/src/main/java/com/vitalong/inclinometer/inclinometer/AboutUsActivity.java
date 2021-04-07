package com.vitalong.inclinometer.inclinometer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Utils;

/**
 * 关于我们
 */
public class AboutUsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textView;
    private ImageView imgCall;
    private ImageView imgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        bindToolBar();
        makeStatusBar(R.color.white);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.tvTitle2);
        imgCall = findViewById(R.id.imgCallPhone);
        imgLink = findViewById(R.id.imgLink);
        textView.setOnClickListener(v -> startActivity(new Intent(AboutUsActivity.this, WebViewActivity.class)));
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.callPhone(AboutUsActivity.this, "+886(0)2-55751088");
            }
        });
        imgLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutUsActivity.this, WebViewActivity.class);
                startActivity(i);
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
            AboutUsActivity.this.finish();
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
}