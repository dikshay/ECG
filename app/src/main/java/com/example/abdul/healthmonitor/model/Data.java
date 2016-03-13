package com.example.abdul.healthmonitor.model;

/**
 * Created by Dikshay on 2/25/2016.
 */
public class Data {

    private int id;
    private String timestamp;
    private double x_value;
    private double y_value;
    private double z_value;

    public String getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getX_value() {
        return x_value;
    }

    public void setX_value(double x_value) {
        this.x_value = x_value;
    }

    public double getY_value() {
        return y_value;
    }

    public void setY_value(double y_value) {
        this.y_value = y_value;
    }

    public double getZ_value() {
        return z_value;
    }

    public void setZ_value(double z_value) {
        this.z_value = z_value;
    }

    @Override
    public String toString()
    {
        return "id: " + this.id + ", timestamp: " + this.timestamp + ", XValue: " + this.x_value + ", YValue: " + this.y_value + ", ZValue: " + this.z_value;
    }


}

