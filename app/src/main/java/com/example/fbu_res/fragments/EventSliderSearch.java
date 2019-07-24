package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.SearchAdapter;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventSliderSearch extends Fragment {
    public static ArrayList<String> events = new ArrayList<String>();
    RecyclerView eventsRv2;
    SearchAdapter adapter;
    SearchView eventsSv;
    SearchView locationSv;
    ArrayList<String> eventsCopy;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsRv2 = view.findViewById(R.id.searchEventsRv);
        queryArray();
        adapter = new SearchAdapter(getContext(), new EventSliderSearch());
        eventsRv2.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        eventsRv2.setLayoutManager(manager);
        eventsSv = view.findViewById(R.id.eventSearchView);
        locationSv = view.findViewById(R.id.eventLocationSearchView);
        eventsCopy = (ArrayList<String>) events.clone();

        eventsSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;

            }
        });
    }

    public void queryArray(){
        ParseQuery<Event> parseQuery = ParseQuery.getQuery("Event");
        parseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i<objects.size(); i++){
                        Toast.makeText(getContext(), "in for loop", Toast.LENGTH_LONG).show();
                        events.addAll(objects.get(i).getTagsArray());
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
