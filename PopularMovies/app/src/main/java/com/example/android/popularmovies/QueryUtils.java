package com.example.android.popularmovies;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.MainActivity.API_KEY;
import static com.example.android.popularmovies.MainActivity.BASE_URL;
import static com.example.android.popularmovies.MainActivity.BEFORE_API_KEY;

/**
 * Created by Amir on 8/5/2017.
 */
class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }
    /**
     * Query the TMDB dataset and return a list of {@link Movie} objects.
    */
    static List<Movie> fetchMovieData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        return extractFeatureFromJson(jsonResponse);
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000 /* milliseconds */);
            urlConnection.setConnectTimeout(1500 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Movie> extractFeatureFromJson(String movieJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movies = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of Result (or movies).
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            // For each movie in the movieArray, create an {@link Movie} object
            for (int i = 0; i < movieArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                // Extract the value for the key called "original_title"
                String originalMovieTitle = currentMovie.getString("original_title");

                // Extract the value for the key called "overview"
                String overView = currentMovie.getString("overview");

                // Extract the value for the key called "release_date"
                String  releaseDate = currentMovie.getString("release_date");

                //Extract the value for the user rating called "vote_average"
                double averageVote = currentMovie.getDouble("vote_average");

                // Extract the value for the key called "poster_path"
                String posterPathUrl = currentMovie.getString("poster_path");

                // Extract the value for the key called "backdrop_path"
                String backdropPathUrl = currentMovie.getString("backdrop_path");

                //extract the value for the key called "id"
                int movieId = currentMovie.getInt("id");

                //to Fetch Reviews from API using id
                //first create the String URL
                String stringReviewUrl = createReviewUrlString(movieId);
                List<String> movieReviews = fetchMovieReviews(stringReviewUrl);

                // Create a new {@link Movie} object with the title, releaseDate,votes,id,reviews
                // and url from the JSON response.

                Movie movie = new Movie(originalMovieTitle, overView, releaseDate, averageVote,
                        posterPathUrl, backdropPathUrl,movieId,movieReviews);
                movies.add(movie);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movies
        return movies;
    }
    static List<String > fetchMovieReviews(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        return extractReviewsFromJson(jsonResponse);
    }

    private static List<String> extractReviewsFromJson(String reviewJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }
        ArrayList<String> reviews = new ArrayList<>();
        try{
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(reviewJSON);
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of Result (or reviews).
            JSONArray reviewsArray = baseJsonResponse.getJSONArray("results");
            // For each movie in the reviewsArray, create an {@link Movie} object
            for (int i = 0; i < reviewsArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentReview = reviewsArray.getJSONObject(i);

                // Extract the value for the key called "original_title"
                String review = currentReview.getString("content");
                reviews.add(review);
            }


        }catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }
        return reviews;
    }


    static List<Movie.MovieTrailer> fetchYouTubeTrailerInfo(String requestUrl){
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        return extractYouTubeKeyAndNameFromJson(jsonResponse);

    }

    private static List<Movie.MovieTrailer> extractYouTubeKeyAndNameFromJson(String movieTrailerJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieTrailerJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding youTube Keys to
        List<Movie.MovieTrailer> movieTrailers = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieTrailerJSON);
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of Result (or movies).
            JSONArray movieTrailerArray = baseJsonResponse.getJSONArray("results");
            // For each movie in the movieArray, create an {@link Movie} object
            for (int i = 0; i < movieTrailerArray.length(); i++) {
                // Get a single key for position i within the list of movie trailers
                JSONObject currentMovieTrailer = movieTrailerArray.getJSONObject(i);

                // Extract the value for the key called "key"
                String key = currentMovieTrailer.getString("key");
                //movieTrailerYouTubeKeys.add(key);
                String name = currentMovieTrailer.getString("name");
                //movieTrailerNames.add(name);
                Movie.MovieTrailer movieTrailer = new Movie.MovieTrailer(key, name);
                movieTrailers.add(movieTrailer);

            }

        }catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movieTrailer JSON results", e);
        }


        // Return the list of movies
        return movieTrailers;
    }
    public static String createReviewUrlString(int movieID){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BASE_URL);
        stringBuilder.append(String.valueOf(movieID) + "/reviews");
        stringBuilder.append(BEFORE_API_KEY);
        stringBuilder.append(API_KEY);
        String movieUri = stringBuilder.toString();
        return movieUri;
    }


    public static URL createUrl(String  stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
