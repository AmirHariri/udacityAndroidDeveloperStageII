package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.popularmovies.MovieDetail.BASE_YOUTUBE_URL;

/**
 * Created by Amir on 9/8/2017.
 */

public class MovieTrailerAdapter extends ArrayAdapter<Movie.MovieTrailer> {
    private Context context;
    public MovieTrailerAdapter(Activity context, ArrayList<Movie.MovieTrailer> movieTrailer) {
        super(context, 0, movieTrailer);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        final int mPosition = position;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            Movie.MovieTrailer currentTrailer = getItem(position);
            viewHolder.trailerNameTextView = listItemView.findViewById(R.id.tv_trailer);
            viewHolder.trailerNameTextView.setText(currentTrailer.getMovieTrailerName());
            viewHolder.buttonTrailer = listItemView.findViewById(R.id.button_play_trailer);
            viewHolder.buttonTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String youtubeKey = getItem(mPosition).getMovieTrailerYouTubeKey();
                    StringBuilder stringTrailerBuilder = new StringBuilder();
                    stringTrailerBuilder.append(BASE_YOUTUBE_URL);
                    stringTrailerBuilder.append(youtubeKey);
                    String youtubeTrailerUri = stringTrailerBuilder.toString();
                    Uri youTubeUrl = Uri.parse(youtubeTrailerUri);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, youTubeUrl );
                    context.startActivity(webIntent);
                }
            });
            listItemView.setTag(viewHolder);
        }
        return listItemView;
    }
    private static class ViewHolder{
        TextView  trailerNameTextView;
        ImageButton buttonTrailer;
    }
}
