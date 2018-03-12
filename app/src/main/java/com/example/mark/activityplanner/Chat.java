package com.example.mark.activityplanner;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity implements View.OnClickListener{

    String chatFriend;
    String mUsername;
    private DatabaseReference root;
    TextView tv_chat;
    Button btn_chat_send;
    EditText chatInput;
    private String temp_key;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tv_chat = findViewById(R.id.tv_chat_id);
        btn_chat_send = findViewById(R.id.btn_chat_send_id);
        chatInput = findViewById(R.id.et_chat_message_id);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            chatFriend = bundle.getString("chat_friend");
            mUsername = bundle.getString("mUsername");
        }

        List<String> sample = new ArrayList<String>();
        sample.add(chatFriend);
        sample.add(mUsername);

        sample.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        String chatName = "";
        for(String s : sample){
            chatName = chatName + s;
        }

        root = FirebaseDatabase.getInstance().getReference().child(chatName);
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btn_chat_send.setOnClickListener(this);
    }

    private String chat_msg, chat_username;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();

            tv_chat.append(chat_username + " : " + chat_msg + " \n\n");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_chat_send_id:{
                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("username", mUsername);
                map2.put("msg", chatInput.getText().toString());

                message_root.updateChildren(map2);

                break;
            }
        }
    }
}
