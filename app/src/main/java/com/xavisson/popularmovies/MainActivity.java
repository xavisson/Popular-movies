package com.xavisson.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xavisson.popularmovies.adapters.MoviesAdapter;
import com.xavisson.popularmovies.data.Movie;
import com.xavisson.popularmovies.utilities.JSONUtils;
import com.xavisson.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final int COLUMN_NUMBER = 2;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView moviesRecycler;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecycler = (RecyclerView) findViewById(R.id.movies_recycler);

        loadMoviesData();

    }

    private  void initRecycler() {

        moviesAdapter = new MoviesAdapter(MainActivity.this, movieList);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, COLUMN_NUMBER);
        moviesRecycler.setLayoutManager(layoutManager);
        moviesRecycler.setHasFixedSize(true);
        moviesRecycler.setAdapter(moviesAdapter);
        moviesAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = movieList.get(position).title;
                Toast.makeText(MainActivity.this, name + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoviesData() {
        String sortBy = "popular";
        new FetchMoviesTask().execute(sortBy);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            String sortBy = strings[0];

            //TODO: librerías Volley y Retrofit
            URL moviesRequestUrl = NetworkUtils.buildUrl(sortBy);
            Log.d(LOG_TAG, "moviesRequestUrl: " + moviesRequestUrl);
            Log.d(LOG_TAG, "static key: " + BuildConfig.MOVIES_API_KEY);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

//                String[] simpleJsonMoviesData = OpenWeatherJsonUtils
//                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonMoviesResponse);
//
//                return simpleJsonMoviesData;

                return jsonMoviesResponse;

            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            if (moviesData != null) {

                Log.d(LOG_TAG, "onPost: " + moviesData);
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
    }
}
