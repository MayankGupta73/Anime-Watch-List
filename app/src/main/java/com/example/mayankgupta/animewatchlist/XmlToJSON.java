package com.example.mayankgupta.animewatchlist;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Created by Mayank Gupta on 31-03-2017.
 */

public class XmlToJSON {

    JSONObject parse(String xml){
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }

        return jsonObj;
    }
}
