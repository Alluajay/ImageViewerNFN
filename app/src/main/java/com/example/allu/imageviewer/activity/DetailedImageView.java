package com.example.allu.imageviewer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.fragments.DetailedFragment;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.utils.Utils;
import com.squareup.picasso.Picasso;

public class DetailedImageView extends AppCompatActivity {
    static String TAG = DetailedImageView.class.getSimpleName();
    public static final String Intent_Image = "Image";
    Utils utils;

    ImagesClass imagesClass;
    DetailedFragment detailedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_image_view);
        utils = new Utils(this);

        if(!getIntent().hasExtra(Intent_Image)){
            utils.Toast("Unable to load the activity");
            return;
        }
        imagesClass = getIntent().getParcelableExtra(Intent_Image);
        detailedFragment = (DetailedFragment)getSupportFragmentManager().findFragmentById(R.id.detailedFragment);
        if(detailedFragment != null){
            detailedFragment.setImageClass(imagesClass);
        }else{
            utils.Toast("Unable to open the image");
        }
    }
}
