package com.example.mayankgupta.animewatchlist.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 18-03-2017.
 */

public class entry {
    private int id;
    private int duration;
    int popularity;

    @SerializedName("total_episodes")
    private int episodes;

    @SerializedName("title_english")
    private String title;

    private String type;
    @SerializedName("airing_status")
    private String status;

    @SerializedName("start_date_fuzzy")
    private String startDate;
    @SerializedName("end_date_fuzzy")
    private String endDate;

    @SerializedName("average_score")
    private float score;

    ArrayList<String> synonyms;
    ArrayList<String> genres;

    @SerializedName("description")
    String synopsis;

    @SerializedName("image_url_lge")
    String image;

    public int getPopularity() {
        return popularity;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getImage() {
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

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate() {
        return formatDate(startDate);
    }

    public String getEndDate() {
        return formatDate(endDate);
    }

    public float getScore() {
        return score;
    }

    public entry(int id, int episodes,int duration,int popularity, String title, ArrayList<String> synonyms, String type, String status, String startDate, String endDate, float score, String synopsis, String image, ArrayList<String> genres) {
        this.id = id;
        this.episodes = episodes;
        this.duration = duration;
        this.popularity = popularity;
        this.title = title;
        this.synonyms = synonyms;
        this.type = type;
        this.status = status;
        this.score = score;
        this.synopsis = synopsis;
        this.image = image;
        this.genres = genres;

        this.startDate = startDate;
        this.endDate = endDate;
    }

    private String formatDate(String date){
        if(date == null)
            return date;
        return date.substring(6,8) + "/" + date.substring(4,6) + "/" + date.substring(0,4);
    }
}
