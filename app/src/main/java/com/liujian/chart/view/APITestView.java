package com.liujian.chart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author : liujian
 * @since : 2018/1/26
 */

public class APITestView extends View {
    private Paint paint;
    private Path path;
    private Path desPath;
    private PathMeasure pathMeasure;

    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private float length;

    public APITestView(Context context) {
        this(context, null);
    }

    public APITestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public APITestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        path = new Path();
        desPath = new Path();
        pathMeasure = new PathMeasure();

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        desPath.reset();
        //解决4.4之前的版本默认开启硬件加速导致的一个Bug
        desPath.lineTo(0, 0);

        path.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 150, Path.Direction.CW);
        pathMeasure.setPath(path, true);
        length = pathMeasure.getLength();

        //startMoveTo为true，截取的path片段保持原样
        float stop = length * mAnimatorValue;
        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * length));
        pathMeasure.getSegment(start, stop, desPath, true);
        canvas.drawPath(desPath, paint);
    }
}
