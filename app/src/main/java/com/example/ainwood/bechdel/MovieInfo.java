package com.example.ainwood.bechdel;

import android.graphics.Bitmap;

/**
 * Created by ainwo_000 on 7/8/2015.
 */
public class MovieInfo {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public long getImdbid() {
        return imdbid;
    }

    public void setImdbid(long imdbid) {
        this.imdbid = imdbid;
    }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }

    MovieInfo() {
        this.imdbid = 0;
        title = "";
        score = 0;
        year = "";
        this.id = 0;
    }
    MovieInfo(MovieInfo other) {
        this.imdbid = other.imdbid;
        this.title = other.title;
        this.score = other.score;
        this.poster = other.poster;
        this.year = other.year;
        this.id = other.id;
    }
    private long imdbid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private String title;
    private int score;
    private Bitmap poster;
    private String year;
}
