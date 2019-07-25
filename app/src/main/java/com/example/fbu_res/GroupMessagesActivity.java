package com.example.fbu_res;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.adapters.ChatAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Message;
import com.example.fbu_res.models.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupMessagesActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    DatabaseReference GroupNameRef, GroupMessageKeyRef;
    int RESULT_OK = 291;
    ParseFile profileImg;

    ChatAdapter adapter;
    RecyclerView rvMessages;
    List<Message> messages;

    private String currentGroupName, currentUsername, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        rvMessages = findViewById(R.id.reyclerview_message_list);
        messages = new ArrayList<>();
        adapter = new ChatAdapter(this, messages);
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        layout = findViewById(R.id.layout_chatbox);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        currentGroupName = getIntent().getStringExtra("channel_name");
        currentUsername = ParseUser.getCurrentUser().getUsername();
        profileImg = ((Consumer) ParseUser.getCurrentUser()).getProfileImg();

        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(currentGroupName);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfotoDatabase();

                messageArea.setText("");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void saveMessageInfotoDatabase() {
        String messageText = messageArea.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if(!messageText.equals("")){
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, YYYY");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<String, Object>();
            GroupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> map = new HashMap<String, Object>();


            map.put("name", currentUsername);
            map.put("message", messageText);
            map.put("date", currentDate);
            map.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(map);
        }
    }


    public void addMessageBox(String message, int type, final String chatName){
        TextView textView = new TextView(GroupMessagesActivity.this.getApplicationContext());
        textView.setText(message);
        final ImageView ivProfile = new ImageView(this);


       layout.addView(findViewById(R.id.layoutMessageReceived));

        new AsyncTask<Object, Object, Object>(){
            @Override
            protected Object doInBackground(Object[] objects) {
                ParseQuery<Consumer> query = ParseQuery.getQuery(Consumer.class);
                query.whereEqualTo("username", chatName);
                query.findInBackground(new FindCallback<Consumer>() {
                    @Override
                    public void done(List<Consumer> objects, ParseException e) {
                        ParseUser user = objects.get(0);
                        try {
                            Glide.with(GroupMessagesActivity.this)
                                    .load(((Consumer) user).getProfileImg().getFile().getAbsolutePath())
                                    .override(100, 100)
                                    .into(ivProfile);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                return null;
            }
        }.execute();


        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }


        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void DisplayMessages(com.google.firebase.database.DataSnapshot dataSnapshot) {
       Iterator iterator = dataSnapshot.getChildren().iterator();
       String chatDate = (String) ((com.google.firebase.database.DataSnapshot) iterator.next()).getValue();
       String chatMessage = (String) ((com.google.firebase.database.DataSnapshot) iterator.next()).getValue();
       String chatName = (String) ((com.google.firebase.database.DataSnapshot) iterator.next()).getValue();
       String chatTime = (String) ((com.google.firebase.database.DataSnapshot) iterator.next()).getValue();

       Message message = new Message();
       message.setCreatedAt(chatTime);
       message.setMessage(chatMessage);
       message.setSender(chatName);

       messages.add(message);
       adapter.notifyItemInserted(messages.size()-1);


/*
        if(chatName.equals(ParseUser.getCurrentUser().getUsername())){
            addMessageBox("You:-\n" + chatMessage, 1, chatName);
        }
        else{
            addMessageBox(chatName + ":-\n" + chatMessage, 2, chatName);
        }*/



    }

    public void goBack(View view){
        Intent data = new Intent();
        ((Activity) view.getContext()).setResult(RESULT_OK, data); // set result code and bundle data for response
        ((Activity) view.getContext()).finish();
    }
}
