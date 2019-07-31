package com.example.fbu_res.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.fragments.BusinessSliderSearch;
import com.example.fbu_res.fragments.EventSliderSearch;
import com.example.fbu_res.fragments.SearchResultsFragment;
import com.example.fbu_res.fragments.SearchSlider;
import com.example.fbu_res.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    ArrayList<String> titles;
    Context mContext;
    Fragment fragment;


    public SearchAdapter(Fragment fragmentReference){
        fragment = fragmentReference;
        this.titles = new ArrayList<String>();

        if(fragment instanceof EventSliderSearch){
            this.titles.addAll(EventSliderSearch.events);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_item_view, parent,false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title;
        if(fragment instanceof EventSliderSearch){
            title = EventSliderSearch.events.get(position);
            holder.name.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        if(fragment instanceof EventSliderSearch){
            return EventSliderSearch.events.size();
        }

        return 0;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        EventSliderSearch.events.clear();
        if (charText.length() == 0) {
            if(fragment instanceof EventSliderSearch){
                EventSliderSearch.events.addAll(titles);
                notifyDataSetChanged();
            }
        } else {
            for(int i = 0; i<titles.size(); i++){
                if(titles.get(i).toLowerCase().contains(charText)) {
                    if(fragment instanceof EventSliderSearch){
                        EventSliderSearch.events.add(titles.get(i));
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public void update(ArrayList<String> newArray){
        titles = (ArrayList<String>) newArray.clone();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.search_itemtv);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    EventSliderSearch.eventsSv.setQuery(EventSliderSearch.events.get(position), false);
                }
            });
        }

    }




}
