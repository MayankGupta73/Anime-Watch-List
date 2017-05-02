package com.example.mayankgupta.animewatchlist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.adapters.AnimeRecyclerAdapter;
import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.util.ArrayList;


public class AnimeListActivity extends AppCompatActivity {

    RecyclerView listRecycler;
    ArrayList<EntryShort> animeList= new ArrayList<>();
    ProgressBar progressLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLoader = (ProgressBar) findViewById(R.id.progressLoader);

        Intent i = getIntent();
        String type = i.getStringExtra("type");
        getSupportActionBar().setTitle(type);
        animeList = i.getParcelableArrayListExtra("list");
        listRecycler = (RecyclerView) findViewById(R.id.listRecycler);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(this,animeList,"LIST_NEW");

        listRecycler.setAdapter(adapter);
        progressLoader.setVisibility(View.GONE);
    }

}
