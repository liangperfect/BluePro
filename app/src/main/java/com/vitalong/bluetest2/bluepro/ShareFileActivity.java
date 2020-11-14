package com.vitalong.bluetest2.bluepro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.bluetest2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShareFileActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvNums)
    TextView tvNums;
    @Bind(R.id.recyclerViewFiles)
    RecyclerView recyclerViewFiles;
    private List<File> cachedFiles;
    private static final String[] NARMAL_PHONE = {"com.android.email", "com.android.email.activity.MessageCompose"};
    private static final String[] MIUI_PHONE = {"com.android.email", "com.kingsoft.mail.compose.ComposeActivity"};
    private static final String[] SAMSUNG_PHONE = {"com.samsung.android.email.provider", "com.samsung.android.email.composer.activity.MessageCompose"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_file);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        bindToolBar();
        makeStatusBar(R.color.white);
        initFiles();
    }

    private void initFiles() {
        cachedFiles = new ArrayList<File>();
        String dirPath = "sdcard/tiltmeter/";//存储的路径
        File dir = new File(dirPath);
        if (dir.listFiles() != null) {
            for (int i = 0; i < Objects.requireNonNull(dir.listFiles()).length; i++) {

                if (dir.listFiles()[i].isDirectory()) {
                    cachedFiles.add(dir.listFiles()[i].listFiles()[0]);//默认只会按时间存一个文件的
                }
            }
        }
        tvNums.setText(String.valueOf(cachedFiles.size()));
        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(ShareFileActivity.this));
        recyclerViewFiles.setItemAnimator(null);
        recyclerViewFiles.setAdapter(new FilesAdapter(ShareFileActivity.this, cachedFiles));
    }

    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            ShareFileActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent(File file) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        if (getDeviceBrand().toUpperCase().contains("HONOR") || getDeviceBrand().toUpperCase().contains("HUAWEI") || getDeviceBrand().toUpperCase().contains("NUBIA")) {
            intent.setClassName(NARMAL_PHONE[0], NARMAL_PHONE[1]);
        } else if (getDeviceBrand().toUpperCase().contains("XIAOMI") || getDeviceBrand().toUpperCase().contains("XIAOMI")) {
            intent.setClassName(MIUI_PHONE[0], MIUI_PHONE[1]);
        } else if (getDeviceBrand().toUpperCase().contains("SAMSUNG")) {
            intent.setClassName(SAMSUNG_PHONE[0], SAMSUNG_PHONE[1]);
        }
        return intent;
    }

    public String getDeviceBrand() {
        Log.e("--获取手机厂商--:", android.os.Build.BRAND);
        return android.os.Build.BRAND;
    }

    class FilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater mLayoutInflater;
        private List<File> datas;

        public FilesAdapter(Context context, List<File> files) {
            mLayoutInflater = LayoutInflater.from(context);
            datas = files;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.item_file, parent, false);
            return new FileItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            FileItemViewHolder holder1 = (FileItemViewHolder) holder;
            holder1.tvFileName.setText("FileName:" + datas.get(position).getName());
            holder1.constraLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按进行分享
//                    String filePath = "sdcard/test.txt";
//                    File file = new File(filePath);
//                    Intent intent = getShareIntent(datas.get(position));
//                    startActivity(Intent.createChooser(intent, "分享一下"));

                    ArrayList<Uri> files = new ArrayList<Uri>();
                    files.add(Uri.fromFile(datas.get(position)));
                    //分享文件
                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);//发送多个文件
                    intent.setType("*/*");//多个文件格式
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);//Intent.EXTRA_STREAM同于传输文件流
                    startActivity(intent);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return cachedFiles.size();
        }

        class FileItemViewHolder extends RecyclerView.ViewHolder {

            ConstraintLayout constraLayout;
            ImageView imageView;
            TextView tvFileName;

            public FileItemViewHolder(@NonNull View itemView) {
                super(itemView);
                constraLayout = itemView.findViewById(R.id.constraLayout);
                imageView = itemView.findViewById(R.id.img);
                tvFileName = itemView.findViewById(R.id.tvFileName);
            }
        }
    }
}
