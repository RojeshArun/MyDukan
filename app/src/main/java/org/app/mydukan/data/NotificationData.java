package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 9/30/16.
 */

public class NotificationData {

    String message;
    long timestamp;

    @SuppressWarnings("unused")
    private NotificationData() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
