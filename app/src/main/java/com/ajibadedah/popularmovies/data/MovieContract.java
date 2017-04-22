package com.ajibadedah.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ajibade on 4/19/17
 */

public class MovieContract {

    static final String CONTENT_AUTHORITY = "com.ajibadedah.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        static final String TABLE_NAME = "movie";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();


        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_PHOTO_PATH = "photo_path";

        public static Uri buildUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon()
                    .appendPath(movieID)
                    .build();
        }
    }
}
