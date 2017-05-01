package com.xavisson.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xavisson.popularmovies.adapters.MoviesAdapter;
import com.xavisson.popularmovies.data.Movie;
import com.xavisson.popularmovies.models.Film;
import com.xavisson.popularmovies.network.MoviesApiService;
import com.xavisson.popularmovies.utilities.JSONUtils;
import com.xavisson.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    public static final String EXTRA_MOVIE = "extraMovie";
    private static final int COLUMN_NUMBER_PORTRAIT = 2;
    private static final int COLUMN_NUMBER_LANDSCAPE = 3;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView moviesRecycler;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecycler = (RecyclerView) findViewById(R.id.movies_recycler);

        getPopularMovies();

    }

    private void initRecycler() {

        moviesAdapter = new MoviesAdapter(MainActivity.this, movieList);
        RecyclerView.LayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new GridLayoutManager(getApplicationContext(), COLUMN_NUMBER_PORTRAIT);
        }
        else{
            layoutManager = new GridLayoutManager(getApplicationContext(), COLUMN_NUMBER_LANDSCAPE);
        }
        moviesRecycler.setLayoutManager(layoutManager);
        moviesRecycler.setHasFixedSize(true);
        moviesRecycler.setAdapter(moviesAdapter);
        moviesAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra(EXTRA_MOVIE, movieList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_popular) {
            getPopularMovies();
            return true;
        } else if (itemId == R.id.action_rating) {
            getTopRatedMovies();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getPopularMovies() {
        String sortBy = "popular";
//        new FetchMoviesTask().execute(sortBy);

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());

                // Do anything with response here

                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.themoviedb.org/3")
                .client(client)
                .build();

        MoviesApiService moviesService = retrofit.create(MoviesApiService.class);
        Observable<Film> movies = moviesService.getPopularMovies();

//        movies.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread().subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // cast to retrofit.HttpException to get the response code
//                        if (e instanceof HttpException) {
//                            HttpException response = (HttpException)e;
//                            int code = response.code();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                    }
//                });


    }

    private void getTopRatedMovies() {
        String sortBy = "top_rated";
        new FetchMoviesTask().execute(sortBy);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            String sortBy = strings[0];

            //TODO: librerías Volley y Retrofit
            URL moviesRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

//                String[] simpleJsonMoviesData = OpenWeatherJsonUtils
//                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonMoviesResponse);
//
//                return simpleJsonMoviesData;

                return jsonMoviesResponse;

            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            if (moviesData != null) {

                Log.d(LOG_TAG, "onPost: " + moviesData);
                try {
                    movieList = JSONUtils.getMoviesListFromJSON(MainActivity.this, moviesData);
                    Log.d(LOG_TAG, "onPost_size: " + movieList.size());

                    initRecycler();

                    moviesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "Exception: " + e.getMessage());
                }

            }
        }
    }
}
