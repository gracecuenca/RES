package com.example.fbu_res.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusinessGroupsFragment extends Fragment {

    DatabaseReference RootRef;
    int RESULT_OK = 291;
    int REQUEST_CODE = 47;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.Viewpager);
        GroupFragmentPagerAdapter pagerAdapter = new GroupFragmentPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(new OwnedGroupsFragment(), "Owned");
        pagerAdapter.addFragment(new MyGroupsFragment(), "Personal");
        pagerAdapter.addFragment(new EventGroupFragment(), "Events");

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        RootRef = FirebaseDatabase.getInstance().getReference();
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
        }
    }


}
