package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Event;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    public ArrayList<Event> events;
    Context context;

    public EventAdapter(ArrayList<Event> events1){events = events1;}

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View event = inflater.inflate(R.layout.event_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(event);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = events.get(position);
        holder.location.setText(event.getLocation());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        holder.date.setText(dateFormat.format(event.getKeyDate()));
        holder.title.setText(event.getKeyName());
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
        }
    }
}
