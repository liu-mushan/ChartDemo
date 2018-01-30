package com.liujian.chart.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分时所需要的 数据字段
 */
public class CMinute implements Serializable,Cloneable {
	private long time;
	//最新价
	private double price;
	//交易量
	private long count;
	//均价
	private double average ;
	//涨跌幅
	private double rate ;
	private double money ;

	public CMinute() {
	}

	public long getTime() {
		return time;
	}

	public String getTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			return sdf.format(new Date(time * 1000));
		} catch (Exception e) {
			return "--:--";
		}
	}
	public void setTime(long time) {
		this.time = time;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Object clone() {
		CMinute o = null;
		try {
			o = (CMinute) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
