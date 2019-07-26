package com.example.fbu_res.util;


import android.os.AsyncTask;
import android.util.Log;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Response;

public class YelpClient {
    public static final String API_HOST = "https://api.yelp.com";
    public static final String SEARCH_PATH = "/v3/businesses/search";
    public static final String BUSINESS_PATH = "/v3/businesses/";
    public static final String KEY = "BnRIYh3GBQfo6tCRdTbCYr1JTxUcS2u3b4Z8nUswd_9ir2Cb27bL2wP-HzREH81wjrIaTEjOdfAF-wx4Zz9cD-2j0AhIZ966pDA8MDMmbaCl30hEAmLk_llJhj87XXYx";
    Map<String, String> params;

    public YelpClient(){

    }

    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
    YelpFusionApi yelpFusionApi;

    {
        try {
            yelpFusionApi = apiFactory.createAPI(KEY);
            params = new HashMap<>();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class FetchData extends AsyncTask<String, String, String>{
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
