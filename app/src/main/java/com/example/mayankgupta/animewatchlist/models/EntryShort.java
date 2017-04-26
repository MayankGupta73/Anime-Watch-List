package com.example.mayankgupta.animewatchlist.models;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

/**
 * Created by Mayank Gupta on 22-03-2017.
 */

public class EntryShort{

    int id;
    int popularity;

    @SerializedName("total_episodes")
    int episodes;

    int episodeCount;

    @SerializedName("title_english")
    String title;

    String type;

    @SerializedName("airing_status")
    String status;

    @SerializedName("average_score")
    float score;

    @SerializedName("image_url_lge")
    String image;

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

    public String getImage() {
        return image;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public EntryShort(int id, int episodes, int episodeCount,int popularity, String title, String type, String status, float score, String image) {
        this.id = id;
        this.episodes = episodes;
        this.popularity = popularity;
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
