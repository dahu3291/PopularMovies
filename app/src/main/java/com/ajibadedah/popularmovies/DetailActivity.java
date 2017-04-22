package com.ajibadedah.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;
import com.ajibadedah.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] PROJECTION = {MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW, MovieEntry.COLUMN_PHOTO_PATH, MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_OVERVIEW = 1;
    public static final int INDEX_PHOTO_PATH = 2;
    public static final int INDEX_RATING = 3;
    public static final int INDEX_RELEASE_DATE = 4;

    private static final int ID_DETAIL_LOADER = 13;
    private Uri uri;

    private TextView titleText;
    private TextView overviewText;
    private TextView ratingText;
    private TextView dateText;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        uri = getIntent().getData();

        titleText = (TextView) findViewById(R.id.list_item_title);
        overviewText = (TextView) findViewById(R.id.list_item_overview);
        ratingText = (TextView) findViewById(R.id.list_item_rating);
        dateText = (TextView) findViewById(R.id.list_item_release_date);
        photo = (ImageView) findViewById(R.id.detail_icon);


        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, uri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        String rating = data.getString(INDEX_RATING) + "/10";
        String releaseDate = data.getString(INDEX_RELEASE_DATE);
        releaseDate = releaseDate.substring(0,4);

        titleText.setText(data.getString(INDEX_TITLE));
        overviewText.setText(data.getString(INDEX_OVERVIEW));
        ratingText.setText(rating);
        dateText.setText(releaseDate);

        String imagePath = NetworkUtils.getImagePath(data.getString(INDEX_PHOTO_PATH));
        Picasso.with(this).load(imagePath).into(photo);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
