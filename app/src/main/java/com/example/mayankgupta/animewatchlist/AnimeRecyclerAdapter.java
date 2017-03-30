package com.example.mayankgupta.animewatchlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 21-03-2017.
 */

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.AnimeHolder> {

    Context context;
    ArrayList<EntryShort> animeList;

    public AnimeRecyclerAdapter(Context context, ArrayList<EntryShort> animeList) {
        this.context = context;
        this.animeList = animeList;
    }

    @Override
    public AnimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.anime_list_item,parent,false);
        return new AnimeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnimeHolder holder, int position) {
        EntryShort currentItem = animeList.get(position);

        holder.image.setImageResource(R.drawable.n);
        holder.tvName.setText(currentItem.getTitle());
        holder.tvTypeStatus.setText(currentItem.getType()+"*"+currentItem.getStatus());
        holder.tvEpisodes.setText("0/"+String.valueOf(currentItem.getEpisodes()));
        holder.tvScore.setText(String.valueOf(currentItem.getScore()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass intent to details activity
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
}
