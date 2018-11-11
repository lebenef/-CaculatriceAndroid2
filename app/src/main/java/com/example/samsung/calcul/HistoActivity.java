package com.example.samsung.calcul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoActivity extends AppCompatActivity {

    ListView histoList;
    ArrayList<Map<String, String>> liste;
    SimpleAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);

        Toolbar  toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        histoList = findViewById(R.id.histo);

        liste = (ArrayList<Map<String, String>>) getIntent().getSerializableExtra("liste");

    /*   for(int i=0; i<10;i++)
        {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("calc", Integer.toString(i) + "+" + Integer.toString(i));
            hashMap.put("res", Integer.toString(i*2));
            liste.add(hashMap);
        }
        */

        String[] from = {"calc", "res"};
        int[] to = {R.id.itemCalc, R.id.itemResult};

         adapter= new SimpleAdapter(this,liste, R.layout.item_histo, from, to);

        histoList.setAdapter(adapter);




    }

    public void btnClear(View view) {

        liste.clear();
        this.recreate();

    }

    public void btnReturn(View view) {
        onBackPressed();

    }


   /* public void onItemClick(AdapterView<?> adapter, View view, int position){
        //ItemClicked item = adapter.getItemAtPosition(position);

        Intent main = new Intent(this, MainActivity.class);
        Map<String, String> val = liste.get(position);

        String calc = val.get("calc");
        String result = val.get("res");
        main.putExtra("calc",calc);
        main.putExtra("res",result);

        startActivity(main);
    }*/
}


