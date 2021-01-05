package com.vitalong.bluetest2.bluepro;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.bean.HoleBean;
import com.vitalong.bluetest2.views.CompanySelectDialog;
import com.vitalong.bluetest2.views.HoleMultiSelectDialog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MeregParametersActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btnSiteValue)
    Button btnSiteValue;
    @Bind(R.id.btnCsvModeValue)
    Button btnCsvModeValue;
    @Bind(R.id.imgAddHold)
    ImageView imgAddHold;
    @Bind(R.id.tvShowHoles)
    TextView tvShowHoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mereg_parameters);
        bindToolBar();
        makeStatusBar(R.color.white);
        initData();
        initListener();
    }


    private void initData() {

    }

    protected void bindToolBar() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationIcon(R.mipmap.ic_search_white_36dp);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            MeregParametersActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initListener() {

        btnSiteValue.setOnClickListener(v -> {

            List<File> list = FileUtils.getFileListByDirPath(Constants.PRO_ROOT_PATH, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            });
            CompanySelectDialog companySelectDialog = null;
            companySelectDialog = new CompanySelectDialog(MeregParametersActivity.this, list, "工地選擇", new CompanySelectDialog.ChangeComapngeListener<File>() {
                @Override
                public void changeComapny(File file) {
                    btnSiteValue.setText(file.getName());
                }
            });
            companySelectDialog.show();
        });

        btnCsvModeValue.setOnClickListener(v -> {
            List<String> csvModes = new ArrayList<String>() {
                {
                    add("所有測量時間");
                    add("最新一筆測量時間");
                    add("手動選擇");
                }
            };
            CompanySelectDialog<String> companySelectDialog = null;
            companySelectDialog = new CompanySelectDialog(MeregParametersActivity.this, csvModes, "模式選擇", new CompanySelectDialog.ChangeComapngeListener<String>() {
                @Override
                public void changeComapny(String modeName) {

                    btnCsvModeValue.setText(modeName);
                }
            });
            companySelectDialog.show();
        });

        imgAddHold.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!btnSiteValue.getText().toString().isEmpty()) {
                    HoleMultiSelectDialog holeMultiSelectDialog;
                    String siteName = btnSiteValue.getText().toString();
                    List<File> csvFileList = FileUtils.getFileListByDirPath(Constants.PRO_ROOT_PATH + "/" + siteName, new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isFile();
                        }
                    });
                    List<HoleBean> holeBeans = new ArrayList<>();
                    csvFileList.forEach(file -> {
                        holeBeans.add(new HoleBean(file.getName(), false));
                    });

                    holeMultiSelectDialog = new HoleMultiSelectDialog(MeregParametersActivity.this, holeBeans, "孔號選擇", new HoleMultiSelectDialog.ChangeHoleListener() {
                        @Override
                        public void selectHole(HoleBean hole) {

                        }

                        @Override
                        public void selectMultiHoles(List<HoleBean> holes) {
                            StringBuilder holesStr = new StringBuilder();
                            for (int i = 0; i < holes.size(); i++) {
                                holesStr.append(i).append("、").append(holes.get(i).getHoleName()).append("\n");
                            }
                            tvShowHoles.setText(holesStr);
                        }
                    });
                    holeMultiSelectDialog.show();
                } else {

                    Toast.makeText(MeregParametersActivity.this, "請先選擇工地", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}