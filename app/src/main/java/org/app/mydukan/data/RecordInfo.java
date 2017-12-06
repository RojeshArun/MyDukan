package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/13/16.
 */

@SuppressWarnings("serial")
public class RecordInfo extends Object implements Serializable {

    String id = "";
    String imei = "";
    String price = "";
    String status = "claim";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
