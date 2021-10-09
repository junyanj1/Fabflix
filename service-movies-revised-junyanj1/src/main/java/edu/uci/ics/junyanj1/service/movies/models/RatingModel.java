package edu.uci.ics.junyanj1.service.movies.models;

public class RatingModel {
    private String movieId;
    private float rating;
    private int numVotes;

    public RatingModel(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    @Override
    public String toString() {
        return "RatingModel{" +
                "movieId='" + movieId + '\'' +
                ", rating=" + rating +
                ", numVotes=" + numVotes +
                '}';
    }


}
