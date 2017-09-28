package com.example.android.popularmovies;
import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    private static final String CURRENT_POSITION = "position";
    final static String BASE_URL = "http://api.themoviedb.org/3/movie/";
    //TODO: add your API key Here
    final static String API_KEY = "add your API key Here";
    final static String BEFORE_API_KEY = "?api_key=";
    SharedPreferences sharedPrefs;
    private MovieAdapter movieAdapter;
    private String orderBy;
    private GridView moviesGridView;
    private int mPosition;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        ArrayList<Movie> movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);
        moviesGridView = (GridView) findViewById(R.id.grid_view_movies);
        moviesGridView.setAdapter(movieAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this).forceLoad();

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //update the position
                mPosition = position;
                // Find the current movie that was clicked on
                Movie currentMovie = movieAdapter.getItem(position);
                //Find Movie Information for the current movie clicked
                String movieTitle = currentMovie.getOriginalMovieTitle();
                String movieReleaseDate = currentMovie.getReleaseDate();
                double movieRating = currentMovie.getUserRating();
                String movieSynopsis = currentMovie.getPlotSynopsis();
                String movieThombnailUrl = currentMovie.getThombnailResourceId();
                int movieId = currentMovie.getMovieId();
                List<String> movieReviews = currentMovie.getReviews();
                Movie.MovieTrailer movieTrailer = currentMovie.getMovieTrailer();
                //start the activity for movie detail screen and send the information to it
                Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetail.class);
                movieDetailIntent.putExtra("MOVIE_TITLE", movieTitle);
                movieDetailIntent.putExtra("MOVIE_RELEASE_DATE", movieReleaseDate);
                movieDetailIntent.putExtra("MOVIE_RATING", movieRating);
                movieDetailIntent.putExtra("MOVIE_RATING", movieRating);
                movieDetailIntent.putExtra("MOVIE_SYNOPSIS", movieSynopsis);
                movieDetailIntent.putExtra("MOVIE_THOMBNAIL_URL", movieThombnailUrl);
                movieDetailIntent.putExtra("MOVIE_ID", movieId);
                movieDetailIntent.putStringArrayListExtra("MOVIE_REVIEWS", (ArrayList<String>) movieReviews);

                startActivity(movieDetailIntent);
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION)) {
            mPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(BASE_URL);
            stringBuilder.append(orderBy);
            stringBuilder.append(BEFORE_API_KEY);
            stringBuilder.append(API_KEY);
            String movieUri = stringBuilder.toString();
            return new MovieLoader(this, movieUri);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        movieAdapter.clear();
        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the GridView to update.
        if (movies != null && !movies.isEmpty()) {
            movieAdapter.addAll(movies);
        }
        if (mPosition != GridView.INVALID_POSITION) {
            moviesGridView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        movieAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String value = sharedPreferences.getString(key, getString(R.string.settings_order_by_default));
        if (value.equals(getString(R.string.settings_order_top_rated)) ||
                value.equals(getString(R.string.settings_order_most_popular))) {
            if (!value.equals(orderBy)) {
                orderBy = null;
                orderBy = value;
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
            }
        } else if(value.equals(getString(R.string.setting_order_favorite))){
            Intent favoriteIntent = new Intent(this, FavoriteMovieActivity.class);
            startActivity(favoriteIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //Store the position(mPosition) in the bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(CURRENT_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}