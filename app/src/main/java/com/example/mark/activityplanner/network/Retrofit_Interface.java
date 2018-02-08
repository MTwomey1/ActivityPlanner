package com.example.mark.activityplanner.network;

/**
 * Created by Mark on 30/01/2018.
 */

import com.example.mark.activityplanner.User;

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
}
