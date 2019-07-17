package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_res.EventsAdapter;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    protected List<Event> mEvents;

    // TODO -- infinite pagination

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO -- customized toolbar color and logo

        rvEvents = (RecyclerView) view.findViewById(R.id.rvEvents);

        // create the data source
        mEvents = new ArrayList<>();
        // create the adapter
        adapter = new EventsAdapter(getContext(), mEvents);
        // set the adapter on the recycler view
        rvEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvEvents.setLayoutManager(staggeredGridLayoutManager);

        loadEvents();
    }

    public void loadEvents(){
        ParseQuery<Event> eventsQuery = new ParseQuery<Event>(Event.class);
        eventsQuery.addDescendingOrder(Event.KEY_DATE);
        eventsQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if(e != null){ // there was an error
                    Log.e(APP_TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mEvents.addAll(events);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
