package com.xavisson.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by javidelpalacio on 2/5/17.
 */

public class FavoritesContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.xavisson.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorites" directory
    public static final String PATH_FAVORITES = "favorites";

    /* FavoritesEntry is an inner class that defines the contents of the Favorites table */
    public static final class FavoritesEntry implements BaseColumns {

        // FavoritesEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


        // Favorites table and column names
        public static final String TABLE_NAME = "favorites";

        // Since FavoritesEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        favorites
         - - - - - - - - - - - - - - - - - - - - - -
        | _id  |    movie_title     |    movie_id   |
         - - - - - - - - - - - - - - - - - - - - - -
        |  1   |        Logan       |     283995    |
         - - - - - - - - - - - - - - - - - - - - - -
        |  2   |     Guardians...   |     233236    |
         - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - -
        | 43   |   Learn guitar     |       2       |
         - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}
