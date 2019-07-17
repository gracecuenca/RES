package com.example.fbu_res;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.models.Event;
import com.parse.ParseFile;

import org.parceler.Parcels;

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

            // TODO set up on click listener for each post in the timeline to redirect to detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Event post = events.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, EventDetailsActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(post));
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Event event){
            ParseFile image = event.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivEventImage);
            }
            tvDescription.setText(event.getName());
            tvDate.setText(event.getDate().toString());
            tvLocation.setText(event.getLocationString());
        }

    }
}
