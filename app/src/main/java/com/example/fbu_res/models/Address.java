package com.example.fbu_res.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Address")
public class Address extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESSLINE1 = "addressLineOne";
    public static final String KEY_ADDRESSLINE2 = "addressLineTwo";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_ZIPCODE = "zipcode";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_PIN = "pin";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getAddressline1() {
        return getString(KEY_ADDRESSLINE1);
    }

    public void setAddressline1(String address) {
        put(KEY_ADDRESSLINE1, address);
    }

    public String getAddressline2() {
        return getString(KEY_ADDRESSLINE2);
    }

    public void setAddressline2(String address) {
        put(KEY_ADDRESSLINE2, address);
    }

    public String getCity() {
        return getString(KEY_CITY);
    }

    public void setCity(String city) {
        put(KEY_CITY, city);
    }

    public String getState() {
        return getString(KEY_STATE);
    }

    public void setState(String state) {
        put(KEY_STATE, state);
    }

    public String getZipcode() {
        return getString(KEY_ZIPCODE);
    }

    public void setZipcode(String address) {
        put(KEY_ZIPCODE, address);
    }

    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    public ParseGeoPoint getPin() {
        ParseGeoPoint point = new ParseGeoPoint();
        try {
            point = fetchIfNeeded().getParseGeoPoint(KEY_PIN);

        } catch (ParseException e) {
            Log.v("Address", e.toString());
            e.printStackTrace();
        }
        return point;
    }

    public void setPin(ParseGeoPoint pin) {
        put(KEY_PIN, pin);
        saveInBackground();
    }

    public Address(){

    }
}
