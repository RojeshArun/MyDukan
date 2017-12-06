package org.app.mydukan.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by arpithadudi on 8/25/16.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties

public class Supplier extends Object implements Serializable {
    String id = "";
    String retailerStatus = "add";
    UserInfo userinfo;
    CompanyInfo companyinfo;
    OtherInfo otherinfo;
    SupplierGroups supplierGroups;

    public Supplier() {
        // empty default constructor, necessary for Firebase to be able to deserialize
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

    public String getRetailerStatus() {
        return retailerStatus;
    }

    public void setRetailerStatus(String retailerStatus) {
        this.retailerStatus = retailerStatus;
    }

    public SupplierGroups getSupplierGroups() {
        return supplierGroups;
    }

    public void setSupplierGroups(SupplierGroups supplierGroups) {
        this.supplierGroups = supplierGroups;
    }
}
