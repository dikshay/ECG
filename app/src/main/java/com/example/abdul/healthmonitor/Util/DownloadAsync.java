package com.example.abdul.healthmonitor.Util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.abdul.healthmonitor.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Dikshay on 3/11/2016.
 */
public class DownloadAsync extends AsyncTask<Void,Void,Void> {

    public MainActivity activity;
    public DownloadAsync(MainActivity a)
    {
        this.activity = a;
    }
    String downloadServerUri = "https://impact.asu.edu/Appenstance/HealthMonitor_ak_dp_bb.db";
    private static String DB_PATH =  "/data/data/com.example.abdul.healthmonitor/databases/";
    final String downloadFilePath = Environment.getExternalStorageDirectory()+"/downloads/";
    final String downloadFileName = "HealthMonitor_ak_dp_bb.db";
    int serverResponseCode = 0;

    private void downloadFile() {
        {
            {
                InputStream input = null;
                OutputStream output = null;
                HttpsURLConnection connection = null;
                File directory;
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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
                }};

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
                    URL url = new URL(downloadServerUri);
                    connection = (HttpsURLConnection) url.openConnection();

                    connection.connect();
                    if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                        Log.d("Warning", "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage());
                        return;
                    }

                    Log.d("Writable", "External Storage Writable");
                    input = connection.getInputStream();

                    if (isExternalStorageWritable()) {
                        //sd card exists
                        directory = new File(Environment.getExternalStorageDirectory()
                                + "/healthmonitor/");
                        if (directory.exists()) {
                            File[] dirFiles = directory.listFiles();
                            if (dirFiles.length > 0) {
                                for (int ii = 0; ii < dirFiles.length; ii++) {
                                    dirFiles[ii].delete();
                                }
                                dirFiles = null;
                            }
                        }
                        // if no directory exists, create new directory to store test
                        // results
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        output = new FileOutputStream(new File(Environment.getExternalStorageDirectory()
                                + "/healthmonitor/" + downloadFileName));
                    } else {
                        //no sd card
                        directory = new File(Environment.getDataDirectory()
                                + "/healthmonitor/");
                        if (directory.exists()) {
                            File[] dirFiles = directory.listFiles();
                            if (dirFiles.length != 0) {
                                for (int ii = 0; ii <= dirFiles.length; ii++) {
                                    dirFiles[ii].delete();
                                }
                            }
                        }
                        // if no directory exists, create new directory
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        output = new FileOutputStream(new File(Environment.getDataDirectory()
                                + "/healthmonitor/" + downloadFileName));
                    }


                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            Log.d("Important", "Cancelled");
                            return;

                        }
                        total += count;
                        output.write(data, 0, count);

                    }
                    Log.d("Success", "File Downloaded");
                    copyDataBase();


                } catch (Exception e) {
                    e.printStackTrace();
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
            }
        }
    }
    @Override
    protected Void doInBackground(Void... params) {

        downloadFile();
        return null;
    }
    @Override
    protected void onPostExecute(Void result)
    {
        activity.updateGraphDownload();
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    private void copyDataBase() throws IOException
    {
        String sourceFileUri = Environment.getExternalStorageDirectory()
                + "/healthmonitor/" + downloadFileName;
        File sourceFile = new File(sourceFileUri);
        FileInputStream mInput = new FileInputStream(sourceFile);
        OutputStream mOutput;
        if(checkDataBase()) {
            Log.d("Database","Database exists");
            String outFileName = DB_PATH + DBHelper.DATABASE_NAME;
            mOutput = new FileOutputStream(outFileName);
        }
        else
        {
            Log.d("Database","Database not yet created");
            mOutput = new FileOutputStream(new File(DB_PATH + downloadFileName));
        }
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
        Log.d("Success","Copy Successful");
    }
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DBHelper.DATABASE_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
