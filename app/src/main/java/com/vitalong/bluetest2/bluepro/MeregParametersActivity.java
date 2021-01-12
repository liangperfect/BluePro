package com.vitalong.bluetest2.bluepro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.vitalong.bluetest2.MyApplication;
import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.SharedPreferencesUtil;
import com.vitalong.bluetest2.bean.HoleBean;
import com.vitalong.bluetest2.bean.RealDataCached;
import com.vitalong.bluetest2.bean.TableRowBean;
import com.vitalong.bluetest2.bean.VerifyDataBean;
import com.vitalong.bluetest2.greendaodb.RealDataCachedDao;
import com.vitalong.bluetest2.views.CompanySelectDialog;
import com.vitalong.bluetest2.views.HoleMultiSelectDialog;

import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.checkRange)
    CheckBox checkRange;
    @Bind(R.id.checkGF)
    CheckBox checkGF;
    @Bind(R.id.checkIR)
    CheckBox checkIR;
    @Bind(R.id.checkDeg)
    CheckBox checkDeg;
    @Bind(R.id.checkIncline)
    CheckBox checkIncline;

    private VerifyDataBean verifyDataBean;
    private EasyCsvCopy easyCsv;
    private RealDataCachedDao realDataCachedDao;
    private int currCreateCsvMode = Constants.CREATE_CSV_ALL_TIME;
    private List<String> holeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mereg_parameters);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        realDataCachedDao = ((MyApplication) getApplication()).realDataCachedDao;
        verifyDataBean = ((MyApplication) getApplication()).getVerifyDataBean();
        easyCsv = new EasyCsvCopy(MeregParametersActivity.this);
        bindToolBar();
        makeStatusBar(R.color.white);
        initListener();
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
            CompanySelectDialog<File> companySelectDialog = null;
            companySelectDialog = new CompanySelectDialog<File>(MeregParametersActivity.this, list, "工地選擇", new CompanySelectDialog.ChangeComapngeListener<File>() {
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
                    switch (modeName) {
                        case "所有測量時間":
                            currCreateCsvMode = Constants.CREATE_CSV_ALL_TIME;
                            btnNext.setText("CREATE Csv");
                            break;
                        case "最新一筆測量時間":
                            currCreateCsvMode = Constants.CREATE_CSV_LAST_TIME;
                            btnNext.setText("CREATE Csv");
                            break;
                        case "手動選擇":
                            currCreateCsvMode = Constants.CREATE_CSV_Manual;
                            btnNext.setText("NEXT STEP");
                            break;
                        default:
                            //nothing todo
                            break;
                    }
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
                            return file.isFile() && !file.getName().contains("TI");
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
                            holeList.clear();
                            StringBuilder holesStr = new StringBuilder();
                            for (int i = 0; i < holes.size(); i++) {
                                holeList.add(holes.get(i).getHoleName());
                                holesStr.append(i).append(". ").append(holes.get(i).getHoleName()).append("\n");
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

        btnNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if (verify()) {

                    switch (currCreateCsvMode) {
                        case Constants.CREATE_CSV_ALL_TIME:
                            //直接生成
                            saveCsvAllTimeOrLast(true);
                            break;
                        case Constants.CREATE_CSV_LAST_TIME:
                            //直接生成
                            saveCsvAllTimeOrLast(false);
                            break;
                        case Constants.CREATE_CSV_Manual:
                            //将参数传给下一张界面
                            //holeList 选择的孔号
                            String siteName = btnSiteValue.getText().toString();
                            Intent i = new Intent(MeregParametersActivity.this, ManualMergeCsvActivity.class);
                            boolean hasRange = checkRange.isChecked();
                            boolean hasGF = checkGF.isChecked();
                            boolean hasIR = checkIR.isChecked();
                            boolean hasDeg = checkDeg.isChecked();
                            boolean hasIncline = checkIncline.isChecked();
                            i.putExtra("siteName", siteName);
                            i.putExtra("holes", (Serializable) holeList);
                            i.putExtra("hasRange", hasRange);
                            i.putExtra("hasGF", hasGF);
                            i.putExtra("hasIR", hasIR);
                            i.putExtra("hasDeg", hasDeg);
                            i.putExtra("hasIncline", hasIncline);
                            startActivity(i);
                            break;
                        default:
                            //nothing todo
                            break;
                    }
                }
            }
        });
    }

    private boolean verify() {

        String siteStr = btnSiteValue.getText().toString();
        String holeStr = tvShowHoles.getText().toString();
        if (siteStr.isEmpty()) {
            Toast.makeText(MeregParametersActivity.this, "請選擇工地號", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (holeStr.isEmpty()) {
            Toast.makeText(MeregParametersActivity.this, "至少添加一個工地號", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 直接保存需要合并的所有数据
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveCsvAllTimeOrLast(boolean isAll) {
        HashMap<String, List<RealDataCached>> dataMap = new HashMap<>();
        List<String> holeNames = new ArrayList<>();
        String siteName = btnSiteValue.getText().toString();
        holeList.forEach(s -> holeNames.add(s.split("_")[1]));
        for (String str : holeNames) {
            List<RealDataCached> listDatas = realDataCachedDao.queryBuilder().where(RealDataCachedDao.Properties.FormName.eq(siteName + "_" + str)).build().list();
            dataMap.put(str, listDatas);
        }
//        SaveMergeCsvData(siteName, dataMap);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currTime = sdf.format(d);
        SaveMergeCsvAllOrLastData(createTableHeader(siteName, currTime), siteName, currTime, dataMap, isAll);
    }

    /**
     * 创建保存的表头
     *
     * @param siteName
     * @param currTime
     * @return
     */
    private List<String> createTableHeader(String siteName, String currTime) {
        String snValue = (String) SharedPreferencesUtil.getData("SNVaule", "");
        boolean hasRange = checkRange.isChecked();
        boolean hasGF = checkGF.isChecked();
        boolean hasIR = checkIR.isChecked();
        boolean hasDeg = checkDeg.isChecked();
        boolean hasIncline = checkIncline.isChecked();
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
        collection.add(new TableRowBean("", "", "", "", "", "", "", "", "").toSaveString());
        if (hasDeg && hasIncline) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "Deg", "Deg", "incline('' '')", "").toSaveString());
        } else if (hasDeg) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "Deg", "Deg", "", "").toSaveString());
        } else if (hasIncline) {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "incline('' '')", "", "", "").toSaveString());
        } else {
            collection.add(new TableRowBean("", "Date/Time", "Direction", "Raw", "Raw", "", "", "", "").toSaveString());
        }
        return collection;
    }

    /**
     * @param collection 需要保存的数据结合
     * @param siteName   工地名称
     * @param currTime   当前时间
     * @param cached     查询出来的数据
     * @param isAll      true:保存所有  false:保存最新的
     */
    private void SaveMergeCsvAllOrLastData(List<String> collection, String siteName, String currTime, HashMap<String, List<RealDataCached>> cached, boolean isAll) {

        boolean hasDeg = checkDeg.isChecked();
        boolean hasIncline = checkIncline.isChecked();
        if (isAll) {
            for (String key : cached.keySet()) {
                for (int index = 0; index < cached.get(key).size(); index = index + 2) {
                    RealDataCached cached13 = cached.get(key).get(index);
                    RealDataCached cached24 = cached.get(key).get(index + 1);
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
        } else {
            for (String key : cached.keySet()) {
                List<RealDataCached> listCached = cached.get(key);
                try {
                    if (!listCached.isEmpty()) {
                        int lastIndex = listCached.size() - 1;
                        RealDataCached lastCached13 = listCached.get(lastIndex - 1);
                        RealDataCached lastCached24 = listCached.get(lastIndex);
                        String degFirst13 = hasDeg ? lastCached13.getDegFirst() : "";
                        String degSecond13 = hasDeg ? lastCached13.getDegSecond() : "";
                        String degFirst24 = hasDeg ? lastCached24.getDegSecond() : "";
                        String degSecond24 = hasDeg ? lastCached24.getDegSecond() : "";
                        String incline13 = hasIncline ? lastCached13.getInclude() : "";
                        String incline24 = hasIncline ? lastCached24.getInclude() : "";
                        if (hasDeg && hasIncline) {
                            collection.add(new TableRowBean(key, lastCached13.getTime(), lastCached13.getDirection(), lastCached13.getRawFirst(), lastCached13.getRawSecond(), degFirst13, degSecond13, incline13, "").toSaveString());
                            collection.add(new TableRowBean("", lastCached24.getTime(), lastCached24.getDirection(), lastCached24.getRawFirst(), lastCached24.getRawSecond(), degFirst24, degSecond24, incline24, "").toSaveString());
                        } else if (hasDeg) {
                            collection.add(new TableRowBean(key, lastCached13.getTime(), lastCached13.getDirection(), lastCached13.getRawFirst(), lastCached13.getRawSecond(), degFirst13, degSecond13, "", "").toSaveString());
                            collection.add(new TableRowBean("", lastCached24.getTime(), lastCached24.getDirection(), lastCached24.getRawFirst(), lastCached24.getRawSecond(), degFirst24, degSecond24, "", "").toSaveString());
                        } else if (hasIncline) {
                            collection.add(new TableRowBean(key, lastCached13.getTime(), lastCached13.getDirection(), lastCached13.getRawFirst(), lastCached13.getRawSecond(), incline13, "", "", "").toSaveString());
                            collection.add(new TableRowBean("", lastCached24.getTime(), lastCached24.getDirection(), lastCached24.getRawFirst(), lastCached24.getRawSecond(), incline24, "", "", "").toSaveString());
                        } else {
                            collection.add(new TableRowBean(key, lastCached13.getTime(), lastCached13.getDirection(), lastCached13.getRawFirst(), lastCached13.getRawSecond(), "", "", "", "").toSaveString());
                            collection.add(new TableRowBean("", lastCached24.getTime(), lastCached24.getDirection(), lastCached24.getRawFirst(), lastCached24.getRawSecond(), "", "", "", "").toSaveString());
                        }
                    }
                } catch (Exception e) {
                    continue;
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
//                Toast.makeText(MeregParametersActivity.this, file.getName() + "合并成功", Toast.LENGTH_SHORT).show();
                showShareDialog(file);
            }

            @Override
            public void onFail(String s) {

            }
        });
    }

    public void showShareDialog(File file) {
        new AlertDialog.Builder(MeregParametersActivity.this)
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
}