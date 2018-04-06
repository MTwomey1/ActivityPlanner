package com.example.mark.activityplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Archive extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String username;
    private ListView lv_archive;
    private PlanAdapter planAdapter;
    private List<Plan> planList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "");

        lv_archive = findViewById(R.id.lv_archive_id);
        planAdapter = new PlanAdapter(this, R.layout.row_layout);
        lv_archive.setAdapter(planAdapter);

        get_archive();
    }

    private void get_archive() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.getArchive(username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
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
}
