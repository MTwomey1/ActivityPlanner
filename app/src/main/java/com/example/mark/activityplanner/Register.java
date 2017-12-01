package com.example.mark.activityplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.text.TextUtils.isEmpty;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText et_username, et_password, et_confirm_password, et_email, et_firstname, et_lastname;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_username_id);
        et_password = findViewById(R.id.et_password_id);
        et_confirm_password = findViewById(R.id.et_confirm_password_id);
        et_email = findViewById(R.id.et_email_id);
        et_firstname = findViewById(R.id.et_first_name_id);
        et_lastname = findViewById(R.id.et_last_name_id);

        btn_register = findViewById(R.id.btn_reg_user_id);

        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // check if user has active internet connection
        if (AppStatus.getInstance(this).isOnline()) {

            switch (view.getId()) {
                case R.id.btn_reg_user_id:

                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();
                    String confirm_pass = et_confirm_password.getText().toString();
                    String email = et_email.getText().toString();
                    String firstname = et_firstname.getText().toString();
                    String lastname = et_lastname.getText().toString();

                    if (isEmpty(username) == false) {
                        et_username.setError("Enter username");
                        return;
                    }

                    if (isEmpty(password) == false) {
                        et_password.setError("Enter password");
                        return;
                    }

                    if (isEmpty(confirm_pass) == false) {
                        et_confirm_password.setError("Enter confirm password");
                        return;
                    }

                    /*if(isEmpty(email) == false){
                        et_email.setError("Enter email");
                        return;
                    }*/

                    if(isEmpty(firstname) == false){
                        et_firstname.setError("Enter firstname");
                        return;
                    }

                    if(isEmpty(lastname) == false){
                        et_lastname.setError("Enter lastname");
                        return;
                    }


                    if (password.equals(confirm_pass)) {
                        User user = new User(username, password, email, firstname, lastname);
                        register_user(user);

                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }else {
            Toast.makeText(getApplicationContext(),"You are offline", Toast.LENGTH_SHORT).show();
        }
    }

    private void register_user(User user) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.store_user_data_in_background(user, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {

                if (returned_string.trim().equals("Username taken")) {
                    et_username.setError("Username Already in Use");
                }
                else {
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                }
            }
        });
    }

    // check if editText is empty
    static private boolean isEmpty(String text) {
        return (text.trim().length() > 0);
    }

}
