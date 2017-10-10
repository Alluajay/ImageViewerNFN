package com.example.allu.imageviewer.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    static final String RequestUrl = "http://www.expns.nfndev.com/images_list?page=1";

    Utils utils;
    Context context;
    RequestQueue requestQueue;

    SwipeRefreshLayout refreshLayout;
    TextView txtNoImages;
    RecyclerView recyclerView;

    ImagesRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ImagesClass> imagesClassArrayList;

    InteractionInterface interactionInterface;

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
        fetchDataFromServer();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof InteractionInterface){
            interactionInterface = (InteractionInterface)context;
        }else{
            throw new RuntimeException(new Throwable("Implement Interaction Interface"));
        }
        if(recyclerViewAdapter != null){
            recyclerViewAdapter.removeSelection();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerViewAdapter != null){
            recyclerViewAdapter.removeSelection();
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

    public void reloadData(){
        recyclerViewAdapter.removeSelection();
    }

    public void shareImages(){
        Log.e(TAG,recyclerViewAdapter.getSelectedImages().size()+"");
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

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, RequestUrl, null, new Response.Listener<JSONObject>() {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayText(getString(R.string.unableToFetch));
                    utils.Toast("Error in fetching data "+e.toString());
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
