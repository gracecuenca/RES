package com.example.fbu_res.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.fragments.BusinessSliderSearch;
import com.example.fbu_res.fragments.EventSliderSearch;

import java.util.ArrayList;
import java.util.Locale;

public class LocationSearchAdapater extends RecyclerView.Adapter<LocationSearchAdapater.ViewHolder> {
    ArrayList<String> titles;
    Context mContext;
    LayoutInflater inflater;
    Fragment fragment;

    public LocationSearchAdapater(Context context, Fragment fragmentReference){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        fragment = fragmentReference;
        this.titles = new ArrayList<String>();

        if(fragment instanceof EventSliderSearch){
            this.titles.addAll(EventSliderSearch.locations);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_item_view, parent,false);
        return new LocationSearchAdapater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title;
        if(fragment instanceof EventSliderSearch){
            title = EventSliderSearch.locations.get(position);
            holder.name.setText(title);
        }

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        EventSliderSearch.locations.clear();
        if (charText.length() == 0) {
            if(fragment instanceof EventSliderSearch){
                EventSliderSearch.locations.addAll(titles);
                notifyDataSetChanged();
            }else{
                notifyDataSetChanged();
            }
        } else {
            for(int i = 0; i<titles.size(); i++){
                if (titles.get(i) == null) {
                    Log.d("Null", "position: " + i);
                }

                if(titles.get(i).contains(charText)){
                    if(fragment instanceof EventSliderSearch){
                        EventSliderSearch.locations.add(titles.get(i));
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return EventSliderSearch.locations.size();
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
                    EventSliderSearch.locationSv.setQuery(EventSliderSearch.locations.get(position), false);
                }
            });
        }
    }

}
