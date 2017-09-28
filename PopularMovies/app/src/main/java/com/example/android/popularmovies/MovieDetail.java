package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieDbHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.android.popularmovies.data.FavoriteMovieListContract.FavoriteMovieListEntry;

import static com.example.android.popularmovies.MainActivity.API_KEY;
import static com.example.android.popularmovies.MainActivity.BASE_URL;
import static com.example.android.popularmovies.MainActivity.BEFORE_API_KEY;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie.MovieTrailer>> {
    private MovieTrailerAdapter trailerAdapter;
    private static final int TRAILER_LOADER_ID = 2;
    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    public int mMovieID;
    final static String BASE_YOUTUBE_URL = "https://youtube.com/watch?v=";
    public CheckBox favoriteCheckBox;
    SharedPreferences sharedPreferences;
    String checkBoxKey;

    String  mMovieTitle = null;
    String mMovieReleaseDate;
    Double mMovieRating;
    String mMovieSynopsis;
    String mMoviePosterUrl;
    ArrayList<String> mMovieReviews;
    ImageView movieThombnail;
    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //get the Movie data from the MainActivity
        Intent movieDetailIntent = getIntent();
        mMovieTitle = movieDetailIntent.getStringExtra("MOVIE_TITLE");
        mMovieReleaseDate = movieDetailIntent.getStringExtra("MOVIE_RELEASE_DATE");
        mMovieRating = movieDetailIntent.getDoubleExtra("MOVIE_RATING", 1.00);
        mMovieSynopsis = movieDetailIntent.getStringExtra("MOVIE_SYNOPSIS");
        mMoviePosterUrl = movieDetailIntent.getStringExtra("MOVIE_THOMBNAIL_URL");
        mMovieID = movieDetailIntent.getIntExtra("MOVIE_ID", 1);
        mMovieReviews = movieDetailIntent.getStringArrayListExtra("MOVIE_REVIEWS");
/*
// following also could be used instead of listView.setFocusable(false) in 104 and 154
        ScrollView scrollView = (ScrollView)findViewById(R.id.sv_detail);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
*/
        final TextView movieTitle = (TextView) findViewById(R.id.tv_movie_title);
        movieTitle.setText(mMovieTitle);

        favoriteCheckBox = (CheckBox) findViewById(R.id.checkbox_favorite);
        loadSavedPreferences(mMovieTitle);
        favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = favoriteCheckBox.isChecked();
                checkBoxKey = mMovieTitle;
                savePreferences(checkBoxKey, value);
                if(((CheckBox) view).isChecked()){
                    Toast.makeText(MovieDetail.this, "Added to your Favorite Movies", Toast.LENGTH_SHORT).show();
                    long newId = addNewMovie();
                    Log.i(LOG_TAG, "New Movie with id of " + newId + " is added");
                }else {
                    Toast.makeText(MovieDetail.this, "Removed from your Favorite Movies", Toast.LENGTH_SHORT).show();
                    boolean isDeleted = removeMovie(mMovieID);
                    Log.i(LOG_TAG, "This movie is removed from data List :" + isDeleted);
                }
            }
        });

        ArrayList<Movie.MovieTrailer> movieTrailers = new ArrayList<>();
        trailerAdapter = new MovieTrailerAdapter(this, movieTrailers);
        ListView trailerListView = (ListView) findViewById(R.id.lv_trailer);
        trailerListView.setFocusable(false);
        trailerListView.setAdapter(trailerAdapter);

        //this is to enable nested ListView in ScrollView
        trailerListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(TRAILER_LOADER_ID, null, this).forceLoad();
        movieThombnail = (ImageView) findViewById(R.id.iv_movie_detail_thombnail);
        Picasso.with(this).load(mMoviePosterUrl)
                .placeholder(R.drawable.movie_poster_placeholder)
                .into(movieThombnail);

        TextView releaseDate = (TextView) findViewById(R.id.tv_release_date);
        releaseDate.setText(mMovieReleaseDate);

        TextView movieRating = (TextView) findViewById(R.id.tv_rating);
        String movieRatingPerMaxRating = String.valueOf(mMovieRating) + "/10";
        movieRating.setText(movieRatingPerMaxRating);

        TextView movieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        movieSynopsis.setText(mMovieSynopsis);

        ArrayAdapter<String> reviewAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mMovieReviews);
        ListView listView = (ListView) findViewById(R.id.lv_reviews);
        listView.setFocusable(false);
        listView.setAdapter(reviewAdapter);
        //this is to enable nested ListView in ScrollView
        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        // Create a DB helper (this will create the DB if run for the first time)
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        //Cursor cursor = getAllMovies();
    }

    private void loadSavedPreferences(String sharedPreferenceKey) {
        sharedPreferences = getApplicationContext().
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean checkBoxValue = sharedPreferences.getBoolean(sharedPreferenceKey, false);
        favoriteCheckBox.setChecked(checkBoxValue);
        Log.i(LOG_TAG, "ChekBox Value is :" + checkBoxValue + " ++ " + "CheckBox Key is : " + sharedPreferenceKey );
    }

    private void savePreferences(String key, boolean value) {
        sharedPreferences = getApplicationContext().
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
        Log.i(LOG_TAG, "ChekBox Value saaved as : " + value +  " ++  "  + "CheckBox Key saved as : " + key );
    }

    @Override
    public Loader<List<Movie.MovieTrailer>> onCreateLoader(int id, Bundle args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BASE_URL);
        stringBuilder.append(String.valueOf(mMovieID) + "/videos");
        stringBuilder.append(BEFORE_API_KEY);
        stringBuilder.append(API_KEY);
        String movieUri = stringBuilder.toString();
        Log.i(LOG_TAG, "MovieTrailer Uri is :" + movieUri);
        return new MovieTrailerLoader(this, movieUri);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie.MovieTrailer>> loader, List<Movie.MovieTrailer> movieTrailers) {
        trailerAdapter.clear();
        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movieTrailers != null && !movieTrailers.isEmpty() ) {
            trailerAdapter.addAll(movieTrailers);
            for(int i=0; i<movieTrailers.size();i++) {
                Log.v(LOG_TAG, "movieTrailers " + i +" Name :" + movieTrailers.get(i).getMovieTrailerName());
                Log.v(LOG_TAG, "movieTrailers " + i +" Key :" + movieTrailers.get(i).getMovieTrailerYouTubeKey());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        trailerAdapter.clear();
    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        favoriteCheckBox.setChecked(sharedPreferences.getBoolean(checkBoxKey, false));
        Log.i(LOG_TAG,"On Start ---------");
    }

*/
    /**
     * Query the mDb and get all guests from the favoritelist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllMovies() {
        return mDb.query(
                FavoriteMovieListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
    private long addNewMovie() {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMovieListEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        cv.put(FavoriteMovieListEntry.COLUMN_MOVIE_ID, mMovieID);
        cv.put(FavoriteMovieListEntry.COLUMN_MOVIE_RELEASE_DATE, mMovieReleaseDate);
        cv.put(FavoriteMovieListEntry.COLUMN_MOVIE_PLOT_SYNOPSYS, mMovieSynopsis);
        cv.put(FavoriteMovieListEntry.COLUMN_MOVIE_RATING, mMovieRating);
        return mDb.insert(FavoriteMovieListEntry.TABLE_NAME, null, cv);

    }
    private boolean removeMovie(int mMovieID) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME
        // and the condition that FavoriteMovieListEntry.COLUMN_MOVIE_ID equals id
        return mDb.delete(FavoriteMovieListEntry.TABLE_NAME,
                FavoriteMovieListEntry.COLUMN_MOVIE_ID + "=" + mMovieID, null) > 0;
    }


}
