package com.example.fbu_res.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class BusinessSliderSearch extends Fragment {
    RecyclerView businessRv;
    SearchAdapter adapter;
    public static ArrayList<String> businesses;
    SearchView businessSearchView;
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    Map<String, String> params;
    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;
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
        adapter = new SearchAdapter(new BusinessSliderSearch());
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

        {
            try {
                yelpFusionApi = apiFactory.createAPI(KEY);
                params = new HashMap<>();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new FetchData().execute();

    }

    public class FetchData extends AsyncTask<String, String, String> {
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            params.put("term", "indian food");
            params.put("latitude", "40.581140");
            params.put("longitude", "-111.914184");
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);

            try {
                Response<SearchResponse> response = call.execute();
                String name = response.body().getBusinesses().get(0).getName();
                Log.d("Yelp", name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
