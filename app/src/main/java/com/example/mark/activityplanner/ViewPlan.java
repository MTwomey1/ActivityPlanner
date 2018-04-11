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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.activityplanner.utils.Globals;
import com.google.firebase.auth.FirebaseAuth;
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
    private ImageButton btn_delete, btn_archive, btn_unhappy;
    private String plan_id;
    Globals g = Globals.getInstance();
    private SharedPreferences sharedPref;
    private String mUsername;

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
        btn_delete = findViewById(R.id.btn_delete_id);
        btn_archive = findViewById(R.id.btn_archive_id);
        btn_unhappy = findViewById(R.id.ibtn_unhappy_id);

        Bundle bundle = getIntent().getExtras();
        String planStr = bundle.getString("Plan");
        Gson gson = new Gson();
        Type type = new TypeToken<Plan>(){
        }.getType();
        myPlan = gson.fromJson(planStr, type);
        Date date = myPlan.getFDate();
        SimpleDateFormat spf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String dateStr = spf.format(date);

        sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mUsername = sharedPref.getString("username", "");

        if(!mUsername.equals(myPlan.getUsername())){
            btn_delete.setVisibility(View.GONE);
            btn_archive.setVisibility(View.GONE);
        }
        else{
            btn_unhappy.setVisibility(View.GONE);
        }

        tv_plan_creator.setText("Creator: "+myPlan.getUsername());
        tv_plan_activity.setText(myPlan.getActivity());
        tv_plan_when.setText(dateStr);
        tv_plan_where.setText("Location: "+myPlan.getLocation());
        plan_id = myPlan.getPlan_id();

        get_plan_users(plan_id);

        btn_people.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_archive.setOnClickListener(this);
        btn_unhappy.setOnClickListener(this);
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
                    Log.d("moopr", String.valueOf(jObject));


                    for (int i = 0; i < jObject.length(); i++){
                        String username  = jObject.get("username"+i).toString();
                        String status  = jObject.get("status"+i).toString();

                        if(status.equals("null")){
                            tv_invited.append(username);

                            if(i <  jObject.length()/2 - 1){
                                tv_invited.append(", ");
                            }

                        }
                        else{
                            tv_accepted.append(username);

                            if(i <  jObject.length()/2 - 1){
                                tv_accepted.append(", ");
                            }
                        }

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

            case R.id.btn_archive_id:{
                AlertDialog.Builder altdial = new AlertDialog.Builder(ViewPlan.this);
                altdial.setMessage("Are you sure you want to save this plan to archive?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                archive_plan(plan_id);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Archive Plan");
                alert.show();

                break;
            }

            case R.id.btn_delete_id:{
                AlertDialog.Builder altdial = new AlertDialog.Builder(ViewPlan.this);
                altdial.setMessage("Are you sure you want to delete this plan?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete_plan(plan_id);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Delete Plan");
                alert.show();

                break;
            }

            case R.id.ibtn_unhappy_id:{
                AlertDialog.Builder altdial = new AlertDialog.Builder(ViewPlan.this);
                altdial.setMessage("Are you sure you want to leave this plan?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                leave_plan(plan_id);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Leave Plan");
                alert.show();

                break;
            }

        }
    }

    private void leave_plan(String plan_id) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.leavePlan(mUsername, plan_id, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if(returned_string.equals("Successful")){
                    Toast.makeText(ViewPlan.this,"Plan Left", Toast.LENGTH_LONG).show();
                    finish();
                    Intent refreshIntent = new Intent(ViewPlan.this, UserHome.class);
                    g.setTest(1);
                    startActivity(refreshIntent);
                }else{
                    Toast.makeText(ViewPlan.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void archive_plan(String plan_id) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.archivePlan(plan_id, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if(returned_string.equals("Successful")){
                    Toast.makeText(ViewPlan.this,"Plan Archived", Toast.LENGTH_LONG).show();
                    finish();
                    Intent refreshIntent = new Intent(ViewPlan.this, UserHome.class);
                    g.setTest(1);
                    startActivity(refreshIntent);
                }else{
                    Toast.makeText(ViewPlan.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void delete_plan(String plan_id) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.deletePlan(plan_id, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if(returned_string.equals("Successful")){
                    Toast.makeText(ViewPlan.this,"Plan Deleted", Toast.LENGTH_LONG).show();
                    finish();
                    Intent refreshIntent = new Intent(ViewPlan.this, UserHome.class);
                    g.setTest(1);
                    startActivity(refreshIntent);
                }else{
                    Toast.makeText(ViewPlan.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
