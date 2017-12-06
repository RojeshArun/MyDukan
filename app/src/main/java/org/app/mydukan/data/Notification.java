package org.app.mydukan.data;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Notification extends Object implements Serializable {

    private String notificationid = "";
    private String category = "";
    private String notificationtext = "";
    private String message = "";
    private String url = "";
    private Long timestamp = 0l;



    private boolean newNotification = false;


    public String getNotificationId() {
        return notificationid;
    }

    public void setNotificationId(String mynotificationid) {
        this.notificationid = mynotificationid;
    }

    public String getNotificationText() {
        return notificationtext;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public Long getTimestamp() {
        return timestamp;
    }


}
