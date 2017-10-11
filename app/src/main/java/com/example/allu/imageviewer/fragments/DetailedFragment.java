package com.example.allu.imageviewer.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.activity.DetailedImageView;
import com.example.allu.imageviewer.activity.MainActivity;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailedFragment extends Fragment {
    static String TAG = DetailedFragment.class.getSimpleName();
    Utils utils;
    Context context;

    ImageView imageView;
    ImageButton imgBtnShare,imgBtnDelete,imgBtnDownload;

    ImagesClass imagesClass;
    ActionInterface actionInterface;

    public DetailedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_detailed, container, false);
        context = view.getContext();
        utils = new Utils(context);
        imageView = (ImageView)view.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.image_placeholder);
        imageView.setDrawingCacheEnabled(true);
        imgBtnShare = (ImageButton)view.findViewById(R.id.imgBtn_share);
        imgBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagesClass == null){
                    utils.Toast(getString(R.string.selectImage));
                    Log.e(TAG,"null image class");
                    return;
                }
            }
        });
        imgBtnDelete = (ImageButton)view.findViewById(R.id.imgBtn_delete);
        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
            }
        });

        imgBtnDownload = (ImageButton)view.findViewById(R.id.imgBtn_download);
        imgBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage();
            }
        });
        return view;
    }


    public void shareImage(){
        if(imagesClass == null){
            utils.Toast(getString(R.string.selectImage));
            Log.e(TAG,"null image class");
            return;
        }
        ArrayList<Uri> uris = new ArrayList<>();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageView.getDrawingCache().recycle();
        String pathofBmp = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false),imagesClass.getId()+"", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        uris.add(bmpUri);

        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("image/*");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share Image?");
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivityForResult(Intent.createChooser(shareIntent, getString(R.string.shareTo)),1000);
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void deleteImage(){
        if(imagesClass == null){
            utils.Toast(getString(R.string.selectImage));
            Log.e(TAG,"null image class");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete this image?");
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                utils.removeImageData(imagesClass.getId());
                if(context instanceof MainActivity){
                    imagesClass = null;
                    imageView.setImageResource(R.drawable.image_placeholder);
                    ((MainActivity) context).loadImageData();
                }else if(context instanceof DetailedImageView) {
                    ((DetailedImageView) context).onBackPressed();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void downloadImage(){
        if(imagesClass == null){
            utils.Toast(getString(R.string.selectImage));
            Log.e(TAG,"null image class");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Download this image?");
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                imageView.buildDrawingCache();
                imageView.getDrawingCache().recycle();
                actionInterface.onDownload(imageView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false),imagesClass.getId()+"");
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    public void setImageClass(ImagesClass imageClass){
        this.imagesClass = imageClass;
        if(imageView != null){
            Picasso.with(context).load(imagesClass.getUrl()).placeholder(R.drawable.image_placeholder).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imgBtnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(imagesClass == null){
                                utils.Toast(getString(R.string.selectImage));
                                Log.e(TAG,"null image class");
                                return;
                            }
                            actionInterface.onShare();
                        }
                    });
                }

                @Override
                public void onError() {
                    utils.Toast(getString(R.string.unableToLoadImg));
                }
            });
        }
    }

    public void removeImageClass(){
        imagesClass = null;
        if(imageView != null){
            imageView.setImageResource(R.drawable.image_placeholder);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ActionInterface){
            actionInterface = (ActionInterface)context;
        }else {
            throw new RuntimeException(new Throwable(getString(R.string.implementActionInterface)));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ActionInterface{
        void onDownload(Bitmap bitmap,String title);
        void onShare();
    }
}
