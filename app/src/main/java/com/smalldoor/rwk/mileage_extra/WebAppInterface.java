package com.smalldoor.rwk.mileage_extra;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by quant on 17/11/2016.
 * Web interface to link javascriopt to android
 */

public class WebAppInterface {
    private Context mContext;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {

        mContext = c;
    }
    /* create the sql database for the backup */

    /** Show a toast from the web page */
    @JavascriptInterface
    private void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }
    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
    /* Checks if external storage is available to at least read */
    @SuppressWarnings("unused")
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
    private void writeToFile(String folderName, String data){
        // get directory to save backup
        String path = Environment.getExternalStorageDirectory() + File.separator + "Mileage" + File.separator + folderName;
        // create the folder
        File folder = new File(path);
        if (!folder.exists()){
            if (!folder.mkdirs()){
                Log.d("writeToFile", "mkdirs failed");
            }
        }
        // make the file name string
        String backupDate = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss", Locale.ENGLISH).format(new Date());
        String backupFileName = "MilesBack_" + backupDate + ".txt";
        // make the file path
        File file = new File(folder, backupFileName);
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if(file.createNewFile()) {
                    // backup file doesn't exist so we write to the new one
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(data);
                    // close the writer
                    osw.close();
                    // close the stream
                    fos.flush();
                    fos.close();
                } else {
                    showToast("Backup file already exists");
                }
            } else {
                ActivityCompat.requestPermissions((Activity)mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed:" + e.toString());
        }
    }
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void makeBackup(String results) {

        showToast("Backing up data");
        Log.d("results", results);

        if (isExternalStorageWritable() ){
            writeToFile("Backup", results);
        } else {
            showToast("Cannot write to external storage");
        }
    }
}
