package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fbu_res.R;
import com.example.fbu_res.fragments.BusinessSliderSearch;
import com.example.fbu_res.fragments.EventGroupFragment;
import com.example.fbu_res.fragments.EventSliderSearch;
import com.example.fbu_res.fragments.InterestGroupFragment;
import com.example.fbu_res.fragments.MyGroupsFragment;
import com.example.fbu_res.fragments.SearchSlider;

public class SearchSliderAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Events", "Businesses"};
    private Context context;

    public SearchSliderAdapter(FragmentManager fm, Context context) {
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
            return new EventSliderSearch();
        } else if (position == 1){
            return new BusinessSliderSearch();
        } else {
            return new EventSliderSearch();
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
                resource = R.layout.event_search_fragment;
                break;
            case 1:
                resource = R.layout.business_search_fragment;
                break;
            default:
                resource = R.layout.event_search_fragment;
                break;
        }
        View v = LayoutInflater.from(context).inflate(resource, null);
        return v;
    }
}
