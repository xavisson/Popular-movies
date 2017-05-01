package com.xavisson.popularmovies.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.xavisson.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by javidelpalacio on 1/5/17.
 */

public class FetchTrailersTask extends AsyncTask<String, Void, String> {

    public static final String LOG_TAG = "FetchTrailersTask";

    public interface AsyncTrailersResponse {

        void taskPostExecute(String trailersData);
    }

    public AsyncTrailersResponse delegate = null;

    public FetchTrailersTask(AsyncTrailersResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 0) {
            return null;
        }

        String sortBy = strings[0];

        //TODO: Retrofit
        URL trailersRequestUrl = NetworkUtils.trailersUrl(sortBy);

        try {
            String jsonTrailersResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailersRequestUrl);

            return jsonTrailersResponse;

        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String trailers) {
        if (trailers != null)
            delegate.taskPostExecute(trailers);
    }
}
