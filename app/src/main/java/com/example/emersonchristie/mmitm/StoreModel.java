package com.example.emersonchristie.mmitm;

import android.graphics.Bitmap;

public class StoreModel {


    public String name, address, distance, duration, placeID;
    public Bitmap placePhoto;
    public Double rating;

    public StoreModel(String name, String address, String distance, String duration, Bitmap placePhoto, String placeID, Double rating) {

        this.name = name;
        this.address = address;
        this.distance = distance;
        this.duration = duration;
        this.placePhoto = placePhoto;
        this.placeID = placeID;
        this.rating = rating;
    }

}
