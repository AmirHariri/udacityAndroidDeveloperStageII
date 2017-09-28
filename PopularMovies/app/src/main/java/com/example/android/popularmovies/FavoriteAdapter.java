package com.example.android.popularmovies;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import static com.example.android.popularmovies.data.FavoriteMovieListContract.FavoriteMovieListEntry;
/**
 * Created by Amir on 9/18/2017.
 */
class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteMoviViewHolder> {
    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    Context mContext;
    ArrayList<Movie.FavoriteMovie> favoriteMovies = new ArrayList<>();
    private static final String TAG = FavoriteMovieActivity.class.getSimpleName();

    public FavoriteAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public FavoriteMoviViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View favoriteView = inflater.inflate(R.layout.movie_favorite_list_item, parent, false);
        favoriteView.setFocusable(true);
        return new FavoriteMoviViewHolder(favoriteView);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(FavoriteMovieListEntry._ID);
        int movieTitleIndex = mCursor.getColumnIndex(FavoriteMovieListEntry.COLUMN_MOVIE_TITLE);
        int movieReleaseDateIndex = mCursor.getColumnIndex(FavoriteMovieListEntry.COLUMN_MOVIE_RELEASE_DATE);
        int movieRatingIndex = mCursor.getColumnIndex(FavoriteMovieListEntry.COLUMN_MOVIE_RATING);
        int movieSynopsisIndex = mCursor.getColumnIndex(FavoriteMovieListEntry.COLUMN_MOVIE_PLOT_SYNOPSYS);

        String movieTitle = mCursor.getString(movieTitleIndex);
        String releaseDate = mCursor.getString(movieReleaseDateIndex);
        Double rating = mCursor.getDouble(movieRatingIndex);
        String movieSynopsis = mCursor.getString(movieSynopsisIndex);

        holder.mFavoriteTitleView.setText(movieTitle);
        holder.mFavoriteReleaseDateView.setText(releaseDate);
        String stringRating = String .valueOf(rating)+ "/10" ;
        holder.mFavoriteRatingView.setText(stringRating);
        holder.mFavoriteSynopsisView.setText(movieSynopsis);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class FavoriteMoviViewHolder extends RecyclerView.ViewHolder{

        TextView mFavoriteTitleView;
        TextView mFavoriteReleaseDateView;
        TextView mFavoriteRatingView;
        TextView mFavoriteSynopsisView;

        public FavoriteMoviViewHolder(View itemView) {
            super(itemView);
            mFavoriteTitleView = itemView.findViewById(R.id.tv_favorite_title);
            mFavoriteReleaseDateView = itemView.findViewById(R.id.tv_favorite_release_date);
            mFavoriteRatingView = itemView.findViewById(R.id.tv_favorite_rating);
            mFavoriteSynopsisView = itemView.findViewById(R.id.tv_favorite_synopsis);
        }
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
