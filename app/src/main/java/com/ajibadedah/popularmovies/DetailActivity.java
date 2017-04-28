package com.ajibadedah.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;
import com.ajibadedah.popularmovies.sync.MovieIntentService;
import com.ajibadedah.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, TrailerAndReviewAdapter.TrailerClickedListener {

    public static final String[] PROJECTION = {MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW, MovieEntry.COLUMN_PHOTO_PATH, MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_RELEASE_DATE, MovieEntry.COLUMN_FAVORITE
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_OVERVIEW = 1;
    public static final int INDEX_PHOTO_PATH = 2;
    public static final int INDEX_RATING = 3;
    public static final int INDEX_RELEASE_DATE = 4;
    public static final int INDEX_FAVORITE = 5;

    private static final int ID_DETAIL_LOADER = 13;
    private Uri movieUri;
    private Cursor cursor;

    private TextView titleText;
    private TextView overviewText;
    private TextView ratingText;
    private TextView dateText;
    private ImageView photo;
    private RecyclerView trailerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        movieUri = getIntent().getData();

        titleText = (TextView) findViewById(R.id.list_item_title);
        overviewText = (TextView) findViewById(R.id.list_item_overview);
        ratingText = (TextView) findViewById(R.id.list_item_rating);
        dateText = (TextView) findViewById(R.id.list_item_release_date);
        photo = (ImageView) findViewById(R.id.detail_icon);
        trailerRecyclerView = (RecyclerView) findViewById(R.id.trilar_recyclerview);

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, movieUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
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

        //update menu_fav
        invalidateOptionsMenu();

        //Load trailers
        new TrailerASyncTask().execute();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_fav, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (cursor != null) {
            cursor.moveToFirst();
            int status = cursor.getInt(INDEX_FAVORITE);
            if (status == 0) {
                menu.findItem(R.id.menu_fav).setIcon(R.drawable.ic_star_outline);
            } else {
                menu.findItem(R.id.menu_fav).setIcon(R.drawable.ic_star_fill);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_fav) {
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_MOVIE_ID, movieUri.getLastPathSegment());
            values.put(MovieEntry.COLUMN_TITLE, cursor.getString(INDEX_TITLE));
            values.put(MovieEntry.COLUMN_OVERVIEW, cursor.getString(INDEX_OVERVIEW));
            values.put(MovieEntry.COLUMN_RATING, cursor.getString(INDEX_RATING));
            values.put(MovieEntry.COLUMN_RELEASE_DATE, cursor.getString(INDEX_RELEASE_DATE));
            values.put(MovieEntry.COLUMN_PHOTO_PATH, cursor.getString(INDEX_PHOTO_PATH));

            int status = cursor.getInt(INDEX_FAVORITE);
            if (status == 0){
                values.put(MovieEntry.COLUMN_FAVORITE, 1);
                item.setIcon(R.drawable.ic_star_fill);
                MovieIntentService.insertFavoriteTable(this, MovieEntry.CONTENT_URI_FAVORITE, values);
            } else {
                values.put(MovieEntry.COLUMN_FAVORITE, 0);
                item.setIcon(R.drawable.ic_star_outline);
                Uri favoriteUri = MovieEntry.
                        buildUriForFavoriteWithMovieID(movieUri.getLastPathSegment());
                MovieIntentService.deleteFavoriteTable(this, favoriteUri);
            }

            MovieIntentService.updateMovieFavorite(this, movieUri, values);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClicked(URL url) {
        Uri webpage = Uri.parse(url.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private class TrailerASyncTask extends AsyncTask<Void, Void, ArrayList<Object>>
    {

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            URL videoURL = NetworkUtils.
                    buildUrlForVideos(DetailActivity.this, movieUri.getLastPathSegment());

            URL reviewsURL = NetworkUtils.
                    buildUrlForReview(DetailActivity.this, movieUri.getLastPathSegment());
            final ArrayList<Object> itemList = new ArrayList<>();
            try {
                String json = NetworkUtils.getResponseFromHttpUrl(videoURL);
                addTrailers(itemList, json);

                json = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                addReviews(itemList, json);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> item) {
            super.onPostExecute(item);
            TrailerAndReviewAdapter trailerAdapter =
                    new TrailerAndReviewAdapter(DetailActivity.this, DetailActivity.this, item);
            trailerRecyclerView.setAdapter(trailerAdapter);
            trailerRecyclerView.setLayoutManager(new LinearLayoutManager(
                    DetailActivity.this, LinearLayoutManager.VERTICAL, false));
        }

        void addTrailers(ArrayList<Object> itemList, String json){
            try {
                JSONObject videoJson = new JSONObject(json);
                JSONArray jsonArray = videoJson.getJSONArray("results");
                String videoKey;
                URL youtubeUrl;
                //No more than 2 trailers
                for (int i = 0; i < jsonArray.length() && i < 2; i++){
                    videoKey = jsonArray.getJSONObject(i).getString("key");
                    youtubeUrl = NetworkUtils.
                            buildUrlForYouTubeRequest(DetailActivity.this, videoKey);
                    itemList.add(youtubeUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void addReviews(ArrayList<Object> itemList, String json){
            try {
                JSONObject videoJson = new JSONObject(json);
                JSONArray jsonArray = videoJson.getJSONArray("results");
                String author;
                String content;
                //No more than 2 reviews
                for (int i = 0; i < jsonArray.length() && i < 2; i++){
                    author = jsonArray.getJSONObject(i).getString("author");
                    content = jsonArray.getJSONObject(i).getString("content");
                    itemList.add(new Review(author, content));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
