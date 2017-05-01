package com.xavisson.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xavisson.popularmovies.R;
import com.xavisson.popularmovies.data.MovieReviewItem;

import java.util.List;

/**
 * Created by javidelpalacio on 1/5/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private static final String LOG_TAG = "ReviewsAdapter";
    private Context context;
    private List<MovieReviewItem> reviewsList;

    private MoviesAdapter.OnItemClickListener listener;


    public ReviewsAdapter(Context context, List<MovieReviewItem> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        MovieReviewItem review = reviewsList.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView author;
        public TextView content;

        public ViewHolder(final View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.review_author);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }
}
