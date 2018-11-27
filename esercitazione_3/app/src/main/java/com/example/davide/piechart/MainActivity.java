package com.example.davide.piechart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ricerco il pie chart
        PieChartView pieChart =  this.findViewById(R.id.piechart);
        // imposto percentuali e relativi colori
        Float[] percent = new Float[]{40.0f, 20.0f, 20.0f, 20.0f};
        Integer[] colors = new Integer[]{0xffedf8fb, 0xffb2e2e2, 0xff66c2a4, 0xff66c2a4};
        pieChart.setPercent(Arrays.asList(percent));
        pieChart.setSegmentColor(Arrays.asList(colors));

        // impostazioni di visualizzazione
        pieChart.setRadius(300);
        pieChart.setStrokeColor(Color.BLACK);
        pieChart.setStrokeWidth(4);
        pieChart.setSelectedColor(0xff238b45);
        pieChart.setSelectedWidth(8);
    }
}
