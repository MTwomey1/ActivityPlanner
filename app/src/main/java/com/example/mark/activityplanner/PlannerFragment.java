package com.example.mark.activityplanner;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerFragment extends Fragment implements View.OnClickListener {

    private ListView lv;
    Button btn_create_plans;
    String username;

    public PlannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);
        retrieve_plans(username);

        lv = view.findViewById(R.id.listView);
        List<String> mArrayList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            mArrayList.add("Plan "+ i);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mArrayList);
        lv.setAdapter(arrayAdapter);

        btn_create_plans = view.findViewById(R.id.btn_create_id);
        btn_create_plans.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    private void retrieve_plans(String username) {
        ServerRequests server_requests = new ServerRequests(this.getActivity());
        server_requests.retrieve_plans(username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                Log.d("myTag", returned_string);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btn_create_id:{
                Intent createIntent = new Intent(getActivity(), CreatePlans.class);
                startActivity(createIntent);

                break;
            }

        }
    }
}
