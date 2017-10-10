package com.example.allu.imageviewer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.fragments.DetailedFragment;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.example.allu.imageviewer.activity.MainActivity.MY_PERMISSIONS_REQUEST_STORAGE_FOR_DOWNLOAD;

public class DetailedImageView extends AppCompatActivity implements DetailedFragment.ActionInterface {
    static String TAG = DetailedImageView.class.getSimpleName();
    public static final String Intent_Image = "Image";
    Utils utils;

    ImagesClass imagesClass;
    DetailedFragment detailedFragment;
    Bitmap downloadBitmap;
    String imgTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_image_view);
        setTitle(getString(R.string.DetailedView));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        utils = new Utils(this);

        if(!getIntent().hasExtra(Intent_Image)){
            utils.Toast(getString(R.string.unableToLoadImg));
            return;
        }
        imagesClass = getIntent().getParcelableExtra(Intent_Image);
        detailedFragment = (DetailedFragment)getSupportFragmentManager().findFragmentById(R.id.detailedFragment);
        if(detailedFragment != null){
            detailedFragment.setImageClass(imagesClass);
        }else{
            utils.Toast(getString(R.string.unableToLoadImg));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_FOR_DOWNLOAD:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    utils.downloadImage(downloadBitmap,imgTitle);
                } else {
                    utils.Toast(getString(R.string.downloadPermission));
                }
                return;
            }
        }
    }

    boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(DetailedImageView.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DetailedImageView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDownload(Bitmap bitmap,String title) {
        if(checkStoragePermission()){
            ActivityCompat.requestPermissions(DetailedImageView.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE_FOR_DOWNLOAD);
            downloadBitmap = bitmap;
            imgTitle = title;
        }else {
            utils.downloadImage(bitmap,title);
        }
    }
}
