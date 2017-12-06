package org.app.mydukan.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 11/2/16.
 */

public class SupplierGroups {

    HashMap<String,Boolean> groups = new HashMap<>();
    ArrayList<String> groupIds = new ArrayList<>();

    public HashMap<String, Boolean> getGroups() {
        return groups;
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> list) {
        this.groupIds = list;
    }
}
