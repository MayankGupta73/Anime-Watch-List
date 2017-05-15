package com.mayank7319.mayankgupta.otakulist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.activities.AnimeDetailActivity;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mayank7319.mayankgupta.otakulist.activities.AnimeDetailActivity.Completed;
import static com.mayank7319.mayankgupta.otakulist.activities.AnimeDetailActivity.Current;
import static com.mayank7319.mayankgupta.otakulist.activities.AnimeDetailActivity.OnHold;

/**
 * Created by Mayank Gupta on 21-03-2017.
 */

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.AnimeHolder> {

    Context context;
    ArrayList<EntryShort> animeList;
    String listType;


    public AnimeRecyclerAdapter(Context context, ArrayList<EntryShort> animeList, String listType) {
        this.context = context;
        this.animeList = animeList;
        this.listType = listType;
    }

    @Override
    public AnimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;

        if(viewType == 0)
            itemView = li.inflate(R.layout.anime_list_item_img,parent,false);
        else if (viewType == 1)
            itemView = li.inflate(R.layout.anime_list_item_new,parent,false);
        else
            itemView = li.inflate(R.layout.anime_list_item,parent,false);

        return new AnimeHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        switch (listType){
            case "LiST_HORIZONTAL": return 0;
            case "LIST_NEW": return 1;
            default: return 2;
        }
    }

    @Override
    public void onBindViewHolder(final AnimeHolder holder, final int position) {
        final EntryShort currentItem = animeList.get(position);
        if(currentItem == null)
            return;

        String uid;
        DatabaseReference mRef;

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        Picasso.with(context)
                .load(currentItem.getImage())
                .fit()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.n)
                .into(holder.image);

//        holder.image.setImageResource(R.drawable.n);
        if(getItemViewType(position) == 0 && currentItem.getTitle().length()>31)
            holder.tvName.setText(currentItem.getTitle().substring(0,30)+"...");
        else if(currentItem.getTitle().length()>25) {
            holder.tvName.setText(currentItem.getTitle().substring(0, 24) + "...");
            holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        }else
            holder.tvName.setText(currentItem.getTitle());


        if(getItemViewType(position) != 0) {
            if (getItemViewType(position) == 2) {
                mRef.child(getListTypeName(listType)).child(String.valueOf(position)).child("episodeCount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(int.class) != null)
                            holder.tvEpisodes.setText(dataSnapshot.getValue(int.class).toString() + "/" + String.valueOf(currentItem.getEpisodes()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.tvEpisodes.setText(String.valueOf(currentItem.getEpisodeCount()) + "/" + String.valueOf(currentItem.getEpisodes()));
                holder.tvScore.setText(String.valueOf(currentItem.getScore()));
            }

            holder.tvTypeStatus.setText(currentItem.getType() + "*" + currentItem.getStatus());
            if (getItemViewType(position) == 1) {
                holder.tvEpisodes.setText(currentItem.getEpisodes() + " eps.");
                holder.tvPopularity.setText("Popularity: " + currentItem.getPopularity());
                holder.tvScore.setText("Score: "+String.valueOf(currentItem.getScore()));
            }

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pass intent to details activity

                    Intent i = new Intent(context, AnimeDetailActivity.class);
                    i.putExtra("listType", listType);
                    i.putExtra("id",currentItem.getId());
                    i.putExtra("position", position);
                    context.startActivity(i);
                }
            };
            if(getItemViewType(position) == 0) {
                holder.tvName.setOnClickListener(ocl);
                holder.image.setOnClickListener(ocl);
            }
            holder.itemView.setOnClickListener(ocl);
        }

    }

    @Override
    public void onViewRecycled(AnimeHolder holder) {
        holder.cleanup();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return animeList!=null ? animeList.size():0;
    }

    public class AnimeHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvTypeStatus, tvEpisodes, tvScore, tvPopularity;
        ImageView image;
        View itemView;

        public AnimeHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTypeStatus = (TextView) itemView.findViewById(R.id.tvTypeStatus);
            tvEpisodes = (TextView) itemView.findViewById(R.id.tvEpisodes);
            tvScore = (TextView) itemView.findViewById(R.id.tvScore);
            tvPopularity = (TextView) itemView.findViewById(R.id.tvPopularity);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        public void cleanup(){
            Picasso.with(image.getContext())
                    .cancelRequest(image);
            image.setImageDrawable(null);
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
