package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    protected ArrayList<Event> mEvents;

    // needed for infinite pagination
    private EndlessRecyclerViewScrollListener scrollListener;

    // needed for swipe to refresh
    private SwipeRefreshLayout swipeContainer;

    // global variable options needed for user-inputted filters
    private String option;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // default search query is Date
        option = "Date";
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO -- customized toolbar color and logo
        // TODO -- make recycler view go to top upon refreshing

        rvEvents = (RecyclerView) view.findViewById(R.id.rvEvents);
        // create the data source
        mEvents = new ArrayList<>();
        // create the adapter
        adapter = new EventAdapter(mEvents);
        // set the adapter on the recycler view
        rvEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvEvents.setLayoutManager(staggeredGridLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadEvents(false, true, option);
            }
        };

        // Adds the scroll listener to RecyclerView
        rvEvents.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(true, false, option);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // setting up sorting by spinner input here
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerSort);

        // creating adapter for the spinner
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.sort_arrays, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(this);
    }

    // TODO -- sort events by: location (radius), currently being done by date
    public void loadEvents(final boolean isRefresh, final boolean isPaginating, String option){
        ParseQuery<Event> eventsQuery = new ParseQuery<Event>(Event.class);
        eventsQuery.setLimit(10);

        // if(option.equals("Date")) Log.d(APP_TAG, "we're in bois");

        // sorting events based on spinner input
        eventsQuery.addDescendingOrder(Event.KEY_DATE);

        if(isRefresh) {
            clear();
            swipeContainer.setRefreshing(false); // signal refresh is completed
        }
        if(isPaginating){
            // if(option.equals("Date")){
                eventsQuery.whereLessThan(Event.KEY_DATE, mEvents.get(mEvents.size()-1).getDate());
            //}
            //else if(option.equals("Distance")){
                // TODO -- insert distance functionality
            //}
        }
        eventsQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if(e != null){
                    e.printStackTrace();
                    return;
                }
                addAll(events);
            }
        });
    }

    // this is where we will implement the sorting functionality of the spinner
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        option = parent.getItemAtPosition(position).toString();
        loadEvents(false, false, option);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // spinner always has a default item selected: Date
    }

    // Clean all elements of the recycler
    public void clear() {
        mEvents.clear();
        adapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Event> list) {
        mEvents.addAll(list);
        adapter.notifyDataSetChanged();
    }

}
