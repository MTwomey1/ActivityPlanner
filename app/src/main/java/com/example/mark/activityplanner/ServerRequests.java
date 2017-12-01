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
import java.util.HashMap;
import java.util.Map;

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

    // for logging in
    public void fetch_user_data_in_background(User user, Get_String_Callback string_callback) {

        // place data to send in a HashMap
        Map<String, String> data_to_send = new HashMap<>();
        data_to_send.put("username", user.username);

        // show progress
        progressDialog.show();

        // send data to sever - the sever will echo back the results
        new connection(data_to_send, "fetch_user_data.php", string_callback).execute();

    }

}
