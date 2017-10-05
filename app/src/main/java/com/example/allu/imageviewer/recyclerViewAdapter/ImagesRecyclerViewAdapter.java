package com.example.allu.imageviewer.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.activity.DetailedImageView;
import com.example.allu.imageviewer.fragments.ListFragment;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.allu.imageviewer.activity.DetailedImageView.Intent_Image;

/**
 * Created by allu on 10/5/17.
 */

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesListHolder> {
    static String TAG = ImagesRecyclerViewAdapter.class.getSimpleName();
    ArrayList<ImagesClass> imagesClassArrayList;
    ArrayList<ImagesClass> selectedImages;
    Context context;
    Utils utils;
    ListFragment.ListItemClickInterface listItemClickInterface;

    static boolean selection = false;
    static final int selectionMargin = 24;

    public ImagesRecyclerViewAdapter(Context context, ListFragment.ListItemClickInterface listItemClickInterface) {
        this.context = context;
        imagesClassArrayList = new ArrayList<>();
        utils = new Utils(context);
        this.listItemClickInterface = listItemClickInterface;
        selectedImages = new ArrayList<>();
    }

    @Override
    public ImagesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item_layout,parent,false);
        return new ImagesListHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesListHolder holder, final int position) {
        Log.e(TAG,"added "+position);
        final ImagesClass imagesClass = imagesClassArrayList.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(imagesClass.getUrl()).placeholder(R.mipmap.ic_launcher_round).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
                imagesClass.setLoaded(true);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
                imagesClass.setLoaded(false);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selection){
                    if(imagesClass.isLoaded()){
                        listItemClickInterface.onItemClicked(imagesClass);
                    }else{
                        utils.Toast("The images is not loaded");
                    }
                }else{
                    if(!imagesClassArrayList.get(position).isSelection()){
                        holder.checkBoxSelection.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(selectionMargin,selectionMargin,selectionMargin,selectionMargin);
                        holder.imageView.setLayoutParams(lp);
                        holder.checkBoxSelection.setChecked(true);
                        imagesClassArrayList.get(position).setSelection(true);
                        imagesClass.setBitmapImage(holder.imageView.getDrawingCache());
                        selectedImages.add(imagesClass);
                    }else {
                        holder.checkBoxSelection.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        holder.imageView.setLayoutParams(lp);
                        holder.checkBoxSelection.setChecked(false);
                        imagesClassArrayList.get(position).setSelection(false);
                        removeSelectedImage(imagesClassArrayList.get(position).getId());
                    }

                }
            }
        });

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(holder.checkBoxSelection.getVisibility() == View.INVISIBLE){
                    holder.checkBoxSelection.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(selectionMargin,selectionMargin,selectionMargin,selectionMargin);
                    holder.imageView.setLayoutParams(lp);
                    holder.checkBoxSelection.setChecked(true);
                    imagesClassArrayList.get(position).setSelection(true);
                    listItemClickInterface.onSelection();
                    imagesClass.setBitmapImage(holder.imageView.getDrawingCache());
                    selectedImages.add(imagesClass);
                    selection = true;
                }
                return true;
            }
        });

        if(!selection){
            holder.checkBoxSelection.setVisibility(View.INVISIBLE);
            imagesClassArrayList.get(position).setSelection(false);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.imageView.setLayoutParams(lp);
        }

    }

    @Override
    public int getItemCount() {
        return imagesClassArrayList.size();
    }

    public void addCustomer(ImagesClass imagesClass){
        Log.e(TAG,"image added "+imagesClass.getUrl());
        this.imagesClassArrayList.add(imagesClass);
        this.notifyItemInserted(imagesClassArrayList.size());
        this.notifyDataSetChanged();
    }

    public void clearList(){
        imagesClassArrayList = new ArrayList<>();
        this.notifyItemRangeRemoved(0,imagesClassArrayList.size()-1);
        this.notifyDataSetChanged();
    }

    public void removeSelection(){
        selection = false;
        notifyDataSetChanged();
        notifyItemRangeChanged(0,imagesClassArrayList.size()-1);
    }

    public ArrayList<ImagesClass> getSelectedImages(){
        return this.selectedImages;
    }

    public void removeSelectedImage(int id){
        for(int i = 0;i<selectedImages.size();i++){
            if(selectedImages.get(i).getId() == id){
                selectedImages.remove(selectedImages.get(i));
                return;
            }
        }
    }
}

class ImagesListHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    ProgressBar progressBar;
    CheckBox checkBoxSelection;

    public ImagesListHolder(View itemView) {
        super(itemView);
        imageView = (ImageView)itemView.findViewById(R.id.image);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progreeBar);
        checkBoxSelection = (CheckBox)itemView.findViewById(R.id.checkbox_selection);
    }
}
