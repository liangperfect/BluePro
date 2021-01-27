package com.vitalong.inclinometer.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.vitalong.inclinometer.bean.ChartPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Package: com.vitalong.inclinometer.views
 * @Description:
 * @Author: 亮
 * @CreateDate: 2021/1/14 10:28
 * @UpdateUser: 更新者
 */
public class SimpleLineView extends View {

    private int w = 0;//图表的宽度
    private int h = 0;//图表的高度
    private Paint mXYPaint;//XY轴画笔
    private Paint mXYTextPaint;//XY轴对应的文本画笔
    private Paint mStrokePaint;
    private Paint mGridLinePaint;//网格的画笔
    private Paint mPointPaint;//原点的画笔
    private Paint mBrokenLinePaint;//折线画笔
    private Paint mLegenPaint;//标题先关文字大小
    private int mYLength = 0; //Y轴的长度
    private int mXLength = 0; //X轴的长度
    private int mYScall = 35; //Y轴两个刻度之间的间距
    private int mXScall = 0;//X轴两个刻度之间的间距
    private int mXYLineWidth = 2;//XY轴的宽度
    private int mGridWidth = 2;//网格线的宽度
    private int mAxislength = 12;//坐标轴上的刻度长度
    private List<String> xLabels;
    private List<String> yLables;
    private List<String> y2Lables;
    private List<ChartPoint> mChartPoints;
    private List<ChartPoint> mChart2Points;
    private float mXOriChange = 0;
    private float mYOriChange = 0;
    private float mXOri = 0;
    private float mYOri = 0;
    private int mXMaxInterval = 0; //X轴最大的间距
    private int mXDegreeNumber = 25;//X轴的固定刻度数
    private float startY;//点击的起始位置
    private float startX;
    private int mPaddingRight = 40;
    private int mPaddingLeft = 40;//表格距离左边的距离
    private int mPaddingTop = 34;//报个距离上面的距离
    private int yTextWidth = 0;//y轴值的宽度
    private int xTextHeight = 0;//X轴值的宽度
    private int mXDuratioVaule = 0; //X轴大刻度的数值间距
    int mRecordXMax = 0;//记录最大值
    int mRecordXMin = 0;//记录最小值
    private int yAxisLengenWidth = 0;//Y轴标题的高度
    private int xAxisLengenHeight = 0;//X轴标题的高度
    private int mYMaxOffset = 0;

    public SimpleLineView(Context context) {
        this(context, null);
    }

    public SimpleLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SimpleLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        xLabels = new ArrayList<>();
        yLables = new ArrayList<>();
        y2Lables = new ArrayList<>();
        mChartPoints = new ArrayList<>();
        mChart2Points = new ArrayList<>();

        mRecordXMax = 0;//记录最大值
        mRecordXMin = 0;//记录最小值
        for (int i = 0; i < 100; i++) {
            yLables.add(String.valueOf(i));
            ChartPoint chartPoint;

            if (i == 5) {
                chartPoint = new ChartPoint(-new Random().nextInt(300), i, "第" + i + "個", "titile is " + i);
            } else {
                chartPoint = new ChartPoint(new Random().nextInt(300), i, "第" + i + "個", "titile is " + i);
            }
            mChartPoints.add(chartPoint);
            mRecordXMax = Math.max(mRecordXMax, chartPoint.getX());
            mRecordXMin = Math.min(mRecordXMin, chartPoint.getX());
        }

        for (int j = 0; j < 100; j++) {
            y2Lables.add(String.valueOf(j));
            ChartPoint chartPoint;
            chartPoint = new ChartPoint(-new Random().nextInt(300), j, "第" + j + "個", "titile is " + j);
            mChart2Points.add(chartPoint);
            mRecordXMax = Math.max(mRecordXMax, chartPoint.getX());
            mRecordXMin = Math.min(mRecordXMin, chartPoint.getX());
        }

        if (mRecordXMin == 0) {
            mRecordXMax = mRecordXMax + (5 - mRecordXMax % 5);
        }

