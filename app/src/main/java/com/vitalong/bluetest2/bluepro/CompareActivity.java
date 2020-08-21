package com.vitalong.bluetest2.bluepro;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vitalong.bluetest2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CompareActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.spinner1)
    Spinner spinner1;
    @Bind(R.id.spinner2)
    Spinner spinner2;
    @Bind(R.id.spinner3)
    Spinner spinner3;
    @Bind(R.id.spinner4)
    Spinner spinner4;
    @Bind(R.id.spinner5)
    Spinner spinner5;
    String[] ctype1 = new String[]{"A001", "A002", "A003", "A004", "A005", "A006", "A007", "A008", "A009", "A010", "A011", "A012", "A013", "A014", "A015", "A016", "A017", "A018", "A019"
            , "A020", "A021", "A022", "A023", "A024", "A025", "A026", "A027", "A028", "A029", "A030", "A031", "A032", "A033", "A034", "A035", "A036", "A037", "A038", "A039"
            , "A040", "A041", "A042", "A043", "A044", "A045", "A046", "A047", "A048", "A049", "A050", "A051", "A052", "A053", "A054", "A055", "A056", "A057", "A058", "A059"
            , "A060", "A061", "A062", "A063", "A064", "A065", "A066", "A067", "A068", "A069", "A070", "A071", "A072", "A073", "A074", "A075", "A076", "A077", "A078", "A079"
            , "A080", "A081", "A082", "A083", "A084", "A085", "A086", "A087", "A088", "A089", "A090", "A091", "A092", "A093", "A094", "A095", "A096", "A097", "A098", "A099"};

    String[] ctype2 = new String[]{"T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10", "T11", "T12", "T13", "T14", "T15", "T16", "T17", "T18", "T19"
            , "T20", "T21", "T22", "T23", "T24", "T25", "T26", "T27", "T28", "T29", "T30", "T31", "T32", "T33", "T34", "T35", "T36", "T37", "T38", "T39"
            , "T40", "T41", "T42", "T43", "T44", "T45", "T46", "T47", "T48", "T49", "T50", "T51", "T52", "T53", "T54", "T55", "T56", "T57", "T58", "T59"
            , "T60", "T61", "T62", "T63", "T64", "T65", "T66", "T67", "T68", "T69", "T70", "T71", "T72", "T73", "T74", "T75", "T76", "T77", "T78", "T79"
            , "T80", "T81", "T82", "T83", "T84", "T85", "T86", "T87", "T88", "T89", "T90", "T91", "T92", "T93", "T94", "T95", "T96", "T97", "T98", "T99"};

    String[] ctype3 = new String[]{"10", "20", "All"};
    String[] ctype4 = new String[]{"All", "(1-3)", "(2-4)"};
    String[] ctype5 = new String[]{"Disable", "Enable"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        bindToolBar();
        makeStatusBar(R.color.white);
        initView();
    }

    private void initView() {
        initSpnr(spinner1, ctype1);
        initSpnr(spinner2, ctype2);
        initSpnr(spinner3, ctype3);
        initSpnr(spinner4, ctype4);
        initSpnr(spinner5, ctype5);
    }

    public void initSpnr(Spinner spinner, String[] ctype) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemSelected(parent, view, position, id);
//                setSharedPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void itemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner1:
                break;
            case R.id.spinner2:
                break;
            case R.id.spinner3:
                break;
            case R.id.spinner4:
                break;
            case R.id.spinner5:
                break;
            default:
                //nothing todo
                break;
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            CompareActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
