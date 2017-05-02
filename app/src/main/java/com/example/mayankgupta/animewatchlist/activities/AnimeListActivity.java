package com.example.mayankgupta.animewatchlist.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.adapters.AnimeRecyclerAdapter;
import com.example.mayankgupta.animewatchlist.api.AnilistAPI;
import com.example.mayankgupta.animewatchlist.api.AnimeClient;
import com.example.mayankgupta.animewatchlist.fragments.HomeFragment;
import com.example.mayankgupta.animewatchlist.models.AuthObj;
import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnimeListActivity extends AppCompatActivity {

    public static final String TAG = "List";

    RecyclerView listRecycler;
    ArrayList<EntryShort> animeList= new ArrayList<>();
    ProgressBar progressLoader;

    AnilistAPI anilistAPI;
    AnimeClient animeClient;
    String accessToken;
    boolean flagAccess = false;

    String type;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLoader = (ProgressBar) findViewById(R.id.progressLoader);

        listRecycler = (RecyclerView) findViewById(R.id.listRecycler);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        Intent i = getIntent();
        type = i.getStringExtra("type");
        getSupportActionBar().setTitle(type);
        ctx = this;

        anilistAPI = new AnilistAPI(this);
        animeClient = anilistAPI.getAnimeClient();

        if(i.getBooleanExtra("getData",false)){

            if(isConnected()) {
                if (!anilistAPI.ifTokenExists()) {
                    Log.d(TAG, "onCreateView: fetching token");
                    animeClient.getaccessToken(AnilistAPI.clientId, AnilistAPI.clientSecret, AnilistAPI.grantType)
                            .enqueue(new Callback<AuthObj>() {
                                @Override
                                public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                                    accessToken = response.body().getAccessToken();
                                    Log.d(TAG, "onResponse: " + accessToken);
                                    anilistAPI.writeToSharedPref(accessToken);
                                    setListRecycler(type);
                                    if (!flagAccess) {
                                        flagAccess = true;
                                        call.cancel();
                                    }
                                }

                                @Override
                                public void onFailure(Call<AuthObj> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Unable to access data. Check internet connection or try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    accessToken = anilistAPI.getFromSharedPref();
                    Log.d(TAG, "onResponse: " + accessToken);
                    setListRecycler(type);

                }
            }
        }
        else {

            animeList = i.getParcelableArrayListExtra("list");
            AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(this, animeList, "LIST_NEW");
            listRecycler.setAdapter(adapter);
            progressLoader.setVisibility(View.GONE);
        }
    }
    void setListRecycler(String listType){
        HashMap queries = new HashMap<>();
        Calendar now =Calendar.getInstance();

        switch (listType) {
            case "Top Anime":
                queries.put("sort", "score-desc");
                break;
            case "Popular":
                queries.put("sort","popularity-desc");
                break;
            case "Seasonal Chart":
                queries.put("season", HomeFragment.getSeason(now.get(Calendar.MONTH)));
                queries.put("year",HomeFragment.getYear(now.get(Calendar.YEAR)));
                queries.put("sort","score-desc");
                break;
            case "Upcoming":
                queries.put("sort","score-desc");
                String season = HomeFragment.getSeason(now.get(Calendar.MONTH)+3);
                queries.put("season",season);
                int year = season.equals("winter") ? now.get(Calendar.YEAR)+1:now.get(Calendar.YEAR);
                queries.put("year",HomeFragment.getYear(year));
                break;
        }

        /*animeClient.browseAnime(accessToken,queries).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                animeList = response.body();
                if(animeList == null)
                    animeList = new ArrayList<EntryShort>();
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx, animeList, "LIST_NEW");
                listRecycler.setAdapter(adapter);
                progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });*/

        animeClient.browseAnime(accessToken,queries).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                animeList = (ArrayList<EntryShort>) response.body();
                if(animeList == null)
                    animeList = new ArrayList<EntryShort>();
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx, animeList, "LIST_NEW");
                listRecycler.setAdapter(adapter);
                progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });


        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(this, animeList, "LIST_NEW");
        listRecycler.setAdapter(adapter);
        progressLoader.setVisibility(View.GONE);
    }

    boolean isConnected(){
        ConnectivityManager connMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected()){
            return true;
        }
        return false;
    }

}
