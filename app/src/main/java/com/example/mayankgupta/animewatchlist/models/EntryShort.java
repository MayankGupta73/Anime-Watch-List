package com.example.mayankgupta.animewatchlist.models;

import java.net.URL;

/**
 * Created by Mayank Gupta on 22-03-2017.
 */

public class EntryShort{

    int id,episodes,episodeCount;
    String title, type, status;
    float score;
    URL image;

    public int getId() {
        return id;
    }

    public int getEpisodeCount(){
        return episodeCount;
    }

    public int getEpisodes() {
        return episodes;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public float getScore() {
        return score;
    }

    public URL getImage() {
        return image;
    }

    public EntryShort(int id, int episodes,int episodeCount, String title, String type, String status, float score, URL image) {
        this.id = id;
        this.episodes = episodes;
        this.title = title;
        this.type = type;
        this.episodeCount = episodeCount;
        this.status = status;
        this.score = score;
        this.image = image;
    }

    public EntryShort() {
    //Empty constructor for firebase database.
    }
}
