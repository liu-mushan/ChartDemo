package com.liujian.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.liujian.chart.bean.CrossBean;
import com.liujian.chart.bean.MinuteResponseData;
import com.liujian.chart.utils.JsonDataUtils;
import com.liujian.chart.view.CrossView;
import com.liujian.chart.view.MinuteChartView;

public class MinuteChartActivity extends AppCompatActivity {
    private MinuteChartView mChartView;
    private CrossView mCrossView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);
        mChartView = (MinuteChartView) findViewById(R.id.chart_view);
        mCrossView = (CrossView) findViewById(R.id.cross_view);

        MinuteResponseData data = new Gson().fromJson(JsonDataUtils.MINUTE_JSON_DATA, MinuteResponseData.class);
        mChartView.setDataAndInvalidate(data);

        mCrossView.setOnMoveListener(new CrossView.OnMoveListener() {
            @Override
            public void onMove(float x, float y) {
                mChartView.onCrossMove(x, y);
            }

            @Override
            public void onDismiss() {
                mChartView.onCrossDismiss();
            }
        });

        mChartView.setCrossDrawListener(new MinuteChartView.OnCrossDrawListener() {
            @Override
            public void drawCrossLine(CrossBean bean) {
                mCrossView.drawLine(bean);
            }
        });
    }
}
