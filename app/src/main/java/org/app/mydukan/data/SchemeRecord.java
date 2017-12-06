package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 11/17/16.
 */
@SuppressWarnings("serial")
public class SchemeRecord extends Object implements Serializable {

    private String earnings = "";
    private long date = 0;
    private String settledby = "";
    private String voucherno = "";
    private Boolean settled = false;
    private Boolean enrolled = false;
    private SupplierInfo supplierinfo;
    private SchemeInfo schemeinfo;

    public SupplierInfo getSupplierinfo() {
        return supplierinfo;
    }

    public void setSupplierinfo(SupplierInfo supplierinfo) {
        this.supplierinfo = supplierinfo;
    }

    public SchemeInfo getSchemeinfo() {
        return schemeinfo;
    }

    public void setSchemeinfo(SchemeInfo schemeinfo) {
        this.schemeinfo = schemeinfo;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getSettledby() {
        return settledby;
    }

    public void setSettledby(String settledby) {
        this.settledby = settledby;
    }

    public String getVoucherno() {
        return voucherno;
    }

    public void setVoucherno(String voucherno) {
        this.voucherno = voucherno;
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
