package com.ajibadedah.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

/**
 * Created by ajibade on 4/25/17.
 */

public class TrailerAndReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TRAILER = 0;
    private static final int VIEW_TYPE_REVIEWS = 1;

    private final List<Object> listItems;
    private final TrailerClickedListener trailerClickedListener;
    private Context context;


    TrailerAndReviewAdapter(Context context, TrailerClickedListener listener, List<Object> listItems){
        this.context = context;
        this.listItems = listItems;
        this.trailerClickedListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        RecyclerView.ViewHolder holder;
        switch (viewType){
            case VIEW_TYPE_TRAILER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trailer_lists, parent, false);
                holder = new TrailerViewHolder(itemView);
                break;

            case VIEW_TYPE_REVIEWS:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_lists, parent, false);
                holder = new ReviewViewHolder(itemView);
                break;
            default:
                throw new UnsupportedOperationException("Unknown view type:");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_TRAILER:
                TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
                trailerViewHolder.bindView(position);
                break;

            case VIEW_TYPE_REVIEWS:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
                reviewViewHolder.bindView(position);
                break;
            default:
                throw new UnsupportedOperationException("Unknown view type:");
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listItems.get(position) instanceof URL) {
            return VIEW_TYPE_TRAILER;
        } else if (listItems.get(position) instanceof Review) {
            return VIEW_TYPE_REVIEWS;
        }
        return -1;
    }

    interface TrailerClickedListener{
        void onTrailerClicked(URL url);
    }

    private class TrailerViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TrailerViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.trailer_name);
        }

        void bindView(final int position){
            String text = "Trailer" + position + 1;
            textView.setText(text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItems.get(position)instanceof URL){
                        trailerClickedListener.onTrailerClicked((URL) listItems.get(position));
                    }

                }
            });
        }
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView author;
        TextView content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.review_author);
            content = (TextView) itemView.findViewById(R.id.review_content);
        }

        void bindView(int position){
            Review review = (Review) listItems.get(position);
            String text = "Author: " + review.getAuthor();
            author.setText(text);
            content.setText(review.getContent());
        }
    }
}
