package com.vitalong.inclinometer.inclinometer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.bean.SurveyDataTable;
import com.vitalong.inclinometer.bean.TableRowBean;

import java.io.DataOutput;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Graph2Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    private LineChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY, tvDis, tvSum,t;
    private Spinner spinner;
    private String csvFileName;
    private String csvFilePath;
    private CsvUtil csvUtil;
    private boolean whichMode = true; //false 是Sum   true是Dis
    List<SurveyDataTable> list;
    List<SurveyDataTable> earlyTableList;
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
        list = new ArrayList<>();
        earlyTableList = new ArrayList<>();
//        list = csvUtil.getSurveyListByCsvName(csvFileName);
//        List<SurveyDataTable> selectTableList = csvUtil.getSurveyListByCsvName(csvFileName);
        File csvFile = new File(csvFilePath);
//      String earlyFileName = getEarlyFileName(csvFile);
        List<File> containFiles = FileUtils.getFileListByDirPath(csvFile.getParentFile().getPath(), new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });

        assert containFiles.get(0) == null;
        File earlyFile = containFiles.get(0);
        for (int i = 0; i < containFiles.size(); i++) {
            String earlyTime = earlyFile.getName().split("_")[2].replace(".csv","");
            double earlyDouble = Double.parseDouble(earlyTime);

            String curr =  containFiles.get(i).getName().split("_")[2].replace(".csv","");
            double currDouble = Double.parseDouble(curr);

            if (earlyDouble >= currDouble){
                earlyFile = containFiles.get(i);
            }
        }
        initView(containFiles);//初始化spinner的列表及其它View
//        List<SurveyDataTable> earlyTableList = csvUtil.getSurveyListByCsvName(earlyFile.getName());
        //获取最老的测量数据
        earlyTableList.addAll(csvUtil.getSurveyListByCsvName(earlyFile.getName()));

//        list.addAll(csvUtil.getSurveyListByCsvName(csvFileName));//装填选中的原始数据
//        List<SurveyDataTable> useList = new ArrayList<>(list);
//        for (int i = 0; i < useList.size(); i++) {
//            double selectA0mm = Double.parseDouble(list.get(i).getA0mm());
//            double earlyA0mm = Double.parseDouble(earlyTableList.get(i).getA0mm());
//            double selectA180mm = Double.parseDouble(list.get(i).getA180mm());
//            double earlyA180mm = Double.parseDouble(earlyTableList.get(i).getA180mm());
//            double selectB0mm = Double.parseDouble(list.get(i).getB0mm());
//            double earlyB0mm = Double.parseDouble(earlyTableList.get(i).getB0mm());
//            double selectB180mm = Double.parseDouble(list.get(i).getB180mm());
//            double earlyB180mm = Double.parseDouble(earlyTableList.get(i).getB180mm());
//
//            useList.get(i).setA0mm(String.valueOf(selectA0mm - earlyA0mm));
//            useList.get(i).setA180mm(String.valueOf(selectA180mm - earlyA180mm));
//            useList.get(i).setB0mm(String.valueOf(selectB0mm - earlyB0mm));
//            useList.get(i).setB180mm(String.valueOf(selectB180mm - earlyB180mm));
//        }

