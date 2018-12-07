package com.example.samsung.calcul;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import org.mariuszgromada.math.mxparser.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {
    double dblResult;
    double dblValue1;
    double dblValue2;


    String op;
    String regd;
    String regf;
    String regm;
    String regt;
    double res;
    String[] result;
    String resultString;
    String strOperation;

    String tmp;

    ArrayList<Map<String, String>> liste = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();


    private TextView textCalc;
    private TextView textResult;
    private String tmpcalc;
    private Bdd bdd;
    private ExecutorService executor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textResult = findViewById(R.id.textResult);
        textCalc =  findViewById(R.id.textCalc);
        bdd = Bdd.getInstance(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textCalc.setShowSoftInputOnFocus(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("calcul", textCalc.getText().toString());
        outState.putString("resultat", textResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String calcul = savedInstanceState.getString("calcul", textCalc.getText().toString());
        String resultat = savedInstanceState.getString("resultat", textCalc.getText().toString());

        textCalc.setText(calcul);
        textResult.setText(resultat);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String calcul = sharedPref.getString("calcul", textCalc.getText().toString());
        String resultat = sharedPref.getString("resultat", textCalc.getText().toString());

        textCalc.setText(calcul);
        textResult.setText(resultat);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("calcul", textCalc.getText().toString());
        editor.putString("resultat", textResult.getText().toString());
        editor.apply();
    }


    public void btn0(View view) {
        chiffreClick("0");
    }

    public void btnC(View view) {
        textResult.setText("0");
        textCalc.setText(null);

    }

    public void btnHisto(View view) {
        Intent hist = new Intent(this, HistoActivity.class);
        //hist.putExtra("liste", this.liste);
        startActivity(hist);
    }

    public void btnGraph(View view) {
        Intent graph = new Intent(this, GraphActivity.class);
        //hist.putExtra("liste", this.liste);
        startActivity(graph);
    }
    public void btnCam(View view) {
        Intent cam = new Intent(this, CameraActivity.class);
        //hist.putExtra("liste", this.liste);
        startActivity(cam);
    }

    public void btnVoice(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Log.d("voice", "btnVoice: Permit OK ");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH);
            Log.d("voice", "btnVoice: top try ");
            try {
                Log.d("voice", "btnVoice: success ");
                startActivityForResult(intent, 200);

            } catch (ActivityNotFoundException a) {
                Log.d("voice", "btnVoice: error ");
            }
        }
        else{
            Log.d("voice", "btnVoice: Permit False ");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            this.recreate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("voice", requestCode + "");
        super.onActivityResult(requestCode,resultCode,data);
        Log.d("voice", requestCode + "");
        if(requestCode == 200){
            Log.d("voice", resultCode + "");
            if(resultCode==RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("voice", result.get(0));
                String format =  result.get(0).replace(" ", "");
                format = format.replace("×","*");
                format = format.replace("x","*");
                format = format.replace("X","*");

                if(format.matches("^(-?([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(+)*-?(\\d*|\\d+(\\.\\d*)?|pi|e)\\)*(\\d\\)*(([+-]|[*#!/^]-?)(([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(-?)*(\\d*|\\d+(\\.\\d*)?|pi|e))?)*$")) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String calcul = sharedPref.getString("calcul", textCalc.getText().toString());
                    editor.putString("calcul", calcul + format);
                    editor.apply();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Expression non reconnue !",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
            }
        }
    }
    public void btnDelete(View view ) {
        //if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS){
            deleteClick();

        /*}
        else if (event.getAction() == MotionEvent.ACTION_DOWN){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Etes vous sur ?",
                    Toast.LENGTH_SHORT);

            toast.show();

        }*/
        }

    public void btnPar(View view) {
        paraClick();
    }

    public void btnPour(View view) {
        operationClick("%");
    }

    public void btnDiv(View view) {
        operationClick("/");
    }

    public void btn9(View view) {
        chiffreClick("9");
    }

    public void btn8(View view) {
        chiffreClick("8");
    }

    public void btn7(View view) {
        chiffreClick("7");
    }

    public void btnMul(View view) {
        operationClick("*");
    }

    public void btn4(View view) {
        chiffreClick("4");
    }

    public void btn5(View view) {
        chiffreClick("5");
    }

    public void btn6(View view) {
        chiffreClick("6");
    }

    public void btnMoins(View view) {
        operationClick("-");
    }

    public void btn1(View view) {
        chiffreClick("1");
    }

    public void btn2(View view) {
        chiffreClick("2");
    }

    public void btn3(View view) {
        chiffreClick("3");
    }

    public void btnPlus(View view) {
        operationClick("+");
    }

    public void btnPlusMoins(View view) {
        plusmoinsClick();
    }

    public void btnVir(View view) {
        chiffreClick(".");
    }

    public void btnSin(View view) {
        operationClick("sin(");
    }
    public void btnCos(View view) {
        operationClick("cos(");
    }
    public void btnTan(View view) {
        operationClick("tan(");
    }
    public void btnLog(View view) {
        operationClick("log(");
    }
    public void btnLog2(View view) {
        operationClick("log2(");
    }
    public void btnLn(View view) {
        operationClick("ln(");
    }
    public void btnE(View view) {
        operationClick("e");
    }
    public void btnPi(View view) {
        operationClick("pi");
    }
    public void btnRac(View view) {
        operationClick("sqrt(");
    }
    public void btnCar(View view) {
        operationClick("^(2)");
    }
    public void btnPui(View view) {
        operationClick("^(");
    }
    public void btnGcd(View view) {
        operationClick("gcd(");
    }


    public void btnRad(View view) {
        operationClick("rad(");
    }

    public void btnMod(View view) {
        operationClick("#");
    }

    public void btnFac(View view) {
        operationClick("!");
    }

    public void chiffreClick(String strChiffre) {
        ajoutCalc(strChiffre);
        /*TextView textView = textCalc;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(textCalc.getText());
        stringBuilder.append(strChiffre);
        textView.setText(stringBuilder.toString());*/
    }

    private void operationClick(String operateur) {


        ajoutCalc(operateur);
        /*TextView textView = this.textCalc;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.);
        stringBuilder.append(this.strOperation);
        textView.setText(stringBuilder.toString());*/
    }

    private void paraClick() {
        tmpcalc = textCalc.getText().toString();
        int parenthese_fermer = 0;
        int parenthese_ouvert = 0;
        for (char c : tmpcalc.toCharArray()) {
            switch (c) {
                case '(':
                    parenthese_ouvert++;
                    break;
                case ')':
                    parenthese_fermer++;
                    break;
                default:
                    break;
            }
        }
        if (tmpcalc.length() == 1 && tmpcalc.charAt(0) == '0') {
            ajoutCalc("(");
        } else if (Pattern.matches("^(.*[(+\\-/*])?$", tmpcalc)) {
            ajoutCalc("(");

        } else if (Pattern.matches("^.*\\(.*[\\w)]$", tmpcalc) && parenthese_ouvert > parenthese_fermer) {
            ajoutCalc(")");

        }
    }



    private void deleteClick() {
        String texte;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EditText editCalc = (EditText) textCalc;
            int positionDebut = textCalc.getSelectionStart();
            int positionFin = textCalc.getSelectionEnd();
            if(positionDebut == positionFin)
            {
                positionDebut = Math.max(positionDebut - 1, 0);
            }
            texte = textCalc.getText().toString();
            String texteDebut = texte.substring(0, positionDebut);
            String texteFin = texte.substring(positionFin);
            texte = texteDebut + texteFin;

            textCalc.setText(texte);

            editCalc.setSelection(positionDebut);
        }
        else
        {
            texte = textCalc.getText().toString();
            textCalc.setText(texte.substring(0, Math.max(texte.length() - 1, 0)));
        }
    }

    private void plusmoinsClick() {
        tmpcalc = textCalc.getText().toString();
        int positionDebut = 0;
        int positionFin = 0;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            positionDebut = textCalc.getSelectionStart();
            positionFin = textCalc.getSelectionEnd();
        }

        if (tmpcalc.length() >= 1 && !tmpcalc.substring(1).equals("0")) {
            if (tmpcalc.charAt(0) != '-') {
                tmpcalc= "-" + tmpcalc;
                positionDebut++;
                positionFin++;

                /*StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("-");
                stringBuilder.append(this.textCalc.getText());
                this.tmpcalc = stringBuilder.toString();*/

            } else {
                tmpcalc = tmpcalc.substring(1);
                positionDebut = Math.max(positionDebut - 1, 0);
                positionFin--;
            }
        }

        textCalc.setText(tmpcalc);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EditText editCalc = (EditText) textCalc;
            if(positionDebut < positionFin) {
                editCalc.setSelection(positionDebut, positionFin);
            }
            else
            {
                editCalc.setSelection(positionDebut);
            }
        }
    }

    public void btnEgal(View view) {
        //String resultString;
        String calcString = textCalc.getText().toString();


        regt = "(?<=[^\\d.])(?=\\d)|(?<=\\d)(?=[^\\d.])";
        regd = "-?\\(?-?[0-9]*\\.?[0-9]*\\)?";
        regm = "\\+?-?\\*?/?-?\\(?-?[0-9]*\\.?[0-9]*\\)?";
        regf = "^" + regd + "(" + regm + ")*$";


        /*StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("^");
        stringBuilder.append(regd);
        stringBuilder.append("(");
        stringBuilder.append(regm);
        stringBuilder.append(")*$");
        regf = stringBuilder.toString();*/

    /*    result = textCalc.getText().toString().split(regt);
        op = null;
        res = 0.0d;
        for (String s : result) {
            if (!(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/"))) {
                if (!s.equals("%")) {
                    if (op == null) {
                        res = Double.parseDouble(s);
                    } else if (op.equals("+")) {
                        res += Double.parseDouble(s);
                    } else if (op.equals("-")) {
                        res -= Double.parseDouble(s);
                    } else if (op.equals("*")) {
                        this.res *= Double.parseDouble(s);
                    } else if (op.equals("/")) {
                        res /= Double.parseDouble(s);
                    } else if (op.equals("%")) {
                        res /= 100.0d;
                    }
                }
            }
            op = s;
        }

        resultString = Double.toString(res);*/


        /*ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("rhino");
        try {
              resultString = engine.eval(calcString).toString();
        } catch (ScriptException e) {
           resultString = "Erreur";
        }*/
        if (calcString.matches("^(-?([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(+)*-?(\\d*|\\d+(\\.\\d*)?|pi|e)\\)*(\\d\\)*(([+-]|[*#!/^]-?)(([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(-?)*(\\d*|\\d+(\\.\\d*)?|pi|e))?)*$")) {
            Expression e = new Expression(calcString);
            double resultDouble = e.calculate();
            DecimalFormat df = new java.text.DecimalFormat();
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            df.setMinimumFractionDigits(0);
            df.setMaximumFractionDigits(15);
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);

            resultString = df.format(resultDouble);
            textResult.setText(resultString);

            // hashMap.put("calc", calcString);
            //  hashMap.put("res", resultString);
            if (!resultString.equals("NaN")) {
                Data data = new Data();
                data.calcul = calcString;
                data.resultat = resultString;

                executor.execute(() -> bdd.data().insertData(data));
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Etes vous sur ?",
                    Toast.LENGTH_SHORT);

            toast.show();

        }
    }


    public void ajoutCalc(String val)
    {
        String texte;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EditText editCalc = (EditText) textCalc;
            int positionDebut = textCalc.getSelectionStart();
            int positionFin = textCalc.getSelectionEnd();
            texte = textCalc.getText().toString();
            String texteDebut = texte.substring(0, positionDebut);
            String texteFin = texte.substring(positionFin);
            texte = texteDebut + val + texteFin;
            if( texte.matches("^(-?([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(+)*-?(\\d*|\\d+(\\.\\d*)?|pi|e)\\)*(\\d\\)*(([+-]|[*#!/^]-?)(([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(-?)*(\\d*|\\d+(\\.\\d*)?|pi|e))?)*$")) {
                 textCalc.setText(texte);
                 editCalc.setSelection(positionDebut + val.length());
             }

        }
        else
        {
            texte = textCalc.getText().toString() + val;

            if( texte.matches("^(-?([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(+)*-?(\\d*|\\d+(\\.\\d*)?|pi|e)\\)*(\\d\\)*(([+-]|[*#!/^]-?)(([a-zA-Z]*|[a-zA-Z]+[0-9]*)\\(-?)*(\\d*|\\d+(\\.\\d*)?|pi|e))?)*$")) {

                textCalc.setText(texte);
            }
        }
    }
}
