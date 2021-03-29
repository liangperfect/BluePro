package com.vitalong.inclinometer.bluepro;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.vitalong.inclinometer.MyApplication;
import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.bean.RealDataCached;
import com.vitalong.inclinometer.greendaodb.RealDataCachedDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GraphActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.chart1)
    LineChart chart;
    @Bind(R.id.btn13)
    Button btn13;
    @Bind(R.id.btn24)
    Button btn24;
    @Bind(R.id.btnwhole)
    Button btnwhole;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    MyApplication application;
    private String tableName = "A001_T01"; //默认表单是A001-T01
    List<RealDataCached> listDatas13; //direction1-3的缓存数据
    List<RealDataCached> listDatas24; //direction2-4的缓存数据
    private LineDataSet set13;
    private LineDataSet set24;
    private boolean isShow13 = true;
    private boolean isShow24 = true;
    private boolean isWhole = false;
    private int regionNums = 8;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        application = (MyApplication) getApplication();
        tableName = getIntent().getStringExtra("tableName");
        bindToolBar();
        makeStatusBar(R.color.white);
        initCachedData();
        initGraph();
        initListener();
    }

    private void initListener() {

        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isShow13 = !isShow13;
                set13.setVisible(isShow13);
                chart.getData().notifyDataChanged();
//                chart.getLineData().getDataSetByIndex(0).notify();
                chart.invalidate();
                if (isShow13) {
                    btn13.setBackgroundResource(R.color.red);
                } else {
                    btn13.setBackgroundResource(R.color.grey);
                }
            }
        });


        btn24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow24 = !isShow24;
                set24.setVisible(isShow24);
                chart.getData().notifyDataChanged();
//                chart.getLineData().getDataSetByIndex(1).notify();
                chart.invalidate();
                if (isShow24) {
                    btn24.setBackgroundResource(R.color.blue);
                } else {
                    btn24.setBackgroundResource(R.color.grey);
                }
            }
        });

        btnwhole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isWhole = !isWhole;
//                if (isWhole){
//                    chart.getXAxis().resetAxisMaximum();
//                    chart.invalidate();
//                }else{
//                    chart.getXAxis().setAxisMaximum(listDatas13.size() + 149);
//                    chart.setVisibleXRange(0, 8);
//                    chart.invalidate();
//                }
            }
        });
    }

    private void initGraph() {
        chartSetData();
        initChartProps();
        initAxisProps();
//        setData(45, 180);
        // draw points over time
        chart.animateX(500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
        // draw legend entries as lines
//        l.setForm(Legend.LegendForm.LINE);
    }

    private void chartSetData() {

        ArrayList<Entry> values13 = new ArrayList<>();
        ArrayList<Entry> values24 = new ArrayList<>();

        for (int i = 0; i < listDatas13.size(); i++) {

            double yDouble = Double.valueOf(listDatas13.get(i).getInclude());
            values13.add(new Entry(i, (int) yDouble, listDatas13.get(i)));
//            float val = (float) (Math.random() * 30) - 30;
//            values13.add(new Entry(i, val));
        }

        for (int j = 0; j < listDatas24.size(); j++) {

            double yDouble = Double.valueOf(listDatas24.get(j).getInclude());
            values24.add(new Entry(j, (int) yDouble, listDatas24.get(j)));
//            float val = (float) (Math.random() * 15) - 30;
//            values24.add(new Entry(j, val));
        }

        //1-3的线
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set13 = (LineDataSet) chart.getData().getDataSetByIndex(0); //第一条线的索引是0
            set13.setValues(values13);
            set13.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set13 = new LineDataSet(values13, "DataSet 1");

            set13.setDrawIcons(false);
            set13.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            // draw dashed line 虚线的设置
//            set13.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set13.setColor(Color.RED);
            set13.setCircleColor(Color.RED);

            // line thickness and point size
            set13.setLineWidth(1f);
            set13.setCircleRadius(3f);

            // draw points as solid circles
            set13.setDrawCircleHole(false);

            // customize legend entry
            set13.setFormLineWidth(1f);
            set13.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set13.setFormSize(15.f);

            // text size of values13
            set13.setValueTextSize(9f);

            // draw selection line as dashed
            set13.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set13.setDrawFilled(true);
            set13.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set13.setFillDrawable(drawable);
            } else {
                set13.setFillColor(Color.BLACK);
            }
        }

        //2-4的线
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set24 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set24.setValues(values24);
            set24.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set24 = new LineDataSet(values24, "DataSet 2");

            set24.setDrawIcons(false);
            set24.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            // draw dashed line 虚线的设置
//            set24.enableDashedLine(10f, 5f, 0f);

            // black lines and points
//            set24.setColor(Color.BLUE);
//            set24.setCircleColor(Color.BLUE);
            set24.setColor(0xFF2EA2E4);
            set24.setCircleColor(0xFF2EA2E4);

            // line thickness and point size
            set24.setLineWidth(1f);
            set24.setCircleRadius(3f);

            // draw points as solid circles
            set24.setDrawCircleHole(false);

            // customize legend entry
            set24.setFormLineWidth(1f);
            set24.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set24.setFormSize(15.f);

            // text size of values13
            set24.setValueTextSize(9f);

            // draw selection line as dashed
            set24.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set24.setDrawFilled(true);
            set24.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                set24.setFillDrawable(drawable);
            } else {
                set24.setFillColor(Color.BLUE);
            }
        }
        //合并图表数据
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set13);
        dataSets.add(set24);
        LineData data = new LineData(dataSets);
        // set data
        chart.setData(data);
    }

    /**
     * 设置坐标轴属性
     */
    private void initAxisProps() {

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
            // vertical grid lines
//            xAxis.setValueFormatter(new ValueFormatter() {
//                @Override
//                public String getAxisLabel(float value, AxisBase axis) {
////                    return super.getAxisLabel(value, axis);
//                    Date date = new Date();
//                    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    return ft.format(date);
//                }
//            });
            xAxis.setGranularity(1);
            xAxis.setGranularityEnabled(true);

//            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setAvoidFirstLastClipping(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelRotationAngle(45);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    if (value < 0) {
                        value = 0;
                    }
                    if (listDatas13.size() > regionNums) {
                        return listDatas13.get((int) value).getTime();
                    } else if ((int) value < listDatas13.size()) {
                        return listDatas13.get((int) value).getTime();
                    }
                    return "";
                }
            });
//            xl.setAxisMinimum(0f);
            //必须这个顺序去设置
//            xAxis.setGridLineWidth(0.5f);
//            xAxis.setGridColor(Color.parseColor("#9E9E9E"));
            //注意顺序，全屏铺满
            xAxis.setAxisMinimum(0f);
            xAxis.setAxisMaximum(listDatas13.size() - 1);
            chart.setVisibleXRange(0, 8);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // yaxis range
//            yAxis.setAxisMaximum(200f);
//            yAxis.setAxisMinimum(-50f);
        }
    }

    /**
     * 图表初始化,chart
     */
    private void initChartProps() {

        //图表初始化
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
//        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        // enable scaling and dragging
        chart.setDragEnabled(true);
//            chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);
    }

    private void initCachedData() {

        //最多展示100条数据
        listDatas13 = application.realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(tableName), RealDataCachedDao.Properties.Direction.eq("(1-3)")).limit(100).build().list();
        listDatas24 = application.realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(tableName), RealDataCachedDao.Properties.Direction.eq("(2-4)")).limit(100).build().list();
    }

    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
        tvTitle.setText(tableName);
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
            GraphActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
