package com.example.mark.activityplanner.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mark on 20/02/2018.
 */

public class Friends {

    @SerializedName("friends")
    @Expose
    public List<String> friends = null;


    public List<String> getFriends() {
        return friends;
    }


}