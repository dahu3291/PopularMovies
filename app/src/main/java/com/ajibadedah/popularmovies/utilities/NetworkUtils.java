package com.ajibadedah.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ajibadedah.popularmovies.R;
import com.ajibadedah.popularmovies.data.SettingsPreferences;

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
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
//    private static final String QUERY_SORT = "sort_by";
    private static final String QUERY_API = "api_key";

    public static URL buildUrlForMovies(Context context) {
        Uri uri = Uri.parse(MOVIE_URL + SettingsPreferences.getPreferredSort(context)).buildUpon()
                .appendQueryParameter(QUERY_API, context.getString(R.string.API_KEY))
                .build();
        return changeUriToURL(uri);
    }

    public static URL buildUrlForVideos(Context context, String movieId) {
        Uri uri = Uri.parse(MOVIE_URL + movieId + "/" + "videos").buildUpon()
                .appendQueryParameter(QUERY_API, context.getString(R.string.API_KEY))
                .build();

        return changeUriToURL(uri);
    }

    public static URL buildUrlForYouTubeRequest(Context context, String key) {
        Uri uri = Uri.parse(YOUTUBE_URL + key).buildUpon().build();
        return changeUriToURL(uri);
    }

    private static URL changeUriToURL(Uri uri){
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL: " + url);
            return url;
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
