package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    Context context;
    public ArrayList<Event> events;
    public Consumer user;

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        user = (Consumer) ParseUser.getCurrentUser();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent,false);
        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileAdapter.ViewHolder holder, int position) {
        ImageView ivProfileImage;
        TextView tvDisplayname;
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }
}
