package com.vitalong.bluetest2.bluepro;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.bean.RealDataCached;
import com.vitalong.bluetest2.greendaodb.RealDataCachedDao;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SurveyListActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.tvSheetName)
    TextView tvTableName;
    MyApplication application;
    private String tableName = "A001_T01"; //默认表单是A001-T01

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        tableName = getIntent().getStringExtra("tableName");
        bindToolBar();
        makeStatusBar(R.color.white);
        application = (MyApplication) getApplication();
        initView();
        if (savedInstanceState == null) {
            initSurveyData();
        }
    }


    private void initView() {
        tvTableName.setText(tableName);
        //获取数据库里面的数据
        List<RealDataCached> listDatas = application.realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(tableName)).build().list();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        recyclerView.setAdapter(new RecyclerAdapter(SurveyListActivity.this, listDatas));
    }

    private void initSurveyData() {


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

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<RealDataCached> datas;
        private LayoutInflater mInflater;

        public RecyclerAdapter(Context context, List<RealDataCached> d) {
            mInflater = LayoutInflater.from(context);
            datas = d;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_survey_data, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.tvColumn1.setText(datas.get(position).getTime());
            itemViewHolder.tvColumn2.setText(datas.get(position).getDirection());
            itemViewHolder.tvColumn3.setText(datas.get(position).getRawFirst());
            itemViewHolder.tvColumn4.setText(datas.get(position).getRawSecond());
            itemViewHolder.tvColumn5.setText(datas.get(position).getInclude());
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView tvColumn1;
            TextView tvColumn2;
            TextView tvColumn3;
            TextView tvColumn4;
            TextView tvColumn5;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                tvColumn1 = itemView.findViewById(R.id.tvTimeValue);
                tvColumn2 = itemView.findViewById(R.id.tvDirectionValue);
                tvColumn3 = itemView.findViewById(R.id.tvRaw1Value);
                tvColumn4 = itemView.findViewById(R.id.tvRaw2Value);
                tvColumn5 = itemView.findViewById(R.id.tvIncludeValue);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            SurveyListActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
