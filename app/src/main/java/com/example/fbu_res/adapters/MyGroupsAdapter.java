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
import com.example.fbu_res.models.Group;

import java.util.ArrayList;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    public ArrayList<Group> groups;
    Context context;

    public MyGroupsAdapter(ArrayList<Group> groups){this.groups = groups;}

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Group group = groups.get(position);

        holder.groupName.setText(group.getKeyName());
        if(group.getKeyImage()!=null){
            Glide.with(context).load(group.getKeyImage().getUrl()).into(holder.groupImage);
        }
        holder.groupType.setText(group.getKeyType());
        holder.numMembers.setText(group.getKeyNumMembs());
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
        public ViewHolder(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivGroupPic);
            groupName = view.findViewById(R.id.tvName);
            numMembers = view.findViewById(R.id.tvNumMembs);
            groupType = view.findViewById(R.id.tvGroupType);
        }
    }
}
