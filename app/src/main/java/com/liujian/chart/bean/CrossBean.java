package com.liujian.chart.bean;

import com.liujian.chart.utils.LineUtil;

/**
 * @author : liujian
 * @since : 2018/1/23
 */

public class CrossBean {
    public float x;
    public float y;
    //价格y轴
    public float priceY;
    //均线y轴
    public float averageY;
    //价格
    public String price;
    //时间
    public long time;
    //十字线显示时，指标左上的文字数组
    public String[] indexText;
    //对应indexText的颜色
    public int[] indexColor;

    public boolean isMinute;

    public boolean isDrawLeftText;

    public CrossBean(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String getTime() {
        return LineUtil.getShortDateJustHour(time);
    }

}
