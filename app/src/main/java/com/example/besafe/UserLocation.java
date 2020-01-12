package com.example.besafe;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    LatLng latlng;

    public UserLocation() {}

    public UserLocation(LatLng latlng){
        this.latlng = latlng;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    
}
