package com.vitalong.inclinometer.inclinometer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.views.ChartView;
import com.vitalong.inclinometer.views.SimpleLineView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph3Activity extends AppCompatActivity {

    ChartView chartView;
    //x轴坐标对应的数据
    private List<String> xValue = new ArrayList<>();
    //y轴坐标对应的数据
    private List<Integer> yValue = new ArrayList<>();
    //折线对应的数据
    private Map<String, Integer> value = new HashMap<>();
    private Spinner spinner;
    SimpleLineView simpleLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph3);

        initView();
    }

    private void initView() {

//        chartView = findViewById(R.id.chartview);
//        for (int i = 0; i < 12; i++) {
//            xValue.add((i + 1) + "月");
//            value.put((i + 1) + "月", (int) (Math.random() * 181 + 60));//60--240
//        }
//
//        for (int i = 0; i < 6; i++) {
//            yValue.add(i * 60);
//        }
//
//        chartView.setValue(value, xValue, yValue);
        simpleLineView = findViewById(R.id.simpleLineView);
        spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                simpleLineView.refreshByData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}