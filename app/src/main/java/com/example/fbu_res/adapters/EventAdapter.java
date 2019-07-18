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
import com.example.fbu_res.EventDetailsActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Event;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    Context context;
    public ArrayList<Event> events;

    public EventAdapter(ArrayList<Event> events1){events = events1;}

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = events.get(position);
        holder.location.setText(event.getLocationString());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        holder.date.setText(dateFormat.format(event.getDate()));
        holder.title.setText(event.getName());
        // holder.date.setText(dateFormat.format(event.getDate()));
        holder.title.setText(event.getName());
        if(event.getImage()!=null){
            Glide.with(context).load(event.getImage().getUrl()).into(holder.eventImage);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView eventImage;
        TextView title;
        TextView location;
        TextView date;

        public ViewHolder(View view){

            super(view);

            eventImage = view.findViewById(R.id.mainEventImage);
            title = view.findViewById(R.id.Title);
            location = view.findViewById(R.id.locationEvent);
            date = view.findViewById(R.id.dateEvent);

            // set up on click listener for each post in the timeline, redirects to detailed view
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Event event = events.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, EventDetailsActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });
        }

    }
}
