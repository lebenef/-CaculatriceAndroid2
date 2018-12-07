package com.example.samsung.calcul;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PlotActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        }
        Intent intent = getIntent();

        int id = intent.getIntExtra("id", 0);
        String idString = intent.getStringExtra("id");
        Log.i("id", "You clicked Item: " + id );


        String name = "";

        switch (id) {
            case 0:
                name ="Sin(x)";
                break;
            case 1:
                name ="Cos(x)";
                break;
            case 2:
                name ="Tan(x)";
                break;
            case 3:
                name ="Sin(x)";
                break;
        }

        toolbar.setTitle(name);

        double y,x;
        x = -10.0;

        GraphView graph = findViewById(R.id.graph1);
        series = new LineGraphSeries<>();
        for(int i =0; i<200; i++) {
            x = x + 0.1;
            y=0;
            switch (id) {

                case 0:
                    y = Math.sin(x);
                    break;
                 case 1:
                     y = Math.cos(x);
                    break;
                case 2:
                    y = Math.tan(x);
                    break;
                case 3:
                    y = Math.sin(x);
                    break;
            }
            series.appendData(new DataPoint(x, y), true, 200);
        }
        graph.addSeries(series);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }
}
