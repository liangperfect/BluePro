package com.vitalong.inclinometer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.Utils.Utils;
import com.vitalong.inclinometer.adapter.OptionsSelectAdapter;
import com.vitalong.inclinometer.bean.Option;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu on 15/8/9.
 */
public class OptionsMenu extends LinearLayout {
    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(200);

    @Bind(R.id.rv_options)
    RecyclerView rv_options;

    private List<Option> list;
    private OptionsSelectAdapter.OptionsOnItemSelectedListener optionsOnItemSelectedListener;

    public OptionsMenu(Context context, List<Option> list, OptionsSelectAdapter.OptionsOnItemSelectedListener optionsOnItemSelectedListener) {
        super(context);
        this.list = list;
        this.optionsOnItemSelectedListener = optionsOnItemSelectedListener;
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.options_menu, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        int height;
        if (list.size() == 2){
            height = Utils.dpToPx(61*list.size());
        }else {
            height = Utils.dpToPx(55*list.size());
        }
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH,height));
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
        initRecycleView();
    }


    private void initRecycleView(){
        OptionsSelectAdapter adapter = new OptionsSelectAdapter(getContext(),list);
        adapter.setConnectionsOnItemSelectedListener(optionsOnItemSelectedListener);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv_options.setLayoutManager(llm);
        rv_options.setAdapter(adapter);
    }


    public void dismiss(){
        ((ViewGroup)getParent()).removeView(OptionsMenu.this);
    }

}
