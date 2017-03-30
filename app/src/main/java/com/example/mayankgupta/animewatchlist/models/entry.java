package com.example.mayankgupta.animewatchlist.models;

import java.net.URL;

/**
 * Created by Mayank Gupta on 18-03-2017.
 */

public class entry {
    private int id,episodes;
    private String title,synonyms,type,status;
    private String start_date, end_date;
    private float score;

    String synopsis;
    URL image;

    public String getSynopsis() {
        return synopsis;
    }

    public URL getImage() {
        return image;
    }

    public int getId() {
        return id;

    }

    public int getEpisodes() {
        return episodes;
    }

    public String getTitle() {
        return title;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public float getScore() {
        return score;
    }

    public entry(int id, int episodes, String title, String synonyms, String type, String status, String start_date, String end_date, float score, String synopsis, URL image) {
        this.id = id;
        this.episodes = episodes;
        this.title = title;
        this.synonyms = synonyms;
        this.type = type;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.score = score;
        this.synopsis = synopsis;
        this.image = image;
    }
}
