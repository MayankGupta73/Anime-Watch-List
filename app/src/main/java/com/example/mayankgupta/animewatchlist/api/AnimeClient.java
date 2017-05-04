package com.example.mayankgupta.animewatchlist.api;

import com.example.mayankgupta.animewatchlist.models.AuthObj;
import com.example.mayankgupta.animewatchlist.models.EntryShort;
import com.example.mayankgupta.animewatchlist.models.entry;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Mayank Gupta on 25-04-2017.
 */

public interface AnimeClient {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("anime/search/{query}")
    Call<ArrayList<EntryShort>> searchAnime (@Path("query") String animeName, @Query("access_token") String accessToken);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("anime/{id}")
    Call<entry> getAnimeDesc (@Path("id") int id, @Query("access_token") String accessToken);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("browse/anime")
    Call<ArrayList<EntryShort>> browseAnime (@Query("access_token") String accessToken, @QueryMap Map<String,String> params);

    @FormUrlEncoded
    @POST("auth/access_token")
    Call<AuthObj> getaccessToken(@Field("client_id") String clientId, @Field("client_secret") String clientSecret ,@Field("grant_type") String grantType);
}
