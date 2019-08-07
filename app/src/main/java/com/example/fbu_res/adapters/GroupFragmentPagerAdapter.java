package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fbu_res.R;
import com.example.fbu_res.fragments.EventGroupFragment;
import com.example.fbu_res.fragments.GroupFragment;
import com.example.fbu_res.fragments.InterestGroupFragment;
import com.example.fbu_res.fragments.MyGroupsFragment;

import java.util.ArrayList;
import java.util.List;

public class GroupFragmentPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 3;
    List<Fragment> mFragmentList = new ArrayList<>();
    List<String> mFragmentTitleList = new ArrayList<>();

    public GroupFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return mFragmentTitleList.get(position);
    }

}
