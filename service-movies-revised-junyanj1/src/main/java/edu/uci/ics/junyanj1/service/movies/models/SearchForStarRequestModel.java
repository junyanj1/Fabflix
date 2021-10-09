package edu.uci.ics.junyanj1.service.movies.models;

public class SearchForStarRequestModel {
    private String name;
    private int birthYear;
    private String movieTitle;
    private int offset;
    private int limit;
    private String direction;
    private String orderby;

    public SearchForStarRequestModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    @Override
    public String toString() {
        return "SearchForStarRequestModel{" +
                "name='" + name + '\'' +
                ", birthYear=" + birthYear +
                ", movieTitle='" + movieTitle + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                ", direction='" + direction + '\'' +
                ", orderby='" + orderby + '\'' +
                '}';
    }
}
