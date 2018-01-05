package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewPlan extends AppCompatActivity {

    TextView tv_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);

        tv_plan = findViewById(R.id.tv_plan_id);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("Plan");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("Plan");
        }
        tv_plan.setText(newString);
    }
}
