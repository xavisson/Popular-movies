package com.xavisson.popularmovies.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by javidelpalacio on 2/4/17.
 */

public class Movie implements Serializable {

    public final long id;
    public String title;
    public final boolean adult;
    public String overview;
    public String posterPath;
    public String backdropPath;
    public double popularity;
    public double voteAverage;
    public long voteCount;
    public String releaseDate;
    public Bitmap poster;

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
