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

import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.adapters.AnimeRecyclerAdapter;
import com.mayank7319.mayankgupta.otakulist.api.AnilistAPI;
import com.mayank7319.mayankgupta.otakulist.api.AnimeClient;
import com.mayank7319.mayankgupta.otakulist.fragments.HomeFragment;
import com.mayank7319.mayankgupta.otakulist.models.AuthObj;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

                animeClient.searchAnime(query.trim(), accessToken).enqueue(new Callback<ArrayList<EntryShort>>() {
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
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
