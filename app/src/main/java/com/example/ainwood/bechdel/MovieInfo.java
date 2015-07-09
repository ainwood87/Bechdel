package com.example.ainwood.bechdel;

import android.media.Image;

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

    public Image getPoster() {
        return poster;
    }

    public void setPoster(Image poster) {
        this.poster = poster;
    }

    private String title;
    private int score;
    private Image poster;
}
