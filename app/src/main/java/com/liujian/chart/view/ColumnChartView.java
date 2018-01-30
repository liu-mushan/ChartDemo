package com.liujian.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liujian.chart.bean.Data;
import com.liujian.chart.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义柱状图
 *
 * @author : liujian
 * @since : 2017/12/20
 */

public class ColumnChartView extends View {
    private static final String NAME = "直方图";
    private List<Data> mDataList;
    private Paint paint;
    private float maxHeight;
    private float width;
    private float space;
    private float startX = 0;
    private Rect rect;

    public ColumnChartView(Context context) {
        super(context);
        init();
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDataList = new ArrayList<>();
        Data data = new Data("Froyo", 10.0f, Color.GREEN);
        mDataList.add(data);
        data = new Data("ICS", 18.0f, Color.RED);
        mDataList.add(data);
        data = new Data("JB", 22.0f, Color.GREEN);
        mDataList.add(data);
        data = new Data("KK", 27.0f, Color.YELLOW);
        mDataList.add(data);
        data = new Data("L", 40.0f, Color.GREEN);
        mDataList.add(data);
        data = new Data("M", 60.0f, Color.BLUE);
        mDataList.add(data);
        data = new Data("N", 33.5f, Color.LTGRAY);
        mDataList.add(data);

        for (Data item : mDataList) {
            maxHeight = Math.max(maxHeight, item.getNumber());
        }

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText(NAME, (canvas.getWidth() - paint.measureText(NAME)) / 2, (float) (canvas.getHeight() * 0.9), paint);

        canvas.translate(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.7f);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawLine(0f, 0f, 0f, -canvas.getHeight() * 0.6f, paint);
        canvas.drawLine(0f, 0f, canvas.getWidth() * 0.8f, 0f, paint);

        width = (canvas.getWidth() * 0.8f - 100) / mDataList.size() * 0.8f;
        space = (canvas.getWidth() * 0.8f - 100) / mDataList.size() * 0.2f;
        startX = 0;

        paint.setStyle(Paint.Style.FILL);

        for (Data data : mDataList) {
            paint.setColor(data.getColor());
            canvas.drawRect(startX + space, -(data.getNumber() / maxHeight) * canvas.getHeight() * 0.5f,
                    startX + space + width, 0f, paint);

            paint.setTextSize(48);
            String text = String.valueOf(data.getNumber());
            canvas.drawText(text, startX + space +
                    ((width - paint.measureText(text)) / 2), -(data.getNumber() / maxHeight) * canvas.getHeight() * 0.5f - 20, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(36);
            canvas.drawText(data.getName(), startX + space +
                    ((width - paint.measureText(data.getName())) / 2), 50, paint);

            startX += (width + space);
        }
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

}
