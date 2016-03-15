package com.example.abdul.healthmonitor.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import com.example.abdul.healthmonitor.model.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;

/**
 * Created by Dikshay on 2/25/2016.
 */
public class DBHandler {
    private static final String TAG = "Debug data";
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private static String DB_PATH =  "/data/data/com.example.abdul.healthmonitor/databases/";
    final String uploadFilePath = Environment.getExternalStorageDirectory()+"/downloads/";
    private static String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    Context context;
    int serverResponseCode = 0;

    String[] allColumns = {HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE,
            HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE,
            HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE,
            HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP};

    public DBHandler(Context context)
    {
        this.context = context;
        dbHelper = new DBHelper(context);
        //database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "DATABASE:" + database);
    }
    public void executeQuery(String Query)
    {
        database.execSQL(Query);
    }
    public void close()
    {
        dbHelper.close();
    }
    public long insert(Data data) {
        if (database == null)
        { open();}

        ContentValues values = new ContentValues();
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE, data.getX_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE, data.getY_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE, data.getZ_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP, data.getTimestamp());
        long id = database.insert(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME, null, values);

        return id;

    }

    public void getAllRecords(){
/*
        Cursor tableCursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        JSONArray tableArray = new JSONArray();
        JSONObject tableObject;
        JSONObject dataObject;
        JSONArray dataArray;
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        tableCursor.moveToFirst();
        try {
            while (!tableCursor.isAfterLast()) {
                tableObject = new JSONObject();
                String table_name = tableCursor.getString(tableCursor.getColumnIndex("name"));
                if (!table_name.equals("android_metadata") && !table_name.equals("sqlite_sequence")) {
                    tableObject.put("table_name", table_name);
                    dataArray = new JSONArray();
                    Log.d("Tables", table_name);
                    Cursor dataCursor = database.query(tableCursor.getString(0),
                            allColumns, null, null, null, null, "DESC");
                    dataCursor.moveToFirst();
                    Log.d(TAG, tableCursor.toString());
                    while (!dataCursor.isAfterLast()) {
                        dataObject = new JSONObject();
                        Data data = cursorToData(dataCursor);

                        if (data != null) {

                            dataObject.put(Constants.X_VALUE, data.getX_value());
                            dataObject.put(Constants.Y_VALUE, data.getY_value());
                            dataObject.put(Constants.Z_VALUE, data.getZ_value());
                            dataObject.put(Constants.TIMESTAMP, data.getTimestamp());
                        } else {
                            Log.d(TAG, "data record empty in upload");
                        }

                        dataArray.put(dataObject);

                    }
                    tableObject.put("data_records", dataArray);
                    tableArray.put(tableObject);
                    tableCursor.moveToNext();
                }
            }




            //put text in a file
            File myDataFile;
            FileOutputStream fos = null;
            try{
                myDataFile = new File("c:/1209342533.txt");
                fos = new FileOutputStream(myDataFile);

                if(!myDataFile.exists()){
                    myDataFile.createNewFile();
                }

                byte[] dataInBytes = tableArray.toString().getBytes();
                fos.write(dataInBytes);
                fos.flush();
                fos.close();

                System.out.println("Done");

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    if(fos != null){
                        fos.close();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            //connection
            Log.d(TAG, "before conn : " + tableArray.toString());
            URL url = new URL("https://impact.asu.edu/Appenstance/UploadToServerGPS.php");
            //String msg = tableArray.toString();
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(msg.getBytes().length);
            Log.d(TAG, "in conn");
            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            conn.connect();
            Log.d(TAG, "connected");
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write("asd");

            //clean up
            os.flush();
            Log.d(TAG, "flushed");
            //do somehting with response
            is = conn.getInputStream();
            //String contentAsString = readIt(is,len);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
            conn.disconnect();
            Log.d(TAG, "disconnected");
        }*/
    }
    public boolean tableExists(String tableName)
    {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    public String getAnyTable()
    {
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata' AND name != 'sqlite_sequence'", null);
        String tableName="";
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                //Toast.makeText(activityName.this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                Log.d("Tables",c.getString(0));
                tableName = c.getString(0);
                c.moveToNext();
                return tableName;


            }
        }
        return tableName;
    }
    public List<Data> getFirstTenRecordsDownload(String tableName)
    {
        try {
            if (!tableExists(tableName)) {
                tableName = getAnyTable();
            }
            List<Data> dataList = new ArrayList<Data>();
            String sortOrder =
                    HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + " DESC";
            Cursor cursor = database.query(tableName,
                    allColumns, null, null, null, null, sortOrder, "10");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Data data = cursorToData(cursor);
                dataList.add(data);
                cursor.moveToNext();
            }
            cursor.close();
            return dataList;
        }
        catch(Exception e)
        {
            return new ArrayList<Data>();
        }
    }
    public List<Data> getFirstTenRecords()
    {
        try {
            List<Data> dataList = new ArrayList<Data>();
            String sortOrder =
                    HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + " DESC";
            Cursor cursor = database.query(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME,
                    allColumns, null, null, null, null, sortOrder, "10");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Data data = cursorToData(cursor);
                dataList.add(data);
                cursor.moveToNext();
            }
            cursor.close();
            return dataList;
        }
        catch(Exception e)
        {
            return new ArrayList<Data>();
        }
    }
    public Data cursorToData(Cursor cursor)
    {

        System.out.println("timestamp" + cursor.getColumnIndex("timestamp"));
        System.out.println("xvalue" + cursor.getColumnIndex("xvalue"));
        System.out.println("yvalue" + cursor.getColumnIndex("yvalue"));
        System.out.println("zvalue" + cursor.getColumnIndex("zvalue"));
        Data data = new Data();
        data.setX_value(cursor.getDouble(0));
        data.setY_value(cursor.getDouble(1));
        data.setZ_value(cursor.getDouble(2));
        data.setTimestamp(cursor.getString(3));
        return data;
    }
}
