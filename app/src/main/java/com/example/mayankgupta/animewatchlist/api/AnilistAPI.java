package com.example.mayankgupta.animewatchlist.api;

import com.example.mayankgupta.animewatchlist.models.EntryShort;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mayank Gupta on 25-04-2017.
 */

public class AnilistAPI {

    public static final String grantType = "client_credentials";
    public static final String clientSecret = "U1692mDMAZ8Sm5UCPuQI";
    public static final String clientId = "abyss73-fphkm";

    public AnimeClient getAnimeClient() {
        String BASE_URL = "https://anilist.co/api/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AnimeClient.class);
    }

    //Define auth methods here
}
