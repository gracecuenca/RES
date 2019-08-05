package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventGroupsAdapter;
import com.example.fbu_res.adapters.MyGroupsAdapter;
import com.example.fbu_res.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import java.util.ArrayList;
import java.util.List;

public class EventGroupFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    PubNub mPubnub_DataStream;
    ArrayList<Group> groups;
    private MyGroupsAdapter adapter;
    RecyclerView rvGroups;
    EndlessRecyclerViewScrollListener scrollListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_groups, container, false);


        return view;
    }

    public void getGroups() {
        // Define the class we would like to query
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.orderByAscending("createdAt");
        query.whereEqualTo("type", "Event");
        query.whereNotEqualTo("members", user);
        query.setLimit(10);

        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); ++i) {
                        groups.add(objects.get(i));
                        adapter.notifyItemInserted(i);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvGroups = view.findViewById(R.id.rvGroups);

        groups = new ArrayList<>();


        // Create adapter passing in the sample user data
        adapter = new MyGroupsAdapter(groups, true);
        // Attach the adapter to the recyclerview to populate items
        rvGroups.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutMan = new LinearLayoutManager(getContext());

        rvGroups.setLayoutManager(layoutMan);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutMan) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        getGroups();
    }

    private void loadNextDataFromApi(int page) {
        // Define the class we would like to query
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.orderByAscending("createdAt");
        query.whereEqualTo("type", "Event");
        query.whereNotEqualTo("members", user);
        query.setLimit(10);
        query.whereGreaterThan("createdAt", groups.get(groups.size()-1).getCreatedAt());

        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); ++i) {
                        groups.add(objects.get(i));
                        adapter.notifyItemInserted(i);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && groups != null) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
