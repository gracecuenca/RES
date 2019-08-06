package com.example.fbu_res.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.DirectMessageActivity;
import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Consumer;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class FriendsAndRequestAdapter extends RecyclerView.Adapter<FriendsAndRequestAdapter.ViewHolder>{

    List<Consumer> users;
    Boolean requests;

    public FriendsAndRequestAdapter(List<Consumer> users, Boolean requests){
        this.users = users;
        this.requests = requests;
    }


    @NonNull
    @Override
    public FriendsAndRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View user = requests ? inflater.inflate(R.layout.item_friend_requests, parent, false)
                        : inflater.inflate(R.layout.item_dm, parent, false);
        return new ViewHolder(user);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Consumer user = users.get(position);

        Glide.with(holder.itemView.getContext())
                .load(user.getProfileImg().getUrl())
                .circleCrop()
                .into(holder.profileImg);

        holder.name.setText(user.getDisplayname());

        if(user.getType().equals("Consumer") && holder.verified.getParent() != null){
            ((ViewGroup) holder.verified.getParent()).removeView(holder.verified);
        }

        if(requests){
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Consumer currentUser = (Consumer) ParseUser.getCurrentUser();
                    (currentUser).addFriend(user);
                    currentUser.saveInBackground();
                    users.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView name;
        ImageView verified;
        Button accept;
        public ViewHolder(View view){
            super(view);
            profileImg = view.findViewById(R.id.ivProfileImg);
            name = view.findViewById(R.id.tvName);
            verified = view.findViewById(R.id.ivVerified);
            accept = view.findViewById(R.id.btnAccept);
        }
    }
}
