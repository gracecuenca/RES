package com.example.fbu_res.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.FriendsActivity;
import com.example.fbu_res.LoginActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.VerticalRecyclerViewAdapter;
import com.example.fbu_res.models.DatedEvent;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ProfileFragment extends Fragment {

    private User user;
    private ImageView ivProfileImage;
    private TextView tvDisplayname;
    // private RecyclerView rvInterestedEvents;
    // private ArrayList<Event> events;
    // private EventAdapter adapter;
    AppCompatActivity activity;
    Toolbar toolbar;

    // set up for the nested recycler views
    private RecyclerView verticalRecyclerView;
    private VerticalRecyclerViewAdapter verticalRecyclerViewAdapter;
    private ArrayList<DatedEvent> datedEvents;

    private TreeMap<Date, DatedEvent> treeMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) ParseUser.getCurrentUser();
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvDisplayname = (TextView) view.findViewById(R.id.tvDisplayname);
        if (user.getProfileImg() != null) {
            Glide.with(getContext()).load(user.getProfileImg().getUrl()).into(ivProfileImage);
        }
        tvDisplayname.setText(user.getDisplayname());

        activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) view.findViewById(R.id.profileToolbar);
        activity.setSupportActionBar(toolbar);
        activity.setTitle("Profile");
        toolbar.setTitleTextColor(getResources().getColor(R.color.turquoise));
        setHasOptionsMenu(true);

        Button btnFriends = view.findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        // rvInterestedEvents = (RecyclerView) view.findViewById(R.id.rvInterestedEvents);
        // events = new ArrayList<>();
        // adapter = new EventAdapter(events);
        // rvInterestedEvents.setAdapter(adapter);
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // rvInterestedEvents.setLayoutManager(gridLayoutManager);

        // nested
        datedEvents = new ArrayList<>();
        verticalRecyclerView = (RecyclerView)view.findViewById(R.id.rvInterestedEvents);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        verticalRecyclerViewAdapter = new VerticalRecyclerViewAdapter(getContext(), datedEvents);
        verticalRecyclerView.setAdapter(verticalRecyclerViewAdapter);

        // loadEvents();

        // setData();

        loadData();

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.menu_profile, menu);
        Drawable maps = menu.findItem(R.id.action_logout).getIcon();
        maps.setColorFilter(getResources().getColor(R.color.turquoise), PorterDuff.Mode.SRC_IN);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    private void loadEvents(){
        if(user.getType().equals("Consumer")){
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
    /*
    // nested recycler view functions
    public void setDatedEvent(Date date){

        DatedEvent datedEvent = new DatedEvent();

        if(user.getType().equals("Consumer")){
            ParseRelation interestedEvents = user.getInterestedEvents();
            ParseQuery<Event> eventsQuery = interestedEvents.getQuery();
            eventsQuery.whereEqualTo(Event.KEY_DATE, date);
            eventsQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> objects, ParseException e) {
                    if(e != null){
                        e.printStackTrace();
                        return;
                    }
                    datedEvent.setEvents((ArrayList<Event>) objects);
                    datedEvents.add(datedEvent);
                    verticalRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDatedEvents(Date startDate, Date endDate){
        String currentDateStr = startDate.toString();
        Date currentDate = startDate;
        while(currentDate.before(endDate)){ // while there are still events to add
            setDatedEvent(currentDate);
            currentDateStr = LocalDate.parse(currentDateStr).plusDays(1).toString();
            currentDate = new Date(currentDateStr);
        }
    }
    */

    public void clear() {
       //  events.clear();
        // adapter.notifyDataSetChanged();
        datedEvents.get(0).getEvents().clear();
        verticalRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void addAll(List<Event> list) {
       //  events.addAll(list);
        // adapter.notifyDataSetChanged();
        // datedEvents.;
        verticalRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void logout(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

    public void loadData(){
        ParseQuery<Event> eventsQuery = null;
        if(user.getType().equals("Consumer")){
            ParseRelation interestedEvents = user.getInterestedEvents();
            eventsQuery = interestedEvents.getQuery();
        } else if(user.getType().equals("Business")){
            ParseRelation createdEvents = user.getCreatedEvents();
            eventsQuery = createdEvents.getQuery();
        }
        eventsQuery.addAscendingOrder(Event.KEY_DATE);
        eventsQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                for(Event event: objects){
                    user.addInterestedMap(event.getDate(), event);
                    Log.d("yur", "added event: " + event.getName());
                }
                treeMap = user.getInterestedMap();
                // setting up the actual data and displaying it
                for(Map.Entry<Date, DatedEvent> entry: treeMap.entrySet()){
                    datedEvents.add(entry.getValue());
                }
                verticalRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }




}
