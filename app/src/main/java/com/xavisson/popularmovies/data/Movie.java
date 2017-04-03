package com.xavisson.popularmovies.data;

import android.graphics.Bitmap;

/**
 * Created by javidelpalacio on 2/4/17.
 */

public class Movie {

    private final long id;
    public final String title;
    private final boolean adult;
    final String overview;
    public final String posterPath;
    private final String backdropPath;
    final double popularity;
    final double voteAverage;
    private final long voteCount;
    final String releaseDate;
    private Bitmap poster;

    public Movie(long id, String title, boolean adult, String overview, String posterPath,
          String backdropPath, double popularity, double voteAverage, long voteCount,
          String releaseDate)
    {
        this.id = id;
        this.title = title;
        this.adult = adult;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.releaseDate = releaseDate;
    }
}
