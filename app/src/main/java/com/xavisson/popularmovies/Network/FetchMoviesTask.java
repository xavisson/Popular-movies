package com.xavisson.popularmovies.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.xavisson.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by javidelpalacio on 1/5/17.
 */

public class FetchMoviesTask extends AsyncTask<String, Void, String> {

    public static final String LOG_TAG = "FetchMoviesTask";

    public interface AsyncMoviesResponse {

        void taskPostExecute(String moviesData);

    }

    public AsyncMoviesResponse delegate = null;

    public FetchMoviesTask(AsyncMoviesResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 0) {
            return null;
        }

        String sortBy = strings[0];

        //TODO: Retrofit
        URL moviesRequestUrl = NetworkUtils.buildUrl(sortBy);

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            return jsonMoviesResponse;

        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String moviesData) {
        if (moviesData != null)
            delegate.taskPostExecute(moviesData);
    }
}