package com.ajibadedah.popularmovies.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;
import com.ajibadedah.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MovieIntentService extends IntentService {
    private static final String TAG = MovieIntentService.class.getSimpleName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MOVIE_SYNC = TAG + ".SYNC";

    private static final String ACTION_UPDATE_FAV = TAG + ".UPDATE.FAV";
    private static final String EXTRA_VALUES = TAG + ".ContentValues";

    private static final String ACTION_FAVORITE_INSERT = TAG + "FAVORITE.INSERT";
    private static final String ACTION_FAVORITE_DELETE = TAG + "FAVORITE.DELETE";


    public MovieIntentService() {
        super("MovieIntentService");
    }

    /**
     * Starts this service to perform movie sync. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startMovieSync(Context context) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_MOVIE_SYNC);
        context.startService(intent);
    }

    //update the favorite column in movie table
    public static void updateMovieFavorite(Context context, Uri uri, ContentValues values) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_UPDATE_FAV);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    //insert new row in favorite table
    public static void insertFavoriteTable(Context context, Uri uri, ContentValues values) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FAVORITE_INSERT);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    //insert new row in favorite table
    public static void deleteFavoriteTable(Context context, Uri uri) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FAVORITE_DELETE);
        intent.setData(uri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MOVIE_SYNC.equals(action)) {
                handleActionMovieSync();
            }
            if (ACTION_UPDATE_FAV.equals(action)) {
                ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
                handleActionUpdate(intent.getData(), values);
            }
            if (ACTION_FAVORITE_INSERT.equals(action)) {
                ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
                handleActionFavoriteInsert(intent.getData(), values);
            }
            if (ACTION_FAVORITE_DELETE.equals(action)) {
                handleActionFavoriteDelete(intent.getData());
            }
        }
    }

    private void handleActionUpdate(Uri uri, ContentValues values) {

        getContentResolver().update(uri, values, null, null);
    }

    /**
     * Handle action movie sync in the provided background thread with the provided
     * parameters.
     */
    private void handleActionMovieSync() {
        URL url = NetworkUtils.buildUrlForMovies(this);
        String json;
        String movieID;
        String title;
        String overview;
        String rating;
        String releaseDate;
        String path;

        try {
            if (NetworkUtils.getResponseFromHttpUrl(url) != null) {
                json = NetworkUtils.getResponseFromHttpUrl(url);

                try {
                    JSONObject movieJson = new JSONObject(json);
                    JSONArray jsonArray = movieJson.getJSONArray("results");

                    getContentResolver().delete(MovieEntry.CONTENT_URI_MOVIE, null, null);
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        movieID = jsonArray.getJSONObject(i).getString("id");
                        title = jsonArray.getJSONObject(i).getString("original_title");
                        overview = jsonArray.getJSONObject(i).getString("overview");
                        rating = jsonArray.getJSONObject(i).getString("vote_average");
                        releaseDate = jsonArray.getJSONObject(i).getString("release_date");
                        path = jsonArray.getJSONObject(i).getString("poster_path");

                        values.put(MovieEntry.COLUMN_MOVIE_ID, movieID);
                        values.put(MovieEntry.COLUMN_TITLE, title);
                        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
                        values.put(MovieEntry.COLUMN_RATING, rating);
                        values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                        values.put(MovieEntry.COLUMN_PHOTO_PATH, path);
                        values.put(MovieEntry.COLUMN_FAVORITE, findFavorite(movieID));

                        getContentResolver().insert(MovieEntry.CONTENT_URI_MOVIE, values);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findFavorite(String movieId){
        int isFavorite = 0;
        Cursor cursor = getContentResolver().
                query(MovieEntry.buildUriForFavoriteWithMovieID(movieId),
                new String[]{MovieEntry.COLUMN_MOVIE_ID} , null, null, null);

        if (cursor != null){
            if (cursor.getCount() > 0) {
                isFavorite = 1;
            }
            cursor.close();
        }
        isFavorite = isFavorite;
        int a = isFavorite + 1;
        return isFavorite;
    }

    private void handleActionFavoriteInsert(Uri uri, ContentValues values) {
        getContentResolver().insert(uri, values);
    }

    private void handleActionFavoriteDelete(Uri uri) {
        getContentResolver().delete(uri, null, null);
    }

}
