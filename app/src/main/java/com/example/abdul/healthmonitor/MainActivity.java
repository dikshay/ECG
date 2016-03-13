package com.example.abdul.healthmonitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
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
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.abdul.healthmonitor.Util.DBHandler;
import com.example.abdul.healthmonitor.Util.DBHelper;
import com.example.abdul.healthmonitor.Util.DownloadAsync;
import com.example.abdul.healthmonitor.Util.HealthMonitorReaderContract;
import com.example.abdul.healthmonitor.Util.UploadAsync;
import com.example.abdul.healthmonitor.model.Data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //float[] val;
    float[] emptyVal;
    String[] horLab;
    String[] verLab;
    LinearLayout myLayout;
    LinearLayout.LayoutParams xParams;
    LinearLayout.LayoutParams yParams;
    LinearLayout.LayoutParams zParams;
    GraphView xView;
    GraphView yView;
    GraphView zView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final String SENSOR_ERROR_TAG = "SensorError";
    private static final String TAG = "Debug data";
    double gravity[] = {0,0,0};
    double linear_acceleration[] = {0,0,0};
    static int ACCE_FILTER_DATA_MIN_TIME = 1000; // 1000ms
    long lastSaved = System.currentTimeMillis();
    DBHandler dbHandler;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    int secondsPassed;
    ProgressDialog mProgressDialog;

    private void deactivateSensor()
    {
        Log.d(TAG,"Sensor Deactivated");
        mSensorManager.unregisterListener(this);
    }
    private void activateSensor()
    {
        deactivateSensor();
        if(mSensor == null)
        {
            Log.d(SENSOR_ERROR_TAG,"Accelerometer not present on device");
        }
        else
        {
            Log.d(TAG,"Sensor Activated");
            mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize db
        dbHandler = new DBHandler(getApplicationContext());
        dbHandler.open();

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        initializeVariables();

        //design layout
        myLayout = (LinearLayout) findViewById(R.id.myLayout);
        myLayout.setBackgroundColor(Color.parseColor("#000000"));
        Button create_table = (Button) findViewById(R.id.create_table);
        Button run = (Button) findViewById(R.id.run);
        Button stop = (Button) findViewById(R.id.stop);
        Button upload = (Button) findViewById(R.id.upload);
        Button download = (Button) findViewById(R.id.download);
        createGraphView(emptyVal, emptyVal, emptyVal, horLab, verLab);

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

                String table_Name = nameString + "_" + idString + "_" + ageString + "_" + sexString;
                HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME = table_Name;
                dbHandler = new DBHandler(getApplicationContext());
                dbHandler.open();
                String SQL_DELETE_ENTRIES =
                        "DROP TABLE IF EXISTS " + HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME;
                String SQL_CREATE_ENTRIES =
                        "CREATE TABLE " + table_Name + " (" +
                                HealthMonitorReaderContract.AccelerometerDataEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                                HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE + " INTEGER" + COMMA_SEP +
                                HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE + " INTEGER" + COMMA_SEP +
                                HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE + " INTEGER" + COMMA_SEP +
                                HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + " INTEGER" +
                                " )";
                dbHandler.executeQuery(SQL_DELETE_ENTRIES);
                dbHandler.executeQuery(SQL_CREATE_ENTRIES);
                Toast.makeText(getApplicationContext(),"Table Created", Toast.LENGTH_SHORT).show();
                //dbHandler.close();
                activateSensor();
            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHandler != null){
                    updateGraph();
                }

                /*while(true){
                    updateGraph();
                    SystemClock.sleep(1000);
                }*/
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearView();
                xView.setValues(emptyVal);
                xView.clear();
                yView.setValues(emptyVal);
                yView.clear();
                zView.setValues(emptyVal);
                zView.clear();
            }
        });
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                //dbHandler.getAllRecords();
                UploadAsync uploadAsync = new UploadAsync();
                uploadAsync.execute();
            }
        });

        download.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {

                DownloadAsync downloadAsync = new DownloadAsync(MainActivity.this);
                downloadAsync.execute();
            }
        });
    }


    protected void getJSONObject(){

    }

    protected void createGraphView(float[] xVal,float[] yVal,float[] zVal, String[] horLab, String[] verLab){
        //myLayout.removeAllViews();
        secondsPassed = 0;
        xView = new GraphView(MainActivity.this, xVal, "X", horLab, verLab, true);
        yView = new GraphView(MainActivity.this, yVal, "Y", horLab, verLab, true);
        zView = new GraphView(MainActivity.this, zVal, "Z", horLab, verLab, true);
        xParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
        xParams.setMargins(20, 20, 20, 20);
        myLayout.addView(xView, xParams);
        yParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
        yParams.setMargins(20, 20, 20, 20);
        myLayout.addView(yView, yParams);
        zParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,300);
        zParams.setMargins(20, 20, 20, 20);
        myLayout.addView(zView, zParams);
    }

    protected void updateGraph(){

        List<Data> dataList = dbHandler.getFirstTenRecords();
        ListIterator<Data> dataListIterator = dataList.listIterator();
        float x_val[] = new float[10];
        float y_val[] = new float[10];
        float z_val[] = new float[10];
        String hor_label[] = new String[10];
        String ver_label[] = new String[6];
        int count = 0;
        Log.d(TAG, "Count: " + dataList.size());
        while(dataListIterator.hasNext())
        {
            Data d = dataListIterator.next();
            Log.d(TAG,"Data: " + d);
            x_val[count] = (float) d.getX_value();
            y_val[count] = (float) d.getY_value();
            z_val[count] = (float)d.getZ_value();
            hor_label[count] = d.getTimestamp();

            count++;
        }

        xView.setValues(x_val);
        xView.setHorLabels(hor_label);
        //xView.setVerLabels(ver_label);
        xView.clear();
        yView.setValues(y_val);
        yView.setHorLabels(hor_label);
        //yView.setVerLabels(ver_label);
        yView.clear();
        zView.setValues(z_val);
        zView.setHorLabels(hor_label);
        //zView.setVerLabels(ver_label);
        zView.clear();
    }
    public void updateGraphDownload(){

        List<Data> dataList = dbHandler.getFirstTenRecordsDownload(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME);
        ListIterator<Data> dataListIterator = dataList.listIterator();
        float x_val[] = new float[10];
        float y_val[] = new float[10];
        float z_val[] = new float[10];
        String hor_label[] = new String[10];
        String ver_label[] = new String[6];
        int count = 0;
        Log.d(TAG, "Count: " + dataList.size());
        while(dataListIterator.hasNext())
        {
            Data d = dataListIterator.next();
            Log.d(TAG,"Data: " + d);
            x_val[count] = (float) d.getX_value();
            y_val[count] = (float) d.getY_value();
            z_val[count] = (float)d.getZ_value();
            hor_label[count] = d.getTimestamp();

            count++;
        }

        xView.setValues(x_val);
        xView.setHorLabels(hor_label);
        //xView.setVerLabels(ver_label);
        xView.clear();
        yView.setValues(y_val);
        yView.setHorLabels(hor_label);
        //yView.setVerLabels(ver_label);
        yView.clear();
        zView.setValues(z_val);
        zView.setHorLabels(hor_label);
        //zView.setVerLabels(ver_label);
        zView.clear();
    }

    protected void onPause()
    {
        super.onPause();
        //  mSensorManager.unregisterListener(this);
    }
    protected void onResume()
    {
        super.onResume();
        //  activateSensor();
    }
    private void clearView(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initializeVariables(){
        /*val = new float[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {10,30,15,20,18,50,0,10,30,15,20,18,50,0,10,30,15,20,18,50,0},
                {9,25,12,20,15,55,0,9,25,12,20,15,55,0,9,25,12,20,15,55,0},
                {12,30,16,17,10,45,0,12,30,16,17,10,45,0,12,30,16,17,10,45,0},
                {7,18,3,35,6,60,0,7,18,3,35,6,60,0,7,18,3,35,6,60,0},
                {10,28,12,20,18,50,0,10,28,12,20,18,50,0,10,28,12,20,18,50,0}};*/

        emptyVal = new float[]{0,0,0,0,0,0,0,0,0,0,0,0};
        horLab = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        verLab = new String[]{"9", "8", "7", "6", "5", "4", "3", "2", "1", "0"};
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
            Data data = new Data();
            data.setX_value(linear_acceleration[0]);
            data.setY_value(linear_acceleration[1]);
            data.setZ_value(linear_acceleration[2]);
            data.setTimestamp(Integer.toString(secondsPassed++));
            Log.d(TAG, linear_acceleration.toString());
            if(dbHandler == null)
            {
                dbHandler = new DBHandler(getApplicationContext());
                dbHandler.open();
            }
            dbHandler.insert(data);
        }
    }

    //upload asyn task
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            //searchButton = (Button) findViewById(R.id.button1);
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            } };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpsURLConnection) url.openConnection();

                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                //downloadButton.setText(Integer.toString(fileLength));
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/downloads/"+sUrl[1]);
                //downloadButton.setText("Connecting .....");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null){
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();


            }/*else{
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
                if(searchButtonPress){
                    extractAppName();
                    searchButtonPress = false;
                }else if(downloadButtonPress){
                    installApp(appName);
                    downloadButtonPress = false;
                }*/

            //uninstallApp();
	            /*Process install;

	            try {

	            install = Runtime.getRuntime().exec("/system/bin/busybox install " + Environment.getExternalStorageDirectory() + "/downloads/" + "RaRandomFlashlight.apk");

	            int iSuccess = install.waitFor();

	            Log.e("TEST", ""+iSuccess);

	            } catch (IOException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            } catch (InterruptedException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            }*/
        }
    }
}
