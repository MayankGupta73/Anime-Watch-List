package com.example.mayankgupta.animewatchlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnimeDetailActivity extends AppCompatActivity {

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    Button btnPlus,btnMinus;
    ImageView image;
    TextView tvName,tvType,tvStatus,tvWatchStatus,tvEpisodeCount,tvEpisodes,tvScore,tvDate,tvSynopsis;

    int episodesWatched = 0;  //Replace both by class member
    int totalEpisodes = 220;
    int position;

    String listType;

    public static final String Current = "LIST_CURRENT";
    public static final String Completed = "LIST_COMPLETED";
    public static final String OnHold = "LIST_ON_HOLD";
    public static final String All = "LIST_ALL";
    public static final String NEW = "LIST_NEW";

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
        position = i.getIntExtra("position",0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAdd);
        if(listType == All){
            fab.hide();
        }
        else fab.show();
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
                    case NEW: popup.inflate(R.menu.menu_add); break;
                    case Current: popup.inflate(R.menu.menu_current);
                        break;
                    case Completed: popup.inflate(R.menu.menu_completed);
                        break;
                    case OnHold: popup.inflate(R.menu.menu_on_hold);
                        break;
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

        mRef.child(getListTypeName(listType)).child(String.valueOf(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                episodesWatched = dataSnapshot.child("episodeCount").getValue(int.class);
                totalEpisodes = dataSnapshot.child("episodes").getValue(int.class);
                tvEpisodeCount.setText(String.valueOf(episodesWatched)+"/"+totalEpisodes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnPlus.setOnClickListener(ocl);
        btnMinus.setOnClickListener(ocl);

    }

    String getListTypeName(String listType){
        switch (listType){
            case Current: return "current_list";
            case Completed: return "completed_list";
            case OnHold: return "on_hold_list";
            default: return "";
        }
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnPlus && episodesWatched != totalEpisodes){
                episodesWatched++;
            }
            else if(v.getId() == R.id.btnMinus && episodesWatched != 0){
                episodesWatched--;
            }
            else return;

            mRef.child(getListTypeName(listType)).child(String.valueOf(position)).child("episodeCount").setValue(episodesWatched);
            tvEpisodeCount.setText(episodesWatched+"/"+totalEpisodes);
            Log.d("AL", "onClick: ");
        }
    };
}

