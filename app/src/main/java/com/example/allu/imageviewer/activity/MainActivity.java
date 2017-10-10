package com.example.allu.imageviewer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.fragments.DetailedFragment;
import com.example.allu.imageviewer.fragments.ListFragment;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.recyclerViewAdapter.ImagesRecyclerViewAdapter;
import com.example.allu.imageviewer.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.allu.imageviewer.activity.DetailedImageView.Intent_Image;

public class MainActivity extends AppCompatActivity implements ListFragment.InteractionInterface {
    static String TAG = MainActivity.class.getSimpleName();
    Utils utils;
    ListFragment listFragment;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 60;

    Menu menu;

    TextView selectionCount;
    ImageView img_cancel;
    ImageView img_share;
    ImageView img_delete;

    ActionBar actionBar;
    View customActionBar;
    boolean customActionBarAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.photos));
        utils = new Utils(this);
        actionBar = getSupportActionBar();

        loadSelectionActionBar();

    }

    void loadSelectionActionBar(){
        LayoutInflater mInflater = LayoutInflater.from(this);

        customActionBar = mInflater.inflate(R.layout.selection_actionbar, null);
        selectionCount = (TextView) customActionBar.findViewById(R.id.txt_selectionCount);
        img_cancel = (ImageView) customActionBar.findViewById(R.id.img_cancel);
        img_share = (ImageView) customActionBar.findViewById(R.id.img_share);
        img_delete = (ImageView) customActionBar.findViewById(R.id.img_delete);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFragment.reloadData();
                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"permission check "+checkStoragePermission());
                if(checkStoragePermission()){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                }else {
                    listFragment.shareImages();
                    listFragment.reloadData();
                    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                }
            }
        });

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.Toast("Under construction");
            }
        });

        customActionBarAdded = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);

        return true;
    }

    @Override
    public void onItemSelected(ImagesClass imagesClass) {
        Log.e(TAG,"item clicked ");
        int ori = getResources().getConfiguration().orientation;
        if(ori == Configuration.ORIENTATION_PORTRAIT){
            Log.e(TAG,"intend");
            Intent i = new Intent(MainActivity.this,DetailedImageView.class);
            i.putExtra(Intent_Image,imagesClass);
            startActivity(i);
        }else{
            DetailedFragment detailedFragment = (DetailedFragment)getSupportFragmentManager().findFragmentById(R.id.detailedFragment);
            if(detailedFragment != null){
                detailedFragment.setImageClass(imagesClass);
            }else{
                Log.e(TAG,"intend");
                Intent i = new Intent(MainActivity.this,DetailedImageView.class);
                i.putExtra(Intent_Image,imagesClass);
                startActivity(i);
            }
        }
    }

    @Override
    public void onSelection(int count) {
        selectionCount.setText(count +" Selected");
        if(!customActionBarAdded){
            actionBar.setCustomView(customActionBar);
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Close Application");
        builder.setMessage("Do you really want to close the application?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    listFragment.shareImages();
                    listFragment.reloadData();
                    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                } else {
                    utils.Toast("Unable to share until you provide storage permission");
                }
                return;
            }
        }
    }

    void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        }else{
            Log.e(TAG,"permission available");
        }
    }

    boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

}
