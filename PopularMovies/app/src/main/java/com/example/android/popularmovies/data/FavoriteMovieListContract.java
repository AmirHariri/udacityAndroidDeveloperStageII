package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Amir on 9/15/2017.
 */

public class FavoriteMovieListContract {
    /* Add content provider constants to the Contract
     Clients need to know how to access the favorite movies data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the tasks directory
        4) Content URI for data in the TaskEntry class
      */

    // The authority, which is how the code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favoritelist" directory
    public static final String PATH_FAVORITELIST = "favoritelist";

    /* TaskEntry is an inner class that defines the contents of the task table */
    public static final class FavoriteMovieListEntry implements BaseColumns {
        // FavoriteMovieListEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITELIST).build();




        public static final String TABLE_NAME = "favoritelist";
        public static final String COLUMN_MOVIE_TITLE = "name";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releasedate";
        public static final String COLUMN_MOVIE_PLOT_SYNOPSYS = "synopsys";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_YOU_TUBE_KEY = "youtubekey";
        public static final String COLUMN_MOVIE_IMAGE = "image";
    }
}
