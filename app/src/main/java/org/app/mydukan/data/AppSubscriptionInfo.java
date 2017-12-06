package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by Shivayogi Hiremath on 31/1/17.
 */

public class AppSubscriptionInfo implements Serializable {


    String subscription_DATE = "";
    String subscription_ISVALID = "";
    String subscription_AMOUNT = "";
    String subscription_TXNID = "";
    String subscription_CURRENCY= "";
    String subscription_ORDERID= "";
    String subscription_EXTRAINFO="";
    String subscription_TRIALDAYS="";
    String subscription_TRIALSTARTDATE="";
    String subscription_USERID  = "";
    String subscription_MID = "";
    String subscription_PLAN = "";
    String subscription_EXPIREDATE = "";
    String subscription_DAYS = "";

    public String getSubscription_DAYS() {
        return subscription_DAYS;
    }

    public void setSubscription_DAYS(String subscription_DAYS) {
        this.subscription_DAYS = subscription_DAYS;
    }

    public String getSubscription_EXPIREDATE() {
        return subscription_EXPIREDATE;
    }

    public void setSubscription_EXPIREDATE(String subscription_EXPIREDATE) {
        this.subscription_EXPIREDATE = subscription_EXPIREDATE;
    }


    public String getSubscription_USERID() {
        return subscription_USERID;
    }

    public void setSubscription_USERID(String subscription_USERID) {
        this.subscription_USERID = subscription_USERID;
    }

    public String getSubscription_MID() {
        return subscription_MID;
    }

    public void setSubscription_MID(String subscription_MID) {
        this.subscription_MID = subscription_MID;
    }

    public String getSubscription_PLAN() {
        return subscription_PLAN;
    }

    public void setSubscription_PLAN(String subscription_PLAN) {
        this.subscription_PLAN = subscription_PLAN;
    }

    public String getSubscription_TRIALSTARTDATE() {
        return subscription_TRIALSTARTDATE;
    }

    public void setSubscription_TRIALSTARTDATE(String subscription_TRIALSTARTDATE) {
        this.subscription_TRIALSTARTDATE = subscription_TRIALSTARTDATE;
    }

    public String getSubscription_TRIALDAYS() {
        return subscription_TRIALDAYS;
    }

    public void setSubscription_TRIALDAYS(String subscription_TRIALDAYS) {
        this.subscription_TRIALDAYS = subscription_TRIALDAYS;
    }

    public String getSubscription_DATE() {
        return subscription_DATE;
    }

    public void setSubscription_DATE(String subscription_DATE) {
        this.subscription_DATE = subscription_DATE;
    }

    public String getSubcription_ISVALID() {
        return subscription_ISVALID;
    }

    public void setSubcription_ISVALID(String subcription_ISVALID) {
        this.subscription_ISVALID = subcription_ISVALID;
    }

    public String getSubcription_AMOUNT() {
        return subscription_AMOUNT;
    }

    public void setSubcription_AMOUNT(String subcription_AMOUNT) {
        this.subscription_AMOUNT = subcription_AMOUNT;
    }

    public String getSubcription_TXNID() {
        return subscription_TXNID;
    }

    public void setSubcription_TXNID(String subcription_TXNID) {
        this.subscription_TXNID = subcription_TXNID;
    }

    public String getSubcription_CURRENCY() {
        return subscription_CURRENCY;
    }

    public void setSubcription_CURRENCY(String subcription_CURRENCY) {
        this.subscription_CURRENCY = subcription_CURRENCY;
    }

    public String getSubcription_ORDERID() {
        return subscription_ORDERID;
    }

    public void setSubcription_ORDERID(String subcription_ORDERID) {
        this.subscription_ORDERID = subcription_ORDERID;
    }

    public String getSubcription_EXTRAINFO() {
        return subscription_EXTRAINFO;
    }

    public void setSubcription_EXTRAINFO(String subcription_EXTRAINFO) {
        this.subscription_EXTRAINFO = subcription_EXTRAINFO;
    }


}
