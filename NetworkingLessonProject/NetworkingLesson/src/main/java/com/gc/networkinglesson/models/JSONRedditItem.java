package com.gc.networkinglesson.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by terry on 10/31/13.
 */
public class JSONRedditItem {

    public String url;
    public String title;
    public String selftext;

    public JSONRedditItem(JSONObject source) {
        try{
            JSONObject data = source.getJSONObject("data");
            this.title = data.getString("title");
            this.url = data.getString("url");
            if (data.has("selftext"))
                this.selftext = data.getString("selftext");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<JSONRedditItem> fromJSON(JSONArray JSONitems) {
        ArrayList<JSONRedditItem> items = new ArrayList<JSONRedditItem>();
        for (int i=0; i < JSONitems.length(); i++) {
            try {
                items.add(new JSONRedditItem(JSONitems.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
