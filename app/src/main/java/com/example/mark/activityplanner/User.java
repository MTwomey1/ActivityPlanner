package com.example.mark.activityplanner;

/**
 * Created by Mark on 30/03/2017.
 */

public class User {

    String username, email, password, firstname, lastname;

    // for logging in
    public User(String username, String password, String email, String firstname, String lastname) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(String username, String firstname, String lastname) {

        this.username = username;
        this.firstname = "";
        this.lastname = "";

    }
}
