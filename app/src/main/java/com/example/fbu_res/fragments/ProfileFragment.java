package com.example.fbu_res.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_res.LoginActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.fbu_res.models.Event.KEY_DISTANCE_TO_USER;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private Consumer user;
    private ImageView ivProfileImage;
    private TextView tvDisplayname;
    private RecyclerView rvInterestedEvents;
    private ArrayList<Event> events;
    private EventAdapter adapter;

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
        // setting up the current user
        user = (Consumer) ParseUser.getCurrentUser();

        // showing the profile of the user
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvDisplayname = (TextView) view.findViewById(R.id.tvDisplayname);
        if (user.getProfileImg() != null) {
            Glide.with(getContext()).load(user.getProfileImg().getUrl()).into(ivProfileImage);
        }
        tvDisplayname.setText(user.getDisplayname());

        // setting up the array and display of interested events
        rvInterestedEvents = (RecyclerView) view.findViewById(R.id.rvInterestedEvents);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        rvInterestedEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvInterestedEvents.setLayoutManager(staggeredGridLayoutManager);

        loadEvents();

        // logout button functionality
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Log.d("profile", "oncreateview is called");

    }

    private void loadEvents(){
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
