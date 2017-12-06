package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 9/2/16.
 */
public class Offer implements Serializable {

    private long startdate = 0l;
    private long enddate = 0l;
    private String offeramount = "";

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    public long getStartdate() {
        return startdate;
    }

    public void setEnddate(long enddate) {
        this.enddate = enddate;
    }

    public long getEnddate() {
        return enddate;
    }

    public String getOfferamount() {
        return offeramount;
    }

    public void setOfferamount(String offeramount) {
        this.offeramount = offeramount;
    }
}
