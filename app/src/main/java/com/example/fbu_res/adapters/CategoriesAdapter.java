package com.example.fbu_res.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.R;
import com.example.fbu_res.fragments.CategoryFragment;
import com.example.fbu_res.models.Categories;
import com.example.fbu_res.models.Categories;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    ArrayList<Categories> categories;
    Context context;

    public CategoriesAdapter(ArrayList<Categories> categories1){
        categories = categories1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.categories_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categories cat = categories.get(position);
        holder.categoryName.setText(cat.getName());
        if(cat.getImage() != null){
            Glide.with(context).load(cat.getImage().getUrl()).into(holder.categoryImage);
        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton categoryImage;
        TextView categoryName;
        public ViewHolder(View view) {
            super(view);
            categoryImage = view.findViewById(R.id.categoryImage);
            categoryName = view.findViewById(R.id.categoryTitle);

            categoryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clicked", "c");
                    int i = getAdapterPosition();
                    Categories c = categories.get(i);
                    CategoryFragment fragment = new CategoryFragment();
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer, fragment);
                    Bundle args = new Bundle();
                    args.putString("name", c.getName());
                    fragment.setArguments(args);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

        }

    }
}
