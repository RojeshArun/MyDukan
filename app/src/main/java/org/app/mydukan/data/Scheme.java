package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 9/8/16.
 */
@SuppressWarnings("serial")
public class Scheme extends Object implements Serializable {

    private String schemeId = "";
    private String category = "";
    private String name = "";
    private String description = "";
    private String url = "";
    private long startdate = 0l;
    private long enddate = 0l;
    private boolean hasEnrolled = false;

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public long getEnddate() {
        return enddate;
    }

    public long getStartdate() {
        return startdate;
    }

    public boolean isHasEnrolled() {
        return hasEnrolled;
    }

    public void setHasEnrolled(boolean hasEnrolled) {
        this.hasEnrolled = hasEnrolled;
    }
}
