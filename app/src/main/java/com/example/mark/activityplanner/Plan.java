package com.example.mark.activityplanner;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mark on 29/12/2017.
 */

public class Plan implements Comparable<Plan>{
    String username, activity, date, location, plan_id;
    Date dDate;

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

    public Date getFDate(){
        SimpleDateFormat spf = new SimpleDateFormat("dd MMM, yyyy");
        try {
            dDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dDate;
    }
    public String getLocation(){
        return location;
    }

    @Override
    public int compareTo(@NonNull Plan plan) {
        return getDate().compareTo(plan.getDate());
    }
}
