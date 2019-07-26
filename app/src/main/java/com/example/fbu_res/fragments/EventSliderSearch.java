package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.LocationSearchAdapater;
import com.example.fbu_res.adapters.SearchAdapter;
import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class EventSliderSearch extends Fragment {
    public static ArrayList<String> events;
    RecyclerView eventsRv2;
    SearchAdapter adapter;
    public static SearchView eventsSv;
    public static SearchView locationSv;
    ArrayList<String> eventsWithoutRepeats;
    public static ArrayList<String> locations;
    RecyclerView eventLocationRecyclerView;
    LocationSearchAdapater locationSearchAdapater;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsRv2 = view.findViewById(R.id.searchEventsRv);
        eventLocationRecyclerView = view.findViewById(R.id.locationEventRecyclerView);
        eventLocationRecyclerView.setVisibility(View.INVISIBLE);
        events = new ArrayList<String>();
        locations = new ArrayList<String>();
        queryEventsArray();
        queryLocationArray();
        adapter = new SearchAdapter(new EventSliderSearch());
        locationSearchAdapater = new LocationSearchAdapater(getContext(), new EventSliderSearch());
        eventsRv2.setAdapter(adapter);
        eventLocationRecyclerView.setAdapter(locationSearchAdapater);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        eventsRv2.setLayoutManager(manager);
        eventLocationRecyclerView.setLayoutManager(manager2);
        eventsSv = view.findViewById(R.id.eventSearchView);
        locationSv = view.findViewById(R.id.eventLocationSearchView);


        eventsSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(locationSv.getQuery().toString().isEmpty() || eventsSv.getQuery().toString().isEmpty() ){
                    Toast.makeText(getContext(), "Enter a location or Event!", Toast.LENGTH_LONG).show();
                }
                else if(!locationSv.getQuery().toString().isEmpty() && !eventsSv.getQuery().toString().isEmpty()){SearchResultsFragment fragment = new SearchResultsFragment ();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                Bundle args = new Bundle();
                args.putString("event", eventsSv.getQuery().toString());
                args.putString("location", locationSv.getQuery().toString());
                fragment.setArguments(args);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;}
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventLocationRecyclerView.setVisibility(View.INVISIBLE);
                eventsRv2.setVisibility(View.VISIBLE);
                adapter.filter(newText);
                return false;

            }
        });

        locationSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(locationSv.getQuery().toString().isEmpty() || eventsSv.getQuery().toString().isEmpty()){
                    Toast.makeText(getContext(), "Enter a location or Event!", Toast.LENGTH_LONG).show();
                }
                else if(!locationSv.getQuery().toString().isEmpty() && !eventsSv.getQuery().toString().isEmpty()) {
                SearchResultsFragment fragment = new SearchResultsFragment ();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                Bundle args = new Bundle();
                args.putString("event", eventsSv.getQuery().toString());
                args.putString("location", locationSv.getQuery().toString());
                fragment.setArguments(args);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventsRv2.setVisibility(View.INVISIBLE);
                eventLocationRecyclerView.setVisibility(View.VISIBLE);
                locationSearchAdapater.filter(newText);
                return false;
            }

        });
    }


    public void queryEventsArray(){
        ParseQuery<Event> parseQuery = ParseQuery.getQuery("Event");
        parseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i<objects.size(); i++) {
                        if (objects.get(i).getTagsArray() != null) {
                            events.addAll(objects.get(i).getTagsArray());
                        }
                    }
                    withoutRepeats(events);
                    adapter.update(events);
                    adapter.notifyDataSetChanged();
                }else{
                    e.printStackTrace();
                }

            }
        });
    }

    public void queryLocationArray(){
        ParseQuery<Address> parseQuery = ParseQuery.getQuery("Address");
        parseQuery.findInBackground(new FindCallback<Address>() {
            @Override
            public void done(List<Address> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i<objects.size(); i++){
                        locations.add(objects.get(i).getString("city"));
                        locations.add(objects.get(i).getString("zipcode"));
                        locations.add(objects.get(i).getString("name"));
                    }
                    withoutRepeats(locations);
                    locationSearchAdapater.update(locations);
                    locationSearchAdapater.notifyDataSetChanged();
                }
                else{
                    e.printStackTrace();
                }

            }
        });

    }

    public void withoutRepeats(ArrayList<String> array){
        eventsWithoutRepeats = new ArrayList<>();
        for(int i =0; i<array.size(); i++){
            if(!eventsWithoutRepeats.contains(array.get(i))){
                eventsWithoutRepeats.add(array.get(i));
            }
        }

        array.clear();
        array.addAll(eventsWithoutRepeats);
    }

}
