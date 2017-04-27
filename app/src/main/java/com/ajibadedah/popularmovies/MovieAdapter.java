package com.ajibadedah.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;
import com.ajibadedah.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ajibade on 4/19/17
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    private Cursor cursor;
    private Context context;

    private final ThumbnailClickedListener thumbnailListener;


    interface ThumbnailClickedListener{
        void onThumbnailClicked(String movieID);
    }

    MovieAdapter(Context context, ThumbnailClickedListener listener){
        this.context = context;
        this.thumbnailListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movies, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (cursor.moveToPosition(position)) {
            int index = cursor.getColumnIndex(MovieEntry.COLUMN_PHOTO_PATH);
            String imagePath = NetworkUtils.getImagePath(cursor.getString(index));
            Picasso.with(context).load(imagePath).fit().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(holder.getAdapterPosition());
                    String movieID = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                    thumbnailListener.onThumbnailClicked(movieID);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (cursor != null){
            return cursor.getCount();
        }
        return 0;
    }

    void swapCursor(Cursor data) {
        if (cursor != null){
            cursor.close();
        }
        cursor = data;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

         ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.list_item_icon);
        }


    }
}
