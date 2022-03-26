package com.vitalong.bluetest2.bluepro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.bean.RealDataCached;
import com.vitalong.bluetest2.bean.TableRowBean;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.RealDataCachedDao;

import net.ozaydin.serkan.easy_csv.FileCallback;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ManualMergeCsvActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.niceSpinner)
    NiceSpinner niceSpinner;
    @Bind(R.id.recyclerViewMerge)
    RecyclerView recyclerView;
    @Bind(R.id.btnCreate)
    Button btnCreate;
    private EasyCsvCopy easyCsv;
    private String siteName;
    private List<String> csvFileNames;
    private List<String> holeNames;
    private HashMap<String, List<RealDataCached>> dataMap;
    private List<RealDataCached> recyclerViewDatas;
    private RealDataCachedDao realDataCachedDao;
    private int defaultValue = 0;
    private VerifyDataBean verifyDataBean;//矫正参数
    private boolean hasRange;
    private boolean hasGF;
    private boolean hasIR;
    private boolean hasDeg;
    private boolean hasIncline;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_merge_csv);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        siteName = getIntent().getStringExtra("siteName");
        hasRange = getIntent().getBooleanExtra("hasRange", true);
        hasGF = getIntent().getBooleanExtra("hasGF", true);
        hasIR = getIntent().getBooleanExtra("hasIR", true);
        hasDeg = getIntent().getBooleanExtra("hasDeg", true);
        hasIncline = getIntent().getBooleanExtra("hasIncline", true);
        csvFileNames = (List<String>) getIntent().getSerializableExtra("holes");
        verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
        realDataCachedDao = ((MyApplication) getApplication()).realDataCachedDao;
        holeNames = new ArrayList<String>();
        recyclerViewDatas = new ArrayList<>();
        easyCsv = new EasyCsvCopy(ManualMergeCsvActivity.this);
        bindToolBar();
        makeStatusBar(R.color.white);
        initData();
        initListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initData() {
        dataMap = new HashMap<>();
        csvFileNames.forEach(s -> holeNames.add(s.split("_")[1]));
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, holeNames);  //创建一个数组适配器
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
//        spinner.setAdapter(adapter);
        niceSpinner.attachDataSource(holeNames);
        for (String str : holeNames) {
            List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(siteName + "_" + str)).build().list();
            //进来后将选中的状态重置为未选中状态
            for (RealDataCached item : listDatas) {
                item.setChecked(false);
            }
            dataMap.put(str, listDatas);
        }
        initDBDatas(siteName, holeNames.get(0));
    }

    private void initDBDatas(String siteName, String holeName) {
        recyclerViewDatas.clear();
        recyclerViewDatas.addAll(Objects.requireNonNull(dataMap.get(holeName)));
//        List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(siteName + "_" + holeName)).build().list();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        recyclerView.setAdapter(new RecyclerAdapter(ManualMergeCsvActivity.this, recyclerViewDatas));
    }

    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.black));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        }
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initListener() {
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == defaultValue) {
//                    //避免初始化spinner的时候onitemSelected被执行了
//                    defaultValue = -1;
//                    return;
//                }
//                String str = holeNames.get(position);
//                recyclerView.setAdapter(new RecyclerAdapter(ManualMergeCsvActivity.this, dataMap.get(str)));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String str = holeNames.get(position);
                recyclerView.setAdapter(new RecyclerAdapter(ManualMergeCsvActivity.this, dataMap.get(str)));
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                SaveMergeCsvData();

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualMergeCsvActivity.this.finish();
            }
        });
    }

    /**
     * 将选中的数据合并到保存到csv文件中去
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SaveMergeCsvData() {
        String snValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = sdf.format(d);
        final List<String> collection = new ArrayList<>();
        collection.add(new TableRowBean("Type:", "Digital Tiltmeter", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Model:", "6600D", "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Serial Number:", snValue, "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Site:", siteName, "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Date:", currTime, "", "", "", "", "", "", "").toSaveString());
        collection.add(new TableRowBean("Compare:", "", "", "", "", "", "", "", "").toSaveString());
        if (hasRange) {
            collection.add(new TableRowBean("Range:", "30 Deg", "", "", "", "", "", "", "").toSaveString());
        }
        if (hasGF) {
            collection.add(new TableRowBean("Gage Factor(A):", "A1=" + verifyDataBean.getAaxisA(), "A2=" + verifyDataBean.getAaxisB(), "A3=" + verifyDataBean.getAaxisC(), "A4=" + verifyDataBean.getAaxisD(), "", "", "", "").toSaveString());
            collection.add(new TableRowBean("Gage Factor(B):", "B1=" + verifyDataBean.getBaxisA(), "B2=" + verifyDataBean.getBaxisB(), "B3=" + verifyDataBean.getBaxisC(), "B4=" + verifyDataBean.getBaxisD(), "", "", "", "").toSaveString());
        }
//        collection.add(new TableRowBean("Range:", "30 Deg", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Communication:", "Bluetooth 4.2", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Firmware:", "v1.2", "Software:", "v2.0", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Units:", "Raw / Deg", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Gage Factor(A):", "A1=" + verifyDataBean.getAaxisA(), "A2=" + verifyDataBean.getAaxisB(), "A3=" + verifyDataBean.getAaxisC(), "A4=" + verifyDataBean.getAaxisD(), "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("Gage Factor(B):", "B1=" + verifyDataBean.getBaxisA(), "B2=" + verifyDataBean.getBaxisB(), "B3=" + verifyDataBean.getBaxisC(), "B4=" + verifyDataBean.getBaxisD(), "", "", "", "").toSaveString());
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toSaveString());
//        collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "", "", "", "").toSaveString());
        if (hasDeg && hasIncline) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "Deg", "Deg", "incline('' '')", "").toSaveString());
        } else if (hasDeg) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "Deg", "Deg", "", "").toSaveString());
        } else if (hasIncline) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "incline('' '')", "", "", "").toSaveString());
        } else {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "", "", "", "").toSaveString());
        }
        for (String key : dataMap.keySet()) {
            for (int index = 0; index < dataMap.get(key).size(); index = index + 2) {
                RealDataCached cached13 = dataMap.get(key).get(index);
                RealDataCached cached24 = dataMap.get(key).get(index + 1);
                if (cached13.isChecked()) {
                    String degFirst13 = hasDeg ? cached13.getDegFirst() : "";
                    String degSecond13 = hasDeg ? cached13.getDegSecond() : "";
                    String degFirst24 = hasDeg ? cached24.getDegSecond() : "";
                    String degSecond24 = hasDeg ? cached24.getDegSecond() : "";
                    String incline13 = hasIncline ? cached13.getInclude() : "";
                    String incline24 = hasIncline ? cached24.getInclude() : "";
                    if (hasDeg && hasIncline) {
                        collection.add(new TableRowBean(key, cached13.getTime(), cached13.getDirection(), cached13.getRawFirst(), cached13.getRawSecond(), degFirst13, degSecond13, incline13, "").toSaveString());
                        collection.add(new TableRowBean("", cached24.getTime(), cached24.getDirection(), cached24.getRawFirst(), cached24.getRawSecond(), degFirst24, degSecond24, incline24, "").toSaveString());
                    } else if (hasDeg) {
                        collection.add(new TableRowBean(key, cached13.getTime(), cached13.getDirection(), cached13.getRawFirst(), cached13.getRawSecond(), degFirst13, degSecond13, "", "").toSaveString());
                        collection.add(new TableRowBean("", cached24.getTime(), cached24.getDirection(), cached24.getRawFirst(), cached24.getRawSecond(), degFirst24, degSecond24, "", "").toSaveString());
                    } else if (hasIncline) {
                        collection.add(new TableRowBean(key, cached13.getTime(), cached13.getDirection(), cached13.getRawFirst(), cached13.getRawSecond(), incline13, "", "", "").toSaveString());
                        collection.add(new TableRowBean("", cached24.getTime(), cached24.getDirection(), cached24.getRawFirst(), cached24.getRawSecond(), incline24, "", "", "").toSaveString());
                    } else {
                        collection.add(new TableRowBean(key, cached13.getTime(), cached13.getDirection(), cached13.getRawFirst(), cached13.getRawSecond(), "", "", "", "").toSaveString());
                        collection.add(new TableRowBean("", cached24.getTime(), cached24.getDirection(), cached24.getRawFirst(), cached24.getRawSecond(), "", "", "", "").toSaveString());
                    }
                }
            }
        }

        String saveFileStr = "tiltmeter/" + siteName + "/" + siteName + "_" + "TI_" + currTime;
        easyCsv.setSeparatorColumn(",");//列分隔符
        easyCsv.setSeperatorLine("/n");//行分隔符
        List<String> headerList = new ArrayList<>();
        easyCsv.createCsvFile(saveFileStr, headerList, collection, 1, new FileCallback() {

            @Override
            public void onSuccess(File file) {

//                Log.d("chenliang", "file:" + file.getPath());
//                Toast.makeText(ManualMergeCsvActivity.this, file.getName() + "合并成功", Toast.LENGTH_SHORT).show();
                showShareDialog(file);
            }

            @Override
            public void onFail(String s) {

            }
        });
    }

    public void showShareDialog(File file) {
        new AlertDialog.Builder(ManualMergeCsvActivity.this)
                .setTitle("Csv合并成功")
                .setMessage("是否分享該" + file.getName())
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Uri> files = new ArrayList<Uri>();
                        files.add(Uri.fromFile(file));
                        //分享文件
                        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);//发送多个文件
                        intent.setType("*/*");//多个文件格式
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);//Intent.EXTRA_STREAM同于传输文件流
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).show();
    }

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<RealDataCached> datas;
        private LayoutInflater mInflater;

        public RecyclerAdapter(Context context, List<RealDataCached> d) {
            mInflater = LayoutInflater.from(context);
            datas = d;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_merge_csv_data, parent, false);
            return new ManualMergeCsvActivity.RecyclerAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ManualMergeCsvActivity.RecyclerAdapter.ItemViewHolder itemViewHolder = (ManualMergeCsvActivity.RecyclerAdapter.ItemViewHolder) holder;
            itemViewHolder.tvColumn1.setText(datas.get(position).getTime());
            itemViewHolder.tvColumn2.setText(datas.get(position).getDirection());
            itemViewHolder.tvColumn3.setText(datas.get(position).getRawFirst());
            itemViewHolder.tvColumn4.setText(datas.get(position).getRawSecond());
            if (position % 2 == 0) {
                itemViewHolder.imgState.setVisibility(View.VISIBLE);
                itemViewHolder.imgState.setImageResource(datas.get(position).getIsChecked() ? R.drawable.check_box_selected : R.drawable.check_box_unselected);
                itemViewHolder.cardView.setOnClickListener(v -> {

                    datas.get(position).setChecked(!datas.get(position).isChecked());//1-3
                    datas.get(position + 1).setChecked(datas.get(position).isChecked());//2-4
                    itemViewHolder.imgState.setImageResource(datas.get(position).getIsChecked() ? R.drawable.check_box_selected : R.drawable.check_box_unselected);
                });
            }
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            ConstraintLayout cardView;
            ImageView imgState;
            TextView tvColumn1;
            TextView tvColumn2;
            TextView tvColumn3;
            TextView tvColumn4;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardView);
                imgState = itemView.findViewById(R.id.img);
                tvColumn1 = itemView.findViewById(R.id.tvTimeValue);
                tvColumn2 = itemView.findViewById(R.id.tvDirectionValue);
                tvColumn3 = itemView.findViewById(R.id.tvRaw1Value);
                tvColumn4 = itemView.findViewById(R.id.tvRaw2Value);
            }
        }
    }
}