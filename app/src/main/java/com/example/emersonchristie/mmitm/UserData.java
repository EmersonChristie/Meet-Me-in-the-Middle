package com.example.emersonchristie.mmitm;

public class UserData {

    String location;
    String invited;
    String date;
    String placeType;

    public UserData(String location, String invited, String date, String placeType) {
        this.location = location;
        this.invited = invited;
        this.date = date;
        this.placeType = placeType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }




}
