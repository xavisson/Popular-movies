package com.xavisson.popularmovies.network;

import com.xavisson.popularmovies.models.Film;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by javidelpalacio on 29/4/17.
 */

public interface MoviesApiService {

    @GET("movie/popular")
    Observable<Film> getPopularMovies();
}
