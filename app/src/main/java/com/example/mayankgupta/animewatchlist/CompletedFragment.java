package com.example.mayankgupta.animewatchlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayankgupta.animewatchlist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class CompletedFragment extends Fragment {

    RecyclerView completedRecycler;
    ArrayList<EntryShort> animeList;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public CompletedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        animeList = new ArrayList<>();
        animeList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(2,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(3,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(4,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(5,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(6,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        animeList.add(new EntryShort(7,220,0,"Naruto","TV","Finished Airing",7.82f,null));

        View rootView = inflater.inflate(R.layout.fragment_completed, container, false);
        completedRecycler = (RecyclerView) rootView.findViewById(R.id.completedRecycler);
        completedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),animeList,"LIST_COMPLETED");

        completedRecycler.setAdapter(adapter);
        updateCurrentDB();
        return rootView;
    }
    void updateCurrentDB(){
        mRef.child("users").child(uid).child("completed_list").setValue(animeList);
    }

}
