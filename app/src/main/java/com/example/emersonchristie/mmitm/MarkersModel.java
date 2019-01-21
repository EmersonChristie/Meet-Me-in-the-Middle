package com.example.emersonchristie.mmitm;

import com.google.android.gms.maps.model.LatLng;

public class MarkersModel {

    public LatLng markLatLng;
    public String markTitle, markIconType, markSnippet;
    public Boolean markVisible;

    public MarkersModel(LatLng markLatLng, String markTitle, String markIconType, String markSnippet, Boolean markVisible) {
        this.markLatLng = markLatLng;
        this.markTitle = markTitle;
        this.markIconType = markIconType;
        this.markSnippet = markSnippet;
        this.markVisible = markVisible;
    }
}
