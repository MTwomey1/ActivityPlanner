package com.example.mark.activityplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FindPlans extends AppCompatActivity {

    ListView lv_invites;
    PlanAdapter planAdapter;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_plans);

        lv_invites = findViewById(R.id.lv_invites_id);
        planAdapter = new PlanAdapter(this, R.layout.row_layout);
        lv_invites.setAdapter(planAdapter);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        get_invites(username);

        lv_invites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Plan myPlan = (Plan) planAdapter.getItem(i);
                String plan_id = myPlan.getPlan_id();
                String name = myPlan.getUsername();

                AlertDialog.Builder altdial = new AlertDialog.Builder(FindPlans.this);
                altdial.setMessage("Accept "+name+"\'s Plan Invite?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                accept_invite(plan_id);
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

            }
        });
    }

    private void accept_invite(String plan_id) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.acceptPlanInvite(username, plan_id, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                Toast.makeText(FindPlans.this,"Yo", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void get_invites(String username) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.getPlanInvites(username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    if (returned_string.equals("[]")) {

                    } else {
                        JSONObject jObject = new JSONObject(returned_string);

                        for (int i = 0; i < jObject.length(); i++) {
                            String plan_id = jObject.get("plan_id" + i).toString();
                            String username = jObject.get("username" + i).toString();
                            String activity = jObject.get("activity" + i).toString();

                            String date = jObject.get("date" + i).toString();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddd");
                            Date newDate = format.parse(date);
                            format = new SimpleDateFormat("dd MMM, yyyy");
                            String myDate = format.format(newDate);

                            String location = jObject.get("location" + i).toString();
                            Plan plan = new Plan(plan_id, username, activity, myDate, location);
                            planAdapter.add(plan);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
