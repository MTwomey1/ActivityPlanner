package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ViewPlan extends AppCompatActivity implements View.OnClickListener {

    TextView tv_plan_creator, tv_plan_activity, tv_plan_when, tv_plan_where;
    ImageButton btn_people;
    Plan myPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);

        tv_plan_creator = findViewById(R.id.tv_plan_creator);
        tv_plan_activity = findViewById(R.id.tv_activity_id);
        tv_plan_when = findViewById(R.id.tv_when_id);
        tv_plan_where = findViewById(R.id.tv_where_id);
        btn_people = findViewById(R.id.btn_people_id);

        Bundle bundle = getIntent().getExtras();
        String planStr = bundle.getString("Plan");
        Gson gson = new Gson();
        Type type = new TypeToken<Plan>(){
        }.getType();
        myPlan = gson.fromJson(planStr, type);

        tv_plan_creator.setText(myPlan.getUsername());
        tv_plan_activity.setText(myPlan.getActivity());
        tv_plan_when.setText(myPlan.getDate());
        tv_plan_where.setText(myPlan.getLocation());

        btn_people.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btn_people_id:{
                if (AppStatus.getInstance(this).isOnline()) {
                    Intent addFriends = new Intent(this, AddFriendsToPlan.class);
                    String planGSON = new Gson().toJson(myPlan);
                    addFriends.putExtra("Plan", planGSON);
                    startActivity(addFriends);
                }
                else {
                    Toast.makeText(this.getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                }

                break;
            }

        }
    }
}
