package org.app.mydukan.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arpithadudi on 11/2/16.
 */

public class SupplierBindData extends Object implements Serializable {

    String id = "";
    String name = "";
    Boolean iscart = false;
    ArrayList<String> groupIds = new ArrayList<>();

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

    public boolean isCartEnabled() {
        return iscart;
    }

    public void setCartEnabled(boolean iscart) {
        this.iscart = iscart;
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> list) {
        this.groupIds = list;
    }
}
