package com.liujian.chart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.liujian.chart.bean.CrossBean;
import com.liujian.chart.utils.ColorUtil;
import com.liujian.chart.utils.ScreenUtils;

import static com.liujian.chart.view.BaseChartView.MAIN_HEIGHT_WEIGHT;

/**
 * @author : liujian
 * @since : 2018/1/23
 */

public class CrossView extends View {
    private GestureDetector mGestureDetector;
    private OnMoveListener onMoveListener;
    private CrossBean mCrossBean;
    private Paint paint;

    public interface OnMoveListener {
        void onMove(float x, float y);

        void onDismiss();
    }

    public CrossView(Context context) {
        this(context, null);
    }

    public CrossView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new CrossSimpleOnGestureListener());
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCrossBean == null) {
            return;
        }
        if (mCrossBean.x == 0 || mCrossBean.y == 0) {
            return;
        }
        canvas.drawLine(0, mCrossBean.y, canvas.getWidth(), mCrossBean.y, paint);
        canvas.drawLine(mCrossBean.x, 0, mCrossBean.x, canvas.getHeight(), paint);
        if (mCrossBean.isMinute) {
            //均线的时候才画出圆点
            //画十字线和均线价格线交汇的圆
            canvas.drawCircle(mCrossBean.x, mCrossBean.priceY, 10, paint);
            paint.setColor(ColorUtil.COLOR_SMA_LINE);
            canvas.drawCircle(mCrossBean.x, mCrossBean.averageY, 10, paint);
        }
        if (mCrossBean.isDrawLeftText) {
            //画价格textView
            drawPriceRect(canvas, mCrossBean.x, mCrossBean.y, mCrossBean.price, paint);
        }
        //画时间textView
        drawTimeRect(canvas, mCrossBean.x, mCrossBean.getTime(), paint);
    }

    private void drawTimeRect(Canvas canvas, float x, String time, Paint paint) {
        float textWidth = paint.measureText(time) + 20;
        float y = (getHeight() - ScreenUtils.dip2px(getContext(), 20)) * MAIN_HEIGHT_WEIGHT;

        Rect rect = new Rect();
        paint.getTextBounds(time, 0, time.length(), rect);
        float height = rect.height() + 8;
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2f);
        //1,先画白底
        float startX = x - textWidth / 2;
        float endX = x + textWidth / 2;
        if (startX < 0) {
            startX = 2f;
            endX = startX + textWidth;
        }
        if (endX > getWidth()) {
            endX = getWidth() - 2;
            startX = endX - textWidth;
        }
        canvas.drawRect(startX, y + 2, endX, y + height, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        //2，再画黑框
        canvas.drawRect(startX, y + 2, endX, y + height, paint);
        //3，写文字
        paint.setTextSize(30);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(time, startX + 5, y + height - 3, paint);
    }

    private void drawPriceRect(Canvas canvas, float x, float y, String price, Paint paint) {
        float textWidth = paint.measureText(price) + 10;
        Rect rect = new Rect();
        paint.getTextBounds(price, 0, price.length(), rect);

        float height = rect.height() + 5;
        float startY = y - height / 2f;
        float endY = y + height / 2f;
        if (startY < 0) {
            startY = 0f;
            endY = startY + height;
        } else if (endY > getHeight()) {
            endY = getHeight();
            startY = endY - height;
        }

        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2f);
        paint.setTextSize(30);
        if (x < textWidth) {
            //X轴在左侧，该框画在右侧
            //1,先画白底
            canvas.drawRect(getWidth() - textWidth, startY, getWidth(), endY, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            //2，再画黑框
            canvas.drawRect(getWidth() - textWidth, startY, getWidth(), endY, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(price, getWidth() - textWidth + 5, endY - 3, paint);
        } else {
            //X轴在右侧，改框画左侧
            canvas.drawRect(0, startY, textWidth, endY, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, startY, textWidth, endY, paint);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(price, 5f, endY - 3, paint);
        }
    }

    class CrossSimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            setVisibility(View.GONE);
            if (onMoveListener != null) {
                onMoveListener.onDismiss();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (onMoveListener != null) {
                onMoveListener.onMove(e2.getX(), e2.getY());
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void drawLine(CrossBean bean) {
        this.mCrossBean = bean;
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
        postInvalidate();
    }

    public void setOnMoveListener(OnMoveListener listener) {
        this.onMoveListener = listener;
    }
}
