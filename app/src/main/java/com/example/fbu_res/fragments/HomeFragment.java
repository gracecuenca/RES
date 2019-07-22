package com.example.fbu_res.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.GeofenceRegistrationService;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Event;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    protected ArrayList<Event> mEvents;

    // needed for infinite pagination
    private EndlessRecyclerViewScrollListener scrollListener;

    // needed for swipe to refresh
    private SwipeRefreshLayout swipeContainer;

    // global variable options needed for user-inputted filters
    private String option;

    // global variable needed for accessing permissions
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    // geofencing variables
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    private com.google.android.gms.location.LocationCallback locationCallback;
    // LocationRequest locationRequest;

    // some more constants
    public static final String GEOFENCE_ID_STAN_UNI = "STAN_UNI";
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;
    public static final HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<>();
    static {
        // stanford university.
        AREA_LANDMARKS.put(GEOFENCE_ID_STAN_UNI, new LatLng(37.427476, -122.170262));
    }
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // default search query is Date
        option = "Date";
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO -- customized toolbar color and logo
        // TODO -- make recycler view go to top upon refreshing

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

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(true, false, option);
            }
        });

        // Configure the refreshing colors
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

        // location permissions checking
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        } else {
            // Permission has already been granted
        }

        // creating instance of Google API client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    // Toast.makeText(getContext(), location.toString(), Toast.LENGTH_LONG).show();
                }
            }

        };

        // locationRequest = new LocationRequest();
    }

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

    // TODO -- sort events by: location (radius), currently being done by date
    public void loadEvents(final boolean isRefresh, final boolean isPaginating, String option) {
        ParseQuery<Event> eventsQuery = new ParseQuery<Event>(Event.class);
        eventsQuery.setLimit(10);

        // sorting events based on spinner input
        eventsQuery.addDescendingOrder(Event.KEY_DATE);

        if (isRefresh) {
            clear();
            swipeContainer.setRefreshing(false); // signal refresh is completed
        }
        if (isPaginating) {
            // if(option.equals("Date")){
            eventsQuery.whereLessThan(Event.KEY_DATE, mEvents.get(mEvents.size() - 1).getDate());
            //}
            //else if(option.equals("Distance")){
            // TODO -- insert distance functionality
            //}
        }
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

    // override methods needed for the geofences
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationMonitor();
        startGeofencing();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(APP_TAG, "Google connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(APP_TAG, "connection failed " + connectionResult.getErrorMessage());
    }

    // overrrided onstart and onstop methods
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // connecting to location services and making location request
    private void startLocationMonitor() {
        Log.d(APP_TAG, "start location monitor");
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            LocationServices.getFusedLocationProviderClient(getActivity())
                    .requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e){
            Log.e(APP_TAG, e.getMessage());
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.getFusedLocationProviderClient(getActivity())
                .removeLocationUpdates(locationCallback);
    }

    // getting the geofence
    @NonNull
    private Geofence getGeofence() {
        LatLng latLng = AREA_LANDMARKS.get(GEOFENCE_ID_STAN_UNI);
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_ID_STAN_UNI)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS_IN_METERS)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(getGeofence());
        return builder.build();
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(APP_TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
        }else{
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(GEOFENCE_ID_STAN_UNI)) {
                Log.d(APP_TAG, "You are inside Stanford University");
            } else {
                Log.d(APP_TAG, "You are outside Stanford University");
            }
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(getContext(), GeofenceRegistrationService.class);
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void startGeofencing() {
        Log.d(APP_TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        if (!mGoogleApiClient.isConnected()) {
            Log.d(APP_TAG, "Google API client not connected");
        } else {
            try { LocationServices.getGeofencingClient(getContext())
                        .addGeofences(getGeofencingRequest(), pendingIntent);
            } catch (SecurityException e) {
                Log.d(APP_TAG, e.getMessage());
            }
        }
    }

}
