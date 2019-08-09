package com.example.fbu_res.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.R.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.CategoriesAdapter;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Categories;
import com.example.fbu_res.models.Categories;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.util.EqualSpacingItemDecoration;
import com.example.fbu_res.util.YelpClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class SearchFragment extends Fragment{
    RecyclerView eventsRv;
    ArrayList<Event> events;
    ArrayList<Event> events2;
    EventAdapter adapter;
    EventAdapter adapter2;
    RecyclerView foodEventRecyclerView;
    CategoriesAdapter adapterCat;
    ArrayList<Categories> categoriesArrayList;
    RecyclerView categoriesRV;
    Toolbar toolbar;
    YelpClient client;

    //GoogleMap mgoogleMap;
    //MapView mMapView;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    public SearchFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar = view.findViewById(R.id.searchToolbar);
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        setHasOptionsMenu(true);
        eventsRv = view.findViewById(R.id.eventsSearchrv);
        foodEventRecyclerView = view.findViewById(R.id.foodEventRecyclerView);
        events = new ArrayList<>();
        events2 = new ArrayList<>();
        adapter = new EventAdapter(events);
        adapter2 = new EventAdapter(events2);
        eventsRv.setAdapter(adapter);
        foodEventRecyclerView.setAdapter(adapter2);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsRv.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        foodEventRecyclerView.setLayoutManager(linearLayoutManager2);
        categoriesRV = view.findViewById(R.id.categoriesrv);
        categoriesRV.addItemDecoration(new EqualSpacingItemDecoration(10));
        categoriesArrayList = new ArrayList<>();
        adapterCat = new CategoriesAdapter(categoriesArrayList);
        categoriesRV.setAdapter(adapterCat);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRV.setLayoutManager(manager);
        queryPopularEvents();
        queryCategories();
        queryFoodEvents();

        /*
        mMapView = view.findViewById(R.id.mapView2);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            //mMapView.getMapAsync(this);
        }*/
    }

    public void queryFoodEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("generalType", "food");
        query.setLimit(5);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Event event;
                        event = objects.get(i);
                        events2.add(event);
                        adapter2.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void queryPopularEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("generalType", "popular");
        query.setLimit(5);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Event event;
                        event = objects.get(i);
                        events.add(event);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                moveToSearchFragment();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
    public void queryCategories(){

        ParseQuery<Categories> query = ParseQuery.getQuery("Categories");
        query.findInBackground(new FindCallback<Categories>() {
            @Override
            public void done(List<Categories> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i < objects.size(); i++){
                        Categories category;
                        category = objects.get(i);
                        categoriesArrayList.add(category);
                        adapterCat.notifyDataSetChanged();
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    public void moveToSearchFragment(){
        SearchSlider fragment = new SearchSlider();
        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /*

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mgoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }
    */
}
