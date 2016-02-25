package com.example.abdul.healthmonitor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.abdul.healthmonitor.Util.DBHelper;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    float[][] val;
    String[] horLab;
    String[] verLab;
    LinearLayout myLayout;
    LinearLayout.LayoutParams myLayoutParams;
    GraphView gv;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final String SENSOR_ERROR_TAG = "SensorError";
    private static final String TAG = "Debug data";
    double gravity[] = {0,0,0};
    double linear_acceleration[] = {0,0,0};
    static int ACCE_FILTER_DATA_MIN_TIME = 1000; // 1000ms
    long lastSaved = System.currentTimeMillis();
    boolean active = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensor == null)
        {
            Log.d(SENSOR_ERROR_TAG,"Accelerometer not present on device");
        }
        else
        {
            mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        }

        initializeVariables();

        //design layout
        myLayout = (LinearLayout) findViewById(R.id.myLayout);
        myLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myLayout.setBackgroundColor(Color.parseColor("#000000"));
        Button create_table = (Button) findViewById(R.id.create_table);
        Button run = (Button) findViewById(R.id.run);
        Button stop = (Button) findViewById(R.id.stop);
        Button upload = (Button) findViewById(R.id.upload);
        Button download = (Button) findViewById(R.id.download);
        gv = new GraphView(MainActivity.this, val[0], "Graph", horLab, verLab, true);
        myLayoutParams.setMargins(20, 20, 20, 20);
        myLayout.addView(gv, myLayoutParams);
        create_table.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                String idString="",ageString="",nameString="",sexString="";
                EditText patient_id = (EditText)findViewById(R.id.patient_id);
                EditText age = (EditText)findViewById(R.id.age);
                EditText name = (EditText)findViewById(R.id.name);
                RadioButton maleRadioButton = (RadioButton)findViewById(R.id.male);
                RadioButton femaleRadioButton = (RadioButton)findViewById(R.id.female);
                if(patient_id.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Patient ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    idString = patient_id.getText().toString().trim();
                }
                if(age.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Age", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    ageString = age.getText().toString().trim();
                }
                if(name.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    nameString = name.getText().toString().trim();
                }
                if(!maleRadioButton.isChecked() && !femaleRadioButton.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"Please Select Sex", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                if(maleRadioButton.isChecked())
                {
                    sexString = "Male";
                }
                else
                if(femaleRadioButton.isChecked())
                {
                    sexString = "Female";
                }

                DBHelper dbHelper = new DBHelper(getApplicationContext());
            }
        });
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
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {

            }
        });

        download.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {

            }
        });
    }

    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
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
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG,"Accuracy Changed");
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
            lastSaved = System.currentTimeMillis();
            final double alpha = 0.8;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            Log.d(TAG, linear_acceleration.toString());
        }
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
