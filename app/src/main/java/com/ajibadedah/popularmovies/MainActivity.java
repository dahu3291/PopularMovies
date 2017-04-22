package com.ajibadedah.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ajibadedah.popularmovies.data.MovieContract.MovieEntry;
import com.ajibadedah.popularmovies.sync.MovieIntentService;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MovieAdapter.ThumbnailClickedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER = 22;

    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyList = (TextView) findViewById(R.id.listview_empty) ;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        movieAdapter = new MovieAdapter(this, this);

        recyclerView.setAdapter(movieAdapter);

        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);

        MovieIntentService.startMovieSync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //resize recyclers gridview depending on orientation
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int thumbnailWidth = Integer.parseInt(getString(R.string.thumbnail_list_width));
        int columns = (int) ((displayMetrics.widthPixels / displayMetrics.density) / thumbnailWidth);

        recyclerView.setLayoutManager(new GridLayoutManager(this, columns, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {MovieEntry.COLUMN_PHOTO_PATH, MovieEntry.COLUMN_MOVIE_ID, MovieEntry.COLUMN_TITLE};
        return new CursorLoader(this, MovieEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0){
            movieAdapter.swapCursor(data);
            showMovies(true);
        } else {
            showMovies(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    @Override
    public void onThumbnailClicked(String movieID) {
        Intent movieDetail = new Intent(MainActivity.this, DetailActivity.class);
        Uri uri = MovieEntry.buildUriWithMovieID(movieID);
        movieDetail.setData(uri);
        startActivity(movieDetail);

    }

    public void showMovies(boolean show){
        if (show){
            recyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
