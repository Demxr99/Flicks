package com.example.dedwards.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    // base URL for loading images
    String imageBaseURL;
    // poster size when fetching images
    String posterSize;

    public Config(JSONObject object) throws JSONException{
        JSONObject images = object.getJSONObject("images");
        // get image base url
        imageBaseURL = images.getString("secure_base_url");
        // get poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        // get option at index 3, or fallback on w342
        posterSize = posterSizeOptions.optString(3, "w342");

    }

    // helper method for creating urls
    public String getImageUrl (String size, String path){
        return String.format("%s%s%s", imageBaseURL, size, path);
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
