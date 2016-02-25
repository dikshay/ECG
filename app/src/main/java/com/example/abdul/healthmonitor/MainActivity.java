package com.example.abdul.healthmonitor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    float[][] val;
    String[] horLab;
    String[] verLab;
    LinearLayout myLayout;
    LinearLayout.LayoutParams myLayoutParams;
    GraphView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        //design layout
        myLayout = (LinearLayout) findViewById(R.id.myLayout);
        myLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myLayout.setBackgroundColor(Color.parseColor("#000000"));

        Button run = (Button) findViewById(R.id.run);
        Button stop = (Button) findViewById(R.id.stop);
        gv = new GraphView(MainActivity.this, val[0], "Graph", horLab, verLab, true);
        myLayoutParams.setMargins(20, 20, 20, 20);
        myLayout.addView(gv, myLayoutParams);

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearView();
                int i = (int) (Math.random() * (val.length-1)) + 1;
                gv.setValues(val[i]);
                gv.clear();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearView();
                gv.setValues(val[0]);
                gv.clear();
            }
        });
    }

    private void clearView(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initializeVariables(){
        val = new float[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {10,30,15,20,18,50,0,10,30,15,20,18,50,0,10,30,15,20,18,50,0},
                {9,25,12,20,15,55,0,9,25,12,20,15,55,0,9,25,12,20,15,55,0},
                {12,30,16,17,10,45,0,12,30,16,17,10,45,0,12,30,16,17,10,45,0},
                {7,18,3,35,6,60,0,7,18,3,35,6,60,0,7,18,3,35,6,60,0},
                {10,28,12,20,18,50,0,10,28,12,20,18,50,0,10,28,12,20,18,50,0}};

        horLab = new String[]{"0","10", "20", "30", "40", "50"};
        verLab = new String[]{"50","40","30", "20", "10", "0"};
    }

    /*private void generateRandomVals(){
        for(int j = 0; j < 5; j++)
        {
            for(int i = 0; i < 7; i++)
            {
                if(i >= 0 && i < 2)
                    val[i+(j*6)] = (int) (Math.random() * 25);
                else if (i >= 2 && i < 5)
                    val[i+(j*6)] = (int) (Math.random() * 10);
                else
                    val[i+(j*6)] = (int) (Math.random() * 50);
            }
        }
    }

    private void clearVals(){
        for(int i = 0; i < val.length; i++){
            val[i] = 0;
        }
    }*/


}
