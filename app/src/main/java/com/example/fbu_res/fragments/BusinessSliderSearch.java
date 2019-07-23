package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.SearchAdapter;

import java.util.ArrayList;

public class BusinessSliderSearch extends Fragment {
    RecyclerView businessRv;
    SearchAdapter adapter;
    public static ArrayList<String> businesses;
    SearchView businessSearchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.business_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        businessRv = view.findViewById(R.id.businessRv);
        businesses = new ArrayList<>();
        businesses.add("Starbucks");
        businesses.add("Jamba Juice");
        businesses.add("Pressed Juicery");
        adapter = new SearchAdapter(getContext(), new BusinessSliderSearch());
        businessRv.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        businessRv.setLayoutManager(manager);
        businessSearchView = view.findViewById(R.id.businessSearchView);

        businessSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

    }

}
