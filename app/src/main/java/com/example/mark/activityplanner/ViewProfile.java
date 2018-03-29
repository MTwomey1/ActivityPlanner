package com.example.mark.activityplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class ViewProfile extends AppCompatActivity implements View.OnClickListener {

    TextView tv_user, tv_name;
    private ImageButton btn_add_user;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        tv_user = findViewById(R.id.tv_user_id);
        tv_name = findViewById(R.id.tv_name_id);
        btn_add_user = findViewById(R.id.add_user_btn_id);

        btn_add_user.setOnClickListener(this);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("User");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("User");
        }
        //tv_user.setText(newString);
        getProfile(newString);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_user_btn_id: {

                AlertDialog.Builder altdial = new AlertDialog.Builder(ViewProfile.this);
                altdial.setMessage("Send " + username + " a friend request?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String mUsername = sharedPref.getString("username", null);
                                add_user(mUsername, username);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Friend Request");
                alert.show();

                break;
            }
        }
    }

    private void add_user(String mUsername, String username) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_user(mUsername, username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Toast.makeText(getApplicationContext(),"Friend Request Sent!",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void getProfile(String newString) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_profile(newString, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    //for (int i = 0; i < jObject.length(); i++){
                        username  = jObject.get("username").toString();
                        String firstname  = jObject.get("firstname").toString();
                        String lastname  = jObject.get("lastname").toString();
                        //String activities  = jObject.get("activities"+i).toString();
                        Log.d("myTag", String.valueOf(jObject.length()));
                        tv_user.setText(username);
                        tv_name.setText(firstname + " " + lastname);
                    //}
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
