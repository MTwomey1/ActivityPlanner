package com.example.mark.activityplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class ViewProfile extends AppCompatActivity {

    TextView tv_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        tv_user = findViewById(R.id.tv_user_id);

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

    private void getProfile(String newString) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.get_profile(newString, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    for (int i = 0; i < jObject.length(); i++){
                        String username  = jObject.get("username"+i).toString();
                        String firstname  = jObject.get("firstname"+i).toString();
                        String lastname  = jObject.get("lastname"+i).toString();
                        //String activities  = jObject.get("activities"+i).toString();
                        Log.d("myTag", lastname);
                        tv_user.setText(firstname + " " + lastname);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
