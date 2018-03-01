package com.example.mark.activityplanner.utils;

/**
 * Created by Mark on 01/03/2018.
 */

public class Globals {
    private static Globals instance;
    private static int Test;

    private Globals(){

    }

    public void setTest(int t){
        Globals.Test=t;
    }

    public static int getTest() {
        return Globals.Test;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
