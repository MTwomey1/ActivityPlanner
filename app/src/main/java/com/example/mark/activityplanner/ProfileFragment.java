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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


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
    private Button btn_friends;


    public ProfileFragment() {
        // Required empty public constructor

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_fullname = view.findViewById(R.id.tv_fullname_id);
        btn_logout = view.findViewById(R.id.imageButton_ID);
        btn_manage = view.findViewById(R.id.manage_btn_id);
        tv_activities = view.findViewById(R.id.tv_activities_id);
        btn_friends = view.findViewById(R.id.btn_friends_id);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String firstname = sharedPref.getString("firstname","");
        String lastname = sharedPref.getString("lastname","");
        tv_fullname.setText(firstname + " " + lastname);

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

        }

        mDataset = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            mDataset.add("Photo # " + i);
        }

        mRecycleView = view.findViewById(R.id.recyclerView);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecycleView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(mDataset);
        mRecycleView.setAdapter(mAdapter);

        btn_logout.setOnClickListener(this);
        btn_manage.setOnClickListener(this);
        btn_friends.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
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
                                    editor.putBoolean("IS_LOGIN", false);
                                    editor.apply();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alert = altdial.create();
                    alert.setTitle("Alert!");
                    alert.show();

                    break;
                }
                case R.id.manage_btn_id: {
                    Intent manageIntent = new Intent(getActivity(), EditProfile.class);
                    startActivity(manageIntent);

                    break;
                }
                case R.id.btn_friends_id: {
                    Intent friendsIntent = new Intent(getActivity(), ViewFriends.class);
                    startActivity(friendsIntent);

                    break;
                }
            }
        }

}
