package com.example.allu.imageviewer.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by allu on 10/5/17.
 */

public class ImagesClass implements Parcelable{
    int id;
    String url;

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    boolean selection;

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    boolean loaded;

    protected ImagesClass(Parcel in) {
        id = in.readInt();
        url = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeByte((byte) (loaded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImagesClass> CREATOR = new Creator<ImagesClass>() {
        @Override
        public ImagesClass createFromParcel(Parcel in) {
            return new ImagesClass(in);
        }

        @Override
        public ImagesClass[] newArray(int size) {
            return new ImagesClass[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImagesClass(int id, String url) {
        this.id = id;
        this.url = url;
        loaded = false;
        selection = false;
    }

}
