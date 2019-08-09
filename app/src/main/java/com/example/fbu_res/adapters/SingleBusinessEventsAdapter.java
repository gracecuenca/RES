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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SingleBusinessEventsAdapter extends RecyclerView.Adapter<SingleBusinessEventsAdapter.ViewHolder>{
    Context context;
    ArrayList<Event> events;

    public SingleBusinessEventsAdapter(ArrayList<Event> events1){
        events = events1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.business_search_event_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventName.setText(event.getName());
        DateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
        holder.eventDate.setText(dateFormat1.format(event.getDate()));
        DateFormat dateFormat2 = new SimpleDateFormat("h:mm a");
        holder.eventTime.setText(dateFormat2.format(event.getDate()));
        if(event.getImage()!=null){
            Glide.with(context).load(event.getImage().getUrl()).into(holder.eventImage);

        }

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView eventImage;
        TextView eventName;
        TextView eventDate;
        TextView eventTime;
        public ViewHolder(View view){
            super(view);
            eventName = view.findViewById(R.id.eventNameTextView);
            eventDate = view.findViewById(R.id.eventDatetextView);
            eventTime = view.findViewById(R.id.eventTimetextView);
            eventImage = view.findViewById(R.id.eventImageSingleBusinessView);

        }
    }
}
