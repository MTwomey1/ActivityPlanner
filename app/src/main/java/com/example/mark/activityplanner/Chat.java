package com.example.mark.activityplanner;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mark.activityplanner.model.ChatMessage;
import com.example.mark.activityplanner.model.SizeNotifierRelativeLayout;
import com.example.mark.activityplanner.model.Status;
import com.example.mark.activityplanner.model.UserType;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

public class Chat extends AppCompatActivity implements View.OnClickListener, SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate{

    String chatFriend;
    String mUsername;
    private DatabaseReference root;
    TextView tv_chat;
    Button btn_chat_send;
    EditText chatInput;
    private String temp_key;
    private ArrayList<ChatMessage> chatMessages;
    private ListView chatListView;
    private ChatListAdapter listAdapter;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private EditText chatEditText1;
    private ImageView enterChatView1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        chatMessages = new ArrayList<>();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

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
        //btn_chat_send.setOnClickListener(this);

        listAdapter = new ChatListAdapter(chatMessages, this);

        chatListView.setAdapter(listAdapter);

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        //sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        //sizeNotifierRelativeLayout.delegate = this;
    }

    private String chat_msg, chat_username, tStamp;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            tStamp = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();

            final ChatMessage message = new ChatMessage();
            message.setMessageStatus(Status.SENT);
            message.setMessageText(chat_msg);

            if(chat_username.equals(mUsername)){
                message.setUserType(UserType.OTHER);
            }
            else{
                message.setUserType(UserType.SELF);
                message.setMessageUser(chatFriend);
            }
            long timestamp = Long.parseLong(tStamp);

            message.setMessageTime(timestamp);
            chatMessages.add(message);
            //tv_chat.append(chat_username + " : " + chat_msg + " \n\n");
        }
        chatListView.setSelection(listAdapter.getCount()-1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }
    }

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    long tStamp = new Date().getTime();
                    String time = Long.toString(tStamp);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("username", mUsername);
                    map2.put("msg", chatEditText1.getText().toString());
                    map2.put("timestamp", time);

                    message_root.updateChildren(map2);
                    listAdapter.notifyDataSetChanged();
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                long tStamp = new Date().getTime();
                String time = Long.toString(tStamp);

                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("username", mUsername);
                map2.put("msg", chatEditText1.getText().toString());
                map2.put("timestamp", time);

                message_root.updateChildren(map2);
                listAdapter.notifyDataSetChanged();
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

    @Override
    public void onSizeChanged(int keyboardHeight) {

    }
}
