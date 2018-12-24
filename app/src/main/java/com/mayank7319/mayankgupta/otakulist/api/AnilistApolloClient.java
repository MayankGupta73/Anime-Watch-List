package com.mayank7319.mayankgupta.otakulist.api;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

/**
 * Created by Mayank Gupta on 24-12-2018.
 */

public class AnilistApolloClient {
    public AnilistApolloClient() {
    }

    public static final String BASE_URL = "https://graphql.anilist.co";

    public ApolloClient getApolloClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        ApolloClient apolloClient = ApolloClient.builder()
                                                .serverUrl(BASE_URL)
                                                .okHttpClient(okHttpClient)
                                                .build();

        return apolloClient;
    }
}
