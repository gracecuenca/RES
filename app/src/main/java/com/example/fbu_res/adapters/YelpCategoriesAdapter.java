package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.R;
import com.example.fbu_res.fragments.YelpCatergoriesFragment;
import com.example.fbu_res.models.BusinessSearch;
import com.example.fbu_res.models.Event;

import java.util.ArrayList;

public class YelpCategoriesAdapter extends RecyclerView.Adapter<YelpCategoriesAdapter.ViewHolder> {
    ArrayList<BusinessSearch> businessSearches;
    Context context;

    public YelpCategoriesAdapter(ArrayList<BusinessSearch> searches){
        businessSearches = searches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent,false);
        return new YelpCategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BusinessSearch search = businessSearches.get(position);
        holder.title.setText(search.getName());
        holder.location.setText(search.getLocation());
        if(search.getURL()!=null){
            Glide.with(context).load(search.getURL()).into(holder.businessImage);
        }

    }

    @Override
    public int getItemCount() {
        return businessSearches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView businessImage;
        TextView title;
        TextView location;
        TextView date;
        public ViewHolder(View view){
            super(view);
            businessImage = view.findViewById(R.id.mainEventImage);
            title = view.findViewById(R.id.Title);
            location = view.findViewById(R.id.locationEvent);
            date = view.findViewById(R.id.dateEvent);
        }
    }
}
