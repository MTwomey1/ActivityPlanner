package com.example.mark.activityplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText et_username_login, et_password_login;
    Button btn_login;
    String f_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username_login = findViewById(R.id.et_username_login_id);
        et_password_login = findViewById(R.id.et_password_login_id);
        btn_login = findViewById(R.id.btn_user_login_id);

        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        // check if user has active internet connection
        if (AppStatus.getInstance(this).isOnline()) {

            switch (view.getId()) {

                case R.id.btn_user_login_id:

                    String username = et_username_login.getText().toString();
                    String password = et_password_login.getText().toString();
                    f_password = password;

                    User user = new User(username, password);

                    authenticate(user);
                    break;
            }

        } else {
            Toast.makeText(getApplicationContext(),"You are offline", Toast.LENGTH_LONG).show();
        }
    }

    private void authenticate(User user) {

        ServerRequests server_requests = new ServerRequests(this);
        server_requests.login_user(user, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if (returned_string.equals("failed")) {
                    show_error_message("Incorrect username or password");
                    Log.d("myTag","Fookd String");
                } else {

                    try {
                        Log.d("myTag","String fill");

                        // Create a JSONObject from the returned String
                        JSONObject jObject = new JSONObject(returned_string);

                        String username = jObject.getString("username");
                        //String password = jObject.getString("password");
                        String email = jObject.getString("email");
                        String firstname = jObject.getString("firstname");
                        String lastname = jObject.getString("lastname");

                        //User user = new User(username,password,email,firstname,lastname);
                        //log_user_in(user);

                        Intent intent = new Intent(Login.this, UserHome.class);

                        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", username);
                        editor.putString("firstname", firstname);
                        editor.putString("lastname", lastname);
                        editor.putString("email", email);
                        editor.putString("f_password", f_password);
                        editor.putBoolean("IS_LOGIN", true);
                        editor.apply();

                        Login.this.startActivity(intent);

                        //finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }// end else
            }// end done();
        });
    }

    private void show_error_message(String error) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(Login.this);
        dialog_builder.setMessage(error);
        dialog_builder.setPositiveButton("Ok", null);
        dialog_builder.show();
    }
}
