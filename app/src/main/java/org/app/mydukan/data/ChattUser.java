package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by Shivayogi Hiremath on 20/07/2017.
 */
@SuppressWarnings("serial")
public class ChattUser  implements Serializable {

    private String name;
    private String email;
    private String uId;
    private String photoUrl;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    private String userType;

    public ChattUser() {
    }

    public ChattUser(String name, String email, String uId, String photoUrl) {
        this.name = name;
        this.email = email;
        this.uId = uId;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
