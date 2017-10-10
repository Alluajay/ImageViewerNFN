package com.example.allu.imageviewer.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by allu on 10/5/17.
 */

public class Utils {
    static String TAG = Utils.class.getSimpleName();
    Context context;
    ProgressDialog progressDialog;

    SharedPreferences preferences;
    static String pref_string = "ImageViewerPref",pref_imgList = "ImageList";
    Gson gson;


    public Utils(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        preferences = context.getSharedPreferences(pref_string,Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void setImageData(ArrayList<ImagesClass> arrayList){
        String data = gson.toJson(arrayList);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(pref_imgList,data);
        editor.apply();
    }

    public ArrayList<ImagesClass> getImageData(){
        String data = preferences.getString(pref_imgList,"");
        ArrayList<ImagesClass> arrayList = new ArrayList<>();
        if(!data.isEmpty()){
            arrayList = gson.fromJson(data,new TypeToken<ArrayList<ImagesClass>>(){}.getType());
            if(arrayList == null){
                arrayList = new ArrayList<>();
            }
        }
        return arrayList;
    }

    public boolean checkImageData(){
        return preferences.contains(pref_imgList);
    }

    public void removeImageData(int id){
        String data = preferences.getString(pref_imgList,"");
        ArrayList<ImagesClass> arrayList = new ArrayList<>();
        if(!data.isEmpty()){
            arrayList = gson.fromJson(data,new TypeToken<ArrayList<ImagesClass>>(){}.getType());
            if(arrayList != null){
                for(int i = 0;i<arrayList.size();i++){
                    if(arrayList.get(i).getId() == id){
                        arrayList.remove(i);
                    }
                }
                setImageData(arrayList);
            }
        }
    }

    public File getOutputMediaFile(String title){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() +"/"+ context.getString(R.string.app_name));
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File mediaFile;
        String mImageName="IV_"+ title +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void downloadImage(Bitmap bitmap,String title){
        File pictureFile = getOutputMediaFile(title);
        if (pictureFile == null) {
            Log.d(TAG, context.getString(R.string.errorCreatingFile));// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast(context.getString(R.string.fileDownloaded)+pictureFile);
        } catch (FileNotFoundException e) {
            Log.d(TAG, context.getString(R.string.fileNotFound) + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, context.getString(R.string.errorAccessing) + e.getMessage());
        }
    }

    public void setProgressDialogMessage(String Msg){
        progressDialog.setMessage(Msg);
    }

    public void showProgressDialog(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void hideProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void Toast(String Msg){
        Toast.makeText(context,Msg,Toast.LENGTH_SHORT).show();
    }


}
