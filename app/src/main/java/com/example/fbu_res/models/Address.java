package com.example.fbu_res.models;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Address extends ParseObject {
    public static final String KEY_ADDRESSLINE1 = "addressLineOne";
    public static final String KEY_ADDRESSLINE2 = "addressLineTwo";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_ZIPCODE = "zipcode";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_PIN = "pin";


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

    public int getZipcode() {
        return getNumber(KEY_ZIPCODE).intValue();
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
        return getParseGeoPoint(KEY_PIN);
    }

    public void setPin(ParseGeoPoint pin) {
        put(KEY_PIN, pin);
    }
}
