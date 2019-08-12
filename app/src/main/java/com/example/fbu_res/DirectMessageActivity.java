package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.fbu_res.adapters.DMAdapter;
import com.example.fbu_res.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

public class DirectMessageActivity extends AppCompatActivity {

    DMAdapter adapter;
    RecyclerView rvUsers;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        Slidr.attach(this);

        rvUsers = findViewById(R.id.rvDM);
        users = new ArrayList<>();
        adapter = new DMAdapter( users);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        getUsers();

        ImageView back = findViewById(R.id.ivBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUsers(){
        // Define the class we would like to query
        User user = (User) ParseUser.getCurrentUser();
        ParseRelation relation = user.getDMUsers();
        ParseQuery query = relation.getQuery();

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
