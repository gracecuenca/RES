package com.example.fbu_res.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_res.FriendsActivity;
import com.example.fbu_res.LoginActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    private User user;
    private ImageView ivProfileImage;
    private TextView tvDisplayname;
    private RecyclerView rvInterestedEvents;
    private ArrayList<Event> events;
    private EventAdapter adapter;
    AppCompatActivity activity;
    Toolbar toolbar;

    // Event removedEvent;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // getting the event that was removed from calendar
        // removedEvent = (Event)getArguments().get("removed event");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting up toolbar
        activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) view.findViewById(R.id.profileToolbar);
        activity.setSupportActionBar(toolbar);
        activity.setTitle("Profile");
        toolbar.setTitleTextColor(getResources().getColor(R.color.turquoise));
        setHasOptionsMenu(true);

        user = (User) ParseUser.getCurrentUser();

        // showing the profile of the user
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvDisplayname = (TextView) view.findViewById(R.id.tvDisplayname);
        if (user.getProfileImg() != null) {
            Glide.with(getContext()).load(user.getProfileImg().getUrl()).into(ivProfileImage);
        }
        tvDisplayname.setText(user.getDisplayname());
        rvInterestedEvents = (RecyclerView) view.findViewById(R.id.rvInterestedEvents);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        rvInterestedEvents.setAdapter(adapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvInterestedEvents.setLayoutManager(staggeredGridLayoutManager);

        loadEvents();

        Button btnFriends = view.findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.menu_profile, menu);

        // setting up icon tinting
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

    private void logout(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

}
