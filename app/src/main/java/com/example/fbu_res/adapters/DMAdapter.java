package com.example.fbu_res.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.User;
import com.parse.ParseUser;

import java.util.List;

public class DMAdapter extends RecyclerView.Adapter<DMAdapter.ViewHolder>{

    List<User> users;

    public DMAdapter(List<User> users){
        this.users = users;
    }


    @NonNull
    @Override
    public DMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View user = inflater.inflate(R.layout.item_dm, parent, false);
        return new ViewHolder(user);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = users.get(position);

        Glide.with(holder.itemView.getContext())
                .load(user.getProfileImg().getUrl())
                .circleCrop()
                .into(holder.profileImg);

        holder.name.setText(user.getDisplayname());

        if(user.getType().equals("Consumer") && holder.verified.getParent() != null){
            ((ViewGroup) holder.verified.getParent()).removeView(holder.verified);
        }

        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user1 = ((User) ParseUser.getCurrentUser()).getUsername();
                String user2 = user.getUsername();
                Intent i = new Intent(holder.itemView.getContext(), GroupMessagesActivity.class);
                i.putExtra("dm_name", user2);
                i.putExtra("channel_name", user1.compareTo(user2) > 0 ? user1 + user2 : user2 + user1);
                holder.itemView.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView name;
        ImageView verified;
        public ViewHolder(View view){
            super(view);
            profileImg = view.findViewById(R.id.ivProfileImg);
            name = view.findViewById(R.id.tvName);
            verified = view.findViewById(R.id.ivVerified);
        }
    }
}
