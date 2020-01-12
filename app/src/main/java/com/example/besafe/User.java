package com.example.besafe;

public class User {

    private String deviceId;
    private String phoneNo;
    private UserLocation location;
    private boolean flag;

    public User(){};

    public User(String deviceId, String phoneNo, UserLocation location, boolean flag) {
        this.deviceId = deviceId;
        this.phoneNo = phoneNo;
        this.location = location;
        this.flag = flag;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
