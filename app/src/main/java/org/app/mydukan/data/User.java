package org.app.mydukan.data;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class User extends Object implements Serializable {
    String id;
    String emailid;
    UserInfo userinfo;
    CompanyInfo companyinfo;
    OtherInfo otherinfo;
    String qrcodedurl = "";
    AppSubscriptionInfo appSubscriptionInfo;
    String vrified_mobilenum="false";
    String verified_location="false";
    String verified_CompanyInfo="false";

    public User() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public String getVrified_mobilenum() {
        return vrified_mobilenum;
    }

    public void setVrified_mobilenum(String vrified_mobilenum) {
        this.vrified_mobilenum = vrified_mobilenum;
    }

    public String getVerified_location() {
        return verified_location;
    }

    public void setVerified_location(String verified_location) {
        this.verified_location = verified_location;
    }

    public String getVerified_CompanyInfo() {
        return verified_CompanyInfo;
    }

    public void setVerified_CompanyInfo(String verified_CompanyInfo) {
        this.verified_CompanyInfo = verified_CompanyInfo;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public AppSubscriptionInfo getAppSubscriptionInfo() {
        return appSubscriptionInfo;
    }

    public void setAppSubscriptionInfo(AppSubscriptionInfo appSubscriptionInfo) {
        this.appSubscriptionInfo = appSubscriptionInfo;
    }

    public String getQrcodedurl() {
        return qrcodedurl;
    }

    public void setQrcodedurl(String qrcodedurl) {
        this.qrcodedurl = qrcodedurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public CompanyInfo getCompanyinfo() {
        return companyinfo;
    }

    public void setCompanyinfo(CompanyInfo companyInfo) {
        this.companyinfo = companyInfo;
    }

    public OtherInfo getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(OtherInfo otherinfo) {
        this.otherinfo = otherinfo;
    }

}