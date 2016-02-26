package com.example.abdul.healthmonitor.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dikshay on 2/25/2016.
 */

public class DBHelper extends SQLiteOpenHelper{
    private DBHelper dbHelper;
    private static final String TAG = "Debug data";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME + " (" +
                    HealthMonitorReaderContract.AccelerometerDataEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                    HealthMonitorReaderContract.AccelerometerDataEntry.XVALUE + "INTEGER" + COMMA_SEP +
                    HealthMonitorReaderContract.AccelerometerDataEntry.YVALUE + "INTEGER" + COMMA_SEP +
                    HealthMonitorReaderContract.AccelerometerDataEntry.ZVALUE + "INTEGER" + COMMA_SEP +
                    HealthMonitorReaderContract.AccelerometerDataEntry.TIMESTAMP + "INTEGER" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HealthMonitorReaderContract.AccelerometerDataEntry.TABLE_NAME;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HealthMonitor.db";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("DEBUG","OnCreate called");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.d("DEBUG","On upgrage called");
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        Log.d("DEBUG","On downgrade called");

    }

}
