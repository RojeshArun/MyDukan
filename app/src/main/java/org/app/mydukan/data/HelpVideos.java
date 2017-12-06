package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by Shivu on 29-03-2017.
 */

public class HelpVideos implements Serializable {
    private String Toshow ;
    private String Video_URL ;
    private String Description;
    private String Title;
    private String  Date;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    public String getToshow() {
        return Toshow;
    }

    public void setToshow(String toshow) {
        Toshow = toshow;
    }

    public String getVideo_URL() {
        return Video_URL;
    }

    public void setVideo_URL(String video_URL) {
        Video_URL = video_URL;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

}
