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


    public String getKeyAddressline1() {
        return getString(KEY_ADDRESSLINE1);
    }

    public void setKeyAddressline1(String address) {
        put(KEY_ADDRESSLINE1, address);
    }

    public String getKeyAddressline2() {
        return getString(KEY_ADDRESSLINE2);
    }

    public void setKeyAddressline2(String address) {
        put(KEY_ADDRESSLINE2, address);
    }

    public String getKeyCity() {
        return getString(KEY_CITY);
    }

    public void setKeyCity(String city) {
        put(KEY_CITY, city);
    }

    public String getKeyState() {
        return getString(KEY_STATE);
    }

    public void setKeyState(String state) {
        put(KEY_STATE, state);
    }

    public int getKeyZipcode() {
        return getNumber(KEY_ZIPCODE).intValue();
    }

    public void setKeyZipcode(String address) {
        put(KEY_ZIPCODE, address);
    }

    public String getKeyCountry() {
        return getString(KEY_COUNTRY);
    }

    public void setKeyCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    public ParseGeoPoint getKeyPin() {
        return getParseGeoPoint(KEY_PIN);
    }

    public void setKeyPin(ParseGeoPoint pin) {
        put(KEY_PIN, pin);
    }
}
