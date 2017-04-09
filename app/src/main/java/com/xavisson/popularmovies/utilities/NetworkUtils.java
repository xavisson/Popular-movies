package com.xavisson.popularmovies.utilities;

import android.net.Uri;

import com.xavisson.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by javidelpalacio on 2/4/17.
 * These utilities will be used to communicate with the movies database.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_URL = "https://api.themoviedb.org/3/movie/";

    final static String QUERY_PARAM = "q";

    final static String POPULAR_MOVIES = "popular";
    final static String RECENT_MOVIES = "";
    final static String TOP_RATED_MOVIES = "top_rated";
    final static String PARAM_API_KEY = "api_key";

    final static String myAPIKey = BuildConfig.MOVIES_API_KEY;


//    http://api.themoviedb.org/3/movie/popular?api_key=d11cabc7185de71d4cbd15a1033a1a46

    public static URL buildUrl(String sortBy) {

        Uri builtUri = Uri.parse(MOVIES_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, myAPIKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
