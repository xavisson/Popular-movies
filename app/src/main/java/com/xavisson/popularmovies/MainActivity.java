package com.xavisson.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xavisson.popularmovies.Network.FetchMoviesTask;
import com.xavisson.popularmovies.adapters.MoviesAdapter;
import com.xavisson.popularmovies.data.FavoritesContract;
import com.xavisson.popularmovies.model.Movie;
import com.xavisson.popularmovies.utilities.JSONUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_BACKDROP_PATH;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_POPULARITY;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE;
import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.COLUMN_VOTE_COUNT;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "MainActivity";
    public static final String EXTRA_MOVIE = "extraMovie";
    public static final String GRID_STATE_KEY = "gridState";
    public static final String STATE_POPULAR = "popular";
    public static final String STATE_RATING = "highestRated";
    public static final String STATE_FAVORITES = "favorites";
    private static final int COLUMN_NUMBER_PORTRAIT = 2;
    private static final int COLUMN_NUMBER_LANDSCAPE = 3;
    private static final int FAVS_LOADER_ID = 0;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView moviesRecycler;
    private MoviesAdapter moviesAdapter;

    private String gridState = STATE_POPULAR;
    private boolean isLoaderInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecycler = (RecyclerView) findViewById(R.id.movies_recycler);

        if (savedInstanceState != null) {
            gridState = savedInstanceState.getString(GRID_STATE_KEY);
        }

        assert gridState != null;
        switch (gridState) {
            case STATE_RATING:
                getTopRatedMovies();
                break;
            case STATE_FAVORITES:
                getFavorites();
                break;
            default:
                getPopularMovies();
                break;
        }


    }

    private void initRecycler() {

        moviesAdapter = new MoviesAdapter(MainActivity.this, movieList);
        RecyclerView.LayoutManager layoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new GridLayoutManager(getApplicationContext(), COLUMN_NUMBER_PORTRAIT);
        }
        else{
            layoutManager = new GridLayoutManager(getApplicationContext(), COLUMN_NUMBER_LANDSCAPE);
        }

        moviesRecycler.setLayoutManager(layoutManager);
        moviesRecycler.setHasFixedSize(true);
        moviesRecycler.setAdapter(moviesAdapter);
        moviesAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra(EXTRA_MOVIE, movieList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_popular) {
            getPopularMovies();
            return true;
        } else if (itemId == R.id.action_rating) {
            getTopRatedMovies();
            return true;
        } else if (itemId == R.id.action_favorites) {
            getFavorites();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getPopularMovies() {

        gridState = STATE_POPULAR;

        String sortBy = "popular";
        new FetchMoviesTask(new FetchMoviesTask.AsyncMoviesResponse() {
            @Override
            public void taskPostExecute(String moviesData) {
                listMovies(moviesData);
            }
        }).execute(sortBy);
    }

    private void getTopRatedMovies() {

        gridState = STATE_RATING;

        String sortBy = "top_rated";
        new FetchMoviesTask(new FetchMoviesTask.AsyncMoviesResponse() {
            @Override
            public void taskPostExecute(String moviesData) {
                listMovies(moviesData);
            }
        }).execute(sortBy);
    }

    private void getFavorites() {

        gridState = STATE_FAVORITES;

        if (!isLoaderInitialized) {
            getSupportLoaderManager().initLoader(FAVS_LOADER_ID, null, this);
            isLoaderInitialized = true;
        } else {
            getSupportLoaderManager().restartLoader(FAVS_LOADER_ID, null, this);
        }
    }

    public void listMovies(String moviesData) {

        try {
            movieList = JSONUtils.getMoviesListFromJSON(MainActivity.this, moviesData);
            displayMovies();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
        }
    }

    public void displayMovies() {

        initRecycler();
        moviesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(GRID_STATE_KEY, gridState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the favs data
            Cursor mFavsData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mFavsData != null) {
                    deliverResult(mFavsData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

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

        movieList.clear();

        if (data.getCount() > 0) {

            while (data.moveToNext()) {

                Movie movie = new Movie();
                movie.setId(data.getInt(data.getColumnIndex(COLUMN_MOVIE_ID)));
                movie.setTitle(data.getString(data.getColumnIndex(COLUMN_MOVIE_TITLE)));
                movie.setPosterPath(data.getString(data.getColumnIndex(COLUMN_POSTER_PATH)));
                movie.setOverview(data.getString(data.getColumnIndex(COLUMN_OVERVIEW)));
                movie.setBackdropPath(data.getString(data.getColumnIndex(COLUMN_BACKDROP_PATH)));
                movie.setPopularity(data.getDouble(data.getColumnIndex(COLUMN_POPULARITY)));
                movie.setVoteAverage(data.getDouble(data.getColumnIndex(COLUMN_VOTE_AVERAGE)));
                movie.setVoteCount(data.getInt(data.getColumnIndex(COLUMN_VOTE_COUNT)));
                movie.setReleaseDate(data.getString(data.getColumnIndex(COLUMN_RELEASE_DATE)));

                movieList.add(movie);
                displayMovies();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
