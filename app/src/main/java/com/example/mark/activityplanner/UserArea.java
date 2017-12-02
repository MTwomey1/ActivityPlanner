package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserArea extends AppCompatActivity {

    TextView tv_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        tv_username = findViewById(R.id.tv_user_id);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String firstname = intent.getStringExtra("firstname");

        tv_username.setText("Welcome " + username);
    }
}
