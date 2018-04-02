package com.example.mark.activityplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Friends;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ViewProfile extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_user, tv_name, tv_activities;
    private ImageButton btn_add_user;
    private String username;
    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mProgressbar = findViewById(R.id.progress);
        mSubscriptions = new CompositeSubscription();

        tv_user = findViewById(R.id.tv_profile);
        tv_name = findViewById(R.id.tv_fullname_id);
        btn_add_user = findViewById(R.id.add_user_btn_id);
        tv_activities = findViewById(R.id.tv_activities_id);

        btn_add_user.setOnClickListener(this);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("User");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("User");
        }
        //tv_user.setText(newString);
        getProfile(newString);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_user_btn_id: {

                AlertDialog.Builder altdial = new AlertDialog.Builder(ViewProfile.this);
                altdial.setMessage("Send " + username + " a friend request?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String mUsername = sharedPref.getString("username", null);
                                add_user(mUsername, username);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Friend Request");
                alert.show();

                break;
            }
        }
    }

    private void add_user(String mUsername, String username) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_user(mUsername, username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Toast.makeText(getApplicationContext(),"Friend Request Sent!",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void getProfile(String newString) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_profile(newString, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    username  = jObject.get("username").toString();
                    String firstname  = jObject.get("firstname").toString();
                    String lastname  = jObject.get("lastname").toString();
                    String privacy = jObject.get("privacy").toString();

                    tv_user.setText("Profile: " + username);
                    tv_name.setText(firstname + " " + lastname);

                    if(privacy.equals("1")){
                        get_activities();
                        get_images();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void get_images() {
    }

    private void get_activities() {
        User user = new User(username);

        mProgressbar.setVisibility(View.VISIBLE);

        mSubscriptions.add(RetrofitRequest.getRetrofit().getActivities(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleResponse(Friends friends) {
        mProgressbar.setVisibility(View.GONE);

        List<String> friendslist = friends.getFriends();
        Set<String> set = new HashSet<String>();

        for (int i = 0; i < friendslist.size(); i++) {
            String name1 = friendslist.get(i);
            //tv_activities.append(name1);
            //tv_activities.append(", ");
            set.add(name1);
        }

        List<String> sample = new ArrayList<String>(set);
        sample.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        tv_activities.setText(sample.toString().replace("[", "").replace("]",""));

        //String tv_act = tv_activities.getText().toString();
        //tv_act = tv_act.substring(0, tv_act.length() - 2);
        //tv_activities.setText(tv_act);

    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Log.d("MyTag", error.toString());
        }
    }

}
