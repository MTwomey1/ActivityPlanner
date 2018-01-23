package com.example.mark.activityplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ViewFriends extends AppCompatActivity {

    String mUsername;
    ListView listView, listView2;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listItems2 = new ArrayList<String>();
    ArrayAdapter<String> adapter, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        SharedPreferences sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mUsername = sharedPref.getString("username", null);
        get_requests(mUsername);
        get_friends(mUsername);

        listView = findViewById(R.id.requests_list_id);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String friend = listView.getItemAtPosition(i).toString();
                alert(friend);
            }
        });

        listView2 = findViewById(R.id.friends_list_id);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems2);
        listView2.setAdapter(adapter2);
    }

    private void get_friends(String mUsername) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_friends(mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String friend_one  = jObject.get("friend_one"+i).toString() + " ";

                        adapter2.add(friend_one);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void alert(final String friend) {
        AlertDialog.Builder altdial = new AlertDialog.Builder(ViewFriends.this);
        altdial.setMessage("Add " + friend + "as friend?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add_friend(friend);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = altdial.create();
        alert.setTitle("Friend Request!");
        alert.show();
    }

    private void add_friend(String friend) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_friend(friend, mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void get_requests(String mUsername) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_requests(mUsername, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String friend_one  = jObject.get("friend_one"+i).toString() + " ";

                        adapter.add(friend_one);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
