package com.xavisson.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xavisson.popularmovies.R;
import com.xavisson.popularmovies.model.MovieTrailer;

import java.util.List;

/**
 * Created by javidelpalacio on 1/5/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private static final String LOG_TAG = "TrailersAdapter";
    private Context context;
    private List<MovieTrailer> trailersList;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public TrailersAdapter(Context context, List<MovieTrailer> moviesList) {
        this.context = context;
        this.trailersList = moviesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieTrailer trailer = trailersList.get(position);

//        https://img.youtube.com/vi/"video id"/0.jpg
        String thumbnailURL = "https://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";

        Picasso.with(context).load(thumbnailURL).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;

        public ViewHolder(final View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.trailer_thumbnail);

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
