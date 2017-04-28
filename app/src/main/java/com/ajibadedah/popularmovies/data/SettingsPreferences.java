package com.ajibadedah.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.ajibadedah.popularmovies.R;

/**
 * Created by ajibade on 4/26/17
 */

public class SettingsPreferences {

    public static String getPreferredSort(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_sort_key);
        String defaultVal = context.getString(R.string.pref_sort_popular);

        return sp.getString(key, defaultVal);
    }

    public static Uri getQueryUri(Context context) {
        String pSort = getPreferredSort(context);
        if (pSort.equals(context.getString(R.string.pref_sort_popular)) ||
                pSort.equals(context.getString(R.string.pref_sort_top_rated))){
            return MovieContract.MovieEntry.CONTENT_URI_MOVIE;
        }
        if (pSort.equals(context.getString(R.string.pref_sort_favorite))){
            return MovieContract.MovieEntry.CONTENT_URI_FAVORITE;
        }
        return null;
    }



//    public static int getFavoriteState(Context context) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//
//        String key = context.getString(R.string.fav_state);
//        int defaultVal = Integer.valueOf(context.getString(R.string.fav_state_false));
//        return sp.getInt(key, defaultVal);
//    }
//
//    public static void setFavoriteState(Context context, int state) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sp.edit();
//
//        editor.putInt(context.getString(R.string.fav_state), state);
//        editor.apply();
//    }
}
