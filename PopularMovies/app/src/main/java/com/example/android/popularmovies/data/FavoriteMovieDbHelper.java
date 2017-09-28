package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.FavoriteMovieListContract.FavoriteMovieListEntry;


public class FavoriteMovieDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "favoritemovies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITEMOVIELIST_TABLE = "CREATE TABLE " +
                FavoriteMovieListEntry.TABLE_NAME + " (" +
                FavoriteMovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_PLOT_SYNOPSYS + " TEXT, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                FavoriteMovieListEntry.COLUMN_MOVIE_YOU_TUBE_KEY + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITEMOVIELIST_TABLE);
// TODO will add the Image BULB
        //FavoriteMovieListEntry.COLUMN_MOVIE_IMAGE + "BULB, " +
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieListEntry.TABLE_NAME);
        onCreate(db);
    }
}
