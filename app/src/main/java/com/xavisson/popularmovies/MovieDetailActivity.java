package com.xavisson.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.xavisson.popularmovies.data.FavoritesContract;
import com.xavisson.popularmovies.model.Movie;
import com.xavisson.popularmovies.model.MovieReview;
import com.xavisson.popularmovies.model.MovieReviewsResults;
import com.xavisson.popularmovies.model.MovieTrailer;
import com.xavisson.popularmovies.model.MovieTrailerResults;

import java.util.ArrayList;
import java.util.List;

import static com.xavisson.popularmovies.MainActivity.EXTRA_MOVIE;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String LOG_TAG = "MovieDetailActivity";
    private static final int FAVS_LOADER_ID = 0;

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
    private FloatingActionButton favoriteButton;

    private List<MovieReview> reviewsList = new ArrayList<>();
    private List<MovieTrailer> trailersList = new ArrayList<>();
    private boolean isFavorite = false;

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
        favoriteButton = (FloatingActionButton) findViewById(R.id.movie_detail_favorite_button);
        favoriteButton.setOnClickListener(this);

        if (getIntent().hasExtra(EXTRA_MOVIE))
            movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

        if (null != movie) {
            fillMovieDetails();
            requestMovieReviews();
            requestMovieTrailers();
        }

        getSupportLoaderManager().initLoader(FAVS_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void fillMovieDetails() {

        String imageURL = "http://image.tmdb.org/t/p/w780" + movie.backdropPath;
        Picasso.with(MovieDetailActivity.this).load(imageURL).into(billboard);

        title.setText(movie.title);
        if (toolbar != null)
            getSupportActionBar().setTitle(movie.title);

        if (null != movie.releaseDate) {
            String releaseString = movie.releaseDate.substring(0, 4);
            releaseDate.setText(releaseString);
        }
        if (null != movie.overview)
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
        trailersAdapter.setOnItemClickListener(new TrailersAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position) {
                String videoKey = trailersList.get(position).getKey();
                openYoutubeVideo(videoKey);
            }
        });

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

    public void openYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void saveFavorite() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, movie.title);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movie.id);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH, movie.posterPath);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW, movie.overview);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_BACKDROP_PATH, movie.backdropPath);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_POPULARITY, movie.popularity);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_VOTE_COUNT, movie.voteCount);
        contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE, movie.releaseDate);

        Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            isFavorite = true;
            favoriteButton.setImageResource(R.drawable.ic_fav_toolbar_on);
        }
    }

    private void deleteFavorite() {

        String stringId = String.valueOf(movie.id);
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);

        getSupportLoaderManager().restartLoader(FAVS_LOADER_ID, null, MovieDetailActivity.this);

        favoriteButton.setImageResource(R.drawable.ic_fav_toolbar_off);
        isFavorite = false;

        MainActivity.updateFavorites = true;
    }

    private void shareTrailer() {

        if ((null != trailersList) && (trailersList.size() > 0)) {

            String videoId = trailersList.get(0).getKey();
            String videoToShare = "http://www.youtube.com/watch?v=" + videoId;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, videoToShare);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
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
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(FAVS_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                shareTrailer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the favs data
            Cursor mFavsData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mFavsData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavsData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                String[] selectionArgs = new String[]{String.valueOf(movie.id)};
                try {
                    return getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                            selectionArgs,
                            FavoritesContract.FavoritesEntry._ID);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mFavsData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > 0) {
            favoriteButton.setImageResource(R.drawable.ic_fav_toolbar_on);
            isFavorite = true;
        } else {
            favoriteButton.setImageResource(R.drawable.ic_fav_toolbar_off);
            isFavorite = false;
        }
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == favoriteButton.getId()) {

            if (!isFavorite) {
                saveFavorite();
            } else {
                deleteFavorite();
            }
        }
    }
}
