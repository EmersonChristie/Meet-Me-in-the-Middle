package com.example.emersonchristie.mmitm;

public class RecentTrip {
    public String date;
    public String lat;
    public String lng;
    public String pid;
    public String place_name;
    public String name;
    public String number;

    public RecentTrip() {
    }

    public RecentTrip(String date, String lat, String lng, String pid, String place_name, String name, String number){
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.pid = pid;
        this.place_name = place_name;
        this.name = name;
        this.number = number;
    }

    public String getDate(){
        return this.date;
    }

    public String getLat(){
        return this.lat;
    }

    public String getLng() {
        return this.lng;
    }

    public String getPid(){
        return this.pid;
    }

    public String getPlace_name(){
        return this.place_name;
    }

    public String getName(){
        return this.name;
    }
    public String getNumber(){
        return this.number;
    }
}

