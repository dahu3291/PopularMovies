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

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_PHOTO_PATH = "photo_path";
        public static final String COLUMN_FAVORITE = "favorite";
        static final String TABLE_NAME_MOVIE = "movie";
        public static final Uri CONTENT_URI_MOVIE = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME_MOVIE)
                .build();
        static final String TABLE_NAME_FAVORITE = "favorite";
        public static final Uri CONTENT_URI_FAVORITE = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME_FAVORITE)
                .build();

//        public static Uri buildUriForMovieWithMovieID(String movieID) {
//            return CONTENT_URI_MOVIE.buildUpon()
//                    .appendPath(movieID)
//                    .build();
//        }

        public static Uri buildUriForFavoriteWithMovieID(String movieID) {
            return CONTENT_URI_FAVORITE.buildUpon()
                    .appendPath(movieID)
                    .build();
        }
    }
}
