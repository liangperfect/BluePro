package com.vitalong.bluetest2;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vitalong.bluetest2.BlueToothLeService.BluetoothLeService;
import com.vitalong.bluetest2.Utils.AnimateUtils;
import com.vitalong.bluetest2.Utils.Constants;
import com.vitalong.bluetest2.Utils.GattAttributes;
import com.vitalong.bluetest2.Utils.Utils;
import com.vitalong.bluetest2.adapter.DevicesAdapter;
import com.vitalong.bluetest2.bean.MDevice;
import com.vitalong.bluetest2.bean.MService;
import com.vitalong.bluetest2.fragments.BleFragment;
import com.vitalong.bluetest2.fragments.SppFragment;
import com.vitalong.bluetest2.views.RevealBackgroundView;
import com.vitalong.bluetest2.views.RevealSearchView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import me.drakeet.materialdialog.MaterialDialog;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends MyBaseActivity implements BleFragment.OnRunningAppRefreshListener, View.OnClickListener {

    //权限申请数组
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
    private static final int OPEN_SET_REQUEST_CODE = 100;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic indicateCharacteristic;
    @Bind(R.id.coll_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static BluetoothAdapter mBluetoothAdapter;
    private MessageHandler msgHandler;
    private Handler hander;
    boolean isShowingDialog = false;
    private ViewPager vpContainer;
    private RadioGroup rgTabButtons;
    private int mCurrentFragment;
    private MyApplication myApplication;
    //    private String[] fragmetns = new String[]{
//            BleFragment.class.getName(),
//            SppFragment.class.getName()};
    private String[] fragmetns = new String[]{
            BleFragment.class.getName()};
    private MDevice mDevice;
    private String mode;
    /**
     * BLE  // 成员域
     */
    private boolean scaning;
    private MaterialDialog progressDialog;
    private RevealSearchView revealSearchView;
    private RevealBackgroundView revealBackgroundView;
    private FloatingActionButton fabSearch;
    private int[] fabStartPosition;
    private TextView tvSearchDeviceCount;
    private RelativeLayout rlSearchInfo;
    private Button stopSearching;
    private RecyclerView recyclerView;
    private String currentDevAddress;
    private String currentDevName;
    private MaterialDialog alarmDialog;
    private String KEEP_CMD_CODE = "123456"; //与板子保持连接的命令

    //停止扫描
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
            disconnectDevice();
        }
    };


    /**
     * spp
     */

    private BluetoothAdapter mBtAdapter;
    private final List<MDevice> list = new ArrayList<>();
    private DevicesAdapter adapter;

    /**
     * 构造
     */
    public MainActivity() {
        hander = new Handler();
        mDevice = new MDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果有连接先关闭连接
        disconnectDevice();
    }


    /**
     * onCreate 入口
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //必须调用，其在setContentView后面调用
        bindToolBar();
        //标题栏
        myApplication = (MyApplication) getApplication();
        msgHandler = new MessageHandler();
        toolbar.setNavigationIcon(R.mipmap.ic_more_vertical_white_18dp);
        collapsingToolbarLayout.setTitle(getString(R.string.devices));
        //设置一个监听，否则会报错，support library design的bug
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //检查蓝牙
        checkBleSupportAndInitialize();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initPermissions();
        initView(); //初始化视图
        initComponents(); //初始化view pager; 默认选中的为0
        initCartoon();//初始化动画
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //搜索按钮点击事件
            case R.id.fab_search:
                SharedPreferences mSharedPreferences = getSharedPreferences("mode", MainActivity.MODE_PRIVATE);
                mode = mSharedPreferences.getString("mode", "");
                if (mode.equals("SPP")) {
                    ((RadioButton) rgTabButtons.getChildAt(1)).setChecked(true);
                    searchAnimate();
                } else if (mode.equals("BLE")) {
                    ((RadioButton) rgTabButtons.getChildAt(0)).setChecked(true);
                    scaning = true;
                    //如果有连接先关闭连接
                    disconnectDevice();
                    //开始扫面动画
                    searchAnimate();
                    //初始化blefragment
                    initbleFragment();
                }
                break;
            //停止搜索按钮点击事件
            case R.id.btn_stop_searching:
                scaning = false;
                stopScan();
                //停止扫描
                mBtAdapter.cancelDiscovery();
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initEvent();//初始化事件
        initBroadcast();//初始化广播
        initService();//初始化服务
    }

    /**
     * 初始化服务，用于连接蓝牙，发送数据，接收数据
     */
    private void initService() {
        Intent gattServiceIntent = new Intent(getApplicationContext(),
                BluetoothLeService.class);
        startService(gattServiceIntent);
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //搜索按钮点击事件
        fabSearch.setOnClickListener(this);
        //停止搜索按钮点击事件
        stopSearching.setOnClickListener(this);
    }

    /**
     * 初始化动画 与动画的状态监听
     */
    private void initCartoon() {

        //动画效果
        collapsingToolbarLayout.setTranslationY(160f);
        toolbar.setTranslationY(-Utils.dpToPx(60));
        collapsingToolbarLayout.setAlpha(0.0f);
        AnimateUtils.translationY(collapsingToolbarLayout, 0, 400, 100);
        AnimateUtils.translationY(toolbar, 0, 400, 200);
        AnimateUtils.alpha(collapsingToolbarLayout, 1f, 400, 100);


        //revealSearchView 动画状态监听
        revealSearchView.setOnStateChangeListener(new RevealSearchView.OnStateChangeListener() {
            public void onStateChange(int state) {
                if (state == RevealSearchView.STATE_FINISHED) {
                    revealSearchView.setVisibility(View.GONE);
                    revealBackgroundView.endFromEdge();
                }
            }
        });
        // revealBackgroundView 动画状态监听
        revealBackgroundView.setOnStateChangeListener(new RevealBackgroundView.OnStateChangeListener() {
            public void onStateChange(int state) {
                if (state == RevealBackgroundView.STATE_FINISHED) {
                    revealSearchView.setVisibility(View.VISIBLE);
                    revealSearchView.startFromLocation(fabStartPosition);
                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, 0));
                    rlSearchInfo.setVisibility(View.VISIBLE);
                    rlSearchInfo.setTranslationY(Utils.dpToPx(70));
                    rlSearchInfo.setAlpha(0);
                    AnimateUtils.translationY(rlSearchInfo, 0, 300, 0);
                    AnimateUtils.alpha(rlSearchInfo, 1.0f, 300, 0);
                    //准备列表视图并开始扫描
                    if (mode.equals("SPP")) {
                        doDiscovery();
                    } else if (mode.equals("BLE")) {
                        onRefresh();
                    }
                }
                if (state == RevealBackgroundView.STATE_END_FINISHED) {
                    revealBackgroundView.setVisibility(View.GONE);
                    rlSearchInfo.setVisibility(View.GONE);
                    scaning = false;
                    if (mode.equals("BLE")) {
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });


    }

    /**
     * ble 停止扫描
     */
    private void stopScan() {
        revealSearchView.setVisibility(View.GONE);
        //停止雷达动画
        revealSearchView.stopAnimate();
        //涟漪动画回缩
        revealBackgroundView.endFromEdge();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 获得ViewPager
        vpContainer = (ViewPager) findViewById(R.id.vpContainer);
        revealSearchView = (RevealSearchView) findViewById(R.id.realsearchiew);
        revealBackgroundView = (RevealBackgroundView) findViewById(R.id.reveal_background_view);
        tvSearchDeviceCount = (TextView) findViewById(R.id.tv_search_device_count);
        rlSearchInfo = (RelativeLayout) findViewById(R.id.rl_search_info);
        fabSearch = (FloatingActionButton) findViewById(R.id.fab_search);
        stopSearching = (Button) findViewById(R.id.btn_stop_searching);
    }

    /**
     * 初始化blefragment
     */
    private void initbleFragment() {
        //获的recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycleviewble);
        //给recyclerView   设置布局样式
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        //获取并设置设备适配器
        adapter = new DevicesAdapter(list, this);
        recyclerView.setAdapter(adapter);
        //recyclerView  添加条目效果
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                adapter.setDelayStartAnimation(false);
                return false;
            }

            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        //adapter 点击事件
        adapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            public void onItemClick(View itemView, int position) {
                if (!scaning) {
                    isShowingDialog = true;
                    showProgressDialog();
                    hander.postDelayed(dismssDialogRunnable, 20000);
                    connectDevice(list.get(position).getDevice());
                }
            }
        });
    }

    /**
     * 显示连接动画
     */
    private void showProgressDialog() {
        progressDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }

    /**
     * /准备列表视图并开始扫描
     */
    public void onRefresh() {
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        startScan();//开始扫描
    }

    /**
     * 开始扫面入口
     */
    private void startScan() {
        scanPrevious21Version();
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        //10秒后停止扫描
        hander.postDelayed(stopScanRunnable, 10000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 发现设备时 处理方法
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    if (list.contains(mDev) || mDev.getDevice().getName() == null || !mDev.getDevice().getName().contains("BL2"))
                        return;
                    list.add(mDev);
                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, list.size()));
                }
            });
        }
    };

    /**
     * 检查蓝牙是否可用
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(this,
                    R.string.device_ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * ble 搜索动画启动
     */
    private void searchAnimate() {
        revealBackgroundView.setVisibility(View.VISIBLE);
        int[] position1 = new int[2];
        fabSearch.getLocationOnScreen(position1);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fabStartPosition = new int[]{(position1[0] + fabSearch.getWidth() / 2),
                    (position1[1] + fabSearch.getHeight() / 4)};
        } else {
            fabStartPosition = new int[]{(position1[0] + fabSearch.getWidth() / 2),
                    position1[1]};
        }
        revealBackgroundView.startFromLocation(fabStartPosition);
    }

    /**
     * spp 搜索启动事件
     */
    public void doDiscovery() {
        // 在窗口显示查找中信息
        //getActivity().setProgressBarIndeterminateVisibility(true);
        //getActivity().setTitle("查找设备中...");
        // 显示其它设备（未配对设备）列表
        //rootView.findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 关闭再进行的服务查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //并重新开始
        mBtAdapter.startDiscovery();
    }

    /**
     * ble 连接
     */
    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED)
            BluetoothLeService.disconnect();
        BluetoothLeService.connect(currentDevAddress, currentDevName, this);
    }


    /**
     * ble 取消连接
     */
    private void disconnectDevice() {
        isShowingDialog = false;
        BluetoothLeService.disconnect();
    }


    /**
     * 菜单键监听
     */
    @Override
    protected void menuHomeClick() {
        Uri uri = Uri.parse("http://www.usr.cn/Product/cat-86.html");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 销毁MainActivity 的方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化view pager
     */
    private void initComponents() {
        rgTabButtons = (RadioGroup) findViewById(R.id.rgTabButtons);
        KickerFragmentAdapter adpater = new KickerFragmentAdapter(getSupportFragmentManager(), this);
        vpContainer.setOnPageChangeListener(onPageChangeListener);
        vpContainer.setAdapter(adpater);
        vpContainer.setCurrentItem(mCurrentFragment);
        rgTabButtons.setOnCheckedChangeListener(onCheckedChangeListener);
        ((RadioButton) rgTabButtons.getChildAt(0)).setChecked(true);
    }

    /***/
    public void onRunningAppRefreshed() {
        SppFragment fragment = (SppFragment) getSupportFragmentManager().getFragments().get(1);
        if (fragment != null) {
            // fragment.refresh();
        }
    }


    class KickerFragmentAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public KickerFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        public Fragment getItem(int arg0) {
            return Fragment.instantiate(mContext, fragmetns[arg0]);
        }

        public int getCount() {
            return fragmetns.length;
        }

    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mCurrentFragment = arg0;
            ((RadioButton) rgTabButtons.getChildAt(arg0)).setChecked(true);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            SharedPreferences mSharedPreferences = getSharedPreferences("mode", MainActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            int checkedItem = 0;
            switch (checkedId) {
                case R.id.rbRunningApp:
                    checkedItem = 0;
                    editor.putString("mode", "BLE");
                    editor.commit();
                    String mode1 = mSharedPreferences.getString("mode", "");
                    System.out.println(mode1);
                    new SppFragment().onPause();
                    break;
                case R.id.rbRunningService:
                    checkedItem = 1;
                    editor.putString("mode", "SPP");
                    editor.commit();
                    String mode = mSharedPreferences.getString("mode", "");
                    System.out.println(mode);
                    new BleFragment().onPause();
                    break;
            }
            vpContainer.setCurrentItem(checkedItem);
            mCurrentFragment = checkedItem;
        }
    };
    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");
                //搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                hander.removeCallbacks(dismssDialogRunnable);
                progressDialog.dismiss();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                progressDialog.dismiss();
                //connect break (连接断开)
                showDialog(getString(R.string.conn_disconnected_home));
            }

            //接收返回的数据
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                System.out.println("GattDetailActivity-------------->onReceive");
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            BluetoothGattCharacteristic requiredCharacteristic = myApplication.getCharacteristic();
                            String uuidRequired = requiredCharacteristic.getUuid().toString();
                            String receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);
                            byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                            com.vitalong.bluetest2.bean.Message msg1 = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array));
                            System.out.println("返回的数据是:" + msg1.getContent());
                            //                            if (isDebugMode) {
