package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 8/29/16.
 */
@SuppressWarnings("serial")
public class PriceDrop implements Serializable {

    private long startdate = 0l;
    private String dropamount = "";

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    public long getStartdate() {
        return startdate;
    }

    public String getDropamount() {
        return dropamount;
    }

    public void setDropamount(String dropamount) {
        this.dropamount = dropamount;
    }
}

