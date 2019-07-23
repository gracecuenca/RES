package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.example.fbu_res.adapters.SearchAdapter;
import com.example.fbu_res.adapters.SearchSliderAdapter;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class SearchSlider extends Fragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_viewer_fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager viewPager = view.findViewById(R.id.searchViewPager);
        SearchSliderAdapter pagerAdapter = new SearchSliderAdapter(getFragmentManager(),
                getContext());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.searchSliderTab);
        tabLayout.setupWithViewPager(viewPager);
    }

    /*The main list holding the event names and business name will be
    * be stored in this fragment. Whenever someone types in a letter, the array
    * will be filtered and sorted accordingly and sent to the correct slider function. In
    * events search fragment, the updated array will be delivered and the adapter will be notified.
    * Currently, we are only working with dummy data. There will be a conditional to determine
    * which fragment the data should go to.*/

    //public void filter(String charText) {
        //charText = charText.toLowerCase(Locale.getDefault());
        //ArrayList<String> filteredEvent = new ArrayList<>();
        //for(int i =0; i < events.size(); i++){
            //if(events.get(i).toLowerCase(Locale.getDefault()).contains(charText)){
                //filteredEvent.add(events.get(i));
            //}
        //}

    //}


}
