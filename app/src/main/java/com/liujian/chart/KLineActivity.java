package com.liujian.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.liujian.chart.bean.CrossBean;
import com.liujian.chart.bean.MyList;
import com.liujian.chart.bean.StickData;
import com.liujian.chart.utils.JsonDataUtils;
import com.liujian.chart.view.CrossView;
import com.liujian.chart.view.KChartView;

import java.util.ArrayList;

public class KLineActivity extends AppCompatActivity {
    private KChartView mChartView;
    private CrossView mCrossView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_line);
        mChartView = (KChartView) findViewById(R.id.chart_view);
        mCrossView = (CrossView) findViewById(R.id.cross_view);

        mChartView.setDataAndInvalidate((ArrayList<StickData>)new Gson().fromJson(JsonDataUtils.KLINE_JSON_DATA.toString(), MyList.class).data);

        mCrossView.setOnMoveListener(new CrossView.OnMoveListener() {
            @Override
            public void onMove(float x, float y) {
                mChartView.onCrossMove(x, y);
            }

            @Override
            public void onDismiss() {
            }
        });

        mChartView.setCrossDrawListener(new KChartView.OnCrossDrawListener() {
            @Override
            public void drawCrossLine(CrossBean bean) {
                mCrossView.drawLine(bean);
            }
        });
    }
}
