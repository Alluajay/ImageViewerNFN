package com.example.allu.imageviewer.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by allu on 10/5/17.
 */

public class Utils {
    static String TAG = Utils.class.getSimpleName();
    Context context;
    ProgressDialog progressDialog;

    public Utils(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
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
