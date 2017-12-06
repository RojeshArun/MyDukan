package org.app.mydukan.data;



import java.io.Serializable;

/**
 * Created by arpithadudi on 8/8/16.
 */
@SuppressWarnings("serial")
public class AddressInfo implements Serializable {

    String street = "";
    String city = "";
    String state = "";
    String country = "";
    String pincode = "";
    String latlong="";
    String longitude="";
    String  latitude="";
    boolean location_verified=false;
    public boolean isLocation_verified() {
        return location_verified;
    }

    public void setLocation_verified(boolean location_verified) {
        this.location_verified = location_verified;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }



    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }



    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
