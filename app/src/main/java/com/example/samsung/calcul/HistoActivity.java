package com.example.samsung.calcul;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;


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
        SwipeMenuListView histoList = findViewById(R.id.histo);
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

        String[] from = {"calc", "res","id"};
        int[] to = {R.id.itemCalc, R.id.itemResult,R.id.itemId};

        adapter= new SimpleAdapter(this,liste, R.layout.item_histo, from, to);

        histoList.setOnItemClickListener((parent, view, position, id) -> {
            TextView textCalc = view.findViewById(R.id.itemCalc);
            TextView textResult = view.findViewById(R.id.itemResult);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            String calcul = sharedPref.getString("calcul", textCalc.getText().toString());
            editor.putString("resultat", "0");
            editor.putString("calcul", calcul + textResult.getText().toString());
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
                    hashMap.put("id", Integer.toString(data.id));

                    liste.add(hashMap);

                }
                adapter.notifyDataSetChanged();
            }
        });


        histoList.setAdapter(adapter);
        SwipeMenuCreator creator = menu -> {

            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            deleteItem.setWidth(120);
            ///deleteItem.setTitle("Delete");
            deleteItem.setIcon(R.drawable.ic_menu_delete);

            menu.addMenuItem(deleteItem);
        };

        histoList.setMenuCreator(creator);

        histoList.setOnMenuItemClickListener((position, menu, index) -> {

             Object test = adapter.getItem(position);
             String idString = test.toString();
             idString = idString.replaceAll("^.*id=(\\d+).*$", "$1");

             int id = Integer.valueOf(idString);

            //View view = (View)adapter.getItem(position);
            //final TextView idString = view.findViewById(R.id.itemId);
            //int id = Integer.valueOf(idString.getText().toString());


            switch (index) {
                case 0:
                    btnDelete(id);
                    break;
            }
            return false;
        });

    }


    public void btnClear(MenuItem menuItem) {
        executor.execute(() -> bdd.data().deleteAll());
        this.recreate();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Historique Supprimé !",
                Toast.LENGTH_SHORT);

        toast.show();



    }

    public void btnDelete(int id)
    {

        executor.execute(() -> bdd.data().deleteData(id));

        this.recreate();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Calcul Supprimé !",
                Toast.LENGTH_SHORT);

        toast.show();
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


