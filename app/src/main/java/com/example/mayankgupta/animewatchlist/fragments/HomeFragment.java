package com.example.mayankgupta.animewatchlist.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.activities.AnimeListActivity;
import com.example.mayankgupta.animewatchlist.adapters.AnimeRecyclerAdapter;
import com.example.mayankgupta.animewatchlist.api.AnilistAPI;
import com.example.mayankgupta.animewatchlist.api.AnimeClient;
import com.example.mayankgupta.animewatchlist.models.AuthObj;
import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "Anime Home";

    RecyclerView seasonalRecycler, popularRecycler;
    TextView tvExpandSeasonal, tvExpandPopular;
    ArrayList<EntryShort> seasonalList, popularList;
    Context ctx;
    TextView tvSeason;
    Calendar now;
    String accessToken;
    Boolean flagAccess = false;
    AnimeClient animeClient; AnilistAPI anilistAPI;
    HashMap<String,String> queriesSeasonal,queriesPopular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ctx = getContext();
        now = Calendar.getInstance();

        queriesSeasonal = new HashMap<>();
        queriesSeasonal.put("season",getSeason(now.get(Calendar.MONTH)));
        queriesSeasonal.put("year",getYear(now.get(Calendar.YEAR)));
        queriesSeasonal.put("sort","score-desc");

        queriesPopular = new HashMap<>();
        queriesPopular.put("sort","popularity-desc");

        tvSeason = (TextView) rootView.findViewById(R.id.tvSeason);
        String season = getSeason(now.get(Calendar.MONTH)).substring(0,1).toUpperCase()+ getSeason(now.get(Calendar.MONTH)).substring(1);
        tvSeason.setText(season+" "+getYear(now.get(Calendar.YEAR)));
        seasonalRecycler = (RecyclerView) rootView.findViewById(R.id.seasonalRecycler);
        popularRecycler = (RecyclerView) rootView.findViewById(R.id.popularRecycler);
        tvExpandSeasonal = (TextView) rootView.findViewById(R.id.tvExpandSeasonal);
        tvExpandPopular = (TextView) rootView.findViewById(R.id.tvExpandPopular);

        anilistAPI = new AnilistAPI(ctx);
        animeClient = anilistAPI.getAnimeClient();
        Log.d(TAG, "onCreateView: starting token access");
        Log.d(TAG, "onCreateView: "+AnilistAPI.clientId+AnilistAPI.clientSecret+AnilistAPI.grantType);
        if(!anilistAPI.ifTokenExists()) {
            Log.d(TAG, "onCreateView: fetching token");
            animeClient.getaccessToken(AnilistAPI.clientId, AnilistAPI.clientSecret, AnilistAPI.grantType)
                    .enqueue(new Callback<AuthObj>() {
                        @Override
                        public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                            accessToken = response.body().getAccessToken();
                            Log.d(TAG, "onResponse: " + accessToken);
                            anilistAPI.writeToSharedPref(accessToken);
                            if (!flagAccess){
                                flagAccess = true;
                                call.cancel();
                                setDataRecycler();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthObj> call, Throwable t) {

                        }
                    });
        }
        else {
            accessToken = anilistAPI.getFromSharedPref();
            Log.d(TAG, "onResponse: "+accessToken);
            setDataRecycler();
        }

        return rootView;



    }

    void setDataRecycler(){
        animeClient.browseAnime(accessToken,queriesSeasonal).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                seasonalList = response.body();
                if(seasonalList == null) {
                    Log.d(TAG, "onResponse: 0 size");
                    seasonalList = new ArrayList<EntryShort>();
                }
                seasonalRecycler.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx,seasonalList,"LiST_HORIZONTAL");

                seasonalRecycler.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });
        anilistAPI.getAnimeClient().browseAnime(accessToken,queriesPopular).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                popularList = response.body();
                if (popularList == null)
                    popularList = new ArrayList<EntryShort>();

                popularRecycler.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx,popularList,"LiST_HORIZONTAL");

                popularRecycler.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });

        tvExpandSeasonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,AnimeListActivity.class);
                i.putExtra("type","Seasonal Chart");
                i.putParcelableArrayListExtra("list",seasonalList);
                startActivity(i);
            }
        });


        tvExpandPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,AnimeListActivity.class);
                i.putExtra("type","Popular");
                i.putParcelableArrayListExtra("list",popularList);
                startActivity(i);
            }
        });
    }

    String getSeason(int month){
        switch (month){
            case 0:
            case 1:
            case 2: return "winter";
            case 3:
            case 4:
            case 5: return "spring";
            case 6:
            case 7:
            case 8: return "summer";
            case 9:
            case 10:
            case 11: return "fall";
            default: return "spring";
        }
    }

    String getYear(int year){
        return  String.valueOf(year);
    }
}
