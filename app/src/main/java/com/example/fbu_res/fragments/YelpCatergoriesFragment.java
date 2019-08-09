package com.example.fbu_res.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.YelpCategoriesAdapter;
import com.example.fbu_res.models.BusinessSearch;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class YelpCatergoriesFragment extends Fragment {
    ArrayList<BusinessSearch> searches;
    YelpCategoriesAdapter adapter;
    String lat;
    String longi;
    String categoryName;
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    Map<String, String> params;
    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;
    ArrayList<Business> businessArrayList;
    RecyclerView categoryBusinessSearchResults;
    Toolbar backToolbar2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.business_categories_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        searches = new ArrayList<>();
        adapter = new YelpCategoriesAdapter(searches);
        categoryBusinessSearchResults = view.findViewById(R.id.businessSearchCategoryResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        categoryBusinessSearchResults.setAdapter(adapter);
        categoryBusinessSearchResults.setLayoutManager(gridLayoutManager);
        backToolbar2 = view.findViewById(R.id.backToolbar2);
        backToolbar2.inflateMenu(R.menu.back_menu);
        Menu menu = backToolbar2.getMenu();
        backToolbar2.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_bueblue_24dp));
        backToolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_LONG);
                SearchSlider fragment = new SearchSlider();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        lat = bundle.getString("lat");
        longi = bundle.getString("long");
        categoryName = bundle.getString("categoryName");
        backToolbar2.setTitle(categoryName);
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
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {
            params.put("term", categoryName);
            params.put("categories", categoryName);
            params.put("latitude", lat);
            params.put("longitude", longi);
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            Response<SearchResponse> response = null;
            try {
                response = call.execute();
                //Log.d("Yelp", name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(response!=null){
                businessArrayList = response.body().getBusinesses();
                for(int i =0; i < businessArrayList.size(); i++){
                    Business business = businessArrayList.get(i);
                    String name = business.getName();
                    String URL = business.getImageUrl();
                    BusinessSearch search = new BusinessSearch(name, 2);
                    search.setURL(URL);
                    search.setLat(Double.toString(business.getCoordinates().getLatitude()));
                    search.setLongi(Double.toString(business.getCoordinates().getLongitude()));
                    search.setRating("Rating: "+ business.getRating());
                    searches.add(search);
                }

            }
            return null;
        }
    }
}
