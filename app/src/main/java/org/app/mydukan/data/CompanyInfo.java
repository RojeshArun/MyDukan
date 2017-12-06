package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/6/16.
 */

@SuppressWarnings("serial")
public class CompanyInfo implements Serializable {

    String name = "";
    String industry = "";
    String vatno = "";
    String cardurl = "";
    String emailid = "";

    public String getName() {
        return name;
    }

    public void setName(String companyname) {
        this.name = companyname;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industryname) {
        this.industry = industryname;
    }

    public String getVatno() {
        return vatno;
    }

    public void setVatno(String vatno) {
        this.vatno = vatno;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getCardurl() {
        return cardurl;
    }

    public void setCardurl(String cardurl) {
        this.cardurl = cardurl;
    }
}
