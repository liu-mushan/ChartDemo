package com.liujian.chart.canvas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.liujian.chart.BaseApplication;
import com.liujian.chart.bean.CMinute;
import com.liujian.chart.utils.ColorUtil;
import com.liujian.chart.utils.LineUtil;
import com.liujian.chart.utils.ScreenUtils;
import com.liujian.chart.view.KChartView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author : liujian
 * @since : 2018/1/21
 */

public class CanvasDrawUtils {
    private static DecimalFormat sharesFormat2;

    static {
        Locale english = new Locale("en", "US");
        sharesFormat2 = (DecimalFormat) NumberFormat.getNumberInstance(english);
        sharesFormat2.applyPattern("#############0.00");
    }

    /**
     * 画网格
     */
    public static void drawGrid(Canvas canvas, float width, float height) {
        if (canvas == null) {
            Log.w("DrawUtils", "canvas为空");
            return;
        }
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(new DashPathEffect(
                new float[]{8, 8, 8, 8}, 1));
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        //横虚线
        canvas.drawLine(0, height * 3 / 4, width, height * 3 / 4, p);
        canvas.drawLine(0, height * 1 / 4, width, height * 1 / 4, p);
        //竖虚线
        canvas.drawLine(width * 3 / 4, 0, width * 3 / 4, height, p);
        canvas.drawLine(width * 1 / 4, 0, width * 1 / 4, height, p);
        p.reset();
        p.setColor(Color.GRAY);
        //中间实线
        canvas.drawLine(0, height * 2 / 4, width, height * 2 / 4, p);
        canvas.drawLine(width * 2 / 4, 0, width * 2 / 4, height, p);
        //四周线
        //下
        canvas.drawLine(0, height - 1, width, height - 1, p);
        //上
        canvas.drawLine(0, 0, width, 0, p);
        //右
        canvas.drawLine(width - 1, 0, width - 1, height, p);
        //左
        canvas.drawLine(0, 0, 0, height, p);
        p.reset();
    }

