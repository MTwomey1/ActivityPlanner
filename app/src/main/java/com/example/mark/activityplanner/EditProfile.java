package com.example.mark.activityplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    Button btn_update;
    ArrayList<String> selectedItems = new ArrayList<>();
    TextView tv;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        btn_update = findViewById(R.id.btn_update_id);
        tv = findViewById(R.id.tv_activities);
        btn_update.setOnClickListener(this);

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

            //selectedItems.addAll(sample);
            for(String o : sample){
                selectedItems.add(o);
                tv.append(o + ", ");
                Log.d("myTag", o);
            }

            //selectedItems = new ArrayList<String>(sample);
        }

        ListView listView = findViewById(R.id.checkable_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] items = {"Airsoft", "American Football", "Archery", "Badminton", "Baseball", "Basketball", "BMX", "Boxing", "Canoe / Kayak", "Climbing", "Cricket", "Curling", "Cycling", "Darts", "Diving", "Dodgeball", "Equestrian", "Fencing", "GAA", "Golf", "Gymnastics", "Handball", "Hiking", "Hockey", "Hurling", "Judo", "Karate", "Motocross", "Mountain Biking", "Mountain Boarding", "Netball", "Paintball", "Rollerblading", "Rowing", "Rugby", "Running", "Sailing", "Scootering", "Shooting", "Skateboarding", "Skiing", "Snooker", "Snowboarding", "Soccer / Football", "Swimming", "Surfing", "Squash", "Table Tennis", "Taekwondo", "Tennis", "Track & Field", "Triathlon", "Ultimate Frisbee", "Unicycling", "Volleyball", "Wakeboarding", "Walking", "Water Polo", "Weightlifting", "Wind Surfing", "Wrestling"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txt_lan, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                }
                else{
                    selectedItems.add(selectedItem);
                }
            }
        });
    }

    public void showSelected(View view){
        String items="";
        for (String item:selectedItems){
            items+="-"+item+"\n";
        }
        Toast.makeText(this, "Selected: \n"+items,Toast.LENGTH_LONG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_id: {
                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Set<String> set = new HashSet<String>();
                set.addAll(selectedItems);
                editor.putStringSet("Activities", set);
                editor.commit();
                Log.d("myTag","Fookd String");

                finish();
                startActivity(getIntent());

                break;
            }
        }
    }
}