//                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
//                                com.vitalong.bluetest2.bean.Message msg = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array));
//                                notifyAdapter(msg);
//                            } else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
//                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
//                                com.vitalong.bluetest2.bean.Message msg = new com.vitalong.bluetest2.bean.Message(com.vitalong.bluetest2.bean.Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array, MyApplication.serviceType));
//                                notifyAdapter(msg);
//                            }
                        }
                    }
                }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        BluetoothGattCharacteristic requiredCharacteristic = myApplication.
                                getCharacteristic();
                        String uuidRequired = requiredCharacteristic.getUuid().toString();
                        String receivedUUID = intent.getStringExtra(
                                Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);

                        byte[] array = intent
                                .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);

//                        System.out.println("GattDetailActivity---------------------->descriptor:" + Utils.ByteArraytoHex(array));
//                        if (isDebugMode) {
//                            updateButtonStatus(array);
//                        } else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
//                            updateButtonStatus(array);
//                        }

                    }
                }
            }

        }
    };

    private void showDialog(String info) {
        if (!isShowingDialog)
            return;
        if (alarmDialog != null)
            return;
        alarmDialog = new MaterialDialog(this);
        alarmDialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        alarmDialog = null;
                    }
                });
        alarmDialog.show();
    }

    /**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);

//        Intent intent = new Intent(this, ServicesActivity.class);
//        intent.putExtra("dev_name", currentDevName);
//        intent.putExtra("dev_mac", currentDevAddress);
//        startActivity(intent);
//        overridePendingTransition(0, 0);
        goCharacteristicsActivity();
    }

    /**
     * Prepare GATTServices data.
     * 将Gateservices加上名字并保存的Application当中去
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;

        List<MService> list = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                continue;
            String name = GattAttributes.lookup(gattService.getUuid().toString(), "UnkonwService");
            MService mService = new MService(name, gattService);
            list.add(mService);
        }

        ((MyApplication) getApplication()).setServices(list);
    }


    private void goCharacteristicsActivity() {

        MService mService = ((MyApplication) getApplication()).getServices().get(0);
        BluetoothGattService service = mService.getService();

        System.out.println("service---------------->" + service.getUuid().toString());

        ((MyApplication) getApplication()).setCharacteristics(service.getCharacteristics());

        Intent intent = new Intent(MainActivity.this, CharacteristicsActivity.class);
        if (service.getUuid().toString().equals(GattAttributes.USR_SERVICE)) {
            intent.putExtra("is_usr_service", true);
            //这里为了方便暂时直接用Application serviceType 来标记当前的服务，应该是和上面的代码合并
            MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_USR_DEBUG;
        } else if (service.getUuid().toString().equals(GattAttributes.BATTERY_SERVICE) ||
                service.getUuid().toString().equals(GattAttributes.RGB_LED_SERVICE_CUSTOM)) {
            MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_NUMBER;
        } else {
            MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_OTHER;
        }


//        startActivity(intent);
//        overridePendingTransition(0, 0);
        setCurrActivityCharacteristics();
    }

    private void setCurrActivityCharacteristics() {
        BluetoothGattCharacteristic usrVirtualCharacteristic =
                new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE), -1, -1);
        ((MyApplication) getApplication()).setCharacteristic(usrVirtualCharacteristic);
        initCharacteristics();
        Intent intent = new Intent(MainActivity.this, GattDetailActivity.class);
        setMaxMut();
        //必须要延迟50ms才能发送数据
        msgHandler.sendEmptyMessageDelayed(0, 50);
//        startActivity(intent);
//        overridePendingTransition(0, 0);
    }

    /**
     * 设置发包和收包的长度
     */
    private void setMaxMut() {
        int sdkInt = Build.VERSION.SDK_INT;
        System.out.println("sdkInt------------>" + sdkInt);
        if (sdkInt >= 21) {
            //设置最大发包、收包的长度为512个字节
            if (BluetoothLeService.requestMtu(512)) {
                Toast.makeText(this, getString(R.string.transmittal_length, "512"), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, getString(R.string.transmittal_length, "20"), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.transmittal_length, "20"), Toast.LENGTH_LONG).show();
        }
    }

    private void initCharacteristics() {
        BluetoothGattCharacteristic characteristic = ((MyApplication) getApplication()).getCharacteristic();
        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)) {
            List<BluetoothGattCharacteristic> characteristics = ((MyApplication) getApplication()).getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                if (Utils.getPorperties(this, c).equals("Notify")) {
                    notifyCharacteristic = c;
                    continue;
                }

                if (Utils.getPorperties(this, c).equals("Write")) {
                    writeCharacteristic = c;
                    continue;
                }
            }
        }
    }

    /**
     * 向蓝牙发送数据
     */
    private void sendLinkCode(String codeStr, boolean isHex) {

        if (!isHex) {
            try {
                byte[] array = codeStr.getBytes("US-ASCII");
                writeCharacteristic(writeCharacteristic, array);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            byte[] array = Utils.hexStringToByteArray(codeStr);
            writeCharacteristic(writeCharacteristic, array);
        }
    }

    /**
     * 向蓝牙发送数据
     *
     * @param characteristic
     * @param bytes
     */
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic,
                    bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    private String formatMsgContent(byte[] data) {
        return "HEX:" + Utils.ByteArraytoHex(data) + "  (ASSCII:" + Utils.byteToASCII(data) + ")";
    }

    private void initPermissions() {
        if (lacksPermission(permissions)) {//判断是否拥有权限
            //请求权限，第二参数权限String数据，第三个参数是请求码便于在onRequestPermissionsResult 方法中根据code进行判断
            ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
        } else {
            //拥有权限执行操作
        }
    }

    //如果返回true表示缺少权限
    public boolean lacksPermission(String[] permissions) {
        for (String permission : permissions) {
            //判断是否缺少权限，true=缺少权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //响应Code
        if (requestCode == OPEN_SET_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "為擁有定位權限，請到手機設置内申請該權限", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                //拥有权限执行操作
            } else {
                Toast.makeText(MainActivity.this, "為擁有定位權限，請到手機設置内申請該權限", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mGattUpdateReceiver);
    }

    class MessageHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                sendLinkCode(KEEP_CMD_CODE, false);
                msgHandler.sendEmptyMessageDelayed(2, 200);
            } else if (msg.what == 1) {
//                sendLinkCode("010300000002C40B", true);
                //进行界面的跳转
                Intent intent = new Intent(MainActivity.this, OperationPanelActivity.class);
                startActivity(intent);

            } else if (msg.what == 2) {
                prepareBroadcastDataNotify(notifyCharacteristic);
                msgHandler.sendEmptyMessageDelayed(1, 200);
            } else if (msg.what == 3) {

            }
        }
    }
}
