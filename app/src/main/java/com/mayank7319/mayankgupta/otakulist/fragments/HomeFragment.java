package com.mayank7319.mayankgupta.otakulist.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.activities.AnimeListActivity;
import com.mayank7319.mayankgupta.otakulist.activities.MainActivity;
import com.mayank7319.mayankgupta.otakulist.adapters.AnimeRecyclerAdapter;
import com.mayank7319.mayankgupta.otakulist.api.AnilistAPI;
import com.mayank7319.mayankgupta.otakulist.api.AnimeClient;
import com.mayank7319.mayankgupta.otakulist.models.AuthObj;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "Anime Home";

    RecyclerView seasonalRecycler, popularRecycler, topRecycler;
    TextView tvExpandSeasonal, tvExpandPopular, tvExpandTop;
    Button btnToStats, btnBrowse;
    ArrayList<EntryShort> seasonalList, popularList, topList;
    Context ctx;
    TextView tvSeason;
    Calendar now;
    String accessToken;
    Boolean flagAccess = false;
    AnimeClient animeClient; AnilistAPI anilistAPI;
    HashMap<String,String> queriesSeasonal,queriesPopular,queriesTop;

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

        queriesTop = new HashMap<>();
        queriesTop.put("sort","score-desc");

        tvSeason = (TextView) rootView.findViewById(R.id.tvSeason);
        String season = getSeason(now.get(Calendar.MONTH)).substring(0,1).toUpperCase()+ getSeason(now.get(Calendar.MONTH)).substring(1);
        tvSeason.setText(season+" "+getYear(now.get(Calendar.YEAR)));
        seasonalRecycler = (RecyclerView) rootView.findViewById(R.id.seasonalRecycler);
        popularRecycler = (RecyclerView) rootView.findViewById(R.id.popularRecycler);
        topRecycler = (RecyclerView) rootView.findViewById(R.id.topRecycler);
        tvExpandSeasonal = (TextView) rootView.findViewById(R.id.tvExpandSeasonal);
        tvExpandPopular = (TextView) rootView.findViewById(R.id.tvExpandPopular);
        tvExpandTop = (TextView) rootView.findViewById(R.id.tvExpandTop);
        btnToStats = (Button) rootView.findViewById(R.id.btnToStats);
        btnBrowse = (Button) rootView.findViewById(R.id.btnBrowse);

        anilistAPI = new AnilistAPI(ctx);
        animeClient = anilistAPI.getAnimeClient();
        if(isConnected()) {
            if (!anilistAPI.ifTokenExists()) {
                animeClient.getaccessToken(AnilistAPI.clientId, AnilistAPI.clientSecret, AnilistAPI.grantType)
                        .enqueue(new Callback<AuthObj>() {
                            @Override
                            public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                                accessToken = response.body().getAccessToken();
                                anilistAPI.writeToSharedPref(accessToken);
                                if (!flagAccess) {
                                    flagAccess = true;
                                    call.cancel();
                                    setSeasonalRecycler();
                                }
                            }

                            @Override
                            public void onFailure(Call<AuthObj> call, Throwable t) {
                                Toast.makeText(ctx, "Unable to access data. Check internet connection or try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                accessToken = anilistAPI.getFromSharedPref();
                setSeasonalRecycler();
            }

            btnBrowse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(ctx,AnimeListActivity.class);
                    i.putExtra("type","Search");
                    i.putExtra("getData",true);
                    startActivity(i);
                }
            });

        }
        else {
            Toast.makeText(ctx,"Not connected to the internet. Cannot load data.",Toast.LENGTH_SHORT).show();
        }

        btnToStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new StatsActivity();
                String title = "Statistics";
                ((MainActivity)getActivity()).setFragment(fragment,title);
            }
        });
        return rootView;

    }

    @Override
    public void onDestroyView() {
        seasonalRecycler.setAdapter(null);
        popularRecycler.setAdapter(null);
        topRecycler.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title",MainActivity.DEFAULT_TITLE);
    }

    boolean isConnected(){
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected()){
            return true;
        }
        return false;
    }

    void setSeasonalRecycler(){
        animeClient.browseAnime(accessToken,queriesSeasonal).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                seasonalList = response.body();
                if(seasonalList == null) {
                    seasonalList = new ArrayList<EntryShort>();
                }
                seasonalRecycler.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx,seasonalList,"LiST_HORIZONTAL");

                seasonalRecycler.setAdapter(adapter);

                tvExpandSeasonal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx,AnimeListActivity.class);
                        i.putExtra("type","Seasonal Chart");
                        i.putParcelableArrayListExtra("list",seasonalList);
                        startActivity(i);
                    }
                });

                setPopularRecycler();
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });

    }

    void setPopularRecycler(){
        anilistAPI.getAnimeClient().browseAnime(accessToken,queriesPopular).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                popularList = response.body();
                if (popularList == null)
                    popularList = new ArrayList<EntryShort>();

                popularRecycler.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx,popularList,"LiST_HORIZONTAL");

                popularRecycler.setAdapter(adapter);

                tvExpandPopular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx,AnimeListActivity.class);
                        i.putExtra("type","Popular");
                        i.putParcelableArrayListExtra("list",popularList);
                        startActivity(i);
                    }
                });

                setTopRecycler();
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });
    }

    void setTopRecycler(){
        anilistAPI.getAnimeClient().browseAnime(accessToken,queriesTop).enqueue(new Callback<ArrayList<EntryShort>>() {
            @Override
            public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                topList = response.body();
                if (topList == null)
                    topList = new ArrayList<EntryShort>();

                topRecycler.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx,topList,"LiST_HORIZONTAL");

                topRecycler.setAdapter(adapter);

                tvExpandTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx,AnimeListActivity.class);
                        i.putExtra("type","Top Anime");
                        i.putParcelableArrayListExtra("list",topList);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

            }
        });
    }


    public static String getSeason(int month){
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

    public static String getYear(int year){
        return  String.valueOf(year);
    }
}
