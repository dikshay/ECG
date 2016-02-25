package com.example.abdul.healthmonitor.model;

/**
 * Created by Dikshay on 2/25/2016.
 */
public class Data {

    private int id;
    private long timestamp;
    private long x_value;
    private long y_value;
    private long z_value;

    public long getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getX_value() {
        return x_value;
    }

    public void setX_value(long x_value) {
        this.x_value = x_value;
    }

    public long getY_value() {
        return y_value;
    }

    public void setY_value(long y_value) {
        this.y_value = y_value;
    }

    public long getZ_value() {
        return z_value;
    }

    public void setZ_value(long z_value) {
        this.z_value = z_value;
    }


}

