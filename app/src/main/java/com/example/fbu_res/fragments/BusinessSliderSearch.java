package com.example.fbu_res.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.BusinessSearchAdapter;
import com.example.fbu_res.adapters.SearchAdapter;
import com.example.fbu_res.models.BusinessSearch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseGeoPoint;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class BusinessSliderSearch extends Fragment {
    RecyclerView businessRv;
    BusinessSearchAdapter adapter;
    SearchView businessSearchView;
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    Map<String, String> params;
    ArrayList<BusinessSearch> businessSearches;
    ArrayList<Business> businessArrayList;
    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;
    String searchText;
    ArrayList<BusinessSearch> locations;
    BusinessSearchAdapter adapterLocations;
    public static SearchView locationSearchView;
    RecyclerView locationSearchRecyclerView;
    Geocoder geocoder;
    public static String lat;
    public static String longi;
    private FusedLocationProviderClient fusedLocationClient;

    ArrayList<LatLng> longLat = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.business_search_fragment, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lat = "40.7128";
        longi = "74.0060";
        getCurrentLocation();
        businessRv = view.findViewById(R.id.businessRv);
        businessSearches = new ArrayList<>();
        locations = new ArrayList<>();
        geocoder = new Geocoder(getContext());
        adapter = new BusinessSearchAdapter(businessSearches);
        adapterLocations = new BusinessSearchAdapter(locations);
        businessRv.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        locationSearchRecyclerView = view.findViewById(R.id.BusinessLocationSearchRecyclerView);
        locationSearchRecyclerView.setLayoutManager(manager2);
        locationSearchRecyclerView.setAdapter(adapterLocations);
        businessRv.setLayoutManager(manager);
        businessSearchView = view.findViewById(R.id.businessSearchView);
        getCurrentLocation();

        businessSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                locationSearchRecyclerView.setVisibility(View.INVISIBLE);
                businessRv.setVisibility(View.VISIBLE);
                businessSearches.clear();
                adapter.notifyDataSetChanged();
                searchText = newText;
                if(locationSearchView.getQuery().toString().isEmpty()){
                    getCurrentLocation();
                }
                new FetchData().execute();
                return false;
            }
        });

        locationSearchView = view.findViewById(R.id.businessLocationSearchView);
        locationSearchView.setQuery("Current Location", false);

        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override

            public boolean onQueryTextSubmit(String query) {
                if(geocoder.isPresent()) {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(query, 5);
                        for (int i = 0; i < addresses.size(); i++) {
                            if (addresses.get(i).hasLatitude() && addresses.get(i).hasLongitude()) {
                                longLat.add(new LatLng(addresses.get(i).getLatitude(), addresses.get(i).getLongitude()));
                                String locationName = addresses.get(i).getFeatureName();
                                BusinessSearch locationSearch = new BusinessSearch(locationName, 1);
                                lat = Double.toString(longLat.get(0).latitude);
                                longi = Double.toString(longLat.get(0).longitude);
                                locations.add(locationSearch);
                            }

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                locationSearchRecyclerView.setVisibility(View.VISIBLE);
                businessRv.setVisibility(View.INVISIBLE);
                locations.clear();
                adapterLocations.notifyDataSetChanged();
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
            adapterLocations.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {
            params.put("term", searchText);
            params.put("categories", searchText);
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
                    businessSearches.add(search);
                    ArrayList<Category> category = business.getCategories();
                        for(int x =0; x < category.size(); x++){
                        String categoryName = category.get(x).getTitle();
                        BusinessSearch categorySearch = new BusinessSearch(categoryName, 1);
                        categorySearch.setLat(Double.toString(business.getCoordinates().getLatitude()));
                        categorySearch.setLongi(Double.toString(business.getCoordinates().getLongitude()));
                        businessSearches.add(categorySearch);
                    }
                }

            }
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = Double.toString(location.getLatitude());
                            longi = Double.toString(location.getLongitude());
                        }
                    }
                });

    }



}
