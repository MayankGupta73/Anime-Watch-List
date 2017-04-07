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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class CurrentFragment extends Fragment {

    RecyclerView currentRecycler;
    ArrayList<EntryShort> currentList;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public CurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentList = new ArrayList<>();
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));

        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        currentRecycler = (RecyclerView) rootView.findViewById(R.id.currentRecycler);
        currentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),currentList,"LIST_CURRENT");

        currentRecycler.setAdapter(adapter);
        updateCurrentDB();
        return rootView;
    }

    void updateCurrentDB(){
        mRef.child("users").child(uid).child("current_list").setValue(currentList);
    }

}
