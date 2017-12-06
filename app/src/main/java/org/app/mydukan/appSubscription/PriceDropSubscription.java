package org.app.mydukan.appSubscription;

import android.content.Context;

import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shivu on 26-02-2017.
 */

public class PriceDropSubscription {

    private boolean isSubscribed = false;
    private Context mContext;
    private String mUserId;
    private AppSubscriptionInfo appSubscriptionInfo;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String subcription_dateString, subcription_plan, subcription_expireDate;
    private String trialStartDate;
    private long subDays;

    public boolean checkSubscription(Context context, User userDetails) {
        mContext = context;
        if (userDetails != null) {
            mUserId = userDetails.getId();
            if ((userDetails.getAppSubscriptionInfo() != null) && (!(userDetails.getAppSubscriptionInfo().getSubscription_DATE().isEmpty()))) {
                appSubscriptionInfo = userDetails.getAppSubscriptionInfo();
                subcription_dateString = appSubscriptionInfo.getSubscription_DATE();
                subcription_plan = appSubscriptionInfo.getSubscription_PLAN();
                subcription_expireDate = appSubscriptionInfo.getSubscription_EXPIREDATE();


                if (subcription_expireDate == null || subcription_expireDate.isEmpty()) {

                    if (subcription_plan.isEmpty() || subcription_plan == null) {
                        if (getDayDifference(subcription_dateString) >= 30) {
                            isSubscribed = false;
                            inValidateSubscription(mContext, "false");
                        } else {
                            isSubscribed = true;
                        }
                    } else if (subcription_plan.equalsIgnoreCase("Yearly") && getDayDifference(subcription_dateString) >= 365) {
                        isSubscribed = false;
                    } else if (subcription_plan.equalsIgnoreCase("Monthly") && getDayDifference(subcription_dateString) >= 30) {
                        isSubscribed = false;
                    } else if (subcription_plan.equalsIgnoreCase("HalfYearly") && getDayDifference(subcription_dateString) >= 180) {
                        isSubscribed = false;
                    } else {
                        isSubscribed = true;
                    }


                } else {
                    if (appSubscriptionInfo.getSubscription_DAYS() != null && !(appSubscriptionInfo.getSubscription_DAYS().isEmpty())) {
                        subDays = Long.parseLong(appSubscriptionInfo.getSubscription_DAYS());
                    }
                    if (subcription_plan.isEmpty() || subcription_plan == null) {
                        if (getDayDifference(subcription_dateString) >= 30) {
                            isSubscribed = false;
                            inValidateSubscription(mContext, "false");
                        } else {
                            isSubscribed = true;
                        }
                    } else if (expDayDifference(subcription_expireDate) <=0) {
                        isSubscribed = false;

                    } else {
                        isSubscribed = true;
                    }
                    //subcription_expireDate;
                }

             /*  if(getDayDifference(subcription_dateString)>=30){
                    isSubscribed=false;
                   inValidateSubscription(mContext, "false");
                }else{
                   isSubscribed=true;
                }*/
            }
        }
        return isSubscribed;
    }

