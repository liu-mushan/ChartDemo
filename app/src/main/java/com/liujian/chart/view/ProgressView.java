package com.liujian.chart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.liujian.chart.utils.ScreenUtils;

/**
 * 转圈进度自定义view
 *
 * @author : liujian
 * @since : 2017/12/27
 */

public class ProgressView extends View {
    private Paint paint;
    private int progress = 0;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 14));

        startAnimator();
    }

    private void startAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 80);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(ScreenUtils.dip2px(getContext(), 14));
        canvas.drawArc(-ScreenUtils.dip2px(getContext(), 60), -ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                135, 270, false, paint);

        paint.setColor(Color.RED);
        float sweepAngle = (progress * 1.0f / 100) * 270;
        canvas.drawArc(-ScreenUtils.dip2px(getContext(), 60), -ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                ScreenUtils.dip2px(getContext(), 60) * 1.0f,
                135, sweepAngle, false, paint);

        paint.setTextSize(ScreenUtils.dip2px(getContext(), 24));
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        String text = String.valueOf(progress) + "%";
        Log.i("TAG", "onDraw: " + text);
        canvas.drawText(text, -getTextWidthAndHeight(text).width() / 2, getTextWidthAndHeight(text).height() / 2, paint);
    }

    private Rect getTextWidthAndHeight(String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }
}
