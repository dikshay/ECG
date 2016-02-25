package com.example.abdul.healthmonitor.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.abdul.healthmonitor.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dikshay on 2/25/2016.
 */
public class DBHandler {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    String[] allColumns = {HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE,
                            HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP};

    public DBHandler(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }
    public void close()
    {
        dbHelper.close();
    }
    public long insert(Data data)
    {
        if(database == null)
        {
            open();
        }
        ContentValues values = new ContentValues();
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE,data.getX_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE,data.getY_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE,data.getZ_value());
        values.put(HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP,data.getTimestamp());
        long id = database.insert(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME,null,values);
        return id;
    }
    public List<Data> getFirstTenRecords()
    {
        List<Data> dataList = new ArrayList<Data>();
        String sortOrder =
                HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + " DESC";
        Cursor cursor = database.query(HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME,
                        allColumns,null,null,null,sortOrder,"10");
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
        Data data = new Data();
        data.setId(cursor.getInt(0));
        data.setX_value(cursor.getLong(1));
        data.setY_value(cursor.getLong(2));
        data.setZ_value(cursor.getLong(3));
        data.setTimestamp(cursor.getLong(4));
        return data;
    }
}
