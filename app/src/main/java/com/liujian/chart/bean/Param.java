package com.liujian.chart.bean;

import java.io.Serializable;

/**
 * 每次绘图的时候 如果后面没有数据 直接画直线到这个时间就可以了
 */
public class Param implements Serializable {
//             "last": "24199.74",
//            "duration": "9:30-11:30|13:00-15:00",
//            "length": 14400,
//            "until": 1450335600
    private double last ;
    private String duration ;
    private int length ;
    private long until ;

    public Param() {
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getUntil() {
        return until;
    }

    public void setUntil(long until) {
        this.until = until;
    }
}
