package com.example.mark.activityplanner;

import android.support.annotation.NonNull;

/**
 * Created by Mark on 29/12/2017.
 */

public class Plan{
    String username, activity, date, location, plan_id;

    public Plan(String plan_id, String username, String activity, String date, String location){
        this.plan_id = plan_id;
        this.username = username;
        this.activity = activity;
        this.date = date;
        this.location = location;
    }

    public String getPlan_id(){
        return plan_id;
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
