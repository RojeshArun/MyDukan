package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 9/8/16.
 */
@SuppressWarnings("serial")
public class Complaint extends Object implements Serializable {

    private String complaintId = "";
    private String subject = "";
    private String message = "";
    private String status = "";
    private long createddate = 0l;
    private String type = "";
    private String comment = "";

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public long getCreateddate() {
        return createddate;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }
}
