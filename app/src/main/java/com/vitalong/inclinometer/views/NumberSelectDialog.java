package com.vitalong.inclinometer.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitalong.inclinometer.R;
import com.vitalong.inclinometer.views.numberpicker.NumberPicker;

import java.util.List;

public class NumberSelectDialog extends Dialog {
    private ImageView imgClose;
    private NumberPicker numberPicker;
    private TextView tvConfirm;
    private List<String> numbers;
    private ChangeNumberListener listener;
    private int currIndex = 0;

    public NumberSelectDialog(Context context, List<String> d, ChangeNumberListener l) {
        super(context, R.style.bottom_dialog);
        this.numbers = d;
        listener = l;
        init(context);
    }

    public NumberSelectDialog(Context context, int themeResId, List<String> d) {
        super(context, themeResId);
        this.numbers = d;
        init(context);

    }

    public NumberSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener, List<String> d) {
        super(context, cancelable, cancelListener);
        this.numbers = d;
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        View view = getLayoutInflater().inflate(R.layout.number_select, null, false);
        setContentView(view);
        imgClose = view.findViewById(R.id.imgClose);
        numberPicker = view.findViewById(R.id.numberPicker);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        imgClose.setOnClickListener(v -> NumberSelectDialog.this.dismiss());
        String[] numberStrs = new String[numbers.size()];
        numbers.toArray(numberStrs);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(numberStrs.length);
        numberPicker.setDisplayedValues(numberStrs);
        numberPicker.setFadingEdgeEnabled(true);
        numberPicker.setScrollerEnabled(true);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setAccessibilityDescriptionEnabled(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Log.d("chenliang", String.format(Locale.US, "oldVal: %d, newVal: %d", oldVal, newVal));
                currIndex = newVal - 1;
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onSelectWhichNum(numbers.get(currIndex));
                NumberSelectDialog.this.dismiss();
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = dp2px(context, 300);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    public interface ChangeNumberListener {

        public void onSelectWhichNum(String numberStr);
    }

    public int dp2px(Context context, float dp) {
        return (int) Math.ceil((double) (context.getResources().getDisplayMetrics().density * dp));
    }
}