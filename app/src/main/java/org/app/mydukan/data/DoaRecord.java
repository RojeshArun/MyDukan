package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 11/24/16.
 */
@SuppressWarnings("serial")
public class DoaRecord extends Object implements Serializable {

    private long date = 0;
    private String settledby = "";
    private String voucherno = "";
    private Boolean settled = false;
    private SupplierInfo supplierinfo;
    private ProductInfo productinfo;

    public SupplierInfo getSupplierinfo() {
        return supplierinfo;
    }

    public void setSupplierinfo(SupplierInfo supplierinfo) {
        this.supplierinfo = supplierinfo;
    }

    public ProductInfo getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(ProductInfo productinfo) {
        this.productinfo = productinfo;
    }

    public String getVoucherno() {
        return voucherno;
    }

    public void setVoucherno(String voucherno) {
        this.voucherno = voucherno;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }

    public String getSettledby() {
        return settledby;
    }

    public void setSettledby(String settledby) {
        this.settledby = settledby;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
