package com.example.mark.activityplanner;

/**
 * Created by Mark on 29/12/2017.
 */

public class Plan {
    String username, activity, date, location;

    public Plan(String username, String activity, String date, String location){
        this.username = username;
        this.activity = activity;
        this.date = date;
        this.location = location;
    }

    public String getUsername(){
        return username;
    }
    public  String getActivity(){
        return activity;
    }
    public String getDate(){
        return date;
    }
    public String getLocation(){
        return location;
    }
}
