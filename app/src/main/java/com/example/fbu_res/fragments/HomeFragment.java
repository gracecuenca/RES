package com.example.fbu_res.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_res.AddEventActivity;
import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.HEAD;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.example.fbu_res.models.Event.KEY_DISTANCE_TO_USER;
import static com.example.fbu_res.models.Event.KEY_OWNER;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    protected ArrayList<Event> mEvents;
    private Button btnAddEvent;

    // needed for infinite pagination
    private EndlessRecyclerViewScrollListener scrollListener;

    // needed for swipe to refresh
    private SwipeRefreshLayout swipeContainer;

    // global variable options needed for user-inputted filters
    private String option = "Date"; // default search query is Date

    // global variable needed for accessing permissions
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    // needed for getting current location
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    // the current user
    private Consumer user;
    // the current location
    private ParseGeoPoint currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO -- customized toolbar color and logo

        // set up current user location
        user = (Consumer) ParseUser.getCurrentUser();
        startLocationUpdates(); // this is where the location is being updated

        rvEvents = (RecyclerView) view.findViewById(R.id.rvEvents);
        // create the data source
        mEvents = new ArrayList<>();
        // create the adapter
        adapter = new EventAdapter(mEvents);
        // set the adapter on the recycler view
        rvEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvEvents.setLayoutManager(staggeredGridLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadEvents(false, true, option);
            }
        };

        // Adds the scroll listener to RecyclerView
        rvEvents.addOnScrollListener(scrollListener);

        // swipe container wraps around the view in order to allow pull to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(true, false, option);
            }
        });

        // Configure the refreshing color
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // setting up sorting by spinner input here
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerSort);

        // creating adapter for the spinner
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.sort_arrays, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(this);

        // make the add event button visible only if the current user is a business
        btnAddEvent = (Button) view.findViewById(R.id.btnAddEvent);

        // if user is logged in
        // consumers: cannot create events
        // businesses: can create events
        if(user != null) {
            if (user.getType().equals("Consumer")) btnAddEvent.setVisibility(View.GONE);
            else if (user.getType().equals("Business")) {
                btnAddEvent.setVisibility(View.VISIBLE);

                // the on click listener will only be set up if the user is a business
                btnAddEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), AddEventActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        currentLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                        user.setLocation(currentLocation);
                        user.saveInBackground();
                    }
                },
                Looper.myLooper());
        if(option.equals("Distance")) sortByDistance();
    }

    // TODO -- test for when the user doesn't allow us to access location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related tasks you need to do
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Location services disabled: nearest events" +
                            "cant't be found", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // this method will query differently based on whether
    public void loadEvents(final boolean isRefresh, final boolean isPaginating, String option) {

        // loading events for a consumer
        Consumer user = (Consumer) ParseUser.getCurrentUser();

        ParseQuery<Event> eventsQuery = new ParseQuery<Event>(Event.class);
        eventsQuery.setLimit(10);

        if(option.equals("Date")){
            eventsQuery.addAscendingOrder(Event.KEY_DATE);
        } else if(option.equals("Distance")){
            eventsQuery.addAscendingOrder(Event.KEY_DISTANCE_TO_USER);
            // this is where the limiting by distance happens
            eventsQuery.whereLessThanOrEqualTo(KEY_DISTANCE_TO_USER, 10);
        }

        if (isRefresh) {
            clear();
            swipeContainer.setRefreshing(false); // signal refresh is completed
        }
        if (isPaginating) {
            if(option.equals("Date")){
                eventsQuery.whereGreaterThan(Event.KEY_DATE, mEvents.get(mEvents.size() - 1).getDate());
            } else if(option.equals("Distance")){
                eventsQuery.whereGreaterThan(Event.KEY_DISTANCE_TO_USER, mEvents.get(mEvents.size() - 1).getDistanceToUser());
            }
        }

        // businesses will only see the events that they've created
        if(user.getType().equals("Business")){ eventsQuery.whereEqualTo(Event.KEY_OWNER, user); }

        eventsQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                addAll(events);
            }
        });
    }

    // this is where we will implement the sorting functionality of the spinner
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        clear();
        option = parent.getItemAtPosition(position).toString();
        loadEvents(false, false, option);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // spinner always has a default item selected: Date
    }

    // Clean all elements of the recycler
    public void clear() {
        mEvents.clear();
        adapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Event> list) {
        mEvents.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public void sortByDistance(){
        for(int i = 0; i < mEvents.size(); i++){
            Event event = mEvents.get(i);
            double distance = currentLocation.distanceInMilesTo(event.getParseGeoPoint());
            event.put(KEY_DISTANCE_TO_USER, distance);
            event.saveInBackground();
        }
    }

}
