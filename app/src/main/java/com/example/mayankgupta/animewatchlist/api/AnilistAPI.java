package com.example.mayankgupta.animewatchlist.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.models.AuthObj;
import com.example.mayankgupta.animewatchlist.models.EntryShort;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mayank Gupta on 25-04-2017.
 */

public class AnilistAPI {

    public static final String TAG = "AnimeClient";

    public static final String grantType = "client_credentials";
    public static final String clientSecret = "U1692mDMAZ8Sm5UCPuQI";
    public static final String clientId = "abyss73-fphkm";

    Context ctx;
    String accessToken;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public boolean ifTokenExists(){

        if((!sharedPref.contains("access_token")) ||
                (sharedPref.getLong("expiry_time",0)<=System.currentTimeMillis()) ||
                (sharedPref.getString("access_token",null) == null)) {
            return false;
        }
        else return true;
    }

    public AnilistAPI(Context ctx) {
        this.ctx = ctx;
        sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_name),Context.MODE_PRIVATE);
        editor =  sharedPref.edit();
    }

    public String getAccessToken(final Context ctx){

        if(!ifTokenExists()) {
            getAnimeClient().getaccessToken(clientId, clientSecret, grantType).enqueue(new Callback<AuthObj>() {
                @Override
                public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                    accessToken = response.body().getAccessToken();
                    editor.putString("access_token",accessToken);
                    editor.putLong("expiry_time",System.currentTimeMillis()+(60*60*1000));
                    editor.apply();
                }

                @Override
                public void onFailure(Call<AuthObj> call, Throwable t) {
                    Toast.makeText(ctx,"Error accessing database",Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
                accessToken = sharedPref.getString("access_token",null);
        }
        Log.d(TAG, "getAccessToken: Waiting");
        while (accessToken==null){
        }
        Log.d(TAG, "getAccessToken: "+accessToken);
        return accessToken;
    }

    public String getFromSharedPref(){
        accessToken = sharedPref.getString("access_token",null);
        Log.d(TAG, "getAccessToken: "+accessToken);
        return accessToken;
    }

    public void writeToSharedPref(String token){
        editor.putString("access_token",token);
        editor.putLong("expiry_time",System.currentTimeMillis()+(60*60*1000));
        editor.apply();
    }

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
