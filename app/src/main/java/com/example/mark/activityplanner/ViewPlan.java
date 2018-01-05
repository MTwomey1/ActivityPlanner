package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewPlan extends AppCompatActivity {

    TextView tv_plan;
    int pos = 0;
    Plan plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);

        tv_plan = findViewById(R.id.tv_plan_id);

        /*Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            tv_plan.setText(bundle.getString("Plan"));
        }*/
        Intent intent = getIntent();
        pos = intent.getExtras().getInt("Plan");
       //plan = PlanAdapter.(pos);
    }
}
