package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/11/16.
 */

@SuppressWarnings("serial")
public class OrderInfo extends Object implements Serializable {

    private String comment = "";
    private String executionstatus = "";
    private String status = "";
    private String totalamount = "0";
    private long timestamp = 0l;


    public String getExecutionstatus() {
        return executionstatus;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public String getComment() {
        return comment;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
