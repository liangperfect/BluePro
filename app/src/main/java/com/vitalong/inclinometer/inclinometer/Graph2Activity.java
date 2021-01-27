package com.vitalong.inclinometer.inclinometer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.bean.SurveyDataTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Graph2Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    private LineChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    private Spinner spinner;
    String csvFileName;
    String csvFilePath;
    CsvUtil csvUtil;
    List<SurveyDataTable> list;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph2);
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initData() {

        csvFileName = getIntent().getStringExtra("csvFileName");
        csvFilePath = getIntent().getStringExtra("csvFilePath");
        csvUtil = new CsvUtil(Graph2Activity.this);
        list = csvUtil.getSurveyListByCsvName(csvFileName);
        initView();
        initChartData(list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initChartData(List<SurveyDataTable> list) {
        XAxis xl = chart.getXAxis();
        xl.setLabelRotationAngle(270);
        xl.setAvoidFirstLastClipping(true);
//        xl.setAxisMinimum(0);
//        xl.setAxisMaximum(100);
        xl.setPosition(XAxis.XAxisPosition.TOP);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setInverted(true);
//        leftAxis.setAxisMinimum(-300); // this replaces setStartAtZero(true)
//        leftAxis.setAxisMaximum(300);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        // add data
        seekBarX.setProgress(25);
        seekBarY.setProgress(50);
        // // restrain the maximum scale-out factor
        // chart.setScaleMinima(3f, 3f);
        //
        // // center the view to a specific position inside the chart
        // chart.centerViewPort(10, 50);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);
        //装载数据
//        setData(0, 0);
        setData(list);
        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void initView() {

        spinner = findViewById(R.id.spinner);
        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);
        seekBarX = findViewById(R.id.seekBar1);
        seekBarY = findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);
        seekBarX.setOnSeekBarChangeListener(this);
        chart = findViewById(R.id.chart1);
        // ************** init chartListener start **************
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        // no description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // ************** init chartListener end **************
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setData(int count, float range) {

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            float xVal = new Random().nextInt(300);
            float yVal = (float) i;
            entries.add(new Entry(yVal, xVal));
        }

        ArrayList<Entry> entriesB = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            float xVal = -new Random().nextInt(300);
            float yVal = (float) i;
            entriesB.add(new Entry(yVal, xVal));
        }
        // sort by x-value
        Collections.sort(entries, new EntryXComparator());
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(entries, "A");
//        set1.setColor(Color.RED);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        LineDataSet setB = new LineDataSet(entriesB, "B");
        setB.setColor(Color.BLUE);
        setB.setCircleColor(Color.BLUE);

        set1.setLineWidth(1.5f);
        set1.setCircleRadius(4f);
        // create a data object with the data sets
        LineData data = new LineData(set1, setB);
        // set data
        chart.setData(data);
    }


    private void setData(List<SurveyDataTable> list) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<Entry> entriesB = new ArrayList<>();
            float maxX = Float.parseFloat(list.get(0).getDepth());
            float minX = Float.parseFloat(list.get(0).getDepth());
            float maxY = Float.parseFloat(list.get(0).getCheckSumA());
            float minY = Float.parseFloat(list.get(0).getCheckSumA());
            for (int i = 0; i < list.size(); i++) {

                float aSum = Float.parseFloat(list.get(i).getCheckSumA());
                float bSum = Float.parseFloat(list.get(i).getCheckSumB());
                float aYVal = Float.parseFloat(list.get(i).getDepth());
                entries.add(new Entry(aYVal, aSum));
                entriesB.add(new Entry(aYVal, bSum));
                maxX = Math.max(maxX, aYVal);
                minX = Math.min(minX, aYVal);
                maxY = Math.max(maxY, aSum);
                maxY = Math.max(maxY, bSum);
                minY = Math.min(minY, aSum);
                minY = Math.min(minY, bSum);
            }
            int YDuration = (int) Math.max(Math.abs(maxY), Math.abs(minY)) + 10;

            XAxis xl = chart.getXAxis();
            xl.setAxisMinimum((int) minX);
            xl.setAxisMaximum((int) maxX);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setInverted(true);
            leftAxis.setAxisMinimum(-YDuration); // this replaces setStartAtZero(true)
            leftAxis.setAxisMaximum(YDuration);
            Collections.sort(entries, new EntryXComparator());
            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(entries, "A");
//        set1.setColor(Color.RED);
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);
            LineDataSet setB = new LineDataSet(entriesB, "B");
            setB.setColor(Color.BLUE);
            setB.setCircleColor(Color.BLUE);

            set1.setLineWidth(1.5f);
            set1.setCircleRadius(4f);
            // create a data object with the data sets
            LineData data = new LineData(set1, setB);
            // set data
            chart.setData(data);
        } catch (Exception exception) {

            Toast.makeText(Graph2Activity.this, "沒有圖表數據", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

//        setData(seekBarX.getProgress(), seekBarY.getProgress());
//
//        // redraw
//        chart.invalidate();
    }

    //    @Override
//    protected void saveToGallery() {
//        saveToGallery(chart, "InvertedLineChartActivity");
//    saveToGallery();
//    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}