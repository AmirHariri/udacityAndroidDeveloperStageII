package com.example.android.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MovieAdapter extends ArrayAdapter<Movie> {

    MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        // Get the {@link Movie} object located at this position in the list
        Movie currentMovie = getItem(position);
        ImageView ivPoster = listItemView.findViewById(R.id.movie_poster);
        if (currentMovie != null) {
            Picasso.with(getContext()).load(currentMovie.getImageResourceId())
                    .placeholder(R.drawable.movie_poster_placeholder)
                    .into(ivPoster);
        }
        return listItemView;
    }
}
