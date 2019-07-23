package com.example.fbu_res;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.example.fbu_res.fragments.HomeFragment.GEOFENCE_ID_STAN_UNI;

public class GeofenceRegistrationService extends IntentService {

    private static final String TAG = "GeoIntentService";

    public GeofenceRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(GEOFENCE_ID_STAN_UNI)) {
                Log.d(TAG, "You are inside Stanford University");
            } else {
                Log.d(TAG, "You are outside Stanford University");
            }
        }
    }
}
