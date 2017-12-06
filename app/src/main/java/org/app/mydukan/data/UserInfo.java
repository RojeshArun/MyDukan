package org.app.mydukan.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {

    String name = "";
    String emailid = "";
    String number = "";
    AddressInfo addressinfo;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public AddressInfo getAddressinfo() {
        return addressinfo;
    }

    public void setAddressinfo(AddressInfo addressInfo) {
        this.addressinfo = addressInfo;
    }


}