    public static void drawGridLine(Canvas canvas, float width, float height, String duration) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8f, 8f, 8f, 8f}, 1f));

        canvas.drawLine(0, height / 4f, width, height / 4f, paint);
        canvas.drawLine(0, height * 3f / 4f, width, height * 3f / 4f, paint);
        canvas.drawLine(0, height / 2f, width, height / 2f, paint);
        canvas.drawLine(width / 4f, 0, width / 4f, height, paint);
        canvas.drawLine(width * 3f / 4f, 0, width * 3f / 4f, height, paint);

        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        canvas.drawLine(0, 0, width, 0, paint);
        canvas.drawLine(0, height, width, height, paint);

        if (LineUtil.getTimes(duration).size() == 2) {
            paint.setPathEffect(new DashPathEffect(new float[]{8f, 8f, 8f, 8f}, 1f));
        }
        canvas.drawLine(width / 2f, 0, width / 2f, height, paint);
    }


    public static void drawVolumeGridLine(Canvas canvas, float y, float width, float height, String duration) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[]{20, 20, 20, 20}, 0));
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        //横虚线
        canvas.drawLine(0, y + height / 2, width, y + height / 2, p);
        //竖虚线
        canvas.drawLine(width / 4f, y, width / 4f, y + height, p);
        canvas.drawLine(width * 3f / 4f, y, width * 3f / 4f, y + height, p);

        p.reset();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);

        if (LineUtil.getTimes(duration).size() == 2) {
            p.setPathEffect(new DashPathEffect(new float[]{20f, 20f, 20f, 20f}, 0f));
        }
        //中间实线
        canvas.drawLine(width / 2f, y, width / 2f, y + height, p);

        //四周线
        canvas.drawLine(0, y + height - 1, width, y + height - 1, p);
        canvas.drawLine(0, y, width, y, p);
        p.reset();
    }

    public static void drawYPercentAndPrice(Canvas canvas, float mWidth, float mMainHeight,
                                            double yMax, double yMin, double close, double padding) {
        double upPercent = Math.abs((yMax - close) / close);
        double downPercent = Math.abs((yMin - close) / close);
        double maxPercent = upPercent > downPercent ? upPercent : downPercent;

        double maxValue = Math.abs(yMax) > Math.abs(yMin) ? Math.abs(yMax) : Math.abs(yMax);
        double distance = Math.abs(yMax - close) > Math.abs(yMin - close) ?
                Math.abs(yMax - close) : Math.abs(yMax - close);

        String topPrice = String.valueOf(maxValue);
        String closePrice = String.valueOf(close);
        String bottomPrice = String.valueOf(maxValue - distance);
        String topPercent = String.valueOf(sharesFormat2.format(maxPercent * 100)) + "%";
        String closePercent = "0.00%";
        String bottomPercent = String.valueOf(sharesFormat2.format(-maxPercent * 100)) + "%";

        Paint paint = new Paint();

        paint.setTextSize(ScreenUtils.dip2px(BaseApplication.INSTANCE, 10));
        paint.setColor(ColorUtil.getChangeTextColor(1));

        Rect rect = new Rect();
        paint.getTextBounds(topPercent, 0, topPercent.length(), rect);
        canvas.drawText(topPercent, mWidth - rect.width(), (float) (padding + rect.height() / 2f), paint);

        paint.getTextBounds(topPrice, 0, topPrice.length(), rect);
        canvas.drawText(topPrice, 0, (float) (padding + rect.height() / 2f), paint);

        paint.setColor(ColorUtil.getChangeTextColor(0));
        paint.getTextBounds(closePercent, 0, closePercent.length(), rect);
        canvas.drawText(closePercent, mWidth - rect.width(), mMainHeight / 2 + rect.height() / 2, paint);

        paint.getTextBounds(closePrice, 0, closePrice.length(), rect);
        canvas.drawText(closePrice, 0, mMainHeight / 2 + rect.height() / 2, paint);

        paint.setColor(ColorUtil.getChangeTextColor(-1));
        paint.getTextBounds(bottomPercent, 0, bottomPercent.length(), rect);
        canvas.drawText(bottomPercent, mWidth - rect.width(), (float) (mMainHeight - padding + rect.height() / 2), paint);

        paint.getTextBounds(bottomPrice, 0, bottomPrice.length(), rect);
        canvas.drawText(bottomPrice, 0, (float) (mMainHeight - padding + rect.height() / 2), paint);
    }

    /**
     * K线：Y轴价格
     *
     * @param canvas
     * @param max
     * @param min
     * @param height
     */
    public static void drawKLineYPrice(Canvas canvas, double max, double min, float height) {
        double diff = max - min;
        String p1 = LineUtil.getMoneyString(max);
        String p2 = LineUtil.getMoneyString(min + diff * 3 / 4);
        String p3 = LineUtil.getMoneyString(min + diff * 2 / 4);
        String p4 = LineUtil.getMoneyString(min + diff * 1 / 4);
        String p5 = LineUtil.getMoneyString(min);
        Paint p = new Paint();
        p.setColor(ColorUtil.getChangeTextColor(1));
        p.setTextSize(30);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p1, 0, 25, p);
        canvas.drawText(p2, 0, height * 1 / 4, p);
        canvas.drawText(p3, 0, height * 2 / 4, p);
        canvas.drawText(p4, 0, height * 3 / 4, p);
        canvas.drawText(p5, 0, height, p);
    }

    public static void drawKLineXTime(Canvas canvas, String s1, String s2, float width, float height) {
        //上面偏移一点
        height = height + 25;
        Paint p = new Paint();
        p.setTextSize(30);
        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(s1, 0, height, p);
        p.setTextAlign(Paint.Align.RIGHT);
        if (s2 != null)
            canvas.drawText(s2, width, height, p);
    }

    public static void drawXText(Canvas canvas, float mWidth, float mMainHeight,
                                 float mXTimeHeight, String duration) {
        ArrayList<String> times = LineUtil.getTimes(duration);
        String leftText = "";
        String middleText = "";
        String rightText = "";
        if (times == null || times.isEmpty()) {
            return;
        }
        if (times.size() == 2) {
            leftText = times.get(0);
            rightText = times.get(1);
        }
        if (times.size() == 4) {
            leftText = times.get(0);
            rightText = times.get(3);
            middleText = times.get(1) + "|" + times.get(2);
        }

        Paint paint = new Paint();
        paint.setTextSize(ScreenUtils.dip2px(BaseApplication.INSTANCE, 12));
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        Rect rect = new Rect();
        paint.getTextBounds(leftText, 0, leftText.length(), rect);
        canvas.drawText(leftText, 0, mMainHeight + mXTimeHeight / 2 + rect.height() / 2, paint);

        canvas.drawText(middleText, (mWidth - paint.measureText(middleText)) / 2,
                mMainHeight + mXTimeHeight / 2 + rect.height() / 2, paint);

        canvas.drawText(rightText, mWidth - paint.measureText(rightText), mMainHeight + mXTimeHeight / 2 + rect.height() / 2, paint);
    }

    /**
     * 画线
     *
     * @param canvas
     * @param prices  价格
     * @param xUnit   x轴每两点距离
     * @param height  图绘制区域盖度
     * @param color   颜色
     * @param max     价格最大值
     * @param min     价格最小值,根据最大值最小值确定y轴每一格的高度
     * @param yOffset Y轴偏移量
     * @param xOffset X轴偏移量
     */
    public static void drawLines(Canvas canvas, float[] prices, float xUnit,
                                 float height, int color, float max, float min,
                                 float yOffset, float xOffset) {
        if (canvas == null) {
            return;
        }
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStrokeWidth(4.0f);
        canvas.drawLines(getLines(prices, xUnit, height, max, min, yOffset, xOffset), p);
        p.reset();
    }


    /**
     * 传入价格和
     *
     * @param prices 价格
     * @param xUnit  x轴每两点距离
     * @param height 控件高度
     * @param max    价格最大值
     * @param min    价格最小值
     * @return
     */
    public static float[] getLines(float[] prices, float xUnit, float height,
                                   float max, float min,
                                   float y, float xOffset) {
        float[] result = new float[prices.length * 4];
        float yUnit = (max - min) / height;
        for (int i = 0; i < prices.length - 1; i++) {
            //排除起点为0的点
            result[i * 4 + 0] = xOffset + i * xUnit;
            result[i * 4 + 1] = y + height - (prices[i] - min) / yUnit;
            result[i * 4 + 2] = xOffset + (i + 1) * xUnit;
            result[i * 4 + 3] = y + height - (prices[i + 1] - min) / yUnit;
        }
        return result;
    }

    public static void drawVolumeRect(Canvas canvas, float xUnit, float y, float height,
                                      long max, double close, List<CMinute> minutes) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(ScreenUtils.dip2px(BaseApplication.INSTANCE, 12));
        p.setStyle(Paint.Style.FILL);
        float yUnit = height / max;
        for (int i = 0; i < minutes.size(); i++) {
            if (minutes.get(i).getCount() != 0) {
                int color = 0;
                if (i == 0) {
                    color = ColorUtil.getChangeTextColor(minutes.get(i).getPrice() > close ? 1 : -1);
                } else {
                    color = ColorUtil.getChangeTextColor(minutes.get(i).getPrice() > minutes.get(i - 1).getPrice() ? 1 : -1);
                }
                p.setColor(color);
                canvas.drawRect(xUnit * (i + 0.05f), y + (height - minutes.get(i).getCount() * yUnit), xUnit * (i + 0.95f), y + height, p);
            }
        }

        canvas.drawText(max / 2 + "", 0, y + height / 2, p);
    }

    /**
     * 画烛形图
     *
     * @param canvas
     * @param maxY   最高 传入计算完成的值（对应x,y轴）max/yUnit
     * @param minY   最低 传入计算完成的值（对应x,y轴）
     * @param openY  开盘 传入计算完成的值（对应x,y轴）
     * @param closeY 收盘 传入计算完成的值（对应x,y轴）
     * @param x      x轴 烛形图左上x坐标
     * @param y      y轴坐标 烛形图左上y坐标
     * @param width  屏幕宽度
     */
    public static void drawCandle(Canvas canvas, float maxY, float minY, float openY, float closeY, float x, float y, float drawCount, float width) {
        if (canvas == null) {
            Log.w("DrawUtils", "canvas为空");
            return;
        }
        //当在坐标系之外，不画
        if (x < 0 || y < 0) return;
        float xUnit = width / drawCount;
        float diff = xUnit - xUnit / KChartView.WIDTH_SCALE;
        //是否上涨,由于计算成了Y轴坐标，所以上面的小，下面的大
        boolean isRise = closeY <= openY;
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ColorUtil.getChangeTextColor(isRise ? 1 : -1));
        p.setStrokeWidth(2.0f);
        canvas.drawLine(x + xUnit / 2, y, x + xUnit / 2, y + (minY - maxY) + 1, p);
        canvas.drawRect(x + diff / 2, y + ((!isRise ? closeY : openY) - maxY), x + xUnit - diff / 2, y + ((!isRise ? openY : closeY) - maxY) + 1, p);
        p.reset();
    }
}
