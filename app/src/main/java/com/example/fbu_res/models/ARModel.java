package com.example.fbu_res.models;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.fbu_res.BusinessSignUp;
import com.example.fbu_res.R;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

public class ARModel {
    public static void render(Context c, BusinessSearch couponInfo, LocationScene locationScene) {

        CompletableFuture<ViewRenderable> couponLayout =
                ViewRenderable.builder()
                        .setView(c, R.layout.example_layout)
                        .build();


        CompletableFuture.allOf(couponLayout)
                .handle(
                        (notUsed, throwable) -> {

                            if (throwable != null) {
                                Log.i("", "Unable to load renderables");
                                return null;
                            }

                            try {
                                // Non scalable info outside location
                                ViewRenderable vr = couponLayout.get();
                                Node base = new Node();
                                base.setRenderable(vr);
                                TextView title = vr.getView().findViewById(R.id.ARtextView2);

                                title.setText(couponInfo.getName());


                                LocationMarker couponLocationMarker = new LocationMarker(
                                        Double.valueOf(couponInfo.getLongi()),
                                        Double.valueOf(couponInfo.getLat()),
                                        base
                                );

                                couponLocationMarker.setRenderEvent(node -> {
                                    View eView = vr.getView();
                                    TextView distanceTextView = eView.findViewById(R.id.ARtextView);
                                    distanceTextView.setText(Math.round(node.getDistanceInAR()) + "M");
                                });

                                locationScene.mLocationMarkers.add(couponLocationMarker);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return null;
                        });
    }
}
