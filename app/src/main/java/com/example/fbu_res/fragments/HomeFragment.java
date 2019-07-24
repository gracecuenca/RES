package com.example.fbu_res.fragments;

import android.Manifest;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.User;
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

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.example.fbu_res.models.Event.KEY_DISTANCE_TO_USER;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener/*,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    protected ArrayList<Event> mEvents;

    // needed for infinite pagination
    private EndlessRecyclerViewScrollListener scrollListener;

    // needed for swipe to refresh
    private SwipeRefreshLayout swipeContainer;

    // global variable options needed for user-inputted filters
    private String option = "Date"; // default search query is Date

    // the current user
    private Consumer user;
    // the current location
    private ParseGeoPoint currentLocation;

    // global variable needed for accessing permissions
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    // needed for getting current location
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    // geofencing variables
    // protected GoogleApiClient mGoogleApiClient;
    // private com.google.android.gms.location.LocationCallback locationCallback;
    // private PendingIntent pendingIntent;

    // converting to geofence clients
    // protected ArrayList<Geofence> mGeofenceList;
    // private GeofencingClient geofencingClient;

    // some more constants
    /*
    public static final String GEOFENCE_ID_STAN_UNI = "STAN_UNI";
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;
    public static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 100;
    public static final HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<>();
    static {
        AREA_LANDMARKS.put(GEOFENCE_ID_STAN_UNI, new LatLng(37.427476, -122.170262));
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO -- customized toolbar color and logo
        // TODO -- make recycler view go to top upon refreshing

        // set up current user location
        user = (Consumer) User.getCurrentUser();
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

        // LOCATION AND GEO FENCING THINGS BELOW \\
        /*
        if(checkLocationPermission()){
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
        }

        // new stuff
        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("test geofence")

                .setCircularRegion(
                        37.427476,
                        -122.170262,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        geofencingClient = LocationServices.getGeofencingClient(getContext());
        */

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
        //getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    //@Override
                    //public void onLocationResult(LocationResult locationResult) {
                        //Location location = locationResult.getLastLocation();
                        //currentLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                        //user.setLocation(currentLocation);
                        // Log.d(APP_TAG, "current location: "+currentPoint.getLatitude()+ " " +currentPoint.getLongitude());
                        //onLocationChanged(location);
                    //}
                //},
                //Looper.myLooper());
    }

    //TODO remeber to uncomment

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
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
        if(option.equals("Date")){
            eventsQuery.addDescendingOrder(Event.KEY_DATE);
        }
        else if(option.equals("Distance")){
            // iterate through all the events (for now) and set up distanceToUser variable for all events
            for(int i = 0; i < mEvents.size(); i++){
                Event event = mEvents.get(i);
                event.put(KEY_DISTANCE_TO_USER, currentLocation.distanceInMilesTo(event.getParseGeoPoint()));
            }
            eventsQuery.addAscendingOrder(KEY_DISTANCE_TO_USER);
        }

        if (isRefresh) {
            clear();
            swipeContainer.setRefreshing(false); // signal refresh is completed
        }
        if (isPaginating) {
            if(option.equals("Date")){
                eventsQuery.whereLessThan(Event.KEY_DATE, mEvents.get(mEvents.size() - 1).getDate());
           }
            else if(option.equals("Distance")){
                eventsQuery.whereGreaterThan(KEY_DISTANCE_TO_USER, mEvents.get(mEvents.size() - 1).getDistanceToUser());
            }
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
        clear();
        option = parent.getItemAtPosition(position).toString();
        Log.d(APP_TAG, option);
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

    /*
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
    */

    // overrrided onstart and onstop methods
    /*
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
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public boolean checkLocationPermission(){
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) { // permission not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext()) // showing explanation for requesting location
                        .setTitle("Location required.")
                        .setMessage("Location required in order to find nearby events!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
            return false;
        } else { // Permission has already been granted
            return true;
        }
    }
    */

}
