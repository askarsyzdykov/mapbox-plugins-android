package com.mapbox.mapboxsdk.plugins.testapp.activity.clustering;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by askar on 1/8/18.
 */

public class MyItem implements IconClusterItem {

    private Marker marker;

    public MyItem(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    @Override
    public LatLng getPosition() {
        return marker.getPosition();
    }

    @Override
    public String getTitle() {
        return marker.getTitle();
    }

    @Override
    public String getSnippet() {
        return marker.getSnippet();
    }

    @Override
    public Icon getIcon() {
        return marker.getIcon();
    }
}
