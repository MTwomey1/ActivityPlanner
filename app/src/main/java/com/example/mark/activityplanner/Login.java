package com.example.mark.activityplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText et_username_login, et_password_login;
    Button btn_login;
    private String f_password;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        et_username_login = findViewById(R.id.et_username_login_id);
        et_password_login = findViewById(R.id.et_password_login_id);
        btn_login = findViewById(R.id.btn_user_login_id);
        progressBar = findViewById(R.id.progressBar2);

        btn_login.setOnClickListener(this);
        et_password_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String username = et_username_login.getText().toString();
                            String password = et_password_login.getText().toString();
                            f_password = password;

                            User user = new User(username, password);

                            authenticate(user);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

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
        progressBar.setVisibility(View.VISIBLE);

        ServerRequests server_requests = new ServerRequests(this);
        server_requests.login_user(user, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if (returned_string.equals("failed")) {
                    progressBar.setVisibility(View.GONE);
                    show_error_message("Incorrect username or password");
                    Log.d("myTag","Fookd String");
                } else {

                    try {
                        Log.d("myTag","String fill");

                        // Create a JSONObject from the returned String
                        JSONObject jObject = new JSONObject(returned_string);

                        String username = jObject.getString("username");
                        String email = jObject.getString("email");
                        String firstname = jObject.getString("firstname");
                        String lastname = jObject.getString("lastname");

                        //User user = new User(username,password,email,firstname,lastname);
                        //log_user_in(user);



                        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", username);
                        editor.putString("firstname", firstname);
                        editor.putString("lastname", lastname);
                        editor.putString("email", email);
                        editor.putBoolean("IS_LOGIN", true);
                        editor.apply();

                        firebase_login(email, f_password);



                        //finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }// end else
            }// end done();
        });
    }

    private void firebase_login(String email, String f_password) {
        mAuth.signInWithEmailAndPassword(email, f_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(Login.this, UserHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, UserHome.class));
        }
    }

    private void show_error_message(String error) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(Login.this);
        dialog_builder.setMessage(error);
        dialog_builder.setPositiveButton("Ok", null);
        dialog_builder.show();
    }
}