        if (mRecordXMax == 0) {
            mRecordXMin = mRecordXMin - (5 + mRecordXMin % 5);
        }
        mXMaxInterval = mRecordXMax - mRecordXMin;
        initChart();
    }

    //初始化
    private void initChart() {
        //XY轴的画笔
        mXYPaint = new Paint();
        mXYPaint.setAntiAlias(true);
        mXYPaint.setStyle(Paint.Style.STROKE);
        mXYPaint.setColor(Color.BLACK);
        mXYPaint.setStrokeWidth(mXYLineWidth);

        //XY轴上文本的画笔
        mXYTextPaint = new Paint();
        mXYTextPaint.setAntiAlias(true);
        mXYTextPaint.setColor(Color.BLACK);
        mXYTextPaint.setTextSize(spToPx(12));
        //标题相关文字大小
        mLegenPaint = new Paint();
        mLegenPaint.setAntiAlias(true);
        mLegenPaint.setColor(Color.BLACK);
        mLegenPaint.setTextSize(spToPx(16));
        //边框的画笔
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(0xFFCCCCCC);
        mStrokePaint.setStrokeWidth(12);
        //坐标轴上的线
        mGridLinePaint = new Paint();
        mGridLinePaint.setAntiAlias(true);
        mGridLinePaint.setColor(0xFF9F9D9E);
        mGridLinePaint.setStrokeWidth(mXYLineWidth);
        mGridLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
        //画点的画笔
        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.RED);
        mPointPaint.setStyle(Paint.Style.FILL);
        //折线画笔
        mBrokenLinePaint = new Paint();
        mBrokenLinePaint.setAntiAlias(true);
        mBrokenLinePaint.setColor(Color.RED);
        mBrokenLinePaint.setStyle(Paint.Style.STROKE);
        //mBrokenLinePaint.setStrokeWidth(dpToPx(3));//放开会卡顿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {

            w = widthSize;
        } else {

            throw new IllegalArgumentException("宽度不能为Wrap");
        }

