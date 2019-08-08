package com.example.fbu_res;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout;
    private User user;
    private ImageView ivProfileImage;
    private TextView tvDisplayname;
    private RecyclerView rvInterestedEvents;
    private ArrayList<Event> events;
    private EventAdapter adapter;
    User currentUser = (User) ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // setting up the current user
        final String userId = getIntent().getStringExtra("objectId");
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo("objectId", userId);

        // showing the profile of the user
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvDisplayname = (TextView) findViewById(R.id.tvDisplayname);


        // setting up the array and display of interested events
        rvInterestedEvents = (RecyclerView) findViewById(R.id.rvInterestedEvents);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        rvInterestedEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rvInterestedEvents.setLayoutManager(gridLayoutManager);


        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if(objects != null && objects.size() > 0) {
                    user = objects.get(0);
                    if (user.getProfileImg() != null) {
                        Glide.with(ivProfileImage.getContext()).load(user.getProfileImg().getUrl()).into(ivProfileImage);
                    }
                    tvDisplayname.setText(user.getDisplayname());
                    loadEvents();
                    Button message = findViewById(R.id.btnMessage);
                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentUser.addDMUser(user);

                            Intent i = new Intent(ProfileActivity.this, GroupMessagesActivity.class);
                            String user1 = currentUser.getUsername();
                            String user2 = user.getUsername();

                            i.putExtra("channel_name", user1.compareTo(user2) > 0 ?
                                     user1 + user2 : user2 + user1);
                            startActivity(i);
                        }
                    });

                    Button button = findViewById(R.id.btnFriend);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentUser.addFriend(user);
                        }
                    });
                }

                ParseQuery<User> query2 = currentUser.getFriends().getQuery();
                query2.whereEqualTo("objectId", userId);
                query2.findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> objects, ParseException e) {
                        if(objects.size() > 0) {
                            ParseQuery<User> query3 = currentUser.getFriends().getQuery();
                            query3.whereEqualTo("objectId", currentUser.getObjectId());
                            query3.findInBackground(new FindCallback<User>() {
                                @Override
                                public void done(List<User> objects, ParseException e) {
                                    Button button = findViewById(R.id.btnFriend);
                                    if(objects.size() > 0){
                                        ((ViewGroup) button.getParent()).removeView(button);
                                    } else {
                                        button.setText("Friend Request sent");
                                        button.setClickable(false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void loadEvents(){
        if(user.getType().equals("User")){
            ParseRelation interestedEvents = user.getInterestedEvents();
            ParseQuery<Event> eventsQuery = interestedEvents.getQuery();
            eventsQuery.addAscendingOrder(Event.KEY_DATE);

            eventsQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    addAll(events);
                }
            });
        } else if(user.getType().equals("Business")){
            ParseRelation createdEvents = user.getCreatedEvents();
            ParseQuery<Event> eventsQuery = createdEvents.getQuery();
            eventsQuery.addAscendingOrder(Event.KEY_DATE);
            eventsQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    addAll(events);
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        events.clear();
        adapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Event> list) {
        events.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
