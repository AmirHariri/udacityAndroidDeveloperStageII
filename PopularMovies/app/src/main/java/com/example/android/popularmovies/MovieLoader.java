package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;



public class MovieLoader extends AsyncTaskLoader<List<Movie>> {


    private String mUrl;
    public MovieLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        } else{
            // Perform the network request, parse the response, and extract a list of movies.
            return QueryUtils.fetchMovieData(mUrl);

        }
    }

}
