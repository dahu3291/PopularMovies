package com.ajibadedah.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ajibadedah.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ajibade on 4/19/17
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

//    private static final String DISC_MOVIE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_DB_URL = "https://image.tmdb.org/t/p";
//    private static final String QUERY_SORT = "sort_by";
    private static final String QUERY_API = "api_key";

    private static String getPreferredSort(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_sort_key);
        String defaultVal = context.getString(R.string.pref_sort_popular);

        return sp.getString(key, defaultVal);
    }
    public static URL buildUrlForMovies(Context context) {
        Uri weatherQueryUri = Uri.parse(MOVIE_URL + getPreferredSort(context)).buildUpon()
                .appendQueryParameter(QUERY_API, context.getString(R.string.API_KEY))
                .build();

        try {
            URL movieUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + movieUrl);
            return movieUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getImagePath(String path) {
        return IMAGE_DB_URL + "/" + "w185" + path;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
                inputStream = urlConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                String response = null;
                if (hasInput) {
                    response = scanner.next();
                }
                scanner.close();

                return response;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
