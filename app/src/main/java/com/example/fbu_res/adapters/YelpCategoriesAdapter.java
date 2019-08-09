package com.example.fbu_res.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.R;
import com.example.fbu_res.SingleBusinessResultFragment;
import com.example.fbu_res.models.BusinessSearch;

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
        holder.location.setText(search.getRating());
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessSearch search = businessSearches.get(getAdapterPosition());
                    // create intent for the new activity
                    Intent intent = new Intent(context, SingleBusinessResultFragment.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra("businessName", search.getName());
                    intent.putExtra("Lat", search.getLat());
                    intent.putExtra("Long", search.getLongi());
                    intent.putExtra("URL", search.getURL());
                    // show the activity
                    context.startActivity(intent);
                }
            });
        }
    }
}
