package org.app.mydukan.utils;

import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shivu on 08-05-2017.
 */





    public class CompositionJso extends JSONObject {


    public JSONObject makeJSONObject(String title, String desc, ArrayList<String> imgPath, ArrayList<Resources> imgView) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("title", title);
            obj.put("desc", desc);
            obj.put("imgPath", imgPath);
            obj.put("imgViewPath", imgView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }


}