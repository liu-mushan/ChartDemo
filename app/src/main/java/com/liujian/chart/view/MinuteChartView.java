package com.liujian.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.liujian.chart.bean.CMinute;
import com.liujian.chart.bean.CrossBean;
import com.liujian.chart.bean.MinuteResponseData;
import com.liujian.chart.canvas.CanvasDrawUtils;
import com.liujian.chart.utils.ColorUtil;
import com.liujian.chart.utils.LineUtil;
import com.liujian.chart.utils.ScreenUtils;

import java.util.List;

/**
 * 自定义分时线
 *
 * @author : liujian
 * @since : 2018/1/14
 */

public class MinuteChartView extends BaseChartView {
    private static final int PADDING = 20;
    //数据
    private MinuteResponseData mData;
    //补全后所有点
    private List<CMinute> minutes;
    //所有价格
    private float[] price;
    //所有均线数据
    private float[] average;
    //分时线昨收
    private double close;
    //最大值
    private double yMax;
    //最小值
    private double yMin;
    //X轴单位距离
    private float xUnit;

    public interface OnCrossDrawListener {
        void drawCrossLine(CrossBean ban);
    }

    private OnCrossDrawListener listener;

    public void setCrossDrawListener(OnCrossDrawListener listener) {
        this.listener = listener;
    }

    public MinuteChartView(Context context) {
        super(context);
    }

    public MinuteChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MinuteChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCrossMove(float x, float y) {
        if (y > mMainHeight && y < mMainHeight + mXTimeHeight) {
            return;
        }
        int position = (int) Math.rint(new Double(x) / new Double(xUnit));
        if (position < minutes.size()) {
            CMinute cMinute = minutes.get(position);
            CrossBean bean = new CrossBean(x, y);
            bean.priceY = (float) getY(cMinute.getPrice());
            bean.averageY = (float) getY(cMinute.getAverage());
            bean.price = cMinute.getPrice() + "";
            bean.time = cMinute.getTime();
            bean.isMinute = true;
            bean.isDrawLeftText = y <= mMainHeight;
            //setIndexTextAndColor(position, cMinute, bean);
            if (listener != null) {
                listener.drawCrossLine(bean);
            }
        }
    }

    public void onCrossDismiss() {

    }

    //获取价格对应的Y轴
    private double getY(double price) {
        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, close);
        if (price == maxAndMin[0])
            return ScreenUtils.dip2px(getContext(), PADDING);
        if (price == maxAndMin[1])
            return mMainHeight - ScreenUtils.dip2px(getContext(), 20);
        return mMainHeight - (new Float(price) - maxAndMin[1]) / ((maxAndMin[0] - maxAndMin[1]) /
                (mMainHeight - ScreenUtils.dip2px(getContext(), PADDING * 2))) - ScreenUtils.dip2px(getContext(), PADDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null) {
            return;
        }
        xUnit = canvas.getWidth() / LineUtil.getShowCount(mData.getParam().getDuration());
        //1.画网格线
        CanvasDrawUtils.drawGridLine(canvas, mWidth, mMainHeight, mData.getParam().getDuration());
        CanvasDrawUtils.drawVolumeGridLine(canvas, mHeight - mVolumeHeight, mWidth, mVolumeHeight, mData.getParam().getDuration());

        //2.画X与Y轴Text文本信息
        CanvasDrawUtils.drawYPercentAndPrice(canvas, mWidth, mMainHeight, yMax, yMin, close, ScreenUtils.dip2px(getContext(), PADDING));
        CanvasDrawUtils.drawXText(canvas, mWidth, mMainHeight, mXTimeHeight, mData.getParam().getDuration());

        //3.画分时线
        drawMinutePriceLine(canvas);
        //4.画均线
        drawAverageLine(canvas);

        //5.画成交量数据
        drawVolumeChart(canvas);
    }

    private void drawVolumeChart(Canvas canvas) {
        long max = 0;
        for (CMinute minute : minutes) {
            max = minute.getCount() > max ? minute.getCount() : max;
        }
        CanvasDrawUtils.drawVolumeRect(canvas, xUnit, mHeight - mVolumeHeight, mVolumeHeight, max, close, minutes);
    }

    private void drawMinutePriceLine(Canvas canvas) {
        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, close);
        CanvasDrawUtils.drawLines(canvas, price, xUnit, mMainHeight - 2 * ScreenUtils.dip2px(getContext(), PADDING),
                ColorUtil.COLOR_PRICE_LINE, (float) maxAndMin[0],
                (float) maxAndMin[1], ScreenUtils.dip2px(getContext(), PADDING), 0);
    }

    private void drawAverageLine(Canvas canvas) {
        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, close);
        CanvasDrawUtils.drawLines(canvas, average, xUnit, mMainHeight- 2 * ScreenUtils.dip2px(getContext(), PADDING),
                ColorUtil.COLOR_SMA_LINE, (float) maxAndMin[0],
                (float) maxAndMin[1], ScreenUtils.dip2px(getContext(), PADDING), 0);
    }

    public void setDataAndInvalidate(MinuteResponseData data) {
        this.mData = data;
        initData();
        invalidate();
    }

    private void initData() {
        if (mData == null) {
            return;
        }
        //补点逻辑
        minutes = LineUtil.getAllMinuteData(mData);
        close = mData.getParam().getLast();
        //计算最大最小值
        for (int i = 0; i < mData.getData().size(); i++) {
            if (i == 0) {
                yMax = mData.getData().get(i).getPrice();
                yMin = mData.getData().get(i).getPrice();
            }
            CMinute c = mData.getData().get(i);
            yMax = c.getPrice() > yMax ? c.getPrice() : yMax;
            yMax = c.getAverage() > yMax ? c.getAverage() : yMax;
            if (c.getPrice() != 0) {
                yMin = c.getPrice() < yMin ? c.getPrice() : yMin;
            }
            if (c.getAverage() != 0 && c.getAverage() != 0.01) {
                yMin = c.getAverage() < yMin ? c.getAverage() : yMin;
            }
        }

        price = new float[minutes.size()];
        average = new float[minutes.size()];
        for (int i = 0; i < minutes.size(); i++) {
            price[i] = (float) minutes.get(i).getPrice();
            average[i] = (float) minutes.get(i).getAverage();
        }
    }


}
