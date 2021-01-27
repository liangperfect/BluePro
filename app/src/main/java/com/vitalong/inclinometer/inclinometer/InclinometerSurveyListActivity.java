package com.vitalong.inclinometer.inclinometer;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.bean.SurveyDataTable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InclinometerSurveyListActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.tvFileName)
    TextView tvFileName;
    String csvFileName;
    String csvFilePath;
    CsvUtil csvUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inclinometer_survey_list);
        bindToolBar();
        makeStatusBar(R.color.black2);
        initData();
    }

    private void initData() {
        csvFileName = getIntent().getStringExtra("csvFileName");
        csvFilePath = getIntent().getStringExtra("csvFilePath");
        csvUtil = new CsvUtil(InclinometerSurveyListActivity.this);
        List<SurveyDataTable> list = csvUtil.getSurveyListByCsvName(csvFileName);
        initView(list);
    }

    private void initView(List<SurveyDataTable> list) {

        tvFileName.setText(csvFileName);
        RecyclerAdapter adapter = new RecyclerAdapter(InclinometerSurveyListActivity.this, list);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(InclinometerSurveyListActivity.this));
        recyclerView.setAdapter(adapter);
    }


    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_search_white_36dp);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<SurveyDataTable> datas;
        private LayoutInflater mInflater;

        public RecyclerAdapter(Context context, List<SurveyDataTable> d) {
            mInflater = LayoutInflater.from(context);
            datas = d;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_inclinometer_survey_data, parent, false);
            return new InclinometerSurveyListActivity.RecyclerAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.tvDepth.setText(datas.get(position).getDepth());
            viewHolder.tvA0.setText(datas.get(position).getA0mm());
            viewHolder.tvA180.setText(datas.get(position).getA180mm());
            viewHolder.tvASum.setText(datas.get(position).getCheckSumA());
            viewHolder.tvB0.setText(datas.get(position).getB0mm());
            viewHolder.tvB180.setText(datas.get(position).getB180mm());
            viewHolder.tvBSum.setText(datas.get(position).getCheckSumB());
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView tvDepth;
            TextView tvA0;
            TextView tvA180;
            TextView tvASum;
            TextView tvB0;
            TextView tvB180;
            TextView tvBSum;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);

                tvDepth = itemView.findViewById(R.id.tvDepth);
                tvA0 = itemView.findViewById(R.id.tvA0);
                tvA180 = itemView.findViewById(R.id.tvA180);
                tvASum = itemView.findViewById(R.id.tvASum);
                tvB0 = itemView.findViewById(R.id.tvB0);
                tvB180 = itemView.findViewById(R.id.tvB180);
                tvBSum = itemView.findViewById(R.id.tvBSum);
            }
        }
    }
}