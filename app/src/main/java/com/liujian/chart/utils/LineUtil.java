package com.liujian.chart.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import com.liujian.chart.bean.CMinute;
import com.liujian.chart.bean.MinuteResponseData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author : liujian
 * @since : 2018/1/21
 */

public class LineUtil {
    /**
     * 获取分时线下的时间，需要展示的点数目
     *
     * @param duration 如9:00-11:00|13:00-15:00
     * @return 上面的是4小时，返回240
     */
    public static int getShowCount(String duration) {
        ArrayList<Integer> mins = getTimesMin(duration);
        switch (mins.size()) {
            case 2:
                return mins.get(1) - mins.get(0);
            case 4:
                return (mins.get(3) - mins.get(2)) + (mins.get(1) - mins.get(0));
            case 6:
                return (mins.get(5) - mins.get(4)) + (mins.get(3) - mins.get(2)) + (mins.get(1) - mins.get(0));
        }
        return 242;
    }

    /**
     * 获取开盘收盘时间对应的分钟数
     *
     * @param duration
     * @return
     */
    public static ArrayList<Integer> getTimesMin(String duration) {
        ArrayList<String> times = getTimes(duration);
        ArrayList<Integer> mins = new ArrayList<>();
        for (String s : times) {
            int min = getMin(s);
            mins.add(min);
        }
        return mins;
    }

