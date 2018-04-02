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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Friend;
import com.example.mark.activityplanner.utils.Friends;
import com.example.mark.activityplanner.utils.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ViewProfile extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_user, tv_name, tv_activities;
    private ImageButton btn_add_user;
    private String username, mUsername;
    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;
    private DatabaseReference mDatabaseRef;
    private ImageView iv_profile_image;
    private DatabaseReference mDatabaseRef2;
    private List<Upload> mUploads;
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mProgressbar = findViewById(R.id.progress);
        mSubscriptions = new CompositeSubscription();
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mUsername = sharedPref.getString("username", null);

        tv_user = findViewById(R.id.tv_profile);
        tv_name = findViewById(R.id.tv_fullname_id);
        btn_add_user = findViewById(R.id.add_user_btn_id);
        tv_activities = findViewById(R.id.tv_activities_id);
        iv_profile_image = findViewById(R.id.image_profile_id);

        mRecycleView = findViewById(R.id.recyclerView);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycleView.setLayoutManager(mLayoutManager);

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

                    getProfileImage(username);
                    checkIfFriends(mUsername, username);

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

    private void checkIfFriends(String mUsername, String username) {
        Friend friend = new Friend(mUsername, username);

        mSubscriptions.add(RetrofitRequest.getRetrofit().checkFriends(friend)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFriendResponse,this::handleError));
    }

    private void handleFriendResponse(Friends friends) {
        List<String> friendslist = friends.getFriends();

        String are_friends = friendslist.get(0);

        if(are_friends.equals("false")){
            btn_add_user.setVisibility(View.VISIBLE);
        }
    }

    private void getProfileImage(String username) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/"+username);
        Query lastQuery = mDatabaseRef.child("profileImages").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String fUrl = child.child("imageUrl").getValue().toString();
                    setProfileImage(fUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Failed to read app title value.", databaseError.toException());
            }
        });
    }

    private void setProfileImage(String fUrl) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(16));
        Glide.with(this)
                .load(fUrl)
                .apply(requestOptions)
                .into(iv_profile_image);
    }

    private void get_images() {
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("users/"+username+"/images");
        mUploads = new ArrayList<>();

        mDatabaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                mAdapter = new MainAdapter(ViewProfile.this, mUploads);
                mRecycleView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
