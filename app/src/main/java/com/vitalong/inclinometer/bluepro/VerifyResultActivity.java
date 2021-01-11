package com.vitalong.inclinometer.bluepro;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vitalong.inclinometer.R;

public class VerifyResultActivity extends AppCompatActivity {

    private TextView tv1Value;
    private TextView tv2Value;
    private TextView tv3Value;
    private TextView tv4Value;
    private TextView tv5Value;
    private TextView tv6Value;
    private TextView tv7Value;
    private TextView tv8Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_result);
        initListener();
    }

    private void initListener() {

        tv1Value = findViewById(R.id.tv1Value);
        tv2Value = findViewById(R.id.tv2Value);
        tv3Value = findViewById(R.id.tv3Value);
        tv4Value = findViewById(R.id.tv4Value);
        tv5Value = findViewById(R.id.tv5Value);
        tv6Value = findViewById(R.id.tv6Value);
        tv7Value = findViewById(R.id.tv7Value);
        tv8Value = findViewById(R.id.tv8Value);

        tv1Value.setText(getIntent().getStringExtra("orginal1Str"));
        tv2Value.setText(getIntent().getStringExtra("orginal2Str"));
        tv3Value.setText(getIntent().getStringExtra("orginal3Str"));
        tv4Value.setText(getIntent().getStringExtra("orginal4Str"));
        tv5Value.setText(getIntent().getStringExtra("orginal5Str"));
        tv6Value.setText(getIntent().getStringExtra("orginal6Str"));
        tv7Value.setText(getIntent().getStringExtra("orginal7Str"));
        tv8Value.setText(getIntent().getStringExtra("orginal8Str"));
    }
}
