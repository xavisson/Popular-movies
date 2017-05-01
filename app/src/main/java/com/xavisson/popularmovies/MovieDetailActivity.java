package com.xavisson.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xavisson.popularmovies.data.Movie;

import static com.xavisson.popularmovies.MainActivity.EXTRA_MOVIE;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MovieDetailActivity";
    private Toolbar toolbar;
    private ImageView billboard;
    private TextView title;
    private TextView releaseDate;
    private TextView description;
    private AppCompatRatingBar ratingBar;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar();

        billboard = (ImageView) findViewById(R.id.detail_backdrop);
        title = (TextView) findViewById(R.id.movie_detail_title);
        releaseDate = (TextView) findViewById(R.id.movie_detail_release_date);
        description = (TextView) findViewById(R.id.movie_detail_description);
        ratingBar = (AppCompatRatingBar) findViewById(R.id.movie_detail_ratingbar);

        if (getIntent().hasExtra(EXTRA_MOVIE))
            movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

        if (null != movie)
            fillMovieDetails();

    }

    private void fillMovieDetails() {

        String imageURL = "http://image.tmdb.org/t/p/w780" + movie.backdropPath;
        Picasso.with(MovieDetailActivity.this).load(imageURL).into(billboard);

        title.setText(movie.title);
        if (toolbar != null)
            getSupportActionBar().setTitle(movie.title);

        String releaseString = movie.releaseDate.substring(0,4);
        releaseDate.setText(releaseString);
        description.setText(movie.overview);

        float ratingAvg = (float) movie.voteAverage / 2;
        ratingBar.setRating(ratingAvg);

    }

    private void setupToolbar() {

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(7);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
