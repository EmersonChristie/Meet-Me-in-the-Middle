package com.example.emersonchristie.mmitm;

public class Contact {
    private String name;
    private String phoneNum;

    public Contact(String name, String phoneNum){
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNum(){
        return phoneNum;
    }
}
