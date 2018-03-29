package com.example.mark.activityplanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mark on 01/12/2017.
 */

public class ServerRequests {
    public static final String SERVER_ADDRESS = "http://www.activityplanner.co.nf/";

    // this is for showing loading box
    ProgressDialog progressDialog;

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public class connection extends AsyncTask<Void, Void, String> {

        Map<String, String> data_to_send;
        Get_String_Callback string_callback;
        String PHP_FILE_NAME;

        public connection(Map<String, String> data_to_send, String PHP_FILE_NAME, Get_String_Callback string_callback){
            this.data_to_send = data_to_send;
            this.PHP_FILE_NAME = PHP_FILE_NAME.trim();
            this.string_callback = string_callback;
        }

        @Override
        protected String doInBackground(Void... params) {
            // Encoded String - we will have to encode string by our custom method above called getEncodedData()
            String encodedStr = getEncodedData(data_to_send);

            // Will be used if we want to read some data from server
            BufferedReader reader = null;

            // store the results, that was echoed from the server in this String
            String result = "";

            // Trying to connect
            try {
                // Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + PHP_FILE_NAME);
                // Opening the connection (Not setting or using CONNECTION_TIMEOUT)
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // Post Method
                con.setRequestMethod("POST");
                // To enable inputting values using POST method
                // Basically, after this we can write the data_to_send to the body of POST method
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                // Writing data_to_send to OutputStreamWriter
                writer.write(encodedStr);
                writer.flush();
                // Sending the data to the server - This much is enough to send data to server


                // But to read the response of the server, you will have to implement the procedure below
                // Data Read Procedure - Basically reading the data coming line by line
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                // Read till there is something available
                while ((result = reader.readLine()) != null) {
                    // Reading and saving line by line - not all at once
                    sb.append(result + "\n");
                }
                // Saving complete data received in string, you can do it differently
                result = sb.toString();

                // Just check to the values received in Logcat
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", result);

                return result.trim();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();     //Closing the
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result.trim();
        }

        // what to do after its finished
        @Override
        protected void onPostExecute(String returned_string) {
            progressDialog.dismiss();
            string_callback.done(returned_string);
            super.onPostExecute(returned_string);
        }
    }

    // method to encode data to be sent to the server
    static String getEncodedData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (sb.length() > 0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }

    // for registering user
    public void store_user_data_in_background(User user, Get_String_Callback string_callback) {

        // store data to send in HashMap
        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("username", user.username);
        data_to_send.put("password", user.password);
        data_to_send.put("email", user.email);
        data_to_send.put("firstname", user.firstname);
        data_to_send.put("lastname", user.lastname);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "Register.php", string_callback).execute();
    }

    public void login_user(User user, Get_String_Callback string_callback) {

        // place data to send in a HashMap
        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("username", user.username);
        data_to_send.put("password", user.password);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "Login.php", string_callback).execute();

    }

    public void create_plan(String username, String activity, String date, String location, Get_String_Callback string_callback){

        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("username", username);
        data_to_send.put("activity", activity);
        data_to_send.put("date", date);
        data_to_send.put("location", location);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "CreatePlan.php", string_callback).execute();
    }

    public void retrieve_plans(String username, Get_String_Callback string_callback){

        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("username", username);

        // show progress
        //progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "RetrievePlans.php", string_callback).execute();
    }

    public void find_users(String s, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("finduser", s);

        // show progress
        //progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "FindUsers.php", string_callback).execute();
    }

    public void get_profile(String username, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("getprofile", username);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "GetProfile.php", string_callback).execute();
    }

    public void add_activities(Set<String> set, String username, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("username", username);

        int i = 0;
        for(String o : set){
            data_to_send.put("activity"+i, o);
            i++;
        }
        String s = String.valueOf(i);
        data_to_send.put("amount", s);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "AddActivities.php", string_callback).execute();
    }

    public void add_user(String mUsername, String username, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("mUsername", mUsername);
        data_to_send.put("username", username);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "AddUser.php", string_callback).execute();
    }

    public void get_requests(String mUsername, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("mUsername", mUsername);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "GetRequests.php", string_callback).execute();
    }

    public void add_friend(String username, String mUsername, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("username", username);
        data_to_send.put("mUsername", mUsername);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "AddFriend.php", string_callback).execute();
    }

    public void get_friends(String mUsername, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("mUsername", mUsername);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "GetFriends.php", string_callback).execute();
    }

    public void removeFriendRequest(String mUsername, String removeFriendRequest, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("mUsername", mUsername);
        data_to_send.put("Username2", removeFriendRequest);

        // show progress
        //progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "RemoveRequest.php", string_callback).execute();
    }

    public void addToPlan(String plan_id, ArrayList<String> selectedItems, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        int i = 0;
        for(String o : selectedItems){
            data_to_send.put("username"+i, o);
            i++;
        }
        data_to_send.put("amount", String.valueOf(i));
        data_to_send.put("plan_id", plan_id);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "AddToPlan.php", string_callback).execute();
    }

    public void getPlanUsers(String plan_id, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("plan_id", plan_id);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "GetPlanUsers.php", string_callback).execute();
    }

    public void getPlanInvites(String username, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("username", username);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "GetPlanInvites.php", string_callback).execute();
    }

    public void acceptPlanInvite(String username, String plan_id, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("username", username);
        data_to_send.put("plan_id", plan_id);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "AcceptPlanInvite.php", string_callback).execute();
    }

    public void makePublic(String username, String choiceStr, Get_String_Callback string_callback){
        Map<String, String> data_to_send = new HashMap<>();

        data_to_send.put("username", username);
        data_to_send.put("choice", choiceStr);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "MakePublic.php", string_callback).execute();
    }

}
