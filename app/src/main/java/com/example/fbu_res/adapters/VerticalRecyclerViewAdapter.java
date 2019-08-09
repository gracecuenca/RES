package com.example.fbu_res.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.DatedEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<DatedEvent> arrayList;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<DatedEvent> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatedEvent verticalModel = arrayList.get(position);
        Date date = verticalModel.getDate();
        List<Event> events = verticalModel.getEvents();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        holder.tvItemTitle.setText(dateFormat.format(date));
        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(context, events);

        holder.rvHoritontalItems.hasFixedSize();
        holder.rvHoritontalItems.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        holder.rvHoritontalItems.setAdapter(horizontalRecyclerViewAdapter);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rvHoritontalItems;
        TextView tvItemTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvHoritontalItems = (RecyclerView)itemView.findViewById(R.id.rvHorizontalItems);
            tvItemTitle = (TextView)itemView.findViewById(R.id.tvItemTitle);
        }
    }
}
