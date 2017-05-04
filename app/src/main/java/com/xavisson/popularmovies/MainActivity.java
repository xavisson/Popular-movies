package com.xavisson.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xavisson.popularmovies.Network.FetchMoviesTask;
import com.xavisson.popularmovies.adapters.MoviesAdapter;
import com.xavisson.popularmovies.model.Movie;
import com.xavisson.popularmovies.utilities.JSONUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    public static final String EXTRA_MOVIE = "extraMovie";
    private static final int COLUMN_NUMBER_PORTRAIT = 2;
    private static final int COLUMN_NUMBER_LANDSCAPE = 3;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView moviesRecycler;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecycler = (RecyclerView) findViewById(R.id.movies_recycler);

        getPopularMovies();

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
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getPopularMovies() {
        String sortBy = "popular";
        new FetchMoviesTask(new FetchMoviesTask.AsyncMoviesResponse() {
            @Override
            public void taskPostExecute(String moviesData) {
                fillMovies(moviesData);
            }
        }).execute(sortBy);
    }

    private void getTopRatedMovies() {
        String sortBy = "top_rated";
        new FetchMoviesTask(new FetchMoviesTask.AsyncMoviesResponse() {
            @Override
            public void taskPostExecute(String moviesData) {
                fillMovies(moviesData);
            }
        }).execute(sortBy);
    }

    public void fillMovies(String moviesData) {

        try {
            movieList = JSONUtils.getMoviesListFromJSON(MainActivity.this, moviesData);
            Log.d(LOG_TAG, "onPost_size: " + movieList.size());

            initRecycler();

            moviesAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
        }
    }


}
