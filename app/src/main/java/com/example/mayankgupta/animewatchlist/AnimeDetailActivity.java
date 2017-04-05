package com.example.mayankgupta.animewatchlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimeDetailActivity extends AppCompatActivity {

    Button btnPlus,btnMinus;
    ImageView image;
    TextView tvName,tvType,tvStatus,tvWatchStatus,tvEpisodeCount,tvEpisodes,tvScore,tvDate,tvSynopsis;

    int episodesWatched = 0;  //Replace both by class member
    int totalEpisodes = 220;

    String listType;

    public static final String Current = "LIST_CURRENT";
    public static final String Completed = "LIST_COMPLETED";
    public static final String OnHold = "LIST_ON_HOLD";
    public static final String All = "LIST_ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Anime Details");

        final Context ctx =  this;
        Intent i = getIntent();
        listType = i.getStringExtra("listType");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make menu to add to list
                PopupMenu popup = new PopupMenu(ctx,view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.list_current: //TODO:
                                return true;
                            case R.id.list_completed: //TODO:
                                return true;
                            case R.id.list_on_hold: //TODO:
                                return true;
                            default: return false;
                        }
                    }
                });
                switch (listType){
                    case All: popup.inflate(R.menu.menu_add); break;
                    case Current: popup.inflate(R.menu.menu_current); break;
                    case Completed: popup.inflate(R.menu.menu_completed); break;
                    case OnHold: popup.inflate(R.menu.menu_on_hold); break;
                }
                popup.show();
            }
        });

        tvEpisodeCount = (TextView) findViewById(R.id.tvEpisodeCount);
        tvName = (TextView) findViewById(R.id.tvName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvWatchStatus = (TextView) findViewById(R.id.tvWatchStatus);
        tvEpisodes = (TextView) findViewById(R.id.tvEpisodes);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnMinus = (Button) findViewById(R.id.btnMinus);
        image = (ImageView) findViewById(R.id.image);

        btnPlus.setOnClickListener(ocl);
        btnMinus.setOnClickListener(ocl);
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnAdd && episodesWatched != totalEpisodes){
                episodesWatched++;
                tvEpisodeCount.setText(episodesWatched+"/"+totalEpisodes);
            }
            if(v.getId() == R.id.btnMinus && episodesWatched != 0){
                episodesWatched--;
                tvEpisodeCount.setText(episodesWatched+"/"+totalEpisodes);
            }
        }
    };
}
