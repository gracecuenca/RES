package com.example.fbu_res;

import android.app.Application;

import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.Categories;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.parse.Parse;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseUser.registerSubclass(User.class);
        // ParseUser.registerSubclass(Business.class);
        ParseUser.registerSubclass(Event.class);
        ParseUser.registerSubclass(Group.class);
        ParseUser.registerSubclass(Categories.class);
        ParseUser.registerSubclass(Address.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("resApp") // should correspond to APP_ID env variable
                .clientKey("fbu_is_awesome")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://fbu-res.herokuapp.com/parse/").build());

    }
}
