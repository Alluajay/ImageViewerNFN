package com.example.allu.imageviewer.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        DetailedFragment detailedFragment = (DetailedFragment)getSupportFragmentManager().findFragmentById(R.id.detailedFragment);
        if(detailedFragment == null){
            Intent i = new Intent(MainActivity.this,DetailedImageView.class);
            i.putExtra(Intent_Image,imagesClass);
            startActivity(i);
        }else{
            detailedFragment.setImageClass(imagesClass);
        }
    }

    @Override
    public void onSelection() {
        if(menu != null){
            MenuItem mi = menu.add("New Item");
            mi.setIcon(R.mipmap.ic_launcher_round);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.e(TAG,"clicked");
                    if(listFragment != null){
                        Log.e(TAG,"clicked");
                        listFragment.reloadData();
                        menu.removeItem(menuItem.getItemId());
                    }

                    return false;
                }
            });
            Log.e(TAG,"selection menu");
        }
    }
}
