package com.example.allu.imageviewer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.utils.Utils;
import com.squareup.picasso.Picasso;

public class DetailedFragment extends Fragment {
    static String TAG = DetailedFragment.class.getSimpleName();
    Utils utils;
    Context context;

    ImageView imageView;
    ImageButton imgBtnDownload,imgBtnDelete;

    ImagesClass imagesClass;

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
        imgBtnDownload = (ImageButton)view.findViewById(R.id.imgBtn_download);
        imgBtnDelete = (ImageButton)view.findViewById(R.id.imgBtn_delete);
        return view;
    }

    public void setImageClass(ImagesClass imageClass){
        this.imagesClass = imageClass;
        if(imageView != null){
            Picasso.with(context).load(imagesClass.getUrl()).into(imageView);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
