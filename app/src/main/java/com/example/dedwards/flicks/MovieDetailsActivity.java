package com.example.dedwards.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dedwards.flicks.models.Config;
import com.example.dedwards.flicks.models.Movie;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailsActivity extends AppCompatActivity {

    // movie to be displayed
    Movie movie;
    Config config;

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

        tvImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View tvImage){
                Intent i = new Intent(tvImage.getContext(), MovieTrailerActivity.class);
                i.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                startActivity(i);
            }
        });

        // retrieve and unwrap movie parcel
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = Parcels.unwrap((getIntent().getParcelableExtra(Config.class.getSimpleName())));
        // logging information to confirm deserialization
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

//        imageUrl = "https://api.themoviedb.org/3/movie/" + movie.id + "/images";

        // set view objects
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

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

    }

    // get movie trailer
//    private void getTrailer(){
//        // create the URL
//        String url = API_BASE_URL + "/movie/" +  Integer.toString(movie.id) + "/videos";
//        // assign request parameters
//        RequestParams params = new RequestParams();
//        params.put(API_KEY_PARAM, getString(R.string.youtube_api_key));
//
//        client.get(url, params, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // load the results from API
//                try {
//                    JSONArray results = response.getJSONArray("results");
//                    JSONObject object = results.getJSONObject(0);
//                    videoId = object.getString("key");
//                    Log.i(TAG, String.format("VideoId is %s", videoId));
//                } catch (JSONException e) {
//                    logError("Failed to parse now playing movie trailer", e, true);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                logError("Failed to get movie data from Videos endpoint", throwable, true);
//            }
//        });
//    }
//
//    // handle errors, log and report to user
//    private void logError(String message, Throwable error, boolean alertUser){
//        // log the error
//        Log.e(TAG, message, error);
//        // report to the user
//        if (alertUser) {
//            // show a long toast with the error message
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//    }
}
