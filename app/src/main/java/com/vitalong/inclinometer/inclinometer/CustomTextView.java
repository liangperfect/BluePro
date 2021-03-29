package com.vitalong.inclinometer.inclinometer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @Package: com.vitalong.inclinometer.inclinometer
 * @Description:
 * @Author: 亮
 * @CreateDate: 2021/1/29 15:48
 * @UpdateUser: 更新者
 */
class CustomTextView extends AppCompatTextView {


    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }
}
