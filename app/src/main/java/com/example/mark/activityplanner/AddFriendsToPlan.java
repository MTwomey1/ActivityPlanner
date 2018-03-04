package com.example.mark.activityplanner;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Activity;
import com.example.mark.activityplanner.utils.Friends;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddFriendsToPlan extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar;
    ListView listview;
    ArrayList<String> selectedItems = new ArrayList<>();
    Button btn_invite;
    Plan myPlan;
    String plan_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_to_plan);

        mSubscriptions = new CompositeSubscription();
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);

        get_friends(username);

        Bundle bundle = getIntent().getExtras();
        String planStr = bundle.getString("Plan");
        Gson gson = new Gson();
        Type type = new TypeToken<Plan>(){
        }.getType();
        myPlan = gson.fromJson(planStr, type);
        plan_id = myPlan.getPlan_id();

        btn_invite = findViewById(R.id.btn_invite_id);
        btn_invite.setOnClickListener(this);

    }

    private void get_friends(String username) {
        User user = new User(username);

        mSubscriptions.add(RetrofitRequest.getRetrofit().getFriends(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGetResponse, this::handleError));
    }

    private void handleGetResponse(Friends friends) {
        listview = findViewById(R.id.check_list);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txt_lan);

        List<String> friendslist = friends.getFriends();

        for (int i = 0; i < friendslist.size(); i++) {
            String name1 = friendslist.get(i);
            adapter.add(name1);
        }

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else {
                    selectedItems.add(selectedItem);
                }
            }
        });
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showSnackBarMessage("What " + errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
            Log.d("MyTag", error.toString());
        }
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_view_friends), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_invite_id: {
                invite_friends(plan_id, selectedItems);

                break;
            }
        }
    }

    private void invite_friends(String plan_id, ArrayList<String> selectedItems) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.addToPlan(plan_id, selectedItems, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Log.d("MyTag", returned_string);
                    setResult(10001);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
