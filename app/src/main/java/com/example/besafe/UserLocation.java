package com.example.besafe;

public class UserLocation {

    private double x;
    private double y;

    public UserLocation() {}

    public UserLocation(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double newValue) {
        this.x = newValue;
    }
    public double getY() {
        return this.y;
    }

    public void setY(double newValue) {
        this.y = newValue;
    }

}
