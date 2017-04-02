package com.xavisson.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xavisson.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadMoviesData();

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

            }
        }
    }
}
