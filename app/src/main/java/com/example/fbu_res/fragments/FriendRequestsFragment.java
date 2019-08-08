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

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.FriendsAndRequestAdapter;
import com.example.fbu_res.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsFragment extends Fragment {

    private RecyclerView rvUsers;
    private List<User> users;
    FriendsAndRequestAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvUsers = view.findViewById(R.id.rvDM);
        users = new ArrayList<>();
        adapter = new FriendsAndRequestAdapter( users, true);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        getUsers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }

    private void getUsers(){
        // Define the class we would like to query
        User user = (User) ParseUser.getCurrentUser();
        ParseRelation friends = user.getFriends();

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereDoesNotMatchKeyInQuery("objectId", "objectId" ,friends.getQuery());
        query.whereEqualTo("friends", user);

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); ++i) {
                        users.add(objects.get(i));
                        adapter.notifyItemInserted(i);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
