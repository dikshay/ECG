package com.example.abdul.healthmonitor.Util;

import android.provider.BaseColumns;

/**
 * Created by Dikshay on 2/24/2016.
 */
public final class HealthMonitorReaderContract {

    public HealthMonitorReaderContract(){}

    public static abstract class AccelerometerDataEntry implements BaseColumns{
        public static  String TABLE_NAME;
        public static String TIMESTAMP = "timestamp";
        public static String XVALUE = "xvalue";
        public static String YVALUE = "yvalue";
        public static String ZVALUE = "zvalue";

    }
}
