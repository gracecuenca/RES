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
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    public ArrayList<Group> groups;
    Context context;
    private boolean publicGroup;

    public MyGroupsAdapter(ArrayList<Group> groups, boolean publicGroup){
        this.groups = groups;
        this.publicGroup = publicGroup;
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
            Glide.with(context).load(group.getImage().getUrl()).circleCrop().into(holder.groupImage);
        }
        if(group.getType().equals("Interests")){
            holder.groupType.setText(group.getType());
        } else {
            Event event = group.getAssociatedEvent();
            event.fetchInBackground(new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    holder.groupType.setText("Event: " + object.getName());
                }
            });
        }
        holder.groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), GroupMessagesActivity.class);
                intent.putExtra("objectId", group.getObjectId());
                intent.putExtra("channel_name", group.getChannelName());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        if(publicGroup){
            holder.leave.setText("Join");
            holder.leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    group.setNumMembs(group.getNumMembs() + 1);
                    group.addMember(ParseUser.getCurrentUser());
                    groups.remove(group);
                    notifyDataSetChanged();
                }
            });
        } else {
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


        if(!group.getOfficial() && holder.verified.getParent() != null) {
            ((ViewGroup) holder.verified.getParent()).removeView(holder.verified);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView groupImage;
        TextView groupName;
        TextView groupType;
        TextView owner;
        Button leave;
        ImageView verified;
        public ViewHolder(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivNewGroup);
            groupName = view.findViewById(R.id.tvDisplayname);
            groupType = view.findViewById(R.id.tvEventName);
            owner = view.findViewById(R.id.tvOwnedBy);
            leave = view.findViewById(R.id.btnJoin);
            verified = view.findViewById(R.id.ivVerified);
        }
    }

    public void clear() {
        groups.clear();
        notifyDataSetChanged();
    }
}
