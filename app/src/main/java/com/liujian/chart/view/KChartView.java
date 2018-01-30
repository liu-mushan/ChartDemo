package com.liujian.chart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.liujian.chart.bean.CrossBean;
import com.liujian.chart.bean.StickData;
import com.liujian.chart.canvas.CanvasDrawUtils;
import com.liujian.chart.utils.LineUtil;

import java.util.ArrayList;

/**
 * @author : liujian
 * @since : 2018/1/13
 */

public class KChartView extends BaseChartView {
    //烛形图加空白的宽度和烛形图宽度之比
    public static final float WIDTH_SCALE = 1.2f;
    //烛形图和右侧空白的宽度
    public float mDefaultWidth = 19;
    //K线所有数据
    private ArrayList<StickData> data;
    //K线展示的数据
    private ArrayList<StickData> showList = new ArrayList<>();
    //一屏烛形图数量
    private int drawCount;
    //每两个烛形图x轴的距离
    private float candleXDistance;

    //当前画图偏移量（往右滑动之后）
    private int offset;
    //y轴最大值
    protected double yMax;
    //y轴最小值
    protected double yMin;

    protected float yUnit;
    protected float xUnit;

    public interface OnCrossDrawListener {
        void drawCrossLine(CrossBean ban);
    }

    private OnCrossDrawListener listener;

    public void setCrossDrawListener(OnCrossDrawListener listener) {
        this.listener = listener;
    }

    private ScaleGestureDetector scaleGestureDetector;

    public KChartView(Context context) {
        this(context, null);
    }

    public KChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaleGestureDetector = new ScaleGestureDetector(context, new SimpleScaleListener());
    }

    public void setDataAndInvalidate(ArrayList<StickData> data) {
        this.data = data;
        offset = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.size() == 0)
            return;

        initData();
        //1.画网格线
        CanvasDrawUtils.drawGrid(canvas, mWidth, mMainHeight);
        //2，画X轴时间
        CanvasDrawUtils.drawKLineXTime(canvas, showList.get(0).getTime(), showList.get(showList.size() - 1).getTime(), mWidth, mMainHeight);
        //3，画Y轴价格
        CanvasDrawUtils.drawKLineYPrice(canvas, yMax, yMin, mMainHeight);
        //4，画K线
        drawCandles(canvas);
    }

    private void drawCandles(Canvas canvas) {
        if (data == null || data.size() == 0)
            return;
        float x = 0;
        if (showList == null || showList.size() == 0)
            return;
        //计算出页面能显示多少个
        for (int i = 0; i < showList.size(); i++) {
            if (drawCount < data.size()) {
                x = mWidth - (mWidth / drawCount * (showList.size() - i));
            } else {
                x = (mWidth / drawCount * i);
            }
            CanvasDrawUtils.drawCandle(canvas,
                    parseNumber(showList.get(i).getHigh()),
                    parseNumber(showList.get(i).getLow()),
                    parseNumber(showList.get(i).getOpen()),
                    parseNumber(showList.get(i).getClose()),
                    x,
                    parseNumber(showList.get(i).getHigh()),
                    candleXDistance,
                    mWidth);
        }
    }

    protected void initData() {
        if (data == null) {
            return;
        }
        drawCount = (int) (mWidth / mDefaultWidth);
        candleXDistance = drawCount * WIDTH_SCALE;
        if (data.size() > 0) {
            if (drawCount < data.size()) {
                getShowList(offset);
            } else {
                showList.clear();
                showList.addAll(data);
            }
        }
        if (showList == null)
            return;
        float[] low = new float[showList.size()];
        float[] high = new float[showList.size()];
        int i = 0;
        for (StickData d : showList) {
            low[i] = (float) d.getLow();
            high[i] = (float) d.getHigh();
            i++;
        }
        float[] maxAndMin = LineUtil.getMaxAndMin(low, high);
        yMax = maxAndMin[0];
        yMin = maxAndMin[1];
        yUnit = (float) (yMax - yMin) / mMainHeight;
        xUnit = mWidth / drawCount;
    }

    @Override
    public void onCrossMove(float x, float y) {
        if (showList == null)
            return;
        int position = (int) Math.rint(x / mDefaultWidth);
        if (position < showList.size()) {
            StickData data = showList.get(position);
            CrossBean bean = new CrossBean(x, y);
            bean.price = data.getClose() + "";
            bean.time = data.getTimeLone();
            if (listener != null) {
                listener.drawCrossLine(bean);
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }


    @Override
    protected boolean onViewScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (data != null && drawCount < data.size() && Math.abs(distanceX) > mDefaultWidth) {
            int temp = offset + (int) (0 - distanceX / mDefaultWidth);
            if (temp > 0 && temp + drawCount <= data.size()) {
                offset = temp;
                invalidate();
                return true;
            }
        }
        return false;
    }

    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (data == null) {
                return super.onScale(detector);
            }
            //放大是由1变大，缩小是由1变小
            float scale = detector.getScaleFactor();
            //这个变化太快，把scale变慢一点
            scale = 1 + ((scale - 1) * 0.2f);
            drawCount = (int) (mWidth / mDefaultWidth);
            if (scale < 1 && drawCount >= data.size() || scale > 1 && drawCount < 50) {
                return super.onScale(detector);
            }
            mDefaultWidth = mDefaultWidth * scale;
            invalidate();
            return super.onScale(detector);
        }
    }

    private void getShowList(int offset) {
        if (offset != 0 && data.size() - drawCount - offset < 0) {
            offset = data.size() - drawCount;
        }
        showList.addAll(data.subList(data.size() - drawCount - offset, data.size() - offset));
    }


    /**
     * 把传入的参数计算成坐标，直接展示到界面上
     *
     * @param input
     * @return 返回里面的StickData的最高价最低价，都是可以直接显示在坐标上的
     */
    private float parseNumber(double input) {
        return mMainHeight - (float) ((input - yMin) / yUnit);
    }
}
