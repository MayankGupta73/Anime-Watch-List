package com.mayank7319.mayankgupta.otakulist.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.mayank7319.mayankgupta.otakulist.DetailsQuery;
import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.api.AnilistAPI;
import com.mayank7319.mayankgupta.otakulist.api.AnilistApolloClient;
import com.mayank7319.mayankgupta.otakulist.api.AnimeClient;
import com.mayank7319.mayankgupta.otakulist.models.AuthObj;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;
import com.mayank7319.mayankgupta.otakulist.models.entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mayank7319.mayankgupta.otakulist.type.MediaType;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    Button btnPlus,btnMinus;
    ImageView image;
    TextView tvName,tvType,tvStatus,tvWatchStatus,tvEpisodeCount,tvEpisodes,tvScore,tvDate,tvSynopsis,tvDuration, tvPopularity, tvGenres, tvSynonyms,tvEpisodeLabel;
    ProgressBar progressLoader;
    LinearLayout detailLayout;

    AnilistAPI anilistAPI;
    AnimeClient animeClient;
    EntryShort entryShort;

    int episodesWatched = 0;  //Replace both by class member
    int totalEpisodes = 0;
    int position;
    int id;
    String animeName = "";

    Boolean flagAccess = false;
    String listType;
    String accessToken;
    String anilistUrl;
    public static final String TAG = "AWL";

    public static final String Current = "LIST_CURRENT";
    public static final String Completed = "LIST_COMPLETED";
    public static final String OnHold = "LIST_ON_HOLD";
    public static final String NewItem = "LIST_NEW";
    public static final String Horizontal = "LiST_HORIZONTAL";

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
        id = i.getIntExtra("id",1);

        progressLoader = (ProgressBar) findViewById(R.id.progressLoader);
        detailLayout = (LinearLayout) findViewById(R.id.detail_layout);
        tvEpisodeCount = (TextView) findViewById(R.id.tvEpisodeCount);
        tvName = (TextView) findViewById(R.id.tvName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvWatchStatus = (TextView) findViewById(R.id.tvWatchStatus);
        tvEpisodes = (TextView) findViewById(R.id.tvEpisodes);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvPopularity = (TextView) findViewById(R.id.tvPopularity);
        tvSynonyms = (TextView) findViewById(R.id.tvSynonyms);
        tvGenres = (TextView) findViewById(R.id.tvGenres);
        tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
        tvEpisodeLabel = (TextView) findViewById(R.id.tvEpisodeLabel);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnMinus = (Button) findViewById(R.id.btnMinus);
        image = (ImageView) findViewById(R.id.image);

        detailLayout.setVisibility(View.INVISIBLE);
        setTvWatchStatus(listType);

        anilistAPI = new AnilistAPI(this);
        animeClient = anilistAPI.getAnimeClient();
        if(!anilistAPI.ifTokenExists()) {
            animeClient.getaccessToken(AnilistAPI.clientId, AnilistAPI.clientSecret, AnilistAPI.grantType)
                    .enqueue(new Callback<AuthObj>() {
                        @Override
                        public void onResponse(Call<AuthObj> call, Response<AuthObj> response) {
                            accessToken = response.body().getAccessToken();
                            anilistAPI.writeToSharedPref(accessToken);
                            if (!flagAccess){
                                flagAccess = true;
                                setAnimeDetails();
                                call.cancel();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthObj> call, Throwable t) {

                        }
                    });
        }
        else {
            accessToken = anilistAPI.getFromSharedPref();
            setAnimeDetails();
        }

        if(listType.equals(Completed)|| listType.equals(OnHold)){
            btnMinus.setVisibility(View.INVISIBLE);
            btnPlus.setVisibility(View.INVISIBLE);
        }
        else {
            btnPlus.setOnClickListener(ocl);
            btnMinus.setOnClickListener(ocl);
        }

        mRef.child(getListTypeName(listType)).child(String.valueOf(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(listType.equals(Horizontal) || listType.equals(NewItem))) {
                    if(dataSnapshot.child("episodeCount").exists() || dataSnapshot.child("episodes").exists()) {
                        episodesWatched = dataSnapshot.child("episodeCount").getValue(int.class);
                        totalEpisodes = dataSnapshot.child("episodes").getValue(int.class);
                        tvEpisodeCount.setText(String.valueOf(episodesWatched) + "/" + totalEpisodes);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAdd);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make menu to add to list
                final PopupMenu popup = new PopupMenu(ctx,view);
                switch (listType){
                    case NewItem: popup.inflate(R.menu.menu_add); break;
                    case Horizontal: popup.inflate(R.menu.menu_add); break;
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
                                            case NewItem:
                                                if(dataSnapshot.child("current_list").exists())
                                                pos = (int) dataSnapshot.child("current_list").getChildrenCount();
                                                else pos =0;
                                                mRef.child("current_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;
                                            case Horizontal:
                                                if(dataSnapshot.child("current_list").exists())
                                                    pos = (int) dataSnapshot.child("current_list").getChildrenCount();
                                                else pos =0;
                                                mRef.child("current_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;
                                        }
                                        setTvWatchStatus(Current);
                                        Toast.makeText(getApplicationContext(),"Operation Completed",Toast.LENGTH_SHORT).show();
                                        finish();
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
                                            case NewItem:
                                                if(dataSnapshot.child("completed_list").hasChildren())
                                                pos = (int) dataSnapshot.child("completed_list").getChildrenCount();
                                                else pos = 0;
                                                entryShort.setEpisodeCount(entryShort.getEpisodes());
                                                mRef.child("completed_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;
                                            case Horizontal:
                                                if(dataSnapshot.child("completed_list").hasChildren())
                                                    pos = (int) dataSnapshot.child("completed_list").getChildrenCount();
                                                else pos = 0;
                                                entryShort.setEpisodeCount(entryShort.getEpisodes());
                                                mRef.child("completed_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;

                                        }
                                        setTvWatchStatus(Completed);
                                        Toast.makeText(getApplicationContext(),"Operation Completed",Toast.LENGTH_SHORT).show();
                                        finish();
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
                                            case NewItem:
                                                if(dataSnapshot.child("on_hold_list").exists())
                                                pos = (int) dataSnapshot.child("on_hold_list").getChildrenCount();
                                                else pos=0;
                                                mRef.child("on_hold_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;
                                            case Horizontal:
                                                if(dataSnapshot.child("on_hold_list").exists())
                                                    pos = (int) dataSnapshot.child("on_hold_list").getChildrenCount();
                                                else pos=0;
                                                mRef.child("on_hold_list").child(String.valueOf(pos)).setValue(entryShort);
                                                break;

                                        }
                                        setTvWatchStatus(OnHold);
                                        Toast.makeText(getApplicationContext(),"Operation Completed",Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                    case R.id.list_delete:
                                        ArrayList<EntryShort> newList = dataSnapshot.child(getListTypeName(listType)).getValue(t);
                                        EntryShort tempItem = newList.get(position);
                                        newList.remove(position);
                                        mRef.child(getListTypeName(listType)).setValue(newList);
                                        Toast.makeText(getApplicationContext(),"Item Deleted",Toast.LENGTH_SHORT).show();
                                        finish();
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


    }

    void setAnimeDetails(){
        AnilistApolloClient anilistClient = new AnilistApolloClient();
        ApolloClient client = anilistClient.getApolloClient();

        DetailsQuery detailsQuery = DetailsQuery.builder()
                .id(id)
                .type(MediaType.ANIME)
                .build();

        client.query(detailsQuery).enqueue(new ApolloCall.Callback<DetailsQuery.Data>() {
            @Override
            public void onResponse(@NotNull com.apollographql.apollo.api.Response<DetailsQuery.Data> response) {
                DetailsQuery.Medium medium = response.data().Page().media().get(0);
                if(medium == null){
                    Toast.makeText(AnimeDetailActivity.this,"Could not fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = medium.id();
                int episodes = medium.episodes() != null? medium.episodes(): 0;
                int duration = medium.duration() != null? medium.duration(): 0;
                int popularity = medium.popularity() != null? medium.popularity(): 0;
                String title = medium.title().romaji() != null ? medium.title().romaji(): "";
                animeName = title;
                ArrayList<String> synonyms = new ArrayList<>(medium.synonyms());
                String type = medium.type().name() != null? medium.type().name(): "";
                String status = medium.status().name() != null? medium.status().name(): "";

                String startDate = "";
                if( medium.startDate().year() != null && medium.startDate().month() != null && medium.startDate().day() != null )
                    startDate = formatDateString(medium.startDate().year(), medium.startDate().month(), medium.startDate().day());

                String endDate = "";
                if( medium.endDate().year() != null && medium.endDate().month() != null && medium.endDate().day() != null )
                    endDate = formatDateString(medium.endDate().year(), medium.endDate().month(), medium.endDate().day());

                int score = medium.averageScore() != null? medium.averageScore(): 0;
                String synopsis = medium.description() != null? medium.description(): "";
                String image = medium.coverImage().large() != null? medium.coverImage().large() : "";
                ArrayList<String> genres = new ArrayList<>(medium.genres());
                final entry animeDetails = new entry(id,episodes,duration,popularity,title,synonyms, type,status, startDate, endDate, score, synopsis, image, genres);


                AnimeDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entryShort = new EntryShort(animeDetails);
                        anilistUrl = "https://anilist.co/anime/"+ animeDetails.getId();
                        detailLayout.setVisibility(View.VISIBLE);
                        setFields(animeDetails);
                        progressLoader.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });


        /*animeClient.getAnimeDesc(id, accessToken).enqueue(new Callback<entry>() {
            @Override
            public void onResponse(Call<entry> call, Response<entry> response) {
                entry animeDetails = response.body();
                entryShort = new EntryShort(animeDetails);
                anilistUrl = "https://anilist.co/anime/"+ animeDetails.getId();
                detailLayout.setVisibility(View.VISIBLE);
                setFields(animeDetails);
                progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<entry> call, Throwable t) {

            }
        });*/
    }

    void setFields(entry animeDetails){
        if (listType.equals(Horizontal) || listType.equals(NewItem)) {
            tvWatchStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tvWatchStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    newIntent.setData(Uri.parse(anilistUrl));
                    startActivity(newIntent);
                }
            });
            tvEpisodeCount.setVisibility(View.INVISIBLE);
            btnPlus.setVisibility(View.INVISIBLE);
            btnMinus.setVisibility(View.INVISIBLE);
            tvEpisodeLabel.setVisibility(View.INVISIBLE);
        }

        tvName.setText(animeDetails.getTitle());
        Picasso.with(this)
                .load(animeDetails.getImage())
                .fit()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.n)
                .into(image);


        tvType.setText(animeDetails.getType());
        tvStatus.setText(animeDetails.getStatus());
        tvScore.setText(String.valueOf(animeDetails.getScore()));
        tvPopularity.setText(String.valueOf(animeDetails.getPopularity()));
        tvEpisodes.setText(String.valueOf(animeDetails.getEpisodes()));

        if(animeDetails.getType().equals("TV"))
            tvDuration.setText(animeDetails.getDuration()+" min per ep");
        else tvDuration.setText(animeDetails.getDuration()+" min");

        tvSynopsis.setText(animeDetails.getSynopsis());
        if(!animeDetails.getEndDate().equals(""))
        tvDate.setText(animeDetails.getStartDate()+" to "+animeDetails.getEndDate());
        else tvDate.setText(animeDetails.getStartDate()+" to ?");

        String genres = TextUtils.join(", ",animeDetails.getGenres());
        tvGenres.setText(genres);
        String synonyms = TextUtils.join("\n",animeDetails.getSynonyms());
        tvSynonyms.setText(synonyms);

        setSharingIntent();
    }

    void setSharingIntent(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Anime: "+ animeName+ "\nLink: "+anilistUrl;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        mShareActionProvider.setShareIntent(sharingIntent);
    }

    void setTvWatchStatus(String listType){
        switch (listType){
            case Current: tvWatchStatus.setText("WATCHING"); break;
            case Completed: tvWatchStatus.setText("COMPLETED"); break;
            case OnHold: tvWatchStatus.setText("ON HOLD"); break;
            case Horizontal:
            case NewItem: tvWatchStatus.setText("VIEW ON ANILIST"); break;
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
        }
    };

    public String formatDateString(Integer year, Integer month, Integer day){
        String m = month < 10 ? "0" + month: month.toString();
        String d = day < 10 ? "0" + day: day.toString();
        return year.toString() + m + d;
    }

    private ShareActionProvider mShareActionProvider;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.detail_option_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;

    }


}

