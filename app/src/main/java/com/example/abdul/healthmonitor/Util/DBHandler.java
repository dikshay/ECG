package com.example.abdul.healthmonitor.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.abdul.healthmonitor.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dikshay on 2/25/2016.
 */
public class DBHandler {
    private static final String TAG = "Debug data";
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    String[] allColumns = {HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP};

    public DBHandler(Context context)
    {
        dbHelper = new DBHelper(context);
        //database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
        Log.d(TAG,"DATABASE:" + database);
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
    public List<Data> getFirstTenRecords()
    {
        List<Data> dataList = new ArrayList<Data>();
        String sortOrder =
                HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + " DESC";
        Cursor cursor = database.query(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME,
                        allColumns,null,null,null,null,sortOrder,"10");
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            Data data = cursorToData(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return dataList;
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
        data.setTimestamp(cursor.getLong(3));
        return data;
    }
}
