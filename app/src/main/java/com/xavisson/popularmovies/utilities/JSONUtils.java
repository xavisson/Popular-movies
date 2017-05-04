package com.xavisson.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.xavisson.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javidelpalacio on 2/4/17.
 */

public class JSONUtils {

    private static final String LOG_TAG = "JSONUtils";

    private static final String MOVIES_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE ="title";
    private static final String KEY_ADULT ="adult";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH ="poster_path";
    private static final String KEY_BACKDROP_PATH ="backdrop_path";
    private static final String KEY_POPULARITY ="popularity";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_ORIGINAL_LANGUAGE = "original_language";

    public static List<Movie> getMoviesListFromJSON(Context context, String moviesJsonStr) throws JSONException {

        List<Movie> movieList = new ArrayList<>();

        JSONObject responseJSON = null;
        JSONArray resultsArray = null;

        try {
            responseJSON = new JSONObject(moviesJsonStr);
            resultsArray = responseJSON.getJSONArray(MOVIES_RESULTS);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject movieItem = resultsArray.getJSONObject(i);

            long id = movieItem.getLong(KEY_ID);
            String title = movieItem.getString(KEY_TITLE);
            boolean adult = movieItem.getBoolean(KEY_ADULT);
            String overview = movieItem.getString(KEY_OVERVIEW);
            String posterPath = movieItem.getString(KEY_POSTER_PATH);
            String backdropPath = movieItem.getString(KEY_BACKDROP_PATH);
            double popularity = movieItem.getDouble(KEY_POPULARITY);
            double voteAverage = movieItem.getDouble(KEY_VOTE_AVERAGE);
            long voteCount = movieItem.getLong(KEY_VOTE_COUNT);
            String releaseDate = movieItem.getString(KEY_RELEASE_DATE);

            Movie movie = new Movie(id, title, adult, overview, posterPath, backdropPath,
                    popularity, voteAverage, voteCount, releaseDate);
            movieList.add(movie);
        }
        Log.d(LOG_TAG, "movieList.size: " + movieList.size());

        return movieList;
    }
}
