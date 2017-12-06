package org.app.mydukan.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 11/2/16.
 */

public class SubCategory extends Object implements Serializable {
    String name;
    String id;
    String order = "-1";
    HashMap<String,Boolean> productlist = new HashMap<>();
    ArrayList<String> productIds = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getOrder() {
        return order;
    }

    public HashMap<String, Boolean> getProductlist() {
        return productlist;
    }

    public ArrayList<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(ArrayList<String> productIds) {
        this.productIds = productIds;
    }
}
