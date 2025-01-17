package com.example.fbu_res;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.adapters.ChatAdapter;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Group;
import com.example.fbu_res.models.Message;
import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.text.SimpleDateFormat;

public class GroupMessagesActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    DatabaseReference GroupNameRef, GroupMessageKeyRef;
    int RESULT_OK = 291;
    ParseFile profileImg;
    Group currentGroup;

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
        final TextView groupName = findViewById(R.id.tvGroupName);
        final ImageView groupPic = findViewById(R.id.ivNewGroup);

        String objectId = getIntent().getStringExtra("objectId");
        if(objectId != null) {
            ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
            query.whereEqualTo("objectId", objectId);
            query.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(List<Group> objects, ParseException e) {
                    if (objects.size() != 0) {
                        currentGroup = objects.get(0);

                        groupName.setText(currentGroup.getName());
                        Glide.with(getApplicationContext())
                                .load(currentGroup.getImage().getUrl())
                                .circleCrop()
                                .into(groupPic);
                    } else {
                        groupName.setText(getIntent().getStringExtra("dm_name"));
                    }
                }
            });
        } else {
            ParseQuery<User> query = ParseQuery.getQuery(User.class);
            query.whereEqualTo("username", getIntent().getStringExtra("dm_name"));
            query.findInBackground(new FindCallback<User>() {
                @Override
                public void done(List<User> objects, ParseException e) {
                    if (objects.size() != 0) {
                        ParseUser user = objects.get(0);
                        groupName.setText(((User) user).getDisplayname());
                        Glide.with(getApplicationContext())
                                .load(((User) user).getProfileImg().getUrl())
                                .circleCrop()
                                .into(groupPic);
                    }
                }
            });
        }


        currentUsername = ParseUser.getCurrentUser().getUsername();
        profileImg = ((User) ParseUser.getCurrentUser()).getProfileImg();

        currentGroupName = getIntent().getStringExtra("channel_name");
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
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MM/dd/YYYY");
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
                ParseQuery<User> query = ParseQuery.getQuery(User.class);
                query.whereEqualTo("username", chatName);
                query.findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> objects, ParseException e) {
                        ParseUser user = objects.get(0);
                        try {
                            Glide.with(GroupMessagesActivity.this)
                                    .load(((User) user).getProfileImg().getFile().getAbsolutePath())
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


    }

    public void goBack(View view){
        Intent data = new Intent();
        ((Activity) view.getContext()).setResult(RESULT_OK, data); // set result code and bundle data for response
        ((Activity) view.getContext()).finish();
    }
}
