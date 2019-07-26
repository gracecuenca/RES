package com.example.fbu_res.adapters;

import android.content.Context;
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
import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Group;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    public ArrayList<Group> groups;
    Context context;

    public MyGroupsAdapter(ArrayList<Group> groups){
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View group = inflater.inflate(R.layout.item_group, parent, false);
        final MyGroupsAdapter.ViewHolder viewHolder = new MyGroupsAdapter.ViewHolder(group);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Group group = groups.get(position);

        holder.groupName.setText(group.getName());
        if(group.getImage()!=null){
            Glide.with(context).load(group.getImage().getUrl()).into(holder.groupImage);
        }
        holder.groupType.setText(group.getType());
        holder.numMembers.setText(String.valueOf(group.getNumMembs()) +
                (group.getNumMembs() > 1 ? " members" : " member"));

        holder.owner.setText("Owned by: " + group.getOwner().getUsername());

        holder.groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), GroupMessagesActivity.class);
                intent.putExtra("channel_name", group.getChannelName());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setNumMembs(group.getNumMembs() - 1);
                group.removeMember(ParseUser.getCurrentUser());
                groups.remove(group);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView groupImage;
        TextView groupName;
        TextView numMembers;
        TextView groupType;
        TextView owner;
        Button leave;
        public ViewHolder(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivGroupPic);
            groupName = view.findViewById(R.id.tvDisplayname);
            numMembers = view.findViewById(R.id.tvNumMembs);
            groupType = view.findViewById(R.id.tvGroupType);
            owner = view.findViewById(R.id.tvOwnedBy);
            leave = view.findViewById(R.id.btnJoin);
        }
    }

    public void clear() {
        groups.clear();
        notifyDataSetChanged();
    }
}
