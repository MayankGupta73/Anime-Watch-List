package com.example.mayankgupta.animewatchlist.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

/**
 * Created by Mayank Gupta on 22-03-2017.
 */

public class EntryShort implements Parcelable {

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

    protected EntryShort(Parcel in) {
        id = in.readInt();
        popularity = in.readInt();
        episodes = in.readInt();
        episodeCount = in.readInt();
        title = in.readString();
        type = in.readString();
        status = in.readString();
        score = in.readFloat();
        image = in.readString();
    }

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

    public EntryShort(entry newEntry){
        id = newEntry.getId();
        episodes = newEntry.getEpisodes();
        episodeCount = 0;
        popularity = newEntry.getPopularity();
        title = newEntry.getTitle();
        type = newEntry.getType();
        status = newEntry.getStatus();
        score = newEntry.getScore();
        image = newEntry.getImage();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(popularity);
        dest.writeInt(episodes);
        dest.writeInt(episodeCount);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeFloat(score);
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EntryShort> CREATOR = new Parcelable.Creator<EntryShort>() {
        @Override
        public EntryShort createFromParcel(Parcel in) {
            return new EntryShort(in);
        }

        @Override
        public EntryShort[] newArray(int size) {
            return new EntryShort[size];
        }
    };
}
