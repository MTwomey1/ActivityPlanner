package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPlan extends AppCompatActivity implements View.OnClickListener {

    TextView tv_plan_creator, tv_plan_activity, tv_plan_when, tv_plan_where;
    TextView tv_invited, tv_accepted;
    ImageButton btn_people;
    Plan myPlan;
    Button btn_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);

        tv_plan_creator = findViewById(R.id.tv_plan_creator);
        tv_plan_activity = findViewById(R.id.tv_activity_id);
        tv_plan_when = findViewById(R.id.tv_when_id);
        tv_plan_where = findViewById(R.id.tv_where_id);
        btn_people = findViewById(R.id.btn_people_id);
        tv_invited = findViewById(R.id.tv_invited_id);
        tv_accepted = findViewById(R.id.tv_accepted_id);
        btn_chat = findViewById(R.id.btn_chat_id);

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
        String plan_id = myPlan.getPlan_id();

        get_plan_users(plan_id);

        btn_people.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10001) {
            finish();
            startActivity(getIntent());
        }
    }

    private void get_plan_users(String plan_id) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.getPlanUsers(plan_id, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String username  = jObject.get("username"+i).toString();
                        String status  = jObject.get("status"+i).toString();

                        if(status.equals("null")){
                            tv_invited.append(username+", ");
                        }
                        else{
                            tv_accepted.append(username+", ");
                        }
                    }
                    fix_views();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void fix_views() {
        String tv_inv = tv_invited.getText().toString();
        tv_inv = tv_inv.substring(0, tv_inv.length() - 2);
        tv_invited.setText(tv_inv);

        String tv_acc = tv_accepted.getText().toString();
        tv_acc = tv_acc.substring(0, tv_acc.length() - 2);
        tv_accepted.setText(tv_acc);
        Log.d("myTo", tv_acc);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btn_people_id:{
                if (AppStatus.getInstance(this).isOnline()) {
                    Intent addFriends = new Intent(this, AddFriendsToPlan.class);
                    String planGSON = new Gson().toJson(myPlan);
                    addFriends.putExtra("Plan", planGSON);
                    startActivityForResult(addFriends, 10001);
                }
                else {
                    Toast.makeText(this.getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case R.id.btn_chat_id:{
                Intent chatRoom = new Intent(this, ChatRoom.class);
                String planGSON = new Gson().toJson(myPlan);
                chatRoom.putExtra("Plan", planGSON);
                startActivity(chatRoom);

                break;
            }

        }
    }
}
