package org.app.mydukan.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                String Full_Address = null; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String House_No = null;
                String Area_Name = null;
                String City_Name = null;
                String PostalCode = null;
                String State_Name = null;
                String Country_Name = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);

                    if (addressList.size() == 0) {

                       Handler mHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message message) {
                                // This is where you do your work in the UI thread.
                                // Your worker tells you in the message what to do.
                                Toast.makeText(context, "Please turn on the GPS and try again.", Toast.LENGTH_SHORT).show();
                            }
                        };
                       return;
                      /*  {
                            // And this is how you call it from the worker thread:
                            Message message = mHandler.obtainMessage();
                            message.sendToTarget();
                        }*/
                    }
                    else

                    Full_Address = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    House_No = addressList.get(0).getFeatureName();
                    Area_Name = addressList.get(0).getSubLocality();
                    City_Name = addressList.get(0).getLocality();
                    PostalCode = addressList.get(0).getPostalCode();
                    State_Name = addressList.get(0).getAdminArea();
                    Country_Name = addressList.get(0).getCountryName();

                    result = Full_Address+"\n"+House_No+"\n"+Area_Name+"\n"+City_Name+"\n"+PostalCode+"\n"+State_Name+"\n"+Country_Name;
                   /* if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(addressList.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        sb.append(addressList.get(0).getLocality());
                        sb.append(addressList.get(0).getAdminArea());
                        sb.append(addressList.get(0).getCountryName());
                        sb.append(addressList.get(0).getPostalCode());
                        sb.append(addressList.get(0).getFeatureName());
                        *//*sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());*//*
                        result = sb.toString();


                    }*/

                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\nAddress:\n" + result;
                        bundle.putString("address", result);

                        bundle.putString("latitude", String.valueOf(latitude));
                        bundle.putString("longitude", String.valueOf(longitude));
                        bundle.putString("fulladdress", Full_Address);
                        bundle.putString("area", Area_Name);
                        bundle.putString("city", City_Name);
                        bundle.putString("pincode", PostalCode);
                        bundle.putString("state", State_Name);
                        bundle.putString("country", Country_Name);


                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        bundle.putString("lat", String.valueOf(latitude));
                        bundle.putString("long", String.valueOf(longitude));
                        bundle.putString("fulladdress", Full_Address);
                        bundle.putString("area", Area_Name);
                        bundle.putString("city", City_Name);
                        bundle.putString("pincode", PostalCode);
                        bundle.putString("state", State_Name);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}