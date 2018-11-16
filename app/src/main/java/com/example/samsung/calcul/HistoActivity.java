package com.example.samsung.calcul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoActivity extends AppCompatActivity  {

    ListView histoList;
    ArrayList<Map<String, String>> liste;
    SimpleAdapter adapter;
    Bdd bdd;
    ExecutorService executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);
        histoList = findViewById(R.id.histo);
        Toolbar  toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        executor = Executors.newSingleThreadExecutor();

        liste = new ArrayList<>();

        String[] from = {"calc", "res"};
        int[] to = {R.id.itemCalc, R.id.itemResult};

        adapter= new SimpleAdapter(this,liste, R.layout.item_histo, from, to);

        bdd = Bdd.getInstance(getApplicationContext());

        bdd.data().getAll().observe(this, histories -> {

            if (histories != null) {
                for(Data data : histories)
                {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("calc", data.calcul);
                    hashMap.put("res", data.resultat);
                    liste.add(hashMap);
                }
                adapter.notifyDataSetChanged();
            }
            
        });

        //histoList.setAdapter(adapter);



        histoList.setOnItemClickListener((parent, view, position, id) -> {

            HashMap<String,String> selected = (HashMap<String,String>) parent.getItemAtPosition(position);
            String selected_calc = selected.get("calc");
            String selected_res = selected.get("res");

            Toast.makeText(getBaseContext(), selected_res, Toast.LENGTH_LONG).show();

            //Intent main = new Intent(this, MainActivity.class);
            //hist.putExtra("liste", this.liste);
            //startActivity(main);
            onBackPressed();


        });
        histoList.setAdapter(adapter);






    }



    public void btnClear(View view) {

        //liste.clear();
        executor.execute(() -> bdd.data().deleteAll());

        this.recreate();

    }

    public void btnReturn(View view) {
        onBackPressed();

    }

    public void btnDel(View view ) {

        Data data = new Data();
        data.id = 2;


        executor.execute(() -> bdd.data().deleteData(data));

        this.recreate();

    }

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    public void onItemClick(AdapterView<?> adapter, View view, int position){
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


