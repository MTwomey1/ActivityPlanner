package com.example.mark.activityplanner;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mark.activityplanner.network.RetrofitRequest;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView tv_fullname, tv_activities;
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataset;
    private ImageButton btn_logout;
    private ImageButton btn_manage;
    private Button btn_friends, btn_archive;
    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar;
    FirebaseAuth mAuth;
    ImageView image_profile;
    private String profileImage;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private DatabaseReference mDatabaseRef, mDatabaseRef2;
    private ValueEventListener listener;
    private List<Upload> mUploads, mUploads2;


    public ProfileFragment() {
        // Required empty public constructor

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mSubscriptions = new CompositeSubscription();
        mAuth = FirebaseAuth.getInstance();


        tv_fullname = view.findViewById(R.id.tv_fullname_id);
        btn_logout = view.findViewById(R.id.imageButton_ID);
        btn_manage = view.findViewById(R.id.manage_btn_id);
        tv_activities = view.findViewById(R.id.tv_activities_id);
        btn_friends = view.findViewById(R.id.btn_friends_id);
        mProgressbar = view.findViewById(R.id.progress);
        image_profile = view.findViewById(R.id.image_profile_id);
        btn_archive = view.findViewById(R.id.btn_archive_id);

        sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String firstname = sharedPref.getString("firstname","");
        String lastname = sharedPref.getString("lastname","");
        String username = sharedPref.getString("username","");
        String email = sharedPref.getString("email","");
        tv_fullname.setText(firstname + " " + lastname);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/"+username+"/images");
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("users/"+username);

        if(sharedPref.contains("Activities")) {
            Set<String> set = sharedPref.getStringSet("Activities", null);
            List<String> sample = new ArrayList<String>(set);

            sample.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });

            tv_activities.setText(sample.toString().replace("[", "").replace("]",""));
        }else{
            getActivities(username);
        }

        if(sharedPref.contains("profileImage")){
            profileImage = sharedPref.getString("profileImage","");
            setProfileImage();
        }
        else{
            getProfileImage();
        }

        if (!AppStatus.getInstance(this.getActivity()).isOnline()) {
            Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
        }
        else {
            getImages(email);
        }

        mUploads = new ArrayList<>();
        //for (int i = 1; i < 11; i++) {
           // mDataset.add("Photo # " + i);
        //}

        mRecycleView = view.findViewById(R.id.recyclerView);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecycleView.setLayoutManager(mLayoutManager);
        //mAdapter = new MainAdapter(mDataset);
        //mRecycleView.setAdapter(mAdapter);

        btn_logout.setOnClickListener(this);
        btn_manage.setOnClickListener(this);
        btn_friends.setOnClickListener(this);
        btn_archive.setOnClickListener(this);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                mAdapter = new MainAdapter(getActivity(), mUploads);
                mRecycleView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void getProfileImage() {

        Query lastQuery = mDatabaseRef2.child("profileImages").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String fUrl = child.child("imageUrl").getValue().toString();
                    setProfileImage2(fUrl);
                    editor.putString("profileImage", fUrl);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Failed to read app title value.", databaseError.toException());
            }
        });
    }

    private void getImages(String email) {

    }

    private void setProfileImage2(String s) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(16));
        Glide.with(this)
                .load(s)
                .apply(requestOptions)
                .into(image_profile);
    }

    private void setProfileImage() {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(16));
            Glide.with(this)
                    .load(profileImage)
                    .apply(requestOptions)
                    .into(image_profile);
    }

    private void getActivities(String username) {
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

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("Activities", set);
        editor.apply();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {

            Log.d("myTag", "100011");
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
    }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageButton_ID: {

                    AlertDialog.Builder altdial = new AlertDialog.Builder(ProfileFragment.this.getActivity());
                    altdial.setMessage("Are you sure you want to logout?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences sharedPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    //editor.putBoolean("IS_LOGIN", false);
                                    //editor.apply();
                                    editor.clear();
                                    editor.commit();
                                    //mDatabaseRef.removeEventListener(listener);
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    FirebaseAuth.getInstance().signOut();


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alert = altdial.create();
                    alert.setTitle("Logout");
                    alert.show();

                    break;
                }
                case R.id.manage_btn_id: {
                    if (AppStatus.getInstance(this.getActivity()).isOnline()) {
                        Intent manageIntent = new Intent(getActivity(), EditProfile.class);
                        startActivityForResult(manageIntent, 10001);
                    }
                    else {
                        Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
                case R.id.btn_friends_id: {
                    if (AppStatus.getInstance(this.getActivity()).isOnline()) {
                        Intent friendsIntent = new Intent(getActivity(), ViewFriends.class);
                        startActivity(friendsIntent);
                    }
                    else {
                        Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                    }

                    break;
                }

                case R.id.btn_archive_id: {
                    if (AppStatus.getInstance(this.getActivity()).isOnline()) {
                        Intent archiveIntent = new Intent(getActivity(), Archive.class);
                        startActivity(archiveIntent);
                    }
                    else {
                        Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
            }
        }

}
