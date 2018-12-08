package com.example.samsung.calcul;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class CameraActivity extends AppCompatActivity {

    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/calcul/";


    public static final String lang = "eng";

    private static final String TAG = "CameraActivity.java";

    protected Button _button;
    // protected ImageView _image;
    protected TextView _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        }

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }



        _field = (TextView) findViewById(R.id.field);
        _path = DATA_PATH + "ocr.jpg";
        startCameraActivity();

    }


        public void btnOcr (View view) {
            Log.v(TAG, "Starting Camera app");

            startCameraActivity();
        }



    protected void startCameraActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ocr start ",
                        Toast.LENGTH_SHORT);

                toast.show();
                _path = DATA_PATH + "ocr.jpg";
                Log.v(TAG, "Path " + _path);


                File file = new File(_path);
                Uri outputFileUri = FileProvider.getUriForFile(this, "com.example.samsung.calcul.fileprovider", file);
                Log.v(TAG, "uri " + outputFileUri);

                final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(intent, 0);
            }
            else{

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 0);
                onBackPressed();

            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            onBackPressed();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            try {
                onPhotoTaken();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CameraActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(CameraActivity.PHOTO_TAKEN)) {
            try {
                onPhotoTaken();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPhotoTaken() throws FileNotFoundException {

        _path = DATA_PATH + "ocr.jpg";
        Log.v(TAG, "Path " + _path);

        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        File file = new File(_path);
        Uri outputFileUri = FileProvider.getUriForFile(this, "com.example.samsung.calcul.fileprovider", file);
        Log.v(TAG, "uri " + outputFileUri);

        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputFileUri), null,options);


        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Expression non reconnue !",
                    Toast.LENGTH_SHORT);

            toast.show();
            onBackPressed();
        }


        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        Log.v(TAG, DATA_PATH + "tessdata/" + lang + ".traineddata ");
        baseApi.init(DATA_PATH , lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();


        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        recognizedText = recognizedText.replaceAll("[x]+", "*");
        recognizedText = recognizedText.replaceAll("[÷]+", "/");
        recognizedText = recognizedText.replaceAll("[^0-9+\\-*/.]+", "");
        recognizedText = recognizedText.replaceAll(" ", "");

        //}
        Log.v(TAG, "OCRED after: " + recognizedText);

        recognizedText = recognizedText.trim();
        Log.v(TAG, "OCRED after 2: " + recognizedText);

        if ( recognizedText.matches("^(-?([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(+)*-?(\\d*|\\d+(\\.\\d*)?|pi|e)((?<!\\))\\d\\)*(([+-]|[*#!/%^]-?)(([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(-?)*(\\d*|\\d+(\\.\\d*)?|pi|e))?)*$") ) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            String calcul = "";
            editor.putString("resultat", "0");
            editor.putString("calcul", calcul + recognizedText);
            editor.apply();
            onBackPressed();

        }
        else{

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Expression non reconnue !",
                    Toast.LENGTH_SHORT);

            toast.show();
            onBackPressed();
        }

    }



}
