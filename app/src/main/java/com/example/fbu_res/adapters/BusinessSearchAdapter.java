package com.example.fbu_res.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbu_res.R;
import com.example.fbu_res.fragments.BusinessSliderSearch;
import com.example.fbu_res.fragments.CategoryFragment;
import com.example.fbu_res.fragments.SingleBusinessResultFragment;
import com.example.fbu_res.fragments.YelpCatergoriesFragment;
import com.example.fbu_res.fragments.YelpCatergoriesFragment;
import com.example.fbu_res.models.Business;
import com.example.fbu_res.models.BusinessSearch;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BusinessSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<BusinessSearch> businesses;
    Context context;
    private final int CATEGORY = 1;
    private final int BUSINESS = 2;

    public BusinessSearchAdapter(ArrayList<BusinessSearch> businessesParam){
        businesses = businessesParam;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CATEGORY:
                View v1 = inflater.inflate(R.layout.search_item_view, parent, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case BUSINESS:
                View v2 = inflater.inflate(R.layout.business_name_item, parent, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder2(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CATEGORY:
                BusinessSearchAdapter.ViewHolder1 vh1 = (BusinessSearchAdapter.ViewHolder1) holder;
                configureViewHolder1(vh1, position);
                break;
            case BUSINESS:
                BusinessSearchAdapter.ViewHolder2 vh2 = (BusinessSearchAdapter.ViewHolder2) holder;
                configureViewHolder2(vh2, position);
                break;
            default:
                BusinessSearchAdapter.ViewHolder2 vh = (BusinessSearchAdapter.ViewHolder2) holder;
                configureViewHolder2(vh, position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (businesses.get(position).getIdentifier() == 1) {
            return CATEGORY;
        }
        return BUSINESS;
    }

    private void configureViewHolder1(ViewHolder1 vh1, int position){
        BusinessSearch searchItem = businesses.get(position);
        vh1.categoryName.setText(searchItem.getName());
    }

    private void configureViewHolder2(ViewHolder2 vh2, int position){
        BusinessSearch searchItem = businesses.get(position);
        Glide.with(context)
                .load(searchItem.getURL())
                .apply(RequestOptions.circleCropTransform())
                .into(vh2.businessImage);
        vh2.businessName.setText(searchItem.getName());

    }




    public class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView categoryName;

        public ViewHolder1(View v1) {
            super(v1);
            categoryName = v1.findViewById(R.id.search_itemtv);
            v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessSearch search = businesses.get(getAdapterPosition());
                    YelpCatergoriesFragment fragment = new YelpCatergoriesFragment();
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer, fragment);
                    Bundle args = new Bundle();
                    args.putString("categoryName", search.getName());
                    args.putString("lat", search.getLat());
                    args.putString("long", search.getLongi());
                    fragment.setArguments(args);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }


    public class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView businessName;
        ImageView businessImage;

        public ViewHolder2(View v2) {
            super(v2);
            businessName = v2.findViewById(R.id.businessNameTextView);
            businessImage = v2.findViewById(R.id.businessNameProfileImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessSearch search = businesses.get(getAdapterPosition());
                    SingleBusinessResultFragment fragment = new SingleBusinessResultFragment();
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer, fragment);
                    Bundle args = new Bundle();
                    args.putString("businessName", search.getName());
                    args.putString("Lat", search.getLat());
                    args.putString("Long", search.getLongi());
                    fragment.setArguments(args);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

    }



}
