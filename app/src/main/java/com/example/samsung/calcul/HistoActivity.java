package com.example.samsung.calcul;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    private ArrayList<Map<String, String>> liste;
    private SimpleAdapter adapter;
    private Bdd bdd;
    private ExecutorService executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);
        ListView histoList = findViewById(R.id.histo);
        Toolbar  toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        }

        executor = Executors.newSingleThreadExecutor();

        liste = new ArrayList<>();

        String[] from = {"calc", "res"};
        int[] to = {R.id.itemCalc, R.id.itemResult};

        adapter= new SimpleAdapter(this,liste, R.layout.item_histo, from, to);

        histoList.setOnItemClickListener((parent, view, position, id) -> {
            TextView textCalc = view.findViewById(R.id.itemCalc);
            TextView textResult = view.findViewById(R.id.itemResult);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("calcul", textCalc.getText().toString());
            editor.putString("resultat", textResult.getText().toString());
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Selected!",
                    Toast.LENGTH_SHORT);

            toast.show();
            editor.apply();
            onBackPressed();
        });

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

        histoList.setAdapter(adapter);

    }


    public void btnClear(MenuItem menuItem) {
        executor.execute(() -> bdd.data().deleteAll());
        this.recreate();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Historique Supprimé !",
                Toast.LENGTH_SHORT);

        toast.show();



    }

    public void btnDelete(View view)
    {
        LinearLayout parent = (LinearLayout)view.getParent();
        final TextView calc = parent.findViewById(R.id.itemCalc);
        executor.execute(() -> bdd.data().deleteData(calc.getText().toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_histo, menu);
        return true;
    }


    /*public void btnClear(View view) {

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

    }*/

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


