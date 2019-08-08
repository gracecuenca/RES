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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    EventAdapter adapter;
    ArrayList<Event> events;
    RecyclerView eventsSearchRv;
    String name;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        eventsSearchRv = view.findViewById(R.id.eventsSearch);
        events = new ArrayList<>();
        adapter = new EventAdapter(events);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        eventsSearchRv.setLayoutManager(gridLayoutManager);
        eventsSearchRv.setAdapter(adapter);
        getEvents();
    }

    public void getEvents(){
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("type",name);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Event event;
                        event = objects.get(i);
                        events.add(event);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
