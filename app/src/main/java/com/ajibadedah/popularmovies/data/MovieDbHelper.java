package com.ajibadedah.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by ajibade on 4/19/17
 */

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 3;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME_MOVIE + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " STRING NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " STRING NOT NULL, " +
                MovieEntry.COLUMN_PHOTO_PATH+ " STRING NOT NULL, " +
                MovieEntry.COLUMN_RATING + " STRING NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL);";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME_FAVORITE + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " STRING NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " STRING NOT NULL, " +
                MovieEntry.COLUMN_PHOTO_PATH+ " STRING NOT NULL, " +
                MovieEntry.COLUMN_RATING + " STRING NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_MOVIE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_FAVORITE);
        onCreate(sqLiteDatabase);
    }
}
