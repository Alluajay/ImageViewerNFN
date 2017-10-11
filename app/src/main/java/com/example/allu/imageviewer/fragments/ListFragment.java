package com.example.allu.imageviewer.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.allu.imageviewer.R;
import com.example.allu.imageviewer.activity.MainActivity;
import com.example.allu.imageviewer.pojo.ImagesClass;
import com.example.allu.imageviewer.recyclerViewAdapter.ImagesRecyclerViewAdapter;
import com.example.allu.imageviewer.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;

import static android.R.attr.bitmap;

public class ListFragment extends Fragment {
    static String TAG = ListFragment.class.getSimpleName();

    Utils utils;
    Context context;
    RequestQueue requestQueue;

    SwipeRefreshLayout refreshLayout;
    TextView txtNoImages;
    RecyclerView recyclerView;

    ImagesRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ImagesClass> imagesClassArrayList;

    InteractionInterface interactionInterface;
    static boolean attach = false;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        context = view.getContext();
        utils = new Utils(context);
        requestQueue = Volley.newRequestQueue(context);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)(context)).removeCustomActionBar();
                recyclerViewAdapter.removeSelection();
                fetchDataFromServer();
            }
        });
        txtNoImages = (TextView)view.findViewById(R.id.txt_noImages);

        recyclerView = (RecyclerView)view.findViewById(R.id.recy_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        recyclerView.setHasFixedSize(false);
        ListItemClickInterface listItemClickInterface = new ListItemClickInterface() {
            @Override
            public void onItemClicked(ImagesClass imagesClass) {
                interactionInterface.onItemSelected(imagesClass);
            }

            @Override
            public void onSelection(int count) {
                interactionInterface.onSelection(count);
            }
        };
        recyclerViewAdapter = new ImagesRecyclerViewAdapter(context,listItemClickInterface);
        recyclerView.setAdapter(recyclerViewAdapter);
        if(utils.checkImageData()){
            loadPrefData();
        }else{
            fetchDataFromServer();
        }
        return view;
    }

    void loadPrefData(){
        stopSwipe();
        ArrayList<ImagesClass> imagesClasses = utils.getImageData();
        if(imagesClasses.size() == 0){
            displayText(getString(R.string.emptyList));
        }else{
            recyclerViewAdapter.setImagesClassArrayList(utils.getImageData());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof InteractionInterface){
            interactionInterface = (InteractionInterface)context;
        }else{
            throw new RuntimeException(new Throwable(getString(R.string.implementInteractionInterface)));
        }
        if(recyclerViewAdapter != null){
            recyclerViewAdapter.removeSelection();
            if(utils.checkImageData()){
                loadPrefData();
            }else{
                fetchDataFromServer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerViewAdapter != null && !attach){
            attach = false;
            recyclerViewAdapter.removeSelection();
            if(utils.checkImageData()){
                loadPrefData();
            }else{
                fetchDataFromServer();
            }
        }else if(!attach){
            attach = false;
        }else {
            recyclerViewAdapter.removeSelection();
            if(utils.checkImageData()){
                loadPrefData();
            }else{
                fetchDataFromServer();
            }
        }
    }

    @Override
    public void onDetach() {
        interactionInterface = null;
        super.onDetach();
    }

    void startSwipe(){
        if(!refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(true);
        }
    }

    void stopSwipe(){
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    public boolean isSelected(){
        return recyclerViewAdapter.getSelectedImages().size()>0;
    }

    public void reloadData(){
        recyclerViewAdapter.removeSelection();
        loadPrefData();
    }

    public void shareImages(){
        if(recyclerViewAdapter.getSelectedImages().size() <= 0){
            utils.Toast(getString(R.string.selectItems));
            return;
        }
        ArrayList<Uri> uris = new ArrayList<>();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        for(int i = 0; i < recyclerViewAdapter.getSelectedImages().size(); i++) {
            Log.e(TAG,i+"");
            String pathofBmp = MediaStore.Images.Media.insertImage(context.getContentResolver(), recyclerViewAdapter.getSelectedImages().get(i).getBitmapImage(),recyclerViewAdapter.getSelectedImages().get(i).getId()+"", null);
            Uri bmpUri = Uri.parse(pathofBmp);
            uris.add(bmpUri);
        }
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("image/*");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share "+recyclerViewAdapter.getSelectedImages().size()+" Image?");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e(TAG,requestCode+" "+resultCode);
        attach = true;
    }



    public void deleteImages(){
        if(recyclerViewAdapter.getSelectedImages().size() <= 0){
            utils.Toast(getString(R.string.selectItems));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete "+recyclerViewAdapter.getSelectedImages().size()+" Image?");
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                dialogInterface.dismiss();
                ArrayList<ImagesClass> selectedImages = recyclerViewAdapter.getSelectedImages();
                for(int i = 0;i<selectedImages.size();i++){
                    utils.removeImageData(selectedImages.get(i).getId());
                }
                reloadData();
                recyclerViewAdapter.setImagesClassArrayList(utils.getImageData());
                ((MainActivity)(context)).removeCustomActionBar();
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

    void fetchDataFromServer(){
        if(!utils.isNetworkAvailable()){
            stopSwipe();
            utils.Toast(getString(R.string.networkNotAvailable));
            displayText(getString(R.string.networkNotAvailable));
            return;
        }
        imagesClassArrayList = new ArrayList<>();
        recyclerViewAdapter.removeSelection();
        clearText();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.requestUrl), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                stopSwipe();
             //   Log.e(TAG,response.toString());
                recyclerViewAdapter.clearList();
                try {
                    JSONArray imageArray = response.getJSONArray("images");
                    if(imageArray.length() == 0){
                        displayText(getString(R.string.noImagesFound));
                        return;
                    }
                    for(int i = 0;i<imageArray.length();i++){
                        JSONObject object = imageArray.getJSONObject(i);
                        int id = object.getInt("id");
                        String url = object.getString("url");
                        ImagesClass imagesClass = new ImagesClass(id,url);
                        imagesClassArrayList.add(imagesClass);
                        recyclerViewAdapter.addCustomer(imagesClass);
                    }
                    utils.setImageData(recyclerViewAdapter.getImagesClassArrayList());
                    Log.e(TAG,utils.getImageData().size()+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayText(getString(R.string.unableToFetch));
                    utils.Toast(getString(R.string.unableToFetch)+" "+e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopSwipe();
                displayText(getString(R.string.unableToFetch));
                Log.e(TAG,error.toString());
            }
        });
        startSwipe();
        requestQueue.add(request);
    }

    public void displayText(String msg){
        recyclerView.setVisibility(View.GONE);
        txtNoImages.setVisibility(View.VISIBLE);
        txtNoImages.setText(msg);
    }

    public void clearText(){
        recyclerView.setVisibility(View.VISIBLE);
        txtNoImages.setVisibility(View.GONE);
    }

    public interface InteractionInterface{
        void onItemSelected(ImagesClass imagesClass);
        void onSelection(int count);
    }

    public interface ListItemClickInterface{
        void onItemClicked(ImagesClass imagesClass);
        void onSelection(int count);
    }

}
