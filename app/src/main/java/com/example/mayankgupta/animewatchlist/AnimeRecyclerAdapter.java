package com.example.mayankgupta.animewatchlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayankgupta.animewatchlist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.mayankgupta.animewatchlist.AnimeDetailActivity.Completed;
import static com.example.mayankgupta.animewatchlist.AnimeDetailActivity.Current;
import static com.example.mayankgupta.animewatchlist.AnimeDetailActivity.OnHold;

/**
 * Created by Mayank Gupta on 21-03-2017.
 */

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.AnimeHolder> {

    Context context;
    ArrayList<EntryShort> animeList;
    String listType;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    public AnimeRecyclerAdapter(Context context, ArrayList<EntryShort> animeList, String listType) {
        this.context = context;
        this.animeList = animeList;
        this.listType = listType;
    }

    @Override
    public AnimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.anime_list_item,parent,false);
        return new AnimeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AnimeHolder holder, final int position) {
        final EntryShort currentItem = animeList.get(position);

        holder.image.setImageResource(R.drawable.n);
        holder.tvName.setText(currentItem.getTitle());
        mRef.child(getListTypeName(listType)).child(String.valueOf(position)).child("episodeCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(int.class)!=null)
                holder.tvEpisodes.setText(dataSnapshot.getValue(int.class).toString()+"/"+String.valueOf(currentItem.getEpisodes()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.tvTypeStatus.setText(currentItem.getType()+"*"+currentItem.getStatus());
        holder.tvEpisodes.setText(String.valueOf(currentItem.getEpisodeCount())+"/"+String.valueOf(currentItem.getEpisodes()));
        holder.tvScore.setText(String.valueOf(currentItem.getScore()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass intent to details activity
                Intent i = new Intent(context,AnimeDetailActivity.class);
                i.putExtra("listType",listType);
                i.putExtra("position",position);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public class AnimeHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvTypeStatus, tvEpisodes, tvScore;
        ImageView image;
        View itemView;

        public AnimeHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTypeStatus = (TextView) itemView.findViewById(R.id.tvTypeStatus);
            tvEpisodes = (TextView) itemView.findViewById(R.id.tvEpisodes);
            tvScore = (TextView) itemView.findViewById(R.id.tvScore);
            image = (ImageView) itemView.findViewById(R.id.image);
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
}
