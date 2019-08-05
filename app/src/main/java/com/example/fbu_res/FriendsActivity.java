package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.example.fbu_res.adapters.DMAdapter;
import com.example.fbu_res.adapters.FriendPagerAdapter;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.example.fbu_res.fragments.EventGroupFragment;
import com.example.fbu_res.fragments.FriendRequestsFragment;
import com.example.fbu_res.fragments.FriendsFragment;
import com.example.fbu_res.fragments.InterestGroupFragment;
import com.example.fbu_res.fragments.MyGroupsFragment;
import com.example.fbu_res.models.Consumer;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    DMAdapter adapter;
    RecyclerView rvUsers;
    List<Consumer> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        FriendPagerAdapter pagerAdapter = new FriendPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FriendsFragment(), "Friends");
        pagerAdapter.addFragment(new FriendRequestsFragment(), "Friend Requests");



        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


}
