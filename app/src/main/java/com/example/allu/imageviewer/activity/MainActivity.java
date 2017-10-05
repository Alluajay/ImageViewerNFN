package com.example.allu.imageviewer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
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

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.photos));
        utils = new Utils(this);

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
    public void onSelection() {
        if(menu != null){
            final MenuItem share = menu.add("Share");
            share.setIcon(R.drawable.ic_share_black_24dp);
            share.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(listFragment != null){
                        listFragment.shareImages();
                    }
                    return false;
                }
            });
            final MenuItem delete = menu.add("Delete");
            delete.setIcon(R.drawable.ic_delete_black_24dp);
            delete.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return false;
                }
            });

            MenuItem mi = menu.add("Cancel");
            mi.setIcon(R.drawable.ic_cancel_black_24dp);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.e(TAG,"clicked");
                    if(listFragment != null){
                        Log.e(TAG,"clicked");
                        listFragment.reloadData();
                        menu.removeItem(menuItem.getItemId());
                        menu.removeItem(share.getItemId());
                        menu.removeItem(delete.getItemId());
                    }
                    return false;
                }
            });
            Log.e(TAG,"selection menu");
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

}
