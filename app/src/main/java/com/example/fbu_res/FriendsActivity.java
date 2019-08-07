package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fbu_res.adapters.DMAdapter;
import com.example.fbu_res.adapters.FriendPagerAdapter;
import com.example.fbu_res.fragments.FriendRequestsFragment;
import com.example.fbu_res.fragments.FriendsFragment;
import com.example.fbu_res.models.User;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    DMAdapter adapter;
    RecyclerView rvUsers;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        FriendPagerAdapter pagerAdapter = new FriendPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FriendsFragment(), "Friends");
        pagerAdapter.addFragment(new FriendRequestsFragment(), "Friend Requests");



        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Button back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
