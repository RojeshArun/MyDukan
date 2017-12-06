package org.app.mydukan.data;

/**
 * Created by vaibhavkumar on 28/10/17.
 */


public class ChatData {

    //private variables
    String _date;

    // Empty constructor
    public ChatData(){

    }
    // constructor
    public ChatData(String date){
        this._date = date;
    }

    // getting ID
    public String getDate(){
        return this._date;
    }

    // setting id
    public void setDate(String date) {
        this._date = date;
    }
}