//        if (heightMode == MeasureSpec.EXACTLY) {
//
//            h = heightSize;
//        } else {
//
//            throw new IllegalAccessError("高度不能为Wrap");
//        }
        h = heightSize;
        mXLength = w - mPaddingRight - mPaddingLeft;//X轴的长度
        mXScall = mXLength / mXDegreeNumber;//X轴的间隔距离,每次都是25个小格子
        mYMaxOffset = mYScall * yLables.size() - h;
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (changed) {
            // Y轴文本动态宽都
            Rect yTextRect = getTextBounds("000", mXYTextPaint);//默认是三位数
            Rect xTextRect = getTextBounds("000", mXYTextPaint);//默认是三位数
            Rect yAxisTextRect = getTextBounds("Depth in meter", mLegenPaint);//获取Y轴标题的Rect
            Rect xAxisTextRect = getTextBounds("CheckSum", mLegenPaint);//获取X轴标题的Rect
            yTextWidth = yTextRect.width();
            xTextHeight = xTextRect.height();
            yAxisLengenWidth = yAxisTextRect.height();
            xAxisLengenHeight = xAxisTextRect.height();
            mXOriChange = yTextWidth + mXYLineWidth + mPaddingLeft;
            mYOriChange = xTextHeight + mXYLineWidth + xAxisLengenHeight + mPaddingTop;
            mXOri = yTextWidth + mXYLineWidth + mPaddingLeft;
            mYOri = xTextHeight + mXYLineWidth + xAxisLengenHeight + mPaddingTop;
            mXDuratioVaule = mXMaxInterval / 5;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //绘制坐标轴
        drawXYAxis(canvas);
        //绘制坐标点
        drawLineAndPoints(canvas);
    }

    //绘制先及坐标点
    private void drawLineAndPoints(Canvas canvas) {
        Path path = new Path();
        Path path1 = new Path();
        path.moveTo(mXOriChange, mYOriChange);
        int layertId = canvas.saveLayer(0, 0, w, h, null, Canvas.ALL_SAVE_FLAG);
        mPointPaint.setColor(Color.RED);
        mBrokenLinePaint.setColor(Color.RED);
        for (int i = 0; i < mChartPoints.size(); i++) {
            ChartPoint chartPoint = mChartPoints.get(i);
            float tempX = mXOriChange + (1 - (chartPoint.getX() - mRecordXMin) / (float) mXMaxInterval) * mXLength;
//            float tempX = mXOriChange + mXLength;
            float tempY = mYOriChange + i * mYScall;
            canvas.drawCircle(tempX, tempY, 6, mPointPaint);
            //连接坐标线
            path.lineTo(tempX, tempY);
            canvas.drawPath(path, mBrokenLinePaint);
        }
        path1.moveTo(mXOriChange, mYOriChange);
        mPointPaint.setColor(Color.BLUE);
        mBrokenLinePaint.setColor(Color.BLUE);
        for (int j = 0; j < mChart2Points.size(); j++) {
            ChartPoint chartPoint = mChart2Points.get(j);
            float tempX = mXOriChange + (1 - (chartPoint.getX() - mRecordXMin) / (float) mXMaxInterval) * mXLength;
//            float tempX = mXOriChange + mXLength;
            float tempY = mYOriChange + j * mYScall;
            canvas.drawCircle(tempX, tempY, 6, mPointPaint);
            //连接坐标线
            path1.lineTo(tempX, tempY);
            canvas.drawPath(path1, mBrokenLinePaint);
        }

        //裁剪超过坐标轴的折线和坐标点
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPointPaint.setXfermode(porterDuffXfermode);
        canvas.drawRect(new Rect((int) mXOri, (int) 0, (int) (mXOri + mXLength), (int) mYOri), mPointPaint);
        mPointPaint.setXfermode(null);
        canvas.restoreToCount(layertId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dis = event.getY() - startY;
                startY = event.getY();
                mYOriChange += dis;
                if (mYOriChange >= mYOri) {
                    mYOriChange = mYOri;
                }

                if (mYOriChange <= -(mYScall * 99 - h)) {
                    mYOriChange = -(mYScall * 99 - h);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //nothing todo
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * 绘制坐标轴
     */
    private void drawXYAxis(Canvas canvas) {
        Path path = new Path();
        float tempXByY = mXOriChange;
        float tempYByY = mYOriChange;
        float tempXByX = mXOri;
        float tempYByX = mYOri;
        //********************Y轴相关绘制********************
        //绘制Y轴，刻度，Y轴的标题
        path.moveTo(tempXByY, tempYByX);
        for (int i = 0; i < yLables.size(); i++) {
            //绘制Y轴的刻度点
            tempYByY = mYOriChange + i * mYScall;
            if (mYOri <= tempYByY) {
                //绘制文本
                if (i % 4 == 0) {
                    //加12是因为数值表示地方的刻度需要加长
                    canvas.drawLine(tempXByY, tempYByY, tempXByY + mAxislength + 12, tempYByY, mXYPaint);
                    canvas.drawText(yLables.get(i), mXOriChange - yTextWidth, tempYByY + xTextHeight / 2, mXYTextPaint);
                    canvas.drawLine(tempXByY + mAxislength, tempYByY, tempXByY + mAxislength + mXLength - 12, tempYByY, mGridLinePaint);
                } else {
                    canvas.drawLine(tempXByY, tempYByY, tempXByY + mAxislength, tempYByY, mXYPaint);
                }
                path.lineTo(tempXByY, tempYByY);
                canvas.drawPath(path, mXYPaint);
            }
        }
        //绘制Y轴竖直方向的文字
        canvas.save();
        canvas.translate(xTextHeight, h / 2);
        canvas.rotate(270);
        canvas.drawText("Depth in meter", 0, yAxisLengenWidth / 2, mLegenPaint);
        canvas.restore();
        //********************X轴相关绘制********************
        //绘制X轴及刻度
        path = new Path();
        path.moveTo(tempXByX, tempYByX);
        for (int i = 0; i <= mXDegreeNumber; i++) {

            tempXByX = mXOri + i * mXScall;
            canvas.drawLine(tempXByX, tempYByX, tempXByX, tempYByX + mAxislength, mXYPaint);
            if (i != 0) {
                canvas.drawLine(tempXByX, tempYByX + mAxislength, tempXByX, tempYByX + mAxislength + tempYByY, mGridLinePaint);
            }
            if (i % 5 == 0) {
                String xStr = String.valueOf(mRecordXMin + mXDuratioVaule * (i / 5));
                Rect rect = getTextBounds(xStr, mXYTextPaint);
                //20是文字和X轴之间的距离
                canvas.drawText(xStr, tempXByX - rect.width() / 2, tempYByX - 20, mXYTextPaint);
            }
        }
        path.lineTo(mXOri + mXLength, tempYByX);
        canvas.drawPath(path, mXYPaint);
        //绘制X轴的文字
        canvas.drawText("CheckSum", w / 2, xAxisLengenHeight, mLegenPaint);
        //绘制Y轴的封边线
        canvas.drawLine(mXOri + mXLength, mYOri, mXOri + mXLength, h, mXYPaint);
        //绘制X轴的封边线
        canvas.drawLine(mXOri, h - mXYLineWidth, mXOri + mXLength, h - mXYLineWidth, mXYPaint);
        //绘制Y轴的0线
        if (mRecordXMin < 0 && mRecordXMax > 0) {
            float tempX = mXOriChange + (Math.abs((0 - mRecordXMin) / (float) mXMaxInterval)) * mXLength;
            mXYPaint.setColor(Color.GREEN);
            canvas.drawLine(tempX, mYOri, tempX, h, mXYPaint);
            mXYPaint.setColor(Color.BLACK);
        }
    }

    //重新装载数据
    public void refreshByData() {

        yLables.clear();
        y2Lables.clear();
        mRecordXMax = 0;//记录最大值
        mRecordXMin = 0;//记录最小值
        for (int i = 0; i < 100; i++) {
            yLables.add(String.valueOf(i));
            ChartPoint chartPoint;

            if (i == 5) {
                chartPoint = new ChartPoint(-new Random().nextInt(200), i, "第" + i + "個", "titile is " + i);
            } else {
                chartPoint = new ChartPoint(new Random().nextInt(300), i, "第" + i + "個", "titile is " + i);
            }
            mChartPoints.add(chartPoint);
            mRecordXMax = Math.max(mRecordXMax, chartPoint.getX());
            mRecordXMin = Math.min(mRecordXMin, chartPoint.getX());
        }

        for (int j = 0; j < 100; j++) {
            y2Lables.add(String.valueOf(j));
            ChartPoint chartPoint;
            chartPoint = new ChartPoint(-new Random().nextInt(200), j, "第" + j + "個", "titile is " + j);
            mChart2Points.add(chartPoint);
            mRecordXMax = Math.max(mRecordXMax, chartPoint.getX());
            mRecordXMin = Math.min(mRecordXMin, chartPoint.getX());
        }

        if (mRecordXMin == 0) {
            mRecordXMax = mRecordXMax + (5 - mRecordXMax % 5);
        }

        if (mRecordXMax == 0) {
            mRecordXMin = mRecordXMin - (5 + mRecordXMin % 5);
        }
        mXMaxInterval = mRecordXMax - mRecordXMin;

        mXOriChange = mXOri;
        mYOriChange = mYOri;
        invalidate();
    }

    /**
     * dp转化成为px
     *
     * @param dp
     * @return
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * 获取丈量文本的矩形
     *
     * @param text
     * @param paint
     * @return
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * sp转化为px
     *
     * @param sp
     * @return
     */
    private int spToPx(int sp) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (scaledDensity * sp + 0.5f * (sp >= 0 ? 1 : -1));
    }
}
