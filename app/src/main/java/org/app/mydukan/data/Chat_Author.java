package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by Shivu on 22-09-2017.
 */

@SuppressWarnings("serial")
public class Chat_Author implements Serializable {

    private String auther_Name="";
    private String auther_ID="";
    private String  auther_photoUrl="";
    private String  auther_EmailID="";

    public Chat_Author(){

    }

    public String getAuther_Name() {
        return auther_Name;
    }

    public void setAuther_Name(String auther_Name) {
        this.auther_Name = auther_Name;
    }

    public String getAuther_ID() {
        return auther_ID;
    }

    public void setAuther_ID(String auther_ID) {
        this.auther_ID = auther_ID;
    }

    public String getAuther_photoUrl() {
        return auther_photoUrl;
    }

    public void setAuther_photoUrl(String auther_photoUrl) {
        this.auther_photoUrl = auther_photoUrl;
    }

    public String getAuther_EmailID() {
        return auther_EmailID;
    }

    public void setAuther_EmailID(String auther_EmailID) {
        this.auther_EmailID = auther_EmailID;
    }



}
