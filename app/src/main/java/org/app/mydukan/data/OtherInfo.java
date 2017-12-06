package org.app.mydukan.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by arpithadudi on 9/16/16.
 */
@IgnoreExtraProperties
public class OtherInfo implements Serializable {

    private String status = "disabled";
    private String role = "";
    private long createddate = 0l;
    private String invitecode = "";
    private Boolean ispublic = false;
    private Boolean iscart = false;
    private Boolean byinvitecode = false;
    private String brands = "";
    private String shopnumber = "";
    private String distributorphonenumber = "";
    private String acceptedprivacypolicy = "";

    public String getAcceptedprivacypolicy() {
        return acceptedprivacypolicy;
    }

    public void setAcceptedprivacypolicy(String acceptedprivacypolicy) {
        this.acceptedprivacypolicy = acceptedprivacypolicy;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getShopnumber() {
        return shopnumber;
    }

    public void setShopnumber(String shopnumber) {
        this.shopnumber = shopnumber;
    }

    public String getDistributorphonenumber() {
        return distributorphonenumber;
    }

    public void setDistributorphonenumber(String distributorphonenumber) {
        this.distributorphonenumber = distributorphonenumber;
    }




    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInviteCode() {
        return invitecode;
    }

    public void setInviteCode(String myinvitecode) {
        this.invitecode = myinvitecode;
    }


    public boolean isUserPublic() {
        return ispublic;
    }

    public void setIsUserPublic(boolean ispublic) {
        this.ispublic = ispublic;
    }

    public boolean getByInviteCode() {
        return byinvitecode;
    }

    public void setByInviteCode(boolean byinvitecode) {
        this.byinvitecode = byinvitecode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isCartEnabled() {
        return iscart;
    }

    public void setCartEnabled(boolean iscart) {
        this.iscart = iscart;
    }

    public long getCreatedDate() {
        return createddate;
    }

}
