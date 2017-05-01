package com.xavisson.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xavisson.popularmovies.Network.FetchReviewsTask;
import com.xavisson.popularmovies.Network.FetchTrailersTask;
import com.xavisson.popularmovies.adapters.ReviewsAdapter;
import com.xavisson.popularmovies.adapters.TrailersAdapter;
import com.xavisson.popularmovies.data.Movie;
import com.xavisson.popularmovies.data.MovieReview;
import com.xavisson.popularmovies.data.MovieReviewsResults;
import com.xavisson.popularmovies.data.MovieTrailer;
import com.xavisson.popularmovies.data.MovieTrailerResults;

import java.util.ArrayList;
import java.util.List;

import static com.xavisson.popularmovies.MainActivity.EXTRA_MOVIE;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MovieDetailActivity";
    private Toolbar toolbar;
    private ImageView billboard;
    private TextView title;
    private TextView releaseDate;
    private TextView description;
    private TextView noReviewsText;
    private TextView noTrailersText;
    private AppCompatRatingBar ratingBar;
    private RecyclerView reviewsRecycler;
    private RecyclerView trailersRecycler;
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private List<MovieReview> reviewsList = new ArrayList<>();
    private List<MovieTrailer> trailersList = new ArrayList<>();

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
        noReviewsText = (TextView) findViewById(R.id.movie_detail_no_reviews_tv);
        noTrailersText = (TextView) findViewById(R.id.movie_detail_no_trailers_tv);
        ratingBar = (AppCompatRatingBar) findViewById(R.id.movie_detail_ratingbar);
        reviewsRecycler = (RecyclerView) findViewById(R.id.movie_detail_reviews_recycler);
        trailersRecycler = (RecyclerView) findViewById(R.id.movie_detail_trailers_recycler);

        if (getIntent().hasExtra(EXTRA_MOVIE))
            movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

        if (null != movie) {
            fillMovieDetails();
            requestMovieReviews();
            requestMovieTrailers();
        }

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

    private void fillReviews(String reviewsData) {

        Gson gson = new Gson();
        MovieReviewsResults reviewResults = gson.fromJson(reviewsData, MovieReviewsResults.class);

        for(MovieReview result : reviewResults.getResults()) {
            reviewsList.add(result);
        }

        reviewsAdapter = new ReviewsAdapter(MovieDetailActivity.this, reviewsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        reviewsRecycler.setLayoutManager(layoutManager);
        reviewsRecycler.setHasFixedSize(true);
        reviewsRecycler.setAdapter(reviewsAdapter);

        if (reviewsList.size() > 0) {
            noReviewsText.setVisibility(View.GONE);
            reviewsRecycler.setVisibility(View.VISIBLE);
        } else {
            noReviewsText.setVisibility(View.VISIBLE);
            reviewsRecycler.setVisibility(View.GONE);
        }
    }

    private void fillTrailers(String trailersData) {

        Gson gson = new Gson();
        MovieTrailerResults trailerResults = gson.fromJson(trailersData, MovieTrailerResults.class);

        for(MovieTrailer result : trailerResults.getResults()) {
            trailersList.add(result);
        }

        trailersAdapter = new TrailersAdapter(MovieDetailActivity.this, trailersList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        trailersRecycler.setLayoutManager(layoutManager);
        trailersRecycler.setHasFixedSize(true);
        trailersRecycler.setAdapter(trailersAdapter);

        if (trailersList.size() > 0) {
            noTrailersText.setVisibility(View.GONE);
            trailersRecycler.setVisibility(View.VISIBLE);
        } else {
            noTrailersText.setVisibility(View.VISIBLE);
            trailersRecycler.setVisibility(View.GONE);
        }
    }

    public void requestMovieReviews() {

        String movieId = String.valueOf(movie.id);
        new FetchReviewsTask(new FetchReviewsTask.AsyncReviewsResponse() {
            @Override
            public void taskPostExecute(String reviewsData) {

                fillReviews(reviewsData);
            }
        }).execute(movieId);
    }

    public void requestMovieTrailers() {

        String movieId = String.valueOf(movie.id);
        new FetchTrailersTask(new FetchTrailersTask.AsyncTrailersResponse() {
            @Override
            public void taskPostExecute(String trailersData) {

                fillTrailers(trailersData);
            }
        }).execute(movieId);
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
