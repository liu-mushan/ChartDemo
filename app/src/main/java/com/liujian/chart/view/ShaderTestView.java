package com.liujian.chart.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liujian.chart.R;

/**
 * @author : liujian
 * @since : 2018/1/29
 */

public class ShaderTestView extends View {
    private Paint paint;
    private LinearGradient linearGradient;
    private RadialGradient radialGradient;

    private SweepGradient sweepGradient;
    private BitmapShader bitmapShader;

    public ShaderTestView(Context context) {
        this(context, null);
    }

    public ShaderTestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShaderTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        linearGradient = new LinearGradient(100, 300, 400, 700, Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);

        radialGradient = new RadialGradient(800, 500, 200, Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);

        sweepGradient = new SweepGradient(300, 1000, Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"));

        bitmapShader = new BitmapShader(BitmapFactory.decodeResource(getResources(), R.mipmap.sample_2), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setShader(linearGradient);
        canvas.drawCircle(300, 500, 200, paint);

        paint.setShader(radialGradient);
        canvas.drawCircle(800, 500, 200, paint);

        paint.setShader(sweepGradient);
        canvas.drawCircle(300, 1000, 200, paint);

        paint.setShader(bitmapShader);
        canvas.drawCircle(800, 1000, 200, paint);
    }
}
