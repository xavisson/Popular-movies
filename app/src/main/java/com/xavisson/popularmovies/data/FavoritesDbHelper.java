package com.xavisson.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by javidelpalacio on 2/5/17.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favoritesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;


    // Constructor
    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +
                FavoritesContract.FavoritesEntry._ID                + " INTEGER PRIMARY KEY, " +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID    + " INTEGER NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH    + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW    + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_BACKDROP_PATH    + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_POPULARITY    + " REAL NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE    + " REAL NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_VOTE_COUNT    + " INTEGER NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE    + " TEXT NOT NULL);"
                ;

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
