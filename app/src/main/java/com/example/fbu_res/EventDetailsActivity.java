package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.adapters.EventsForGroupAdapter;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {

    // event to display
    Event event;
    List<Object> groups;

    // event attributes
    private ImageView ivImage;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvLocation;
    RecyclerView recyclerView;
    EventsForGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        // TODO -- setup customized actionbar/ toolbar

        // unwrapping the event sent by the intent and initializing attributes
        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        RecyclerView rvDetails = (RecyclerView) findViewById(R.id.rvDetails);

        groups = new ArrayList<>();
        // Create adapter passing in the sample user data
        adapter = new EventsForGroupAdapter(groups, event);
        // Attach the adapter to the recyclerview to populate items
        rvDetails.setAdapter(adapter);
        // Set layout manager to position the items
        rvDetails.setLayoutManager(new LinearLayoutManager(this));

        getGroups();
    }

    public void getGroups() {
        // Define the class we would like to query
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.include("associated_event");
        ParseQuery<Event> queryEvent = ParseQuery.getQuery(Event.class);
        queryEvent.whereEqualTo("objectId", event.getObjectId());
        query.whereMatchesQuery("associated_event", queryEvent);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e == null) {
                    groups.add("");
                    adapter.notifyItemInserted(0);
                    for(int i = 0; i < objects.size(); ++i) {
                        groups.add(objects.get(i));
                        adapter.notifyItemInserted(i+1);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
