package com.xavisson.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.xavisson.popularmovies.data.FavoritesContract.FavoritesEntry.TABLE_NAME;

/**
 * Created by javidelpalacio on 2/5/17.
 */

public class FavoritesContentProvider extends ContentProvider {

    private static final String LOG_TAG = "FavoritesContentProv";

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_MOVIE_ID);

        return uriMatcher;
    }

    private FavoritesDbHelper mFavsDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavsDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFavsDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the tasks directory
            case FAVORITES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES_WITH_MOVIE_ID:
                String movieId = uri.getPathSegments().get(1);
                String mSelection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[] {movieId};

                retCursor = db.query(TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Log.d(LOG_TAG, "insert: " + uri.toString());

        final SQLiteDatabase db = mFavsDbHelper.getWritableDatabase();

        int favorites = sUriMatcher.match(uri);
        Log.d(LOG_TAG, "insert_favorites: " + favorites);

        Uri returnUri;

        switch (favorites) {
            case FAVORITES:

                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = mFavsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favsDeleted;

        switch (match) {

            case FAVORITES_WITH_MOVIE_ID:

                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                favsDeleted = db.delete(TABLE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favsDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
