package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.fbu_res.adapters.ChatAdapter;
import com.example.fbu_res.adapters.DMAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.fusion.client.models.User;

import java.util.ArrayList;
import java.util.List;

public class DirectMessageActivity extends AppCompatActivity {

    DMAdapter adapter;
    RecyclerView rvUsers;
    List<Consumer> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        rvUsers = findViewById(R.id.reyclerview_message_list);
        users = new ArrayList<>();
        adapter = new DMAdapter( users);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getUsers(){
        // Define the class we would like to query
        Consumer user = (Consumer) ParseUser.getCurrentUser();
        ParseRelation relation = user.getDMUsers();
        ParseQuery query = relation.getQuery();

        query.findInBackground(new FindCallback<Consumer>() {
            @Override
            public void done(List<Consumer> objects, ParseException e) {
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
