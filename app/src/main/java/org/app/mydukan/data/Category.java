package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/7/16.
 */

public class Category extends Object implements Serializable {
    String name;
    String id;
    String order = "-1";

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
}
