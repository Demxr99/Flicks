package com.example.dedwards.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dedwards.flicks.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.example.dedwards.flicks.MovieListActivity.API_BASE_URL;
import static com.example.dedwards.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    AsyncHttpClient client;
    Movie movie;
    String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        client = new AsyncHttpClient();
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        // temporary test video id -- TODO replace with movie trailer video id
        getTrailer(movie);

        // resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

        // initialize with API key stored in secrets.xml
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
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
                    videoId = results.getString(0);
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
        // report to the user
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
