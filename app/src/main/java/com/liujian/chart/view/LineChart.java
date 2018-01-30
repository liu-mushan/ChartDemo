package com.liujian.chart.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liujian.chart.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义一个折线图
 *
 * @author : liujian
 * @since : 2017/12/26
 */

public class LineChart extends View {
    private Paint paint;
    //写死圆的半径
    private static final int POINT_RADIO = 12;

    private String[] mXAxis = {"1", "2", "3", "4", "5", "6", "7"};
    private String[] mYAxis = {"10", "20", "30", "40", "50", "60"};
    private List<Float> mDataList = new ArrayList();

    private float mXSpace;//X轴的间距
    private float mYSpace;//Y轴的间距

    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //写几个随机数据，0-50
        for (String mXAxi : mXAxis) {
            mDataList.add((float) (Math.random() * 60));
        }

        paint = new Paint();
        //写死
        paint.setTextSize(ScreenUtils.dip2px(getContext(), 16));
        paint.setColor(Color.parseColor("#66FF99"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //如果是warp_content，指定一个宽高
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = ScreenUtils.getWidth(getContext());
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = ScreenUtils.getHeight(getContext()) / 2 - ScreenUtils.dip2px(getContext(), 50);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //支持Padding
        int width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        int height = canvas.getHeight() - getPaddingBottom() - getPaddingTop();
        mXSpace = width * 0.7f / 7;
        mYSpace = height * 0.7f / 6;

        //移动绘制的原点
        canvas.translate(width * 0.15f, height * 0.9f - getPaddingBottom());

        //绘制坐标轴
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, 0, -height * 0.7f - 100, paint);
        canvas.drawLine(0, 0, width * 0.7f + 100, 0, paint);

        for (int i = 0; i < mXAxis.length; i++) {
            canvas.drawText(mXAxis[i], (i + 1) * mXSpace - paint.measureText(mXAxis[i]) / 2, getXAxisHeight() + 30, paint);
        }
        for (int i = 0; i < mYAxis.length; i++) {
            canvas.drawText(String.valueOf(mYAxis[i]), -getYAxisWidth() - 40, -(i + 1) * mXSpace + paint.measureText(String.valueOf(mYAxis[i])) / 2, paint);
        }
        paint.setColor(Color.parseColor("#A4D3EE"));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < mDataList.size(); i++) {
            if (i > 0) {
                canvas.drawLine(i * mXSpace, -mYSpace * (mDataList.get(i - 1) / 10), (i + 1) * mXSpace, -mYSpace * (mDataList.get(i) / 10), paint);
            }
            canvas.drawCircle((i + 1) * mXSpace, -mYSpace * (mDataList.get(i) / 10), POINT_RADIO, paint);
        }
    }

    /**
     * 获取X轴TextView的宽度
     *
     * @return int
     */
    private int getXAxisHeight() {
        String text = "1234";
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    /**
     * 获取Y轴坐标TextView的宽度
     *
     * @return int
     */
    private int getYAxisWidth() {
        String text = "34";
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }


    private class Point {
        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
