package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {
    EventAdapter adapter;
    ArrayList<Event> events;
    RecyclerView searchedEventsRecycleView;
    ArrayList<String> tags;
    String queryEvent;
    String queryLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        queryEvent = bundle.getString("event");
        queryLocation = bundle.getString("location");
        searchedEventsRecycleView = view.findViewById(R.id.eventSearchedRecyclerView);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        searchedEventsRecycleView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        searchedEventsRecycleView.setLayoutManager(manager);
        queryResults();
    }

    public void queryResults(){
        final ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.whereContains("tags",queryEvent);

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e == null){
                    for(int i =0; i < objects.size(); i++){
                        Event event = objects.get(i);
                        Address location = objects.get(i).getLocation();
                        String city = null;
                        String name = null;
                        String zipcode = null;
                        try {
                            city = location.fetchIfNeeded().getString("city");
                            name = location.fetchIfNeeded().getString("name");
                            zipcode = location.fetchIfNeeded().getString("zipcode");

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        if(queryLocation.equals(city) || queryLocation.equals(name) || queryLocation.equals(zipcode)) {
                            events.add(event);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
    }
}