    /**
     * 解析开收盘时间点
     *
     * @param duration 9:30-11:30|13:00-15:00(可能会有1、2、3段时间)
     * @return {"9:30", "11:30", "13:00", "15:00"}
     */
    public static ArrayList<String> getTimes(String duration) {
        ArrayList<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(duration))
            return result;
        if (duration.contains("|")) {
            String[] t1 = duration.split("\\|");
            for (String s : t1) {
                result.add(s.split("-")[0]);
                result.add(s.split("-")[1]);
            }
        } else {
            result.add(duration.split("-")[0]);
            result.add(duration.split("-")[1]);
        }
        return result;
    }

    /**
     * 传入时间和分钟如15:00，解析成分钟数，
     *
     * @param minStr 17:00
     * @return
     */
    public static int getMin(String minStr) throws NumberFormatException {
        return Integer.parseInt(minStr.split(":")[0]) * 60 + Integer.parseInt(minStr.split(":")[1]);
    }

    public static double[] getMaxAndMinByYd(double max, double min, double yd) {
        double distance = Math.abs(max - yd) > Math.abs(yd - min) ? Math.abs(max - yd) : Math.abs(yd - min);
        double[] result = new double[2];
        result[0] = yd + distance;
        result[1] = yd - distance;
        return result;
    }

    public static ArrayList<CMinute> getAllMinuteData(MinuteResponseData response) {
        ArrayList<CMinute> dataList = new ArrayList<CMinute>();
        if(response.getData() == null || response.getData().size() == 0){
            return  dataList;
        }
        dataList.addAll(response.getData());

        int nightStartMin = 0, nightStopMin = 0;
        int morningStartMin = 0, morningStopMin = 0, afternoonStartMin = 0, afternoonStopMin = 0;
        if (response.getParam() == null) return dataList;
        //停盘的 duration 9:30-11:30|13:00-15:00"
        //我这里全部转换成分钟
        String duration = response.getParam().getDuration();
        if (duration.contains("|")) {
            String ds[] = duration.split("\\|");
            if (ds[0].contains("-")) {
                //9:30-11:30
                String mins[] = ds[0].split("-");
                morningStartMin = getMin(mins[0]);
                morningStopMin = getMin(mins[1]);
            }
            if (ds[1].contains("-")) {
                //13:00-15:00
                String mins[] = ds[1].split("-");
                afternoonStartMin = getMin(mins[0]);
                afternoonStopMin = getMin(mins[1]);

            }
            if (ds.length == 3) {
                String mins[] = ds[2].split("-");
                nightStartMin = getMin(mins[0]);
                nightStopMin = getMin(mins[1]);
            }
        } else {
            if (duration.contains("-")) {
                //9:30-11:30
                String mins[] = duration.split("-");
                morningStartMin = getMin(mins[0]);
                afternoonStopMin = getMin(mins[1]);
            }
        }
        int drawCount = 0;
        //是否有夜盘
        boolean hasNight = nightStartMin > 0;
        if (!hasNight)
            drawCount = afternoonStopMin - morningStartMin - (afternoonStartMin - morningStopMin);
        else
            drawCount = nightStopMin - morningStartMin - (nightStartMin - afternoonStopMin) - (afternoonStartMin - morningStopMin);
        int firstMin = getMin(dataList.get(0).getTime());
        while (firstMin < morningStartMin && dataList != null && dataList.size() > 1) {
            dataList.remove(0);
            response.getData().remove(0);
            firstMin = getMin(dataList.get(0).getTime());
        }

        //服务器返回数据的第一个数据时间
        long firstLongTime = dataList.get(0).getTime();
        for (int i = 0; i < response.getData().size(); i++) {
            if (i == 0) {
                //1,先补全第一点到开盘时间中间的点
                int div = firstMin - morningStartMin - 1;
                if (div >= 1) {
                    for (int j = 0; j <= div; j++) {
                        int min = getMin(firstLongTime - (j + 1) * 60);
                        if(hasNight) {
                            if((min > morningStopMin && min < afternoonStartMin) || (min > afternoonStopMin && min < nightStartMin))
                                continue;
                        }else {
                            if(min > morningStopMin && min < afternoonStartMin && afternoonStartMin != 0)
                                continue;
                        }
                        CMinute temp = new CMinute();
                        temp.setTime(firstLongTime - (j + 1) * 60);
                        temp.setPrice(response.getParam().getLast());
                        dataList.add(0, temp);
                    }
                }
            } else {
                CMinute currentObject = response.getData().get(i);
                CMinute beforeObject = response.getData().get(i - 1);
                int currentMin = getMin(currentObject.getTime());
                int beforeMin = getMin(beforeObject.getTime());
                //当前时间 比上一次的时间要大2分钟   正常数据的时候是1分钟
                int div = currentMin - beforeMin - 1;
                //没有休盘时间或者是 在休盘时间外
                if (morningStopMin == 0 || currentMin <= morningStopMin || currentMin >= afternoonStartMin) {
                    for (int j = 0; j < div; j++) {
                        CMinute temp = (CMinute) beforeObject.clone();
                        temp.setTime(beforeObject.getTime() + ((j + 1) * 60));
                        temp.setCount(0);
                        temp.setMoney(0);
                        int tempMin = beforeMin + (j + 1);
                        if (morningStopMin > 0) {
                            //有停盘点
                            //没有夜盘
                            if (!hasNight) {
                                if (tempMin > morningStopMin && tempMin < afternoonStartMin) {
                                    //正好在停盘时间内
                                } else {
                                    dataList.add(i + dataList.size() - response.getData().size() + 1, temp);
                                }
                            } else {
                                //有夜盘
                                if ((tempMin > morningStopMin && tempMin < afternoonStartMin) || (tempMin > afternoonStopMin && tempMin < nightStartMin)) {
                                    //正好在停盘时间内
                                } else {
                                    dataList.add(i + dataList.size() - response.getData().size() + 1, temp);
                                }
                            }

                        } else {
                            //没有中间停盘时间
                            dataList.add(i + dataList.size() - response.getData().size() + 1, temp);
                        }
                    }
                }
            }
        }

        //until 画线最后到达的位置
        int until = getMin(response.getParam().getUntil());
        if (until <= (hasNight ? nightStopMin : afternoonStopMin) && dataList.size() < drawCount) {
            CMinute lasteObject = dataList.get(dataList.size() - 1);
            int lasteMin = getMin(lasteObject.getTime());
            int div = until - lasteMin - 1;
            if (div >= 1) {
                for (int j = 0; j <= div; j++) {
                    CMinute temp = new CMinute();
                    temp.setTime((lasteObject.getTime() + (j + 1) * 60));
                    temp.setPrice(lasteObject.getPrice());
                    temp.setAverage(lasteObject.getAverage());
                    if (morningStopMin > 0) {
                        //有停盘点
                        int tempMin = lasteMin + (j + 1);
                        if (hasNight) {
                            if ((tempMin > morningStopMin && tempMin < afternoonStartMin) || (tempMin > afternoonStopMin && tempMin < nightStartMin)) {
                                //正好在停盘时间内  不添加
                            } else {
                                dataList.add(dataList.size(), temp);
                            }
                        } else {
                            if (tempMin > morningStopMin && tempMin < afternoonStartMin) {
                                //正好在停盘时间内  不添加
                            } else {
                                dataList.add(dataList.size(), temp);
                            }
                        }

                    }

                }
            }
        }
        //20160801 解析后的时间有时错乱了，需要对时间进行排序
        //TODO 此处需要优化，应该从循环中发现哪里添加错误，而不是简单的进行排序，排序反而会消耗内存、时间，需要修改
        Collections.sort(dataList, new Comparator<CMinute>() {
            @Override
            public int compare(CMinute cMinute, CMinute t1) {
                return new Long(cMinute.getTime()).compareTo(new Long(t1.getTime()));
            }
        });
        return dataList;
    }

    /**
     * 通过long时间获取分钟数
     *
     * @param time
     * @return
     */
    public static int getMin(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time * 1000);
        return getMin(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }

    public static String getShortDateJustHour(long date) {
        String shortDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        try {
            Long time = new Long(date * 1000);
            shortDate = formatter.format(time);
        } catch (Exception e) {
            shortDate = null;
        }
        return shortDate;
    }

    /**
     * 获取数组中最大最小值
     * @param list1
     * @return
     */
    public static float[] getMaxAndMin(float[] list1, float[] list2) {
        float max = 0, min = 0;
        float[] f1 = getMaxAndMin(list1);
        float[] f2 = getMaxAndMin(list2);
        max = f1[0] > f2[0] ? f1[0] : f2[0];
        min = f1[1] < f2[1] ? f1[1] : f2[1];
        return new float[] {max, min};
    }

    /**
     * 获取数组中最大最小值
     * @param list
     * @return
     */
    public static float[] getMaxAndMin(float[] list) {
        if(list == null || list.length == 0) return new float[]{0, 0};
        float max = 0,min =0;
        float[] temp = list.clone();
        Arrays.sort(temp);
        max = temp[temp.length - 1];
        min = temp[0];
        return new float[] {max, min};
    }


    /**
     * 根据double  获取 万  亿  万亿
     *
     * @param d
     * @return
     */
    public static String getMoneyString(double d) {

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        double wan = 10000;
        double fwan = -10000;
        double yi = 100000000; //  一亿以下
        double fyi = -100000000; //  一亿以下
        double wyi = 1000000000000d; //  万亿以下
        double fwyi = -1000000000000d; //  万亿以下

        if (d < wan && d > fwan) {
            return df.format(d);
        } else if (d < yi && d > fyi) {
            return df.format(d / Math.pow(10, 4)) + "万";
        } else if (d < wyi && d > fwyi) {
            return df.format(d / Math.pow(10, 8)) + "亿";
        } else {
            return df.format(d / Math.pow(10, 12)) + "万亿";
        }

    }
}
