package com.example.mark.activityplanner;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerFragment extends Fragment implements View.OnClickListener {

    private ListView lv;
    PlanAdapter planAdapter;
    Button btn_create_plans, btn_find_plans;
    String username;
    List<String> mArrayList = new ArrayList<>();
    List<Plan> planList = new ArrayList<>();
    ProgressBar mProgressbar;

    public PlannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);

        mProgressbar = view.findViewById(R.id.progressBar4);
        lv = view.findViewById(R.id.listView);
        planAdapter = new PlanAdapter(this.getActivity(), R.layout.row_layout);

        lv.setAdapter(planAdapter);

        if (AppStatus.getInstance(this.getActivity()).isOnline()) {
            retrieve_plans(username);
        }
        else {
            Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
        }

        //planAdapter.addAll(planList);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent planIntent = new Intent(PlannerFragment.this.getActivity(), ViewPlan.class);
                //planIntent.putExtra("Plan", planAdapter.getItem(i));
                Plan myPlan = (Plan) planAdapter.getItem(i);
                String planGSON = new Gson().toJson(myPlan);
                planIntent.putExtra("Plan", planGSON);
                Log.d("myTag", myPlan.getLocation());
                startActivity(planIntent);
            }
        });

        btn_create_plans = view.findViewById(R.id.btn_create_id);
        btn_find_plans = view.findViewById(R.id.btn_find_id);
        btn_create_plans.setOnClickListener(this);
        btn_find_plans.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10001) {

            Log.d("myTag", "100011");
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
    }

    private void retrieve_plans(String username) {
        mProgressbar.setVisibility(View.VISIBLE);

        ServerRequests server_requests = new ServerRequests(this.getActivity());
        server_requests.retrieve_plans(username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                //Log.d("myTag", returned_string);
                mProgressbar.setVisibility(View.GONE);
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length()/5; i++){
                        String plan_id  = jObject.get("plan_id"+i).toString();
                        String username  = jObject.get("username"+i).toString();
                        String activity  = jObject.get("activity"+i).toString();

                        String date  = jObject.get("date"+i).toString();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddd");
                        Date newDate = format.parse(date);
                        format = new SimpleDateFormat("dd MMM, yyyy");
                        String myDate = format.format(newDate);

                        String location = jObject.get("location"+i).toString();
                        Plan plan = new Plan(plan_id, username, activity, myDate, location);
                        planList.add(plan);
                        //planAdapter.add(plan);
                    }

                    Collections.sort(planList, new Comparator<Plan>() {
                        SimpleDateFormat spf = new SimpleDateFormat("dd MMM, yyyy");

                        @Override
                        public int compare(Plan plan, Plan t1) {
                            return plan.getFDate().compareTo(t1.getFDate());
                        }
                    });
                    for(Plan p : planList){
                        planAdapter.add(p);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btn_create_id:{
                if (AppStatus.getInstance(this.getActivity()).isOnline()) {
                    Intent createIntent = new Intent(getActivity(), CreatePlans.class);
                    startActivityForResult(createIntent, 1001);
                }
                else {
                    Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case R.id.btn_find_id:{
                if (AppStatus.getInstance(this.getActivity()).isOnline()) {
                    Intent findIntent = new Intent(getActivity(), FindPlans.class);
                    findIntent.putExtra("username", username);
                    startActivity(findIntent);
                }
                else {
                    Toast.makeText(this.getActivity().getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                }

                break;
            }

        }
    }


}
