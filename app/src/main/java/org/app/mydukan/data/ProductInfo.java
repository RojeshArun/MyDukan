package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 11/24/16.
 */
@SuppressWarnings("serial")
public class ProductInfo extends Object implements Serializable {
    private String id = "";
    private String name = "";
    private String category = "";
    private String imei = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
