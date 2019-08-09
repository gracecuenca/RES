package com.example.fbu_res;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbu_res.adapters.SingleBusinessEventsAdapter;
import com.example.fbu_res.fragments.SearchSlider;
import com.example.fbu_res.fragments.YelpCatergoriesFragment;
import com.example.fbu_res.models.BusinessSearch;
import com.example.fbu_res.models.Event;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class SingleBusinessResultFragment extends AppCompatActivity {
    public static final String TAG = SingleBusinessResultFragment.class.getSimpleName();
    ConstraintLayout bottomSheet;
    BottomSheetBehavior sheetBehavior;
    ImageView businessProfileImage;
    TextView name;
    TextView restaurantOpen;
    TextView priceTextView;
    TextView ratingTextView;
    Button callButton;
    TextView address;
    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;
    String businessName;
    RecyclerView businessEventsRecyclerView;
    ArrayList<Business> businessArrayList;
    String lat;
    String longi;
    BusinessSearch business;
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    Map<String, String> params;
    ArrayList<Event> events;
    SingleBusinessEventsAdapter adapter;
    ImageView largeImage;
    ImageView backButton;
    private static final int REQUEST_CALL = 1;
    @Nullable

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_business_search_fragment);
        businessName = getIntent().getStringExtra("businessName");
        lat = getIntent().getStringExtra("Lat");
        longi = getIntent().getStringExtra("Long");
        {
            try {
                yelpFusionApi = apiFactory.createAPI(KEY);
                params = new HashMap<>();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new FetchData().execute();
        events = new ArrayList<>();
        adapter = new SingleBusinessEventsAdapter(events);
        bottomSheet = findViewById(R.id.bottomSheetConstraintLayout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        businessProfileImage = findViewById(R.id.businessImageProfile);
        name = findViewById(R.id.businessNametextView);
        restaurantOpen = findViewById(R.id.openNowTextView);
        priceTextView = findViewById(R.id.pricetextView);
        ratingTextView = findViewById(R.id.ratingtextView);
        callButton = findViewById(R.id.callButton);
        largeImage = findViewById(R.id.largeBusinessImageView);
        address = findViewById(R.id.addresstextView);
        businessEventsRecyclerView = findViewById(R.id.businessEventsRecyclerView);
        businessEventsRecyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        businessEventsRecyclerView.setLayoutManager(manager);
        callButton = findViewById(R.id.callButton);
        backButton = findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchSlider fragment = new SearchSlider();
                finish();
            }
        });
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
        loadEvents();
    }




    private void makeCall(){
        String phoneNumber = business.getPhone();
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(phoneIntent);
    }

    private void loadEvents(){
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", businessName);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    Log.d("Success", "Success");
                    if(objects.size() != 0){
                        ParseRelation relation = objects.get(0).getRelation("createdEvents");
                        ParseQuery<Event> queryEvents = relation.getQuery();
                        queryEvents.addAscendingOrder(Event.KEY_DATE);
                        queryEvents.findInBackground(new FindCallback<Event>() {
                            @Override
                            public void done(List<Event> objects, ParseException e) {
                                for(int x =0; x <objects.size(); x++){
                                    Event event;
                                    event = objects.get(x);
                                    events.add(event);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }



                }
            }
        });
    }

    public class FetchData extends AsyncTask<String, String, String> {
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            name.setText(business.getName());
            restaurantOpen.setText(business.getOpen());
            priceTextView.setText(business.getPrice());
            ratingTextView.setText(business.getRating());
            address.setText(business.getLocation());
            if (business.getURL() != null) {
                Glide.with(SingleBusinessResultFragment.this)
                        .load(business.getURL())
                        .apply(RequestOptions.circleCropTransform())
                        .into(businessProfileImage);
                Glide.with(SingleBusinessResultFragment.this)
                        .load(business.getURL())
                        .into(largeImage);
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            params.put("term", businessName);
            //TODO: initially, there will be search by current location, then user can searchjjljg
            params.put("latitude",lat);
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
                Business yelpBusiness = businessArrayList.get(0);
                business = new BusinessSearch(yelpBusiness.getName(), 2);
                business.setPrice(yelpBusiness.getPrice());
                business.setRating(Double.toString(yelpBusiness.getRating()));
                business.setPhone(yelpBusiness.getPhone());
                business.setURL(yelpBusiness.getImageUrl());
                business.setLocation(yelpBusiness.getLocation().getAddress1() + "\n" +yelpBusiness.getLocation().getCity()  +" " + yelpBusiness.getLocation().getState()+ " " +yelpBusiness.getLocation().getZipCode());
                if(yelpBusiness.getIsClosed()){
                    business.setOpen("Closed");
                } else{
                    business.setOpen("Open");
                }
            }
            return null;
        }
    }


}
