package com.example.dedwards.flicks;

import android.os.Bundle;
import android.util.Log;

import com.example.dedwards.flicks.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.parceler.Parcels;

import static com.example.dedwards.flicks.MovieDetailsActivity.VIDEO_ID;


public class MovieTrailerActivity extends YouTubeBaseActivity {

    // declare movie object
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        // unwrap movie object from previous activity
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        // retrieve video id from previous activity
        final String videoId = getIntent().getStringExtra(VIDEO_ID);

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
}
