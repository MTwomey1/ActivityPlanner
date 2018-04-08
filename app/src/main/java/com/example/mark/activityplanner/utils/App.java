package com.example.mark.activityplanner.utils;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.example.mark.activityplanner.R;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
    }
}