package com.example.fbu_res.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fbu_res.AddEventActivity;
import com.example.fbu_res.DirectMessageActivity;
import com.example.fbu_res.EndlessRecyclerViewScrollListener;
import com.example.fbu_res.HomeActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.EventAdapter;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.example.fbu_res.models.Event.KEY_DISTANCE_TO_USER;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public final String APP_TAG = "HomeFragment";
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    protected ArrayList<Event> mEvents;
    private Toolbar toolbar;
    private AppCompatActivity activity;

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
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // the current user
    private Consumer user;
    // the current location
    private ParseGeoPoint currentLocation;

    // push notification logic
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" +
            "AAAATqE5Zos:APA91bHgAP71ezc4Ir2F042-RFZ19KOKMC3pSPyDFjtKom0kwR-vCCaZ7fMOv2P5T7BKoL--" +
            "93g0qIAG1jdq0os6XCHAD_fnCDX2ln1qeoqD8v12sP3XIgO_O9I8C0_Q1DU1OuRKXWo6";
    private static final String contentType = "application/json";

    // request queue
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setting up the request queue
        requestQueue = Volley.newRequestQueue(view.getContext());


        // setting up personalized toolbar
        activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) view.findViewById(R.id.homeToolbar);
        activity.setSupportActionBar(toolbar);
        activity.setTitle("");

        // set up current user location
        user = (Consumer) ParseUser.getCurrentUser();

        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){ // if permissions not granted
            requestPermissions();
        } else{
            startLocationUpdates();
        }

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

        /*// make the add event button visible only if the current user is a business
        if(user != null) {
            if (user.getType().equals("Business")) setHasOptionsMenu(true);
            else if(user.getType().equals("Consumer")) setHasOptionsMenu(false);
        }*/
        setHasOptionsMenu(true);

    }

    // Menu things
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.menu_home, menu);
        inflater.inflate(R.menu.menu_toolbar, menu);

        // setting up icon tinting
        Drawable maps = menu.findItem(R.id.action_ar).getIcon();
        maps.setColorFilter(getResources().getColor(R.color.turquoise), PorterDuff.Mode.SRC_IN);

        Drawable addEvent = menu.findItem(R.id.action_add_event).getIcon();
        addEvent.setColorFilter(getResources().getColor(R.color.turquoise), PorterDuff.Mode.SRC_IN);

        Drawable chat = menu.findItem(R.id.directMessage).getIcon();
        chat.setColorFilter(getResources().getColor(R.color.turquoise), PorterDuff.Mode.SRC_IN);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_event:
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                startActivity(intent);
                break;
            case R.id.directMessage:
                Intent intent2 = new Intent(getContext(), DirectMessageActivity.class);
                startActivity(intent2);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
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

        // location has not been granted
        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        currentLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d("loc", location.getLatitude() +" "+ location.getLongitude());
                        if(!currentLocation.equals(user.getLocation())){
                            Log.d("loc", "hi");
                            user.setLocation(currentLocation);
                            user.saveInBackground();
                            if(option.equals("Distance")) {
                                clear(); // comment out if u dont want it to spasm
                                sortByDistance();
                                loadEvents(false, false, option); // comment out if u dont want it to spasm
                            }
                        }
                    }
                },
                Looper.myLooper());
    }

    // requesting permissions
    public void requestPermissions(){
        new AlertDialog.Builder(getContext())
                .setTitle("Location Permissions needed")
                .setMessage("Please enable location services")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
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
            eventsQuery.whereLessThanOrEqualTo(KEY_DISTANCE_TO_USER, 10000); // 10 mile radius
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
            Log.d(APP_TAG, Double.toString(distance));
            event.setDistanceToUser(distance);
        }
    }

}
