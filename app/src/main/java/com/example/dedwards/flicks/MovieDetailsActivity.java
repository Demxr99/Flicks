package com.example.dedwards.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dedwards.flicks.models.Config;
import com.example.dedwards.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.dedwards.flicks.MovieListActivity.API_BASE_URL;
import static com.example.dedwards.flicks.MovieListActivity.API_KEY_PARAM;
import static com.example.dedwards.flicks.MovieListActivity.TAG;


public class MovieDetailsActivity extends AppCompatActivity {

    public static final String VIDEO_ID = "videoID";

    // movie to be displayed
    Movie movie;
    Config config;
    AsyncHttpClient client;
    String videoId;

    // views to be displayed
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView tvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve view objects
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        tvImage = findViewById(R.id.tvImage);

        client = new AsyncHttpClient();

        // retrieve and unwrap movie parcel
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = Parcels.unwrap((getIntent().getParcelableExtra(Config.class.getSimpleName())));
        // logging information to confirm deserialization
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set view objects
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvOverview.setMovementMethod(new ScrollingMovementMethod());

        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        String imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        int placeholderId = R.drawable.flicks_backdrop_placeholder;

        // load image using glide
        GlideApp.with(this)
                .load(imageUrl)
                .placeholder(placeholderId)
                .error(R.drawable.flicks_movie_placeholder)
                .transform(new RoundedCornersTransformation(45 , 0))
                .into(tvImage);

        getMovieId();
        setupOnClickListener();
    }

    private void setupOnClickListener(){
        tvImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View tvImage){
                Intent i = new Intent(tvImage.getContext(), MovieTrailerActivity.class);
                i.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                i.putExtra(VIDEO_ID, videoId);
                startActivity(i);
            }
        });
    }

    private void getMovieId(){
        // create the URL
        String url = API_BASE_URL + "/movie/" +  Integer.toString(movie.id) + "/videos";
        // assign request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results from API
                try {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject object = results.getJSONObject(0);
                    videoId = object.getString("key");
                    Log.i(TAG, String.format("VideoId is %s", videoId));
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
