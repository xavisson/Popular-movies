package com.xavisson.popularmovies.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.xavisson.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by javidelpalacio on 1/5/17.
 */

public class FetchReviewsTask extends AsyncTask<String, Void, String> {

    public static final String LOG_TAG = "FetchReviewsTask";

    public interface AsyncReviewsResponse {

        void taskPostExecute(String reviewsData);

    }

    public AsyncReviewsResponse delegate = null;

    public FetchReviewsTask(AsyncReviewsResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 0) {
            return null;
        }

        String sortBy = strings[0];

        //TODO: Retrofit
        URL reviewsRequestUrl = NetworkUtils.reviewsUrl(sortBy);

        try {
            String jsonReviewsResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewsRequestUrl);

            return jsonReviewsResponse;

        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String reviews) {
        Log.d(LOG_TAG, "reviews: " + reviews);
        if (reviews != null)
            delegate.taskPostExecute(reviews);
    }
}
