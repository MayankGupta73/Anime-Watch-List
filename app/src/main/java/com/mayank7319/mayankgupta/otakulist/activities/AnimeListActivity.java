package com.mayank7319.mayankgupta.otakulist.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.mayank7319.mayankgupta.otakulist.BrowseQuery;
import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.SearchQuery;
import com.mayank7319.mayankgupta.otakulist.SeasonalQuery;
import com.mayank7319.mayankgupta.otakulist.adapters.AnimeRecyclerAdapter;
import com.mayank7319.mayankgupta.otakulist.api.AnilistAPI;
import com.mayank7319.mayankgupta.otakulist.api.AnilistApolloClient;
import com.mayank7319.mayankgupta.otakulist.api.AnimeClient;
import com.mayank7319.mayankgupta.otakulist.fragments.HomeFragment;
import com.mayank7319.mayankgupta.otakulist.models.AuthObj;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;
import com.mayank7319.mayankgupta.otakulist.type.MediaSeason;
import com.mayank7319.mayankgupta.otakulist.type.MediaSort;
import com.mayank7319.mayankgupta.otakulist.type.MediaType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnimeListActivity extends AppCompatActivity {

    public static final String TAG = "List";

    RecyclerView listRecycler;
    ArrayList<EntryShort> animeList;
    ProgressBar progressLoader;
    SearchView searchView;
    TextView tvSearchInfo;

    AnilistAPI anilistAPI;
    AnimeClient animeClient;
    AnilistApolloClient anilistClient;
    ApolloClient client;
    String accessToken;
    boolean flagAccess = false;
    boolean searchFlag = false;

    String type;
    Context ctx;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: ");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        if(searchView == null)
        Log.d(TAG, "onCreateOptionsMenu: added searchView");

        if(searchFlag) {
            setSearchView();
            Log.d(TAG, "onCreateOptionsMenu: set searchFlag");
        }
        else searchView.setVisibility(View.INVISIBLE);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Log.d(TAG, "onOptionsItemSelected: clicked");
                //Do stuff
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLoader = (ProgressBar) findViewById(R.id.progressLoader);
        progressLoader.setVisibility(View.VISIBLE);
        tvSearchInfo = (TextView) findViewById(R.id.tvSearchInfo);
        tvSearchInfo.setVisibility(View.INVISIBLE);

        listRecycler = (RecyclerView) findViewById(R.id.listRecycler);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));


        Intent i = getIntent();
        type = i.getStringExtra("type");
        getSupportActionBar().setTitle(type);
        ctx = this;

        anilistAPI = new AnilistAPI(this);
        animeClient = anilistAPI.getAnimeClient();

        anilistClient = new AnilistApolloClient();
        client = anilistClient.getApolloClient();

        if(i.getBooleanExtra("getData",false)){

            if(isConnected()) {
                if (!anilistAPI.ifTokenExists()) {
                    animeClient.getaccessToken(AnilistAPI.clientId, AnilistAPI.clientSecret, AnilistAPI.grantType)
                            .enqueue(new Callback<AuthObj>() {
                                @Override
                                public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                                    accessToken = response.body().getAccessToken();
                                    anilistAPI.writeToSharedPref(accessToken);
                                    if(type == "Search"){
                                        progressLoader.setVisibility(View.INVISIBLE);
                                        tvSearchInfo.setVisibility(View.VISIBLE);
                                        searchFlag = true;
                                    }
                                    else {
                                        setListRecycler(type);
                                        searchFlag = false;
                                    }

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
                    if(type.equals("Search")){
                        progressLoader.setVisibility(View.INVISIBLE);
                        tvSearchInfo.setVisibility(View.VISIBLE);
                        searchFlag = true;
                    }
                    else{
                        searchFlag = false;
                        setListRecycler(type);
                    }
                }
            }
            else
            {
                Toast.makeText(ctx,"Unable to connect to the internet. Try again later.",Toast.LENGTH_SHORT).show();
            }
        }
        else {

            animeList = i.getParcelableArrayListExtra("list");
            AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(this, animeList, "LIST_NEW");
            listRecycler.setAdapter(adapter);
            progressLoader.setVisibility(View.GONE);
        }

    }

    void setSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                progressLoader.setVisibility(View.VISIBLE);
                if(query.isEmpty()){
                    Toast.makeText(ctx,"Enter a query",Toast.LENGTH_SHORT).show();
                    progressLoader.setVisibility(View.INVISIBLE);
                    return true;
                }


                SearchQuery searchQuery = SearchQuery.builder()
                                                     .query(query.trim())
                                                     .type(MediaType.ANIME)
                                                     .build();

                client.query(searchQuery).enqueue(new ApolloCall.Callback<SearchQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull com.apollographql.apollo.api.Response<SearchQuery.Data> response) {
                        List<SearchQuery.Medium> mediaList = response.data().Page().media();

                        animeList = new ArrayList<EntryShort>();

                        if(mediaList == null || mediaList.isEmpty()){
                            Toast.makeText(ctx,"No items match the query",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for(SearchQuery.Medium medium: mediaList){
                            if(medium == null){
                                Log.d(TAG, "onResponse: Medium is Null");
                            }
                            else{
                                Log.d(TAG, "onResponse: Medium "+ medium.toString());
                            }

                            EntryShort entry = new EntryShort();
                            assert medium != null;
                            entry.setId(medium.id());
                            entry.setEpisodes(medium.episodes() != null?medium.episodes() : 0);
                            entry.setEpisodeCount(medium.episodes() != null?medium.episodes() : 0);
                            entry.setPopularity(medium.popularity() != null?medium.popularity() : 0);
                            entry.setTitle(medium.title().romaji() != null ? medium.title().romaji(): "");
                            entry.setType(MediaType.ANIME.name());
                            entry.setStatus(medium.status().name() != null ? medium.status().name(): "");
                            entry.setScore(medium.averageScore() != null?medium.averageScore() : 0);
                            entry.setImage(medium.coverImage().large() != null ? medium.coverImage().large(): "");

                            animeList.add(entry);
                        }
                        AnimeListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateSearchResults();
                            }
                        });

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                });

                /*animeClient.searchAnime(query.trim(), accessToken).enqueue(new Callback<ArrayList<EntryShort>>() {
                    @Override
                    public void onResponse(Call<ArrayList<EntryShort>> call, Response<ArrayList<EntryShort>> response) {
                        animeList = response.body();

                        if(animeList == null) animeList = new ArrayList<EntryShort>();
                        if(animeList.isEmpty()){
                            Toast.makeText(ctx,"No items match the query",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AnimeRecyclerAdapter adapterSearch = new AnimeRecyclerAdapter(ctx,animeList,"LIST_NEW");
                        listRecycler.setAdapter(adapterSearch);
                        tvSearchInfo.setVisibility(View.INVISIBLE);
                        progressLoader.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<EntryShort>> call, Throwable t) {

                    }
                });*/

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    void populateSearchResults(){
        AnimeRecyclerAdapter adapterSearch = new AnimeRecyclerAdapter(ctx,animeList,"LIST_NEW");
        listRecycler.setAdapter(adapterSearch);
        tvSearchInfo.setVisibility(View.INVISIBLE);
        progressLoader.setVisibility(View.INVISIBLE);
    }

    void setSeasonalData(SeasonalQuery seasonalQuery){
        client.query(seasonalQuery).enqueue(new ApolloCall.Callback<SeasonalQuery.Data>() {
            @Override
            public void onResponse(@NotNull com.apollographql.apollo.api.Response<SeasonalQuery.Data> response) {
                List<SeasonalQuery.Medium> mediaList = response.data().Page().media();

                animeList = new ArrayList<EntryShort>();

                if(mediaList == null || mediaList.isEmpty()){
                    Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
                    return;
                }

                for(SeasonalQuery.Medium medium: mediaList){
                    if(medium == null){
                        Log.d(TAG, "onResponse: Medium is Null");
                    }
                    else{
                        Log.d(TAG, "onResponse: Medium "+ medium.toString());
                    }

                    animeList.add(HomeFragment.parseSeasonalApiResponse(medium));
                }
                AnimeListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx, animeList, "LIST_NEW");
                        listRecycler.setAdapter(adapter);
                        progressLoader.setVisibility(View.GONE);
                    }
                });


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    void setBrowseData(BrowseQuery popularQuery){
        client.query(popularQuery).enqueue(new ApolloCall.Callback<BrowseQuery.Data>() {
            @Override
            public void onResponse(@NotNull com.apollographql.apollo.api.Response<BrowseQuery.Data> response) {
                List<BrowseQuery.Medium> mediaList = response.data().Page().media();

                animeList = new ArrayList<EntryShort>();

                if(mediaList == null || mediaList.isEmpty()){
                    Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
                    return;
                }

                for(BrowseQuery.Medium medium: mediaList){
                    if(medium == null){
                        Log.d(TAG, "onResponse: Medium is Null");
                    }
                    else{
                        Log.d(TAG, "onResponse: Medium "+ medium.toString());
                    }

                    animeList.add(HomeFragment.parseBrowseApiResponse(medium));
                }
                AnimeListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(ctx, animeList, "LIST_NEW");
                        listRecycler.setAdapter(adapter);
                        progressLoader.setVisibility(View.GONE);
                    }
                });


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });

    }

    void setListRecycler(String listType){
        HashMap queries = new HashMap<>();
        Calendar now =Calendar.getInstance();
        ArrayList<MediaSort> sort;

        switch (listType) {
            case "Top Anime":
                sort = new ArrayList<MediaSort>();
                sort.add(MediaSort.SCORE_DESC);
                BrowseQuery topQuery = BrowseQuery.builder()
                        .type(MediaType.ANIME)
                        .sort(sort)
                        .build();

                setBrowseData(topQuery);
                break;
            case "Popular":
                sort = new ArrayList<MediaSort>();
                sort.add(MediaSort.POPULARITY_DESC);
                BrowseQuery popularQuery = BrowseQuery.builder()
                        .type(MediaType.ANIME)
                        .sort(sort)
                        .build();

                setBrowseData(popularQuery);
                break;
            case "Seasonal Chart":
                sort= new ArrayList<MediaSort>();
                sort.add(MediaSort.POPULARITY_DESC);
                int year = now.get(Calendar.YEAR);
                Log.d(TAG, "setListRecycler: Now "+ year+ now.get(Calendar.MONTH)+ HomeFragment.getMediaSeason(now.get(Calendar.MONTH)));
                SeasonalQuery seasonalQuery = SeasonalQuery.builder()
                        .season(HomeFragment.getMediaSeason(now.get(Calendar.MONTH)))
                        .seasonYear(year)
                        .type(MediaType.ANIME)
                        .sort(sort)
                        .build();

                setSeasonalData(seasonalQuery);

                break;
            case "Upcoming":
                sort= new ArrayList<MediaSort>();
                sort.add(MediaSort.POPULARITY_DESC);
                MediaSeason season = HomeFragment.getMediaSeason((now.get(Calendar.MONTH)+3)%12);
                year = (now.get(Calendar.MONTH) + 3) >= 11? now.get(Calendar.YEAR) + 1: now.get(Calendar.YEAR);
                Log.d(TAG, "setListRecycler: Upcoming "+ year+ now.get(Calendar.MONTH)+ season);
                SeasonalQuery upcomingQuery = SeasonalQuery.builder()
                        .season(season)
                        .seasonYear(year)
                        .type(MediaType.ANIME)
                        .sort(sort)
                        .build();

                setSeasonalData(upcomingQuery);

                break;
        }


        /*animeClient.browseAnime(accessToken,queries).enqueue(new Callback() {
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
        });*/


       /* AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(this, animeList, "LIST_NEW");
        listRecycler.setAdapter(adapter);
        progressLoader.setVisibility(View.GONE);*/
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
