package com.example.mark.activityplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    Button btn_update;
    ArrayList<String> selectedItems = new ArrayList<>();
    TextView tv;
    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mSubscriptions = new CompositeSubscription();

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        btn_update = findViewById(R.id.btn_update_id);
        tv = findViewById(R.id.tv_activities);
        mProgressbar = findViewById(R.id.progressBar);
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

            tv.setText(sample.toString().replace("[", "").replace("]",""));
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

                String username = sharedPref.getString("username", null);

                Activity activity = new Activity(username, set);
                add_activities(activity);

                finish();
                startActivity(getIntent());

                break;
            }
        }
    }

    private void add_activities(Activity activity) {
        mSubscriptions.add(RetrofitRequest.getRetrofit().addActivities(activity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response<ResponseBody> responseBodyResponse) {
        Log.e("Response", responseBodyResponse.message());
    }

    private void handleError(Throwable error) {
        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage("What " + errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
            Log.d("MyTag", error.toString());
        }
    }

    private void add_activities2(Set<String> set, String username) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_activities(set, username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Log.d("myTag", returned_string);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_edit_profile), message, Snackbar.LENGTH_SHORT).show();
    }

}
