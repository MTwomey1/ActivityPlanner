package com.example.mark.activityplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mark.activityplanner.utils.Activity;
import com.example.mark.activityplanner.utils.Globals;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreatePlans extends AppCompatActivity implements View.OnClickListener {

    Globals g = Globals.getInstance();

    Spinner activity_spinner;
    ArrayAdapter<String> adapter;
    String[] items = {"Airsoft", "American Football", "Archery", "Badminton", "Baseball", "Basketball", "BMX", "Boxing", "Canoe / Kayak", "Climbing", "Cricket", "Curling", "Cycling", "Darts", "Diving", "Dodgeball", "Equestrian", "Fencing", "GAA", "Golf", "Gymnastics", "Handball", "Hiking", "Hockey", "Hurling", "Judo", "Karate", "Motocross", "Mountain Biking", "Mountain Boarding", "Netball", "Paintball", "Rollerblading", "Rowing", "Rugby", "Running", "Sailing", "Scootering", "Shooting", "Skateboarding", "Skiing", "Snooker", "Snowboarding", "Soccer / Football", "Swimming", "Surfing", "Squash", "Table Tennis", "Taekwondo", "Tennis", "Track & Field", "Triathlon", "Ultimate Frisbee", "Unicycling", "Volleyball", "Wakeboarding", "Walking", "Water Polo", "Weightlifting", "Wind Surfing", "Wrestling"};
    TextView tv_date;
    Calendar mCurrentDate;
    int day, month, year;
    Button btn_create;
    EditText et_location;
    String username, date;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plans);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);

        activity_spinner = findViewById(R.id.activity_spinner_id);
        tv_date = findViewById(R.id.date_id);
        btn_create = findViewById(R.id.btn_create_id);
        et_location = findViewById(R.id.et_location_id);

        if(sharedPref.contains("Activities")) {
            Log.d("myTag", "Trying");
            Set<String> set = sharedPref.getStringSet("Activities", null);
            List<String> sample = new ArrayList<String>(set);
            sample.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sample);

        }
        else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        }

        activity_spinner.setAdapter(adapter);

        mCurrentDate = Calendar.getInstance();
        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        month = month+1;

        tv_date.setText(day+"-"+month+"-"+year);
        date = (year+"-"+month+"-"+day);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePlans.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        tv_date.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
                        date = (year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btn_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_create_id: {

                String activity = activity_spinner.getSelectedItem().toString();
                String location = et_location.getText().toString();

                if (isEmpty(location) == false) {
                    et_location.setError("Enter location");
                    return;
                }

                create_plan(username, activity, date, location);

                break;
            }
        }
    }

    private void create_plan(String username, String activity, String date, String location) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.create_plan(username, activity, date, location, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                createChat(returned_string);
                g.setTest(1);
                setResult(10001);
                finish();

            }
        });
    }

    private void createChat(String returned_string) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(returned_string,"");
        root.updateChildren(map);
    }

    // check if editText is empty
    static private boolean isEmpty(String text) {
        return (text.trim().length() > 0);
    }

}
