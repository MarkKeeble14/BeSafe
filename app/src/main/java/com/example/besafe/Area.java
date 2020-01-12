package com.example.besafe;

public class Area {

    private double latitude;
    private double longitude;
    private int radius;

    public Area() {}

    public Area(double latitude, double longitude, int radius){
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    // write method to return/set GeoFence
    // given lat long radius

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


}
