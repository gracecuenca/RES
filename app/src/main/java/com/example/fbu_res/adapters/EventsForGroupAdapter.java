package com.example.fbu_res.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.google.common.base.MoreObjects;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventsForGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> objects;
    Context context;
    int REQUEST_CODE = 47;
    private final int DETAILS = 0, GROUPS = 1;
    Event event;


    public EventsForGroupAdapter(List<Object> objects, Event event){
        this.objects = objects;
        this.event = event;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case DETAILS:
                View v1 = inflater.inflate(R.layout.item_details, viewGroup, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case GROUPS:
                View v2 = inflater.inflate(R.layout.item_event_group, viewGroup, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                viewHolder = new ViewHolder2(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DETAILS:
                ViewHolder1 vh1 = (ViewHolder1) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case GROUPS:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                ViewHolder2 vh = (ViewHolder2) viewHolder;
                configureViewHolder2(vh, position);
                break;
        }    }

    private void configureViewHolder1(ViewHolder1 vh1, int position) {
        // loading the information from the Event object into the view
        vh1.tvTitle.setText(event.getName());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        vh1.tvDate.setText(dateFormat.format(event.getDate()));
        vh1.tvLocation.setText(event.getLocationString());
        vh1.tvDescription.setText(event.getDescription());
        ParseFile image = event.getImage();
        if(image != null){
            Glide.with(vh1.itemView.getContext()).load(image.getUrl()).into(vh1.ivImage);
        }

        vh1.btnAddToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consumer currentUser = (Consumer) ParseUser.getCurrentUser();
                currentUser.setInterestedEvents(event);
                Toast.makeText(v.getContext(), event.getName()+ " has been added to itinerary" +
                                "under profile", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configureViewHolder2(final ViewHolder2 vh1, int position) {
        final Group group = (Group) objects.get(position);

        vh1.groupName.setText(group.getName());
        if(group.getImage()!=null){
            Glide.with(context).load(group.getImage().getUrl()).into(vh1.groupImage);
        }
        vh1.groupType.setText(group.getType());
        vh1.numMembers.setText(String.valueOf(group.getNumMembs()) +
                (group.getNumMembs() > 1 ? " members" : " member"));

        vh1.owner.setText("Owned by: " + group.getOwnerName());

        vh1.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setNumMembs(group.getNumMembs() + 1);
                group.addMember(ParseUser.getCurrentUser());
                Intent intent = new Intent(vh1.itemView.getContext(), GroupMessagesActivity.class);
                intent.putExtra("channel_name", group.getChannelName());
                ((Activity) vh1.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE);
                objects.remove(group);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (objects.get(position) instanceof Group) {
            return GROUPS;
        }
        return DETAILS;
    }
    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder{
        ImageView groupImage;
        TextView groupName;
        TextView numMembers;
        TextView groupType;
        TextView owner;
        Button join;
        public ViewHolder2(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivGroupPic);
            groupName = view.findViewById(R.id.tvDisplayname);
            numMembers = view.findViewById(R.id.tvNumMembs);
            groupType = view.findViewById(R.id.tvGroupType);
            owner = view.findViewById(R.id.tvOwnedBy);
            join = view.findViewById(R.id.btnJoin);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvDate;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvLocation;
        private Button btnAddToCalendar;


        public ViewHolder1(View v) {
            super(v);
            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            btnAddToCalendar = (Button) v.findViewById(R.id.btnAddToCalendar);
        }
    }

    public void clear() {
        objects.clear();
        notifyDataSetChanged();
    }
}
