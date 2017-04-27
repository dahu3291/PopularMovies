package com.ajibadedah.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ajibade on 4/25/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{

    private final ArrayList<URL> trailerUrls;
    private Context context;

    private final TrailerClickedListener trailerClickedListener;


    interface TrailerClickedListener{
        void onTrailerClicked(URL url);
    }

    TrailerAdapter(Context context, TrailerClickedListener listener, ArrayList<URL> trailerUrls){
        this.context = context;
        this.trailerUrls = trailerUrls;
        this.trailerClickedListener = listener;
    }
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_lists, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, final int position) {
        String text = "Trailer" + position;
        holder.textView.setText(text);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerClickedListener.onTrailerClicked(trailerUrls.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.trailer_name);
        }
    }
}
