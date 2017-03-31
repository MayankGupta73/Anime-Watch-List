package com.example.mayankgupta.animewatchlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.util.ArrayList;


public class OnHoldFragment extends Fragment {

    RecyclerView onHoldRecycler;
    ArrayList<EntryShort> onHoldList;

    public OnHoldFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onHoldList = new ArrayList<>();
        onHoldList.add(new EntryShort(1,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(2,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(3,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(4,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(5,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(6,220,"Naruto","TV","Finished Airing",7.82f,null));
        onHoldList.add(new EntryShort(7,220,"Naruto","TV","Finished Airing",7.82f,null));

        View rootView = inflater.inflate(R.layout.fragment_on_hold, container, false);
        onHoldRecycler = (RecyclerView) rootView.findViewById(R.id.onHoldRecycler);
        onHoldRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),onHoldList);

        onHoldRecycler.setAdapter(adapter);
        return rootView;
    }

}
