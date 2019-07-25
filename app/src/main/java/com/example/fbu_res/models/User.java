package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser{
    public static final String KEY_LOCATION = "userLocation";

    public void setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }


}
