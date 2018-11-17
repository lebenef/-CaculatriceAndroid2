package com.example.samsung.calcul;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import org.mariuszgromada.math.mxparser.*;

import java.util.ArrayList;
import java.util.HashMap;
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
    TextView textCalc;
    TextView textResult;
    String tmp;
    String tmpcalc;

    ArrayList<Map<String, String>> liste = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    Bdd bdd;
    ExecutorService executor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textResult = findViewById(R.id.textResult);
        textCalc =  findViewById(R.id.textCalc);
        bdd = Bdd.getInstance(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
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

    public void btnDelete(View view) {
        deleteClick();
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
        if (textCalc.getText().equals(null)) {
            textCalc.setText(strChiffre);
        }
        else {
            textCalc.setText(textCalc.getText() + strChiffre);
        }
        /*TextView textView = textCalc;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(textCalc.getText());
        stringBuilder.append(strChiffre);
        textView.setText(stringBuilder.toString());*/
    }

    private void operationClick(String operateur) {
        strOperation = operateur;
        tmpcalc = textCalc.getText().toString();

        if (tmpcalc == null) {
            if (operateur == "/" || operateur == "*" || operateur == "+") {
                textCalc.setText(tmpcalc);
            } else {
                textCalc.setText(operateur);
            }
        }
        else {

            textCalc.setText(textCalc.getText() + operateur);
        }
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
            textCalc.setText("(");
        } else if (Pattern.matches("^(.*[(+\\-/*])?$", tmpcalc)) {
            textCalc.setText(textCalc.getText()+"(");

        } else if (Pattern.matches("^.*\\(.*[\\d)]$", tmpcalc) && parenthese_ouvert > parenthese_fermer) {
            textCalc.setText(textCalc.getText()+")");

        }
    }



    private void deleteClick() {
        tmpcalc = textCalc.getText().toString();
        if (tmpcalc.length() > 1) {
            tmpcalc = tmpcalc.substring(0, tmpcalc.length() - 1);
        } else if (tmpcalc.length() == 1) {
            tmpcalc = null;
        }
        textCalc.setText(tmpcalc);
    }

    private void plusmoinsClick() {
        tmpcalc = textCalc.getText().toString();
        if (tmpcalc.length() >= 1 && !tmpcalc.substring(1).equals("0")) {
            if (tmpcalc.charAt(0) != '-') {
                tmpcalc= "-" + tmpcalc;

                /*StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("-");
                stringBuilder.append(this.textCalc.getText());
                this.tmpcalc = stringBuilder.toString();*/

            } else {
                tmpcalc = tmpcalc.substring(1);
            }
        }
        textCalc.setText(tmpcalc);
    }

    public void btnEgal(View view) {
        //String resultString;
        String calcString = textCalc.getText().toString();

        regt = "(?<=[^\\d.])(?=\\d)|(?<=\\d)(?=[^\\d.])";
        regd = "-?\\(?-?[0-9]*\\.?[0-9]*\\)?";
        regm = "\\+?-?\\*?/?-?\\(?-?[0-9]*\\.?[0-9]*\\)?";
        //regf = "^"regd"("regm")*$";

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

        Expression e = new Expression(calcString);
        resultString = Double.toString(e.calculate());

        textResult.setText(resultString);

       // hashMap.put("calc", calcString);
      //  hashMap.put("res", resultString);
        if(resultString != "NaN") {
            Data data = new Data();
            data.calcul = calcString;
            data.resultat = resultString;

            executor.execute(() -> bdd.data().insertData(data));
        }





    }
}
