package com.example.samsung.calcul;

import android.Manifest;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoActivity extends AppCompatActivity  {

    private ArrayList<Map<String, String>> liste;
    private SimpleAdapter adapter;
    private Bdd bdd;
    private ExecutorService executor;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Bitmap mBitmapToSave;
    private static final int REQUEST_CODE_CREATE_FILE = 2;
    String filepath;
    String filename;
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
            String calc = textResult.getText().toString().replaceAll("\\s+", "");
            editor.putString("calcul", calcul + calc);
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


            switch (index) {
                case 0:
                    btnDelete(id);
                    break;
            }
            return false;
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_histo, menu);
        return true;
    }



    public void btnClear(MenuItem menuItem) {
        executor.execute(() -> bdd.data().deleteAll());
        this.recreate();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Historique Supprimé !",
                Toast.LENGTH_SHORT);

        toast.show();



    }
    public void btnDrive(MenuItem menuItem) throws IOException {
        save();
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

    public void save() throws IOException {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            File file = new File(Environment.getExternalStorageDirectory(), "Calcul");
            filename = "Save_" + String.valueOf(System.currentTimeMillis());
            filepath = file.getPath() + File.separator + filename + ".csv";
            Log.e("path", filepath);

            if (!file.exists()) {
                file.mkdirs();
            }
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(filepath);

            fileWriter.append("id,calcul,resultat");
            fileWriter.append("\n");
            FileWriter finalFileWriter = fileWriter;
            FileWriter finalFileWriter1 = fileWriter;
            bdd.data().getAll().observe(this, histories -> {

                if (histories != null) {
                    for (Data data : histories) {
                        try {
                            finalFileWriter.append(Integer.toString(data.id));
                            finalFileWriter.append(",");
                            finalFileWriter.append(data.calcul);
                            finalFileWriter.append(",");
                            finalFileWriter.append(data.resultat);
                            finalFileWriter.append("\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        finalFileWriter1.flush();
                        finalFileWriter1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });




            signIn();

        }
        else
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            this.recreate();
        }
    }

    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }


    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        onDriveClientReady();
    }
    protected void onDriveClientReady() {
        createFileWithIntent();
    }

    private void createFileWithIntent() {
        Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        createContentsTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    Uri uri = Uri.parse(filepath);
                    OutputStream outputStream = contents.getOutputStream();
                    try {
                        FileInputStream inputStream = new FileInputStream(filepath);

                            byte[] data = new byte[1024];
                            while (inputStream.read(data) != -1) {
                                outputStream.write(data);
                            }
                            inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(filename)
                            .setMimeType("text/csv")
                            .setStarred(true)
                            .build();

                    CreateFileActivityOptions createOptions =
                            new CreateFileActivityOptions.Builder()
                                    .setInitialDriveContents(contents)
                                    .setInitialMetadata(changeSet)
                                    .build();
                    return getDriveClient().newCreateFileActivityIntentSender(createOptions);
                })
                .addOnSuccessListener(this,
                        intentSender -> {
                            try {
                                startIntentSenderForResult(
                                        intentSender, REQUEST_CODE_CREATE_FILE, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, "Unable to create file", e);
                                finish();
                            }
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    finish();
                });
    }


    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

}


