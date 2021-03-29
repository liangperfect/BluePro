package com.vitalong.inclinometer.inclinometer;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Utils;

public class TestMainActivity extends AppCompatActivity {

    TextView tv1;
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        initView();
    }

    private void initView() {
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        Utils.calculateTag1(tv1, tv2, (String) tv1.getText());
    }
}