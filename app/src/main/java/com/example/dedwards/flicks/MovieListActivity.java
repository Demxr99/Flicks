package com.example.dedwards.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.dedwards.flicks.models.Config;
import com.example.dedwards.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MovieListActivity extends AppCompatActivity {
    // constants
    // base URL for API
    public final static String API_BASE_URL= "https://api.themoviedb.org/3";
    // parameter name for API key
    public final static String API_KEY_PARAM= "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    // declare instance fields
    AsyncHttpClient client;
    // list of movies currently playing
    ArrayList<Movie> movies;
    // the recycler view for movies
    RecyclerView rvMovies;
    // the adapter connected to the recycler view
    MovieAdapter adapter;
    // image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // initialize the client
        client = new AsyncHttpClient();
        // initialize the list of movies
        movies = new ArrayList<>();
        // initialize the adapter
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and movie adapter
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // get configuration
        getConfiguration();
    }

    // get list of movies from API
    private void getNowPlaying(){
        // create the URL
        String url = API_BASE_URL + "/movie/now_playing";
        // assign request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results from API
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through results and create Movie objects with data
                    for (int i=0; i<results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        // add each movie to list of movies
                        movies.add(movie);
                        // notify adapter when movie is added to list (last element in list)
                        adapter.notifyItemChanged(movies.size()-1);
                    }

                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get movie data from Now Playing endpoint", throwable, true);
            }
        });
    }

    // get configuration from API
    private void getConfiguration(){
        // create the URL
        String url = API_BASE_URL + "/configuration";
        // assign request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // execute a get request, expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with base URL %s and poster size %s", config.getImageBaseURL(), config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                    // get now playing movie list
                    getNowPlaying();
                } catch (JSONException e){
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

    }

    // get movie trailer
    private void getTrailer(final Movie movie){
        // create the URL
        String url = API_BASE_URL + "/movie/" +  Integer.toString(movie.id) + "/videos";
        // assign request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.youtube_api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results from API
                try {
                    JSONArray results = response.getJSONArray("results");
                    Log.i(TAG, String.format("Loaded movie with id %s", movie.id));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movie trailer", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get movie data from Videos endpoint", throwable, true);
            }
        });
    }

    // handle errors, log and report to user
    private void logError(String message, Throwable error, boolean alertUser){
        // log the error
        Log.e(TAG, message, error);
        // report to the user
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }


}
