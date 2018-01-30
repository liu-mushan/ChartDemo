package com.liujian.chart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.liujian.chart.utils.ScreenUtils;

/**
 * @author : liujian
 * @since : 2018/1/24
 */

public abstract class BaseChartView extends View {
    public static final float MAIN_HEIGHT_WEIGHT = 290f / 400f;
    private static final float VOLUME_HEIGHT_WEIGHT = 110f / 400f;
    protected int mWidth;
    protected int mHeight;
    protected float mMainHeight;
    protected float mXTimeHeight;
    protected float mVolumeHeight;

    private GestureDetector mGestureDetector;

    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mGestureDetector = new GestureDetector(context, listener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mXTimeHeight = ScreenUtils.sp2px(getContext(), 20);
        mMainHeight = (mHeight - mXTimeHeight) * MAIN_HEIGHT_WEIGHT;
        mVolumeHeight = (mHeight - mXTimeHeight) * VOLUME_HEIGHT_WEIGHT;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            onCrossMove(e.getX(), e.getY());
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return onViewScroll(e1, e2, distanceX, distanceY);
        }
    };

    protected boolean onViewScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public abstract void onCrossMove(float x, float y);
}
