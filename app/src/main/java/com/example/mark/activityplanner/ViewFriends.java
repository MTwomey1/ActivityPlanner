package com.example.mark.activityplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Friend;
import com.example.mark.activityplanner.utils.Friends;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ViewFriends extends AppCompatActivity {

    String mUsername, removeFriendRequest;
    ListView listView, listView2;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listItems2 = new ArrayList<String>();
    ArrayAdapter<String> adapter, adapter2;
    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        mSubscriptions = new CompositeSubscription();
        SharedPreferences sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mUsername = sharedPref.getString("username", null);
        get_requests(mUsername);
        get_friends(mUsername);

        listView = findViewById(R.id.requests_list_id);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String friend = listView.getItemAtPosition(i).toString();
                alert(friend);
            }
        });

        listView2 = findViewById(R.id.friends_list_id);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems2);
        listView2.setAdapter(adapter2);

        mProgressbar = findViewById(R.id.progressBar);
    }

    private void get_friends(String mUsername) {
            User user = new User(mUsername);

            mSubscriptions.add(RetrofitRequest.getRetrofit().getFriends(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleGetResponse,this::handleError));
    }

    private void handleGetResponse(Friends friends) {

        List<String> friendslist = friends.getFriends();

        for (int i = 0; i < friendslist.size(); i++) {
            String name1 = friendslist.get(i);
            adapter2.add(name1);
        }
    }

    /*
    private void get_friends(String mUsername) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_friends(mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String friend_one  = jObject.get("friend_one"+i).toString();

                        adapter2.add(friend_one);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    */

    private void alert(final String friend) {
        AlertDialog.Builder altdial = new AlertDialog.Builder(ViewFriends.this);
        altdial.setMessage("Add " + friend + "as friend?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add_friend(friend);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = altdial.create();
        alert.setTitle("Friend Request!");
        alert.show();
    }

    private void add_friend(String friend) {
        Friend mfriend = new Friend(mUsername, friend);
        removeFriendRequest = friend;

        mSubscriptions.add(RetrofitRequest.getRetrofit().addFriend(mfriend)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response<ResponseBody> responseBodyResponse) {
        Log.e("Error", responseBodyResponse.message());

        Toast.makeText(this, "Friend Added!",
                Toast.LENGTH_LONG).show();

        removeRequest(removeFriendRequest);
    }

    private void removeRequest(String removeFriendRequest) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.removeFriendRequest(mUsername, removeFriendRequest, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Log.d("MyTag", "Request Gone");
                    finish();
                    startActivity(getIntent());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage("What " + errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
            Log.d("MyTag", error.toString());
        }
    }

    /*
    private void add_friend(String friend) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_friend(friend, mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    */

    private void get_requests(String mUsername) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_requests(mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String friend_one  = jObject.get("friend_one"+i).toString();

                        adapter.add(friend_one);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.actvity_register), message, Snackbar.LENGTH_SHORT).show();
    }
}
