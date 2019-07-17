package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    RecyclerView eventsRv;
    ArrayList<Event> events;
    EventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "In Search Tab", Toast.LENGTH_LONG);
        eventsRv = view.findViewById(R.id.eventsSearchrv);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        eventsRv.setAdapter(adapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        eventsRv.setLayoutManager(staggeredGridLayoutManager);
        queryEvents();
    }


    public void queryEvents(){
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i< objects.size(); i++){
                        Event event;
                        event = objects.get(i);
                        events.add(event);
                        Log.d("image", event.getImage().getUrl());
                        adapter.notifyDataSetChanged();
                    }
                } else{
                    e.printStackTrace();
                }
            }
        });
    }
}
