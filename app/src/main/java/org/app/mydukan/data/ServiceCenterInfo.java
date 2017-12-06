package org.app.mydukan.data;

import java.io.Serializable;

public class ServiceCenterInfo implements Serializable {
    private String servicecenter_NAME = "";
    private String servicecenter_BRAND = "";
    private String servicecenter_CITY = "";
    private String Stateservicecenter_STATE = "";
    private String servicecenter_ADDRESS = "";
    private String servicecenter_PINCODE = "";
    private String servicecenter_PHONENUMBER = "";
    private String servicecenter_CONTACTPERSION = "";
    private String servicecenter_EMAILID = "";


    public ServiceCenterInfo() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }



    public String getStateservicecenter_STATE() {
        return Stateservicecenter_STATE;
    }

    public void setStateservicecenter_STATE(String stateservicecenter_STATE) {
        Stateservicecenter_STATE = stateservicecenter_STATE;
    }

    public String getServicecenter_PINCODE() {
        return String.valueOf(servicecenter_PINCODE);
    }

    public void setServicecenter_PINCODE(String servicecenter_PINCODE) {
        this.servicecenter_PINCODE = servicecenter_PINCODE;
    }

    public String getServicecenter_CONTACTPERSION() {
        return servicecenter_CONTACTPERSION;
    }

    public void setServicecenter_CONTACTPERSION(String servicecenter_CONTACTPERSION) {
        this.servicecenter_CONTACTPERSION = servicecenter_CONTACTPERSION;
    }

    public String getServicecenter_EMAILID() {
        return servicecenter_EMAILID;
    }

    public void setServicecenter_EMAILID(String servicecenter_EMAILID) {
        this.servicecenter_EMAILID = servicecenter_EMAILID;
    }

    public String getServicecenter_NAME() {
        return servicecenter_NAME;
    }

    public void setServicecenter_NAME(String servicecenter_NAME) {
        this.servicecenter_NAME = servicecenter_NAME;
    }

    public String getServicecenter_BRAND() {
        return servicecenter_BRAND;
    }

    public void setServicecenter_BRAND(String servicecenter_BRAND) {
        this.servicecenter_BRAND = servicecenter_BRAND;
    }

    public String getServicecenter_CITY() {
        return servicecenter_CITY;
    }

    public void setServicecenter_CITY(String servicecenter_CITY) {
        this.servicecenter_CITY = servicecenter_CITY;
    }

    public String getServicecenter_ADDRESS() {
        return servicecenter_ADDRESS;
    }

    public void setServicecenter_ADDRESS(String servicecenter_ADDRESS) {
        this.servicecenter_ADDRESS = servicecenter_ADDRESS;
    }


    public String getServicecenter_PHONENUMBER() {
        return servicecenter_PHONENUMBER;
    }

    public void setServicecenter_PHONENUMBER(String servicecenter_PHONENUMBER) {
        this.servicecenter_PHONENUMBER = servicecenter_PHONENUMBER;
    }


}