    public String checkDueDays(Context context, User userDetails) {
        mContext = context;
        String Duedays = "";
        String mExpireDate = "";

        if (userDetails != null) {
            mUserId = userDetails.getId();
            if ((userDetails.getAppSubscriptionInfo() != null) && (!(userDetails.getAppSubscriptionInfo().getSubscription_DATE().isEmpty()))) {
                appSubscriptionInfo = userDetails.getAppSubscriptionInfo();
                subcription_dateString = appSubscriptionInfo.getSubscription_DATE();
                subcription_plan = appSubscriptionInfo.getSubscription_PLAN();
                mExpireDate = dateFormatter(appSubscriptionInfo.getSubscription_EXPIREDATE());
                subcription_expireDate = appSubscriptionInfo.getSubscription_EXPIREDATE();

                if (subcription_expireDate == null || subcription_expireDate.isEmpty()) {

                    if (subcription_plan.isEmpty() || subcription_plan == null) {
                        if (getDayDifference(subcription_dateString) >= 30) {
                            isSubscribed = false;
                            inValidateSubscription(mContext, "false");
                        } else {
                            isSubscribed = true;
                        }
                    } else if (subcription_plan.equalsIgnoreCase("Yearly") && (getDayDifference(subcription_dateString) >= 360) && (getDayDifference(subcription_dateString) <= 360)) {
                        isSubscribed = false;
                        if (getDayDifference(subcription_dateString) >= 365) {
                            Duedays = "Your MyDukan Prime Membership is Expired. " + mExpireDate;
                        } else if (getDayDifference(subcription_dateString) >= 360) {
                            long remainingDays = 365 - getDayDifference(subcription_dateString);
                            Duedays = "Your MyDukan Prime Membership will expire in " + remainingDays + " days. " + mExpireDate;
                        }
                    } else if (subcription_plan.equalsIgnoreCase("Monthly") && (getDayDifference(subcription_dateString) >= 25)) {
                        isSubscribed = false;
                        if (getDayDifference(subcription_dateString) >= 30) {
                            Duedays = "Your MyDukan Prime Membership is Expired." + mExpireDate;
                        } else if (getDayDifference(subcription_dateString) >= 25) {
                            long remainingDays = 30 - getDayDifference(subcription_dateString);
                            Duedays = "Your MyDukan Prime Membership will expire in " + remainingDays + " days. " + mExpireDate;
                        }
                    } else if (subcription_plan.equalsIgnoreCase("HalfYearly") && getDayDifference(subcription_dateString) >= 175) {
                        isSubscribed = false;
                        if (getDayDifference(subcription_dateString) >= 180) {
                            Duedays = "Your MyDukan Prime Membership is Expired." + mExpireDate;
                        } else if (getDayDifference(subcription_dateString) >= 175) {
                            long remainingDays = 180 - getDayDifference(subcription_dateString);
                            Duedays = "Your MyDukan Prime Membership will expire in " + remainingDays + " days." + mExpireDate;
                        }
                    } else {
                        // Duedays="You are a MyDukan Subscribed user, Your last Subscription Date is "+ subcription_dateString;
                    }

                } else {
                    if (appSubscriptionInfo.getSubscription_DAYS() != null && !(appSubscriptionInfo.getSubscription_DAYS().isEmpty())) {
                        subDays = Long.parseLong(appSubscriptionInfo.getSubscription_DAYS());
                        long expDays=(expDayDifference(subcription_expireDate));

                        if (subcription_plan.isEmpty() || subcription_plan == null) {
                        if (getDayDifference(subcription_dateString) >= 30) {
                            isSubscribed = false;
                            inValidateSubscription(mContext, "false");
                        } else {
                            isSubscribed = true;
                        }
                    } else if ((expDays<=6)) {

                        if (expDays <= 0) {
                            Duedays = "Your MyDukan Prime Membership is Expired. " + mExpireDate;
                        } else if (expDays <= 6) {
                            Duedays = "Your MyDukan Prime Membership will expire in " + expDays + " days. " + mExpireDate;
                        }
                    } else {
                        // Duedays="You are a MyDukan Subscribed user, Your last Subscription Date is "+ subcription_dateString;
                    }

                }
                }

            }
        }
        return Duedays;
    }

    public long getDayDifference(String dateString) {
        long daydiff = 0;
        Date current_Date = new Date();
        Date mDate = null;//2017-02-03 11:37:18.0
        try {
            mDate = (Date) df.parse(dateString);
            long diff = current_Date.getTime() - mDate.getTime();
            daydiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return daydiff;
    }

    public long expDayDifference(String dateString) {
        long daydiff = 0;
        Date current_Date = new Date();
        Date mDate = null;//2017-02-03 11:37:18.0
        try {
            mDate = (Date) df.parse(dateString);
            long diff =mDate.getTime()- current_Date.getTime();
            daydiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return daydiff;
    }


    public long getDaysRemaining(String dateString) {

        long daydiff = 0;
        Date current_Date = new Date();
        java.util.Date dtt;
        try {
            DateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dtt = formatter_date.parse(dateString);
            //   java.sql.Date ds = new java.sql.Date(dtt.getTime());
            long diff = current_Date.getTime() - dtt.getTime();
            daydiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                SimpleDateFormat formatter_date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH); // its a bug for trial days
                dtt = formatter_date.parse(dateString);
                //  java.sql.Date ds = new java.sql.Date(dtt.getTime());
                long diff = current_Date.getTime() - dtt.getTime();
                daydiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

        }
        return daydiff;
    }


    private void inValidateSubscription(final Context mContext, String status) {

        ApiManager.getInstance(mContext).validateAppSubscriptionInfo(mUserId, status, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    return;
                }
            }

            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
            }
        });
    }

    private String dateFormatter(String mdate) {
        String formated_Date = "";
        String returingText = "";

        Date dDate;
        if (mdate != null && !(mdate.isEmpty())) {
            try {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dDate = df.parse(mdate);
                formated_Date = formatter.format(dDate);
                returingText = "Expiry Date: " + formated_Date;

            } catch (ParseException e) {
                e.printStackTrace();
                returingText = "";
            }
            return returingText;
        } else {
            return formated_Date;
        }

    }
}
