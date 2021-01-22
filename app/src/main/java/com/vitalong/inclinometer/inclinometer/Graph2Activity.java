package com.vitalong.inclinometer.inclinometer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.vitalong.inclinometer.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Graph2Activity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.barChart)
    public HorizontalBarChart mHorizontalBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);
        bindToolBar();
        makeStatusBar(R.color.white);
        initLineChart();
    }

    private void initLineChart() {
        //正负堆叠条形图
//        mHorizontalBarChart.setOnChartValueSelectedListener(this);
        mHorizontalBarChart.setDrawGridBackground(false);
        mHorizontalBarChart.getDescription().setEnabled(false);

        // 扩展现在只能分别在x轴和y轴
        mHorizontalBarChart.setPinchZoom(false);

        mHorizontalBarChart.setDrawBarShadow(false);
        mHorizontalBarChart.setDrawValueAboveBar(true);
        mHorizontalBarChart.setHighlightFullBarEnabled(false);

        mHorizontalBarChart.getAxisLeft().setEnabled(false);
        mHorizontalBarChart.getAxisRight().setAxisMaximum(25f);
        mHorizontalBarChart.getAxisRight().setAxisMinimum(-25f);
        mHorizontalBarChart.getAxisRight().setDrawGridLines(false);
        mHorizontalBarChart.getAxisRight().setDrawZeroLine(true);
        mHorizontalBarChart.getAxisRight().setLabelCount(7, false);
//        mHorizontalBarChart.getAxisRight().setValueFormatter(new CustomFormatter());
        mHorizontalBarChart.getAxisRight().setTextSize(9f);

        XAxis xAxis = mHorizontalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(9f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(100);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(100,true);
        xAxis.setGranularity(10f);
        //日期格式化
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//
//            private DecimalFormat format = new DecimalFormat("###");
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return format.format(value) + "-" + format.format(value + 10);
//            }
//
//            @Override
//            public int getDecimalDigits() {
//                return 0;
//            }
//        });

        Legend l = mHorizontalBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        // 重要:当使用负值在堆叠酒吧,总是确保-值数组中的第一个
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        float tempIndex = 0;
        while (tempIndex < 100) {
            yValues.add(new BarEntry(tempIndex, new float[]{-tempIndex % 10, tempIndex % 10}));
            tempIndex += 0.5f;
        }
//        yValues.add(new BarEntry(5, new float[]{-10, 10}));
//        yValues.add(new BarEntry(5.5f, new float[]{-11, 12}));
//        yValues.add(new BarEntry(15, new float[]{-12, 13}));
//        yValues.add(new BarEntry(25, new float[]{-15, 15}));
//        yValues.add(new BarEntry(35, new float[]{-17, 17}));
//        yValues.add(new BarEntry(45, new float[]{-19, 20}));
//        yValues.add(new BarEntry(55, new float[]{-19, 19}));
//        yValues.add(new BarEntry(65, new float[]{-16, 16}));
//        yValues.add(new BarEntry(75, new float[]{-13, 14}));
//        yValues.add(new BarEntry(85, new float[]{-10, 11}));
//        yValues.add(new BarEntry(95, new float[]{-5, 6}));
//        yValues.add(new BarEntry(105, new float[]{-1, 2}));

        BarDataSet set = new BarDataSet(yValues, "全国人口普查");
//        set.setValueFormatter(new CustomFormatter());
        set.setValueTextSize(7f);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setColors(new int[]{Color.rgb(99, 67, 72), Color.rgb(14, 181, 136)});
        set.setStackLabels(new String[]{"男人", "女人"});

        String[] xLabels = new String[]{"0-10", "10-20", "20-30", "30-40", "40-50", "50-60", "60-70", "70-80", "80-90", "90-100", "100+"};

        BarData data = new BarData(set);
        data.setBarWidth(0.1f);
        mHorizontalBarChart.setData(data);
        mHorizontalBarChart.invalidate();
    }
    private void bindToolBar() {
        ButterKnife.bind(this);
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
            Graph2Activity.this.finish();
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