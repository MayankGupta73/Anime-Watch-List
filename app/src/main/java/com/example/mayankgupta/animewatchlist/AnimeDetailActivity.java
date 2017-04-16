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
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnimeDetailActivity extends AppCompatActivity {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    Button btnPlus,btnMinus;
    ImageView image;
    TextView tvName,tvType,tvStatus,tvWatchStatus,tvEpisodeCount,tvEpisodes,tvScore,tvDate,tvSynopsis;

    int episodesWatched = 0;  //Replace both by class member
    int totalEpisodes = 0;
    int position;

    String listType;
    public static final String TAG = "AWL";

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

        setTvWatchStatus(listType);

        if(listType.equals(Completed)|| listType.equals(OnHold)){
            btnMinus.setVisibility(View.INVISIBLE);
            btnPlus.setVisibility(View.INVISIBLE);
        }

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
                switch (listType){
                    case NEW: popup.inflate(R.menu.menu_add); break;
                    case Current: popup.inflate(R.menu.menu_current);
                        break;
                    case Completed: popup.inflate(R.menu.menu_completed);
                        break;
                    case OnHold: popup.inflate(R.menu.menu_on_hold);
                        break;
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        Log.d(TAG, "onMenuItemClick: ");
                        //final String list = getListTypeName(listType);
                        final GenericTypeIndicator<ArrayList<EntryShort>> t = new GenericTypeIndicator<ArrayList<EntryShort>>() {};

                        // TODO: Add the NEW case in switch later.
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                switch (item.getItemId()) {
                                    case R.id.list_current:
                                        switch (listType) {
                                            case Completed:
                                                ArrayList<EntryShort> newList;
                                                EntryShort tempItem;
                                                newList = dataSnapshot.child("completed_list").getValue(t);
                                                tempItem = newList.get(position);
                                                newList.remove(position);
                                                mRef.child("completed_list").setValue(newList);
                                                int pos = (int) dataSnapshot.child("current_list").getChildrenCount();
                                                mRef.child("current_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;

                                            case OnHold:
                                                newList = dataSnapshot.child("on_hold_list").getValue(t);
                                                tempItem = newList.get(position);
                                                newList.remove(position);
                                                mRef.child("on_hold_list").setValue(newList);
                                                pos = (int) dataSnapshot.child("current_list").getChildrenCount();
                                                mRef.child("current_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;
                                        }
                                        setTvWatchStatus(Current);
                                        break;
                                    case R.id.list_completed:
                                        switch (listType) {
                                            case Current:
                                                ArrayList<EntryShort> newList;
                                                EntryShort tempItem;
                                                newList = dataSnapshot.child("current_list").getValue(t);
                                                tempItem = newList.get(position);
                                                tempItem.setEpisodeCount(tempItem.getEpisodes());
                                                newList.remove(position);
                                                mRef.child("current_list").setValue(newList);
                                                int pos = (int) dataSnapshot.child("completed_list").getChildrenCount();
                                                mRef.child("completed_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;

                                            case OnHold:
                                                newList = dataSnapshot.child("on_hold_list").getValue(t);
                                                tempItem = newList.get(position);
                                                tempItem.setEpisodeCount(tempItem.getEpisodes());
                                                newList.remove(position);
                                                mRef.child("on_hold_list").setValue(newList);
                                                pos = (int) dataSnapshot.child("completed_list").getChildrenCount();
                                                mRef.child("completed_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;
                                        }
                                        setTvWatchStatus(Completed);
                                        break;
                                    case R.id.list_on_hold:
                                        switch (listType) {
                                            case Current:
                                                ArrayList<EntryShort> newList;
                                                EntryShort tempItem;
                                                newList = dataSnapshot.child("current_list").getValue(t);
                                                tempItem = newList.get(position);
                                                newList.remove(position);
                                                mRef.child("current_list").setValue(newList);
                                                int pos = (int) dataSnapshot.child("on_hold_list").getChildrenCount();
                                                mRef.child("on_hold_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;

                                            case Completed:
                                                newList = dataSnapshot.child("completed_list").getValue(t);
                                                tempItem = newList.get(position);
                                                newList.remove(position);
                                                mRef.child("completed_list").setValue(newList);
                                                pos = (int) dataSnapshot.child("on_hold_list").getChildrenCount();
                                                mRef.child("on_hold_list").child(String.valueOf(pos)).setValue(tempItem);
                                                break;
                                        }
                                        setTvWatchStatus(OnHold);
                                        break;
                                    default: break;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ctx,"Unable to make changes.Try again Later.",Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    }
                });
                popup.show();
            }
        });


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

    void setTvWatchStatus(String listType){
        switch (listType){
            case Current: tvWatchStatus.setText("WATCHING"); break;
            case Completed: tvWatchStatus.setText("COMPLETED"); break;
            case OnHold: tvWatchStatus.setText("ON HOLD"); break;
        }
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

