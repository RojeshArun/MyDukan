package org.app.mydukan.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.app.mydukan.data.User;


/**
 * Created by Codespeak on 05-07-2016.
 */
public class AppPreference {
    private final String PREF_USER = "user";
    private final String PREF_APP_STATE = "appstate";
    private final String PREF_TOPICS = "notifytopic";


    //This function is used to clear all the preferences.
    public void clearAll(Context context) {
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor oEditor = oSharedPreference.edit();
        oEditor.clear();
        oEditor.commit();
    }

    private void setPreference(Context context, String name, String value) {
        if (context == null)
            return;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor oEditor = oSharedPreference.edit();
        oEditor.putString(name, value);
        oEditor.commit();
    }

    private void setPreference(Context context, String name, boolean value) {
        if (context == null)
            return;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor oEditor = oSharedPreference.edit();
        oEditor.putBoolean(name, value);
        oEditor.commit();
    }

    private void setPreference(Context context, String name, int value) {
        if (context == null)
            return;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor oEditor = oSharedPreference.edit();
        oEditor.putInt(name, value);
        oEditor.commit();
    }

    private String getPreference(Context context, String name, String defaultValue) {
        if (context == null)
            return null;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        return oSharedPreference.getString(name, defaultValue);
    }

    private boolean getPreference(Context context, String name, boolean defaultValue) {
        if (context == null)
            return defaultValue;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        return oSharedPreference.getBoolean(name, defaultValue);
    }

    private int getPreference(Context context, String name, int defaultValue) {
        if (context == null)
            return defaultValue;
        SharedPreferences oSharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        return oSharedPreference.getInt(name, defaultValue);
    }

    //Get and set the current app state.
    public void setAppState(Context context, int val) {
        if (context == null)
            return;
        setPreference(context, PREF_APP_STATE, val);
    }

    public int getAppState(Context context) {
        if (context == null)
            return -1;
        return getPreference(context, PREF_APP_STATE, -1);
    }

    //Get and set of user.
    public void setUser(Context context, User user) {
        if (context == null)
            return;

        Gson gson = new Gson();
        String json = gson.toJson(user);

        setPreference(context, PREF_USER, json);
    }

    public User getUser(Context context) {
        if (context == null)
            return null;
        User user = null;
        String userStr = getPreference(context, PREF_USER, null);
        if(userStr != null){
            Gson gson = new Gson();
            user = gson.fromJson(userStr, User.class);
        }
        return user;
    }

    //Get and set the current app state.
    public void setTopicsRegistered(Context context, boolean val) {
        if (context == null)
            return;
        setPreference(context, PREF_TOPICS, val);
    }

    public Boolean isTopicsRegistered(Context context) {
        if (context == null)
            return false;
        return getPreference(context, PREF_TOPICS, false);
    }
}

