package com.leon.lfilepickerlibrary.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.leon.lfilepickerlibrary.R;
import com.leon.lfilepickerlibrary.adapter.PathAdapter;
import com.leon.lfilepickerlibrary.filter.LFileFilter;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.utils.BaseResponse;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.leon.lfilepickerlibrary.utils.FileuploadService;
import com.leon.lfilepickerlibrary.utils.LoadingDialog;
import com.leon.lfilepickerlibrary.utils.RetrofitHelper;
import com.leon.lfilepickerlibrary.utils.SharedPreferencesUtil;
import com.leon.lfilepickerlibrary.utils.StringUtils;
import com.leon.lfilepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sakura.bottommenulibrary.bottompopfragmentmenu.BottomMenuFragment;

public class LFilePickerActivity extends AppCompatActivity {

    private final String TAG = "FilePickerLeon";
    private EmptyRecyclerView mRecylerView;
    private View mEmptyView;
    private TextView mTvPath;
    private TextView mTvBack;
    private Button mBtnAddBook;
    private Button mBtnShared;
    private String mPath;
    private List<File> mListFiles;
    private ArrayList<String> mListNumbers = new ArrayList<String>();//存放选中条目的数据地址
    private PathAdapter mPathAdapter;
    private Toolbar mToolbar;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private boolean mIsAllSelected = false;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        SharedPreferencesUtil.getInstance(LFilePickerActivity.this, "tfsmkjy");
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        mParamEntity = (ParamEntity) getIntent().getExtras().getSerializable("param");
        setTheme(mParamEntity.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);
        initView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();
        makeStatusBar(R.color.lfile_white);
        updateAddButton();
        if (!checkSDState()) {
            Toast.makeText(this, R.string.lfile_NotFoundPath, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = mParamEntity.getPath();
        if (StringUtils.isEmpty(mPath)) {
            //如果没有指定路径，则使用默认路径
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mTvPath.setText(mPath);
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter = new PathAdapter(mListFiles, this, mFilter, mParamEntity.isMutilyMode(), mParamEntity.isGreater(), mParamEntity.getFileSize());
        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPathAdapter.setmIconStyle(mParamEntity.getIconStyle());
        mRecylerView.setAdapter(mPathAdapter);
        mRecylerView.setmEmptyView(mEmptyView);
        initListener();
    }

    /**
     * 更新Toolbar展示
     */
    private void initToolbar() {
        if (mParamEntity.getTitle() != null) {
            mToolbar.setTitle(mParamEntity.getTitle());
        }
        if (mParamEntity.getTitleStyle() != 0) {
            mToolbar.setTitleTextAppearance(this, mParamEntity.getTitleStyle());
        }
        if (mParamEntity.getTitleColor() != null) {
            mToolbar.setTitleTextColor(Color.parseColor(mParamEntity.getTitleColor())); //设置标题颜色
        }
        if (mParamEntity.getBackgroundColor() != null) {
            mToolbar.setBackgroundColor(Color.parseColor(mParamEntity.getBackgroundColor()));
        }
//        switch (mParamEntity.getBackIcon()) {
//            case Constant.BACKICON_STYLEONE:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back1);
//                break;
//            case Constant.BACKICON_STYLETWO:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back2);
//                break;
//            case Constant.BACKICON_STYLETHREE:
//                //默认风格
//                break;
//        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateAddButton() {
        if (!mParamEntity.isMutilyMode()) {
            mBtnAddBook.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseMode()) {
            mBtnAddBook.setVisibility(View.VISIBLE);
            mBtnAddBook.setText(getString(R.string.lfile_OK));
            //文件夹模式默认为单选模式
            mParamEntity.setMutilyMode(false);
        }
    }

    /**
     * 添加点击事件处理
     */
    private void initListener() {
        // 返回目录上一级
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null) {
                    return;
                }
                mPath = tempPath;
                mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
                mPathAdapter.setmListData(mListFiles);
                mPathAdapter.updateAllSelelcted(false);
                mIsAllSelected = false;
                updateMenuTitle();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
                mRecylerView.scrollToPosition(0);
                setShowPath(mPath);
                //清除添加集合中数据
                mListNumbers.clear();
                if (mParamEntity.getAddText() != null) {
                    mBtnAddBook.setText(mParamEntity.getAddText());
                } else {
                    mBtnAddBook.setText(R.string.lfile_Selected);
                }
            }
        });
        mPathAdapter.setOnItemClickListener(new PathAdapter.OnItemClickListener() {
            @Override
            public void click(final int position) {
                if (mParamEntity.isMutilyMode()) {
                    if (mListFiles.get(position).isDirectory()) {
                        //如果当前是目录，则进入继续查看目录
                        chekInDirectory(position);
                        mPathAdapter.updateAllSelelcted(false);
                        mIsAllSelected = false;
                        updateMenuTitle();
                        mBtnAddBook.setText(getString(R.string.lfile_Selected));
                    } else {
                        //如果已经选择则取消，否则添加进来
                        if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
                            mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
                        } else {
                            mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        }
                        if (mParamEntity.getAddText() != null) {
                            mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                        } else {
                            mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                        }
                        //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
                        if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                            Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    //单选模式直接返回
                    if (mListFiles.get(position).isDirectory()) {
                        if (mParamEntity.getSelectormode() == Constant.SELECTOR_MODE_2) {
                            if (mListFiles.get(position).getName().startsWith("Namber")) {
                                //进行弹框
                                new BottomMenuFragment(LFilePickerActivity.this)
                                        .addMenuItems(new sakura.bottommenulibrary.bottompopfragmentmenu.MenuItem("開始測量"))
                                        .addMenuItems(new sakura.bottommenulibrary.bottompopfragmentmenu.MenuItem("修改記錄檔"))
                                        .setOnItemClickListener(new BottomMenuFragment.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(TextView textView, int i) {

                                                if (i == 0) {
                                                    //前往实时界面进行测量
                                                    Intent intent = new Intent();
                                                    intent.putExtra("selectModel", Constant.SELECTOR_MODE_2_1);
                                                    intent.putExtra("selectFile", mListFiles.get(position).getParentFile().getName() + "," + mListFiles.get(position).getName());
                                                    setResult(RESULT_OK, intent);
                                                    LFilePickerActivity.this.finish();
                                                } else {
                                                    //获取下一层数据
                                                    chekInDirectory(position);
                                                }
                                            }
                                        }).show();
                            } else {
                                //如果当前是目录，则进入继续查看目录
                                chekInDirectory(position);
                            }
                        } else {
                            chekInDirectory(position);
                        }
                        return;
                    }
                    if (mParamEntity.isChooseMode()) {
                        //选择文件模式,需要添加文件路径，否则为文件夹模式，直接返回当前路径
                        mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        chooseDone();
                    } else {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_ChooseTip, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //选择的文件点击
        mBtnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParamEntity.isChooseMode() && mListNumbers.size() < 1) {
                    String info = mParamEntity.getNotFoundFiles();
                    if (TextUtils.isEmpty(info)) {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_NotFoundBooks, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LFilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //返回
                    chooseDone();
                }
            }
        });

        //原本手机分享文件
//        mBtnShared.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//
//                //进行文件分享
//                final ArrayList<Uri> files = new ArrayList<Uri>();
//                mListNumbers.forEach(new Consumer<String>() {
//                    @Override
//                    public void accept(String path) {
//                        files.add(Uri.fromFile(new File(path)));
//                    }
//                });
//                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);//发送多个文件
//                intent.setType("*/*");//多个文件格式
//                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);//Intent.EXTRA_STREAM同于传输文件流
//                startActivity(intent);
//            }
//        });
        mBtnShared.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(LFilePickerActivity.this);
                inputServer.setBackgroundResource(R.drawable.edittext_border);
                String email = (String) SharedPreferencesUtil.getData("email", "");
                inputServer.setText(email);
                AlertDialog.Builder builder = new AlertDialog.Builder(LFilePickerActivity.this);
                builder.setTitle("發送郵箱地址").setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("發送", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String email = inputServer.getText().toString();
                        SendFielsToEmail(email);
                    }
                });
                builder.show();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SendFielsToEmail(final String email) {
        final ArrayList<File> files = new ArrayList<File>();
        mListNumbers.forEach(new Consumer<String>() {
            @Override
            public void accept(String path) {
                files.add(new File(path));
            }
        });
        FileuploadService fileuploadService = RetrofitHelper.buildRetrofit().create(FileuploadService.class);
        Call<BaseResponse<String>> uploadCall = fileuploadService.uploadFilesWithParts(filesToMultipartBodyParts(files), email);

        LoadingDialog.getInstance(LFilePickerActivity.this).show();
        uploadCall.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {

                if (response.isSuccessful()) {
                    SharedPreferencesUtil.putData("email", email);
                    LoadingDialog.getInstance(LFilePickerActivity.this).hide();
                    assert response.body() != null;
                    Toast.makeText(LFilePickerActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {

                Toast.makeText(LFilePickerActivity.this, "郵件發送失敗", Toast.LENGTH_SHORT).show();
                LoadingDialog.getInstance(LFilePickerActivity.this).hide();
            }
        });
    }

    public MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse(""), file);
            builder.addFormDataPart("file", file.getName(), requestBody);
        }

        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    public List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            String tempFileName = "";
            try {
                tempFileName = URLEncoder.encode(file.getName(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            MultipartBody.Part part = MultipartBody.Part.createFormData("file",tempFileName, requestBody);
            parts.add(part);
        }
        return parts;
    }

    private static String encodeHeadInfo( String headInfo ) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = headInfo.length(); i < length; i++) {
            char c = headInfo.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append( String.format ("\\u%04x", (int)c) );
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 点击进入目录
     *
     * @param position
     */
    private void chekInDirectory(int position) {
        mPath = mListFiles.get(position).getAbsolutePath();
        setShowPath(mPath);
        //更新数据源
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.notifyDataSetChanged();
        mRecylerView.scrollToPosition(0);
    }

    /**
     * 完成提交
     */
    private void chooseDone() {
        //判断是否数量符合要求
        if (mParamEntity.isChooseMode()) {
            if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("paths", mListNumbers);
        intent.putExtra("path", mTvPath.getText().toString().trim());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 进行文件分享
     */
    private void sharedFiles() {

        //这里进行文件的分享


    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecylerView = findViewById(R.id.recylerview);
        mTvPath = findViewById(R.id.tv_path);
        mTvBack = findViewById(R.id.tv_back);
        mBtnAddBook = findViewById(R.id.btn_addbook);
        mBtnShared = findViewById(R.id.btn_shared);
        mEmptyView = findViewById(R.id.empty_view);
        mToolbar = findViewById(R.id.toolbar);

        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        }

        if (mParamEntity.isMutilyMode()) {
            mBtnShared.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 显示顶部地址
     *
     * @param path
     */
    private void setShowPath(String path) {
        mTvPath.setText(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        this.mMenu = menu;
        updateOptionsMenu(menu);
        return true;
    }

    /**
     * 更新选项菜单展示，如果是单选模式，不显示全选操作
     *
     * @param menu
     */
    private void updateOptionsMenu(Menu menu) {
        mMenu.findItem(R.id.action_selecteall_cancel).setVisible(mParamEntity.isMutilyMode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            //将当前目录下所有文件选中或者取消
            mPathAdapter.updateAllSelelcted(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
            if (mIsAllSelected) {
                for (File mListFile : mListFiles) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mListNumbers.contains(mListFile.getAbsolutePath())) {
                        mListNumbers.add(mListFile.getAbsolutePath());
                    }
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                    }
                }
            } else {
                mListNumbers.clear();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
            }
            updateMenuTitle();
        }
        return true;
    }

    /**
     * 更新选项菜单文字
     */
    public void updateMenuTitle() {

        if (mIsAllSelected) {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_Cancel));
        } else {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_SelectAll));
        }
    }

    protected void makeStatusBar(int colorId) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorId));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
