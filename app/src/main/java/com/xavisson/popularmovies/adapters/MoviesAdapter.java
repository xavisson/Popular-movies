package com.xavisson.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xavisson.popularmovies.R;
import com.xavisson.popularmovies.data.Movie;

import java.util.List;

/**
 * Created by javidelpalacio on 3/4/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private static final String LOG_TAG = "MoviesAdapter";
    private Context context;
    private List<Movie> moviesList;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public MoviesAdapter(Context context, List<Movie> moviesList) {
        this.context = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        Log.d(LOG_TAG, "onBindViewHolder_image: " + movie.posterPath);
        String imageURL = "http://image.tmdb.org/t/p/w780" + movie.posterPath;
        holder.title.setText(movie.title);
        Picasso.with(context).load(imageURL).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.movie_item_billboard);
            title = (TextView) view.findViewById(R.id.movie_item_title);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
        }


    }
}