package com.example.fbu_res.adapters;

import android.app.Activity;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class EventGroupsAdapter extends RecyclerView.Adapter<EventGroupsAdapter.ViewHolder>{

    public ArrayList<Group> groups;
    Context context;
    int REQUEST_CODE = 47;


    public EventGroupsAdapter(ArrayList<Group> groups){
        this.groups = groups;
    }

    @NonNull
    @Override
    public EventGroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View group = inflater.inflate(R.layout.item_event_group, parent, false);
        return new EventGroupsAdapter.ViewHolder(group);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventGroupsAdapter.ViewHolder holder, final int position) {
        final Group group = groups.get(position);

        holder.groupName.setText(group.getName());
        if(group.getImage()!=null){
            Glide.with(context).load(group.getImage().getUrl()).into(holder.groupImage);
        }

        final Event event = group.getAssociatedEvent();
        event.fetchInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                holder.groupType.setText("Event : " + object.getString(Event.KEY_NAME));
            }
        });
        if(group.getOfficial()){
            holder.groupType.setText(holder.groupType.getText() + " OFFICIAL");
        }

        holder.numMembers.setText(String.valueOf(group.getNumMembs()) +
                (group.getNumMembs() > 1 ? " members" : " member"));

        ParseUser owner = group.getOwner();
        owner.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                String username = (String) object.get("username");
                holder.owner.setText("Owned by: " + username);

            }
        });
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setNumMembs(group.getNumMembs() + 1);
                group.addMember(ParseUser.getCurrentUser());
                groups.remove(group);
                notifyDataSetChanged();
                Intent intent = new Intent(holder.itemView.getContext(), GroupMessagesActivity.class);
                intent.putExtra("channel_name", group.getChannelName());
                ((Activity) holder.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        if(!group.getOfficial() && holder.verified.getParent() != null) {
            ((ViewGroup) holder.verified.getParent()).removeView(holder.verified);
        }

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
        Button join;
        ImageView verified;
        public ViewHolder(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivNewGroup);
            groupName = view.findViewById(R.id.tvDisplayname);
            numMembers = view.findViewById(R.id.tvNumMembs);
            groupType = view.findViewById(R.id.tvEventName);
            owner = view.findViewById(R.id.tvOwnedBy);
            join = view.findViewById(R.id.btnJoin);
            verified = view.findViewById(R.id.ivVerified);
        }
    }

    public void clear() {
        groups.clear();
        notifyDataSetChanged();
    }
}
