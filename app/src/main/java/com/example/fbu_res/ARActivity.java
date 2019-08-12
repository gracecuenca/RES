package com.example.fbu_res;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_res.fragments.BusinessSliderSearch;
import com.example.fbu_res.models.ARModel;
import com.example.fbu_res.models.BusinessSearch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import uk.co.appoly.arcorelocation.LocationScene;


import static com.example.fbu_res.DemoUtils.createArSession;


public class ARActivity extends AppCompatActivity{
    private boolean installRequested;
    private boolean hasFinishedLoading = false;
    private FusedLocationProviderClient fusedLocationClient;
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    private ArSceneView arSceneView;
    GoogleApiClient mGoogleApiClient;

    // Renderables for this example
    private ModelRenderable andyRenderable;
    private ViewRenderable exampleLayoutRenderable;
    LocationScene locationScene;
    protected LocationManager locationManager;
    private Context mContext;
    Location location;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

    // Declaring a Location Manager



    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;




    // Our ARCore-Location scene
    Map<String, String> params;
    ArrayList<BusinessSearch> businessSearches;
    ArrayList<Business> businessArrayList;
    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;
    LocationScene locationScene2;
    String lat;
    String longi;
    private Location mLastLocation;


    @SuppressLint("MissingPermission")
    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        mContext = getApplicationContext();
        arSceneView = findViewById(R.id.ux_fragment);

        businessSearches = new ArrayList<>();
        {
            try {
                yelpFusionApi = apiFactory.createAPI(KEY);
                params = new HashMap<>();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new FetchData().execute();

        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);
    }




    /**
     * Example node of a layout
     *
     * @return
     */
    private Node getExampleView() {
        Node base = new Node();
        base.setRenderable(exampleLayoutRenderable);
        Context c = this;
        // Add  listeners etc here
        View eView = exampleLayoutRenderable.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Location marker touched.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }

    /***
     * Example Node of a 3D model
     *
     * @return
     */
    private Node getAndy() {
        Node base = new Node();
        base.setRenderable(andyRenderable);
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Andy touched.", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    public class FetchData extends AsyncTask<String, String, String> {
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Build a renderable from a 2D View.
            CompletableFuture<ViewRenderable> exampleLayout =
                    ViewRenderable.builder()
                            .setView(ARActivity.this, R.layout.example_layout)
                            .build();

            // When you build a Renderable, Sceneform loads its resources in the background while returning
            // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
            CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                    .setSource(ARActivity.this,Uri.parse("bear.sfb"))
                    .build();


            CompletableFuture.allOf(
                    exampleLayout,
                    andy)
                    .handle(
                            (notUsed, throwable) -> {
                                // When you build a Renderable, Sceneform loads its resources in the background while
                                // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                // before calling get().

                                if (throwable != null) {
                                    DemoUtils.displayError(ARActivity.this, "Unable to load renderables", throwable);
                                    return null;
                                }

                                try {
                                    exampleLayoutRenderable = exampleLayout.get();
                                    andyRenderable = andy.get();
                                    andyRenderable = andy.get();
                                    hasFinishedLoading = true;

                                } catch (InterruptedException | ExecutionException ex) {
                                    DemoUtils.displayError(ARActivity.this, "Unable to load renderables", ex);
                                }

                                return null;
                            });


            // Set an update listener on the Scene that will hide the loading message once a Plane is
            // detected.
            arSceneView.getScene().addOnUpdateListener(
                    frameTime -> {
                        if (!hasFinishedLoading) {
                            return;
                        }

                        if (locationScene == null) {
                            // If our locationScene object hasn't been setup yet, this is a good time to do it
                            // We know that here, the AR components have been initiated.
                            locationScene = new LocationScene(ARActivity.this, arSceneView);
                            locationScene.setOffsetOverlapping(true);
                            //locationScene.setAnchorRefreshInterval(900);
                            // Now lets create our location markers.
                            // First, a layout

                            for(int i = 0; i< businessSearches.size(); i++){
                                ARModel.render(ARActivity.this, businessSearches.get(i), locationScene);
                            }

                        }

                        Frame frame = arSceneView.getArFrame();
                        if (frame == null) {
                            return;
                        }

                        if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                            return;
                        }

                        if (locationScene != null) {
                            locationScene.processFrame(frame);
                        }

                    });


        }

        @SuppressLint("MissingPermission")
        @Override
        protected String doInBackground(String... strings) {
            params.put("term", "coffee");
            params.put("sort_by", "distance");
            //params.put("location", "1219 E Marion St, Seattle, WA 98122");
            params.put("limit", "5");
            params.put("radius", "1200");
            params.put("latitude", BusinessSliderSearch.lat);
            params.put("longitude", BusinessSliderSearch.longi);
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
                }

            }
            return null;
        }
    }

}