//        initChartData(list);
        initChartData(getDrawTableList());
    }

    private List<SurveyDataTable> getDrawTableList(){
        list.clear();
        list.addAll(csvUtil.getSurveyListByCsvName(csvFileName));//装填选中的原始数据
        List<SurveyDataTable> useList = new ArrayList<>(list);
        for (int i = 0; i < useList.size(); i++) {
            double selectA0mm = Double.parseDouble(list.get(i).getA0mm());
            double earlyA0mm = Double.parseDouble(earlyTableList.get(i).getA0mm());
            double selectA180mm = Double.parseDouble(list.get(i).getA180mm());
            double earlyA180mm = Double.parseDouble(earlyTableList.get(i).getA180mm());
            double selectB0mm = Double.parseDouble(list.get(i).getB0mm());
            double earlyB0mm = Double.parseDouble(earlyTableList.get(i).getB0mm());
            double selectB180mm = Double.parseDouble(list.get(i).getB180mm());
            double earlyB180mm = Double.parseDouble(earlyTableList.get(i).getB180mm());

            useList.get(i).setA0mm(String.valueOf(selectA0mm - earlyA0mm));
            useList.get(i).setA180mm(String.valueOf(selectA180mm - earlyA180mm));
            useList.get(i).setB0mm(String.valueOf(selectB0mm - earlyB0mm));
            useList.get(i).setB180mm(String.valueOf(selectB180mm - earlyB180mm));
        }
        if (list.size() != earlyTableList.size()){
            Toast.makeText(Graph2Activity.this,"與最舊csv文档的数据量不匹配",Toast.LENGTH_SHORT).show();
        }
        return useList;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initChartData(List<SurveyDataTable> list) {
        XAxis xl = chart.getXAxis();
        xl.setLabelRotationAngle(270);
        xl.setAvoidFirstLastClipping(true);
//        xl.setAxisMinimum(0);
//        xl.setAxisMaximum(100);
//        xl.setPosition(XAxis.XAxisPosition.TOP);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setInverted(true);
//        leftAxis.setAxisMinimum(-300); // this replaces setStartAtZero(true)
//        leftAxis.setAxisMaximum(300);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        // add data
        seekBarX.setProgress(25);
        seekBarY.setProgress(50);
        Legend l = chart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);
        //装载数据
//        setData(0, 0);
        if (whichMode) {
            setDisData(list);
        } else {
            setData(list);
        }

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView(List<File> containFiles) {

        spinner = findViewById(R.id.spinner);
        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);
        tvDis = findViewById(R.id.tvDis);
        tvSum = findViewById(R.id.tvSum);
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
//        spinner.setAdapter(new Ada);
        List<String> csvNames = new ArrayList<>();
        containFiles.forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                csvNames.add(file.getName());
            }
        });

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Graph2Activity.this, android.R.layout.simple_spinner_item, csvNames);

        int initPosition = csvNames.indexOf(csvFileName);
        spinner.setSelection(initPosition, true);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                list.clear();
//                list.addAll(csvUtil.getSurveyListByCsvName(csvNames.get(position)));
                csvFileName = csvNames.get(position);
                initChartData(getDrawTableList());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvDis.setBackgroundResource(R.color.select_bg);
                tvSum.setBackgroundResource(R.color.white);
                whichMode = true;
                //获取处理后的数据
                initChartData(getDrawTableList());
            }
        });

        tvSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDis.setBackgroundResource(R.color.white);
                tvSum.setBackgroundResource(R.color.select_bg);
                whichMode = false;
                initChartData(list);
            }
        });
    }

    /**
     * set checkSum
     *
     * @param list
     */
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
            Log.e("chenliang", "exception.getMessage:" + exception.getMessage());
            Toast.makeText(Graph2Activity.this, "圖標數據解析出錯", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置Dis的数据
     *
     * @param list
     */
    private void setDisData(List<SurveyDataTable> list) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<Entry> entriesB = new ArrayList<>();
            float maxX = Float.parseFloat(list.get(0).getDepth());
            float minX = Float.parseFloat(list.get(0).getDepth());
            float maxY = (Float.parseFloat(list.get(0).getA0mm()) - Float.parseFloat(list.get(0).getA180mm())) / 2;
            float minY = (Float.parseFloat(list.get(0).getA0mm()) - Float.parseFloat(list.get(0).getA180mm())) / 2;
            for (int i = 0; i < list.size(); i++) {

                float aDisSum = (Float.parseFloat(list.get(i).getA0mm()) - Float.parseFloat(list.get(i).getA180mm())) / 2;
                float bDisSum = (Float.parseFloat(list.get(i).getB0mm()) - Float.parseFloat(list.get(i).getB180mm())) / 2;
                float aYVal = Float.parseFloat(list.get(i).getDepth());
                entries.add(new Entry(aYVal, aDisSum));
                entriesB.add(new Entry(aYVal, bDisSum));
                maxX = Math.max(maxX, aYVal);
                minX = Math.min(minX, aYVal);
                maxY = Math.max(maxY, aDisSum);
                maxY = Math.max(maxY, bDisSum);
                minY = Math.min(minY, aDisSum);
                minY = Math.min(minY, bDisSum);
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
            Log.e("chenliang", "exception.getMessage:" + exception.getMessage());
            Toast.makeText(Graph2Activity.this, "圖標數據解析出錯", Toast.LENGTH_SHORT).show();
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
    }

//        @Override
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