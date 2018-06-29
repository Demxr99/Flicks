package com.example.dedwards.flicks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.dedwards.flicks.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    // movie to be displayed
    Movie movie;
//    Config config;

    Context context;

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
//        config = Parcels.unwrap(getIntent().getParcelableExtra(Config.class.getSimpleName()));
        // logging information to confirm deserialization
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

//        imageUrl = "https://api.themoviedb.org/3/movie/" + movie.id + "/images";

        // set view objects
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

//        String imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        int placeholderId = R.drawable.flicks_backdrop_placeholder;

        // load image using glide
//        GlideApp.with(context)
//                .load(imageUrl)
//                .placeholder(placeholderId)
//                .error(R.drawable.flicks_movie_placeholder)
//                .transform(new RoundedCornersTransformation(45 , 0))
//                .into(tvImage);


    }
}
