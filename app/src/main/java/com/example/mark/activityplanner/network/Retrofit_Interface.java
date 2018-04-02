package com.example.mark.activityplanner.network;

/**
 * Created by Mark on 30/01/2018.
 */

import com.example.mark.activityplanner.User;
import com.example.mark.activityplanner.utils.Activity;
import com.example.mark.activityplanner.utils.Friend;
import com.example.mark.activityplanner.utils.Friends;

import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface Retrofit_Interface {

    // posting registered username
    @POST("user")
    Observable<Response<ResponseBody>> postUsername(@Body User user2);

    // Make friendship
    @POST("friend")
    Observable<Response<ResponseBody>> addFriend(@Body Friend friend);

    // Get Friends
    @POST("myFriends")
    Observable<Friends> getFriends(@Body User user);

    // Add Activities
    @POST("addActivities")
    Observable<Response<ResponseBody>> addActivities(@Body Activity activity);

    // Get Suggestions
    @POST("getSuggestions")
    Observable<Friends> getSuggestions(@Body User user);

    // Get Suggestions
    @POST("getActivities")
    Observable<Friends> getActivities(@Body User user);

    // Check friendship
    @POST("checkFriends")
    Observable<Friends> checkFriends(@Body Friend friend);

}
