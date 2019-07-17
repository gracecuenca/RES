package com.example.fbu_res.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fbu_res.R;
import com.example.fbu_res.fragments.EventGroupFragment;
import com.example.fbu_res.fragments.GroupFragment;
import com.example.fbu_res.fragments.InterestGroupFragment;
import com.example.fbu_res.fragments.MyGroupsFragment;

public class GroupFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "My Groups", "For Interests", "For Events" };
    private Context context;

    public GroupFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MyGroupsFragment();
        } else if (position == 1){
            return new InterestGroupFragment();
        } else if (position == 2){
            return new EventGroupFragment();
        } else {
            return new EventGroupFragment();
        }    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        int resource;
        switch (position) {
            case 0:
                resource = R.layout.fragment_my_groups;
                break;
            case 1:
                resource = R.layout.fragment_interest_groups;
                break;
            case 2:
            default:
                resource = R.layout.fragment_event_groups;
                break;
        }
        View v = LayoutInflater.from(context).inflate(resource, null);
        return v;
    }
}
