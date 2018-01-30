package com.liujian.chart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onShaderTest(View view) {
        Intent intent = new Intent(this, ShaderTestActivity.class);
        startActivity(intent);
    }

    public void onPathMeasure(View view) {
        Intent intent = new Intent(this, PathMeasureActivity2.class);
        startActivity(intent);
    }

    public void onApiTest(View view) {
        Intent intent = new Intent(this, APITestActivity.class);
        startActivity(intent);
    }

    public void onLineChart(View view) {
        Intent intent = new Intent(this, PathMeasureActivity.class);
        startActivity(intent);
    }

    public void onTimeLineChart(View view) {
        Intent intent = new Intent(this, MinuteChartActivity.class);
        startActivity(intent);
    }

    public void onVolumeChart(View view) {
        Intent intent = new Intent(this, VolumeActivity.class);
        startActivity(intent);
    }

    public void onPieChart(View view) {
        Intent intent = new Intent(this, PieChartActivity.class);
        startActivity(intent);
    }


    public void onKLineChart(View view) {
        Intent intent = new Intent(this, KLineActivity.class);
        startActivity(intent);
    }
}
