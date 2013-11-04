package com.gc.networkinglesson.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by terry on 10/31/13.
 */
public class JSONSubreddit {

    private JSONArray items;

    public JSONSubreddit(String source) {
        try {
            JSONObject root = new JSONObject(source);
            JSONObject data = root.getJSONObject("data");
            items = data.getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<JSONRedditItem> getItemList() {
        return JSONRedditItem.fromJSON(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }

}
