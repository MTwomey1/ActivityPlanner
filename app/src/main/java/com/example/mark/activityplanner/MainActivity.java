package com.example.mark.activityplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private Button btn_login;
    private TextView tv_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        boolean check = sharedPref.getBoolean("IS_LOGIN", false);
        if(check){
            Intent intent = new Intent(this, UserHome.class);
            startActivity(intent);
            finish();
        }

        btn_register = findViewById(R.id.btn_register_id);
        btn_login = findViewById(R.id.btn_login_id);
        tv_welcome = findViewById(R.id.tv_welcome_id);

        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_welcome.setText("Welcome to Activity Planner!");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btn_register_id:{
                Intent registerIntent = new Intent(this, Register.class);
                startActivity(registerIntent);

                break;
            }
            case R.id.btn_login_id:{
                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);

                break;
            }

        }
    }
}
