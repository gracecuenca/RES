package com.example.fbu_res;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.models.Event;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;

    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivEventImage;
        private TextView tvDescription;
        private TextView tvDate;
        private TextView tvLocation;

        public ViewHolder(View itemView){

            super(itemView);

            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvDescription = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);

            // TODO set up on click listener for each post in the timeline, redirects to detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(Event event){
            // set up only text views for now
            tvDescription.setText(event.getKeyName());
            tvDate.setText(event.getKeyDate().toString());
            // TODO tvLocation.setText(event.getKeyLocation().toString());
        }

    }
}
