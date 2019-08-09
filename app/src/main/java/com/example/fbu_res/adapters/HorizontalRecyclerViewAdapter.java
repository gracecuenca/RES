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
import java.util.List;

class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Event> events;

    public HorizontalRecyclerViewAdapter(Context context, List<Event> events){
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.location.setText(event.getLocationString());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.date.setText(dateFormat.format(event.getDate()));
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

        public ViewHolder(@NonNull View view) {
            super(view);
            eventImage = view.findViewById(R.id.mainEventImage);
            title = view.findViewById(R.id.Title);
            location = view.findViewById(R.id.locationEvent);
            date = view.findViewById(R.id.dateEvent);
        }
    }


}
