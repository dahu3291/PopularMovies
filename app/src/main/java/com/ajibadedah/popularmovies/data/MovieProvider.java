package com.ajibadedah.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by ajibade on 4/19/17
 */

public class MovieProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;
    private UriMatcher matcher = buildMatcher();

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private UriMatcher buildMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.TABLE_NAME, CODE_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.TABLE_NAME + "/*", CODE_MOVIE_WITH_ID);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (matcher.match(uri)) {
            case CODE_MOVIE:
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case CODE_MOVIE_WITH_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing this in popular movies");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Context context = getContext();
        switch (matcher.match(uri)){
            case CODE_MOVIE:
                long i = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if (i > 0 && context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                    return uri;
                }
                break;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int num;
        if (null == selection) selection = "1";

        switch (matcher.match(uri)) {

            case CODE_MOVIE:
                num = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (num != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return num;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,String selection, String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int index;

        switch (matcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                index = db.update(MovieEntry.TABLE_NAME, contentValues,selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (index > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return index;
    }
}
