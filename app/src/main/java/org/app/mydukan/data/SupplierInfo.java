package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/13/16.
 */

@SuppressWarnings("serial")
public class SupplierInfo extends Object implements Serializable {

    private String id = "";
    private String name = "";

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
}
