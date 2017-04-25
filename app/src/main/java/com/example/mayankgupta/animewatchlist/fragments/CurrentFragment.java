package com.example.mayankgupta.animewatchlist.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.adapters.AnimeRecyclerAdapter;
import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CurrentFragment extends Fragment {

    RecyclerView currentRecycler;
    ArrayList<EntryShort> currentList;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    SearchView searchView;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if(searchItem!=null) {
            searchView = (SearchView) searchItem.getActionView();
            Log.d("AL", "onPrepareOptionsMenu: item");
            searchView.setVisibility(View.VISIBLE);
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        }if(searchView!=null) {
            Log.d("AL", "onPrepareOptionsMenu: view");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    if(query.isEmpty()){
                        Toast.makeText(getContext(),"Enter a query",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    ArrayList<EntryShort> searchList = new ArrayList<EntryShort>();
                    for(EntryShort entry:currentList){
                        if(query.trim().equalsIgnoreCase(entry.getTitle()) || entry.getTitle().toLowerCase().contains(query.toLowerCase())){
                            searchList.add(entry);
                        }
                    }
                    if(searchList.isEmpty()){
                        Toast.makeText(getContext(),"No items match the query",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    AnimeRecyclerAdapter adapterSearch = new AnimeRecyclerAdapter(getContext(),searchList,"LIST_COMPLETED");
                    currentRecycler.setAdapter(adapterSearch);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),currentList,"LIST_COMPLETED");
                    currentRecycler.setAdapter(adapter);
                    return true;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*currentList = new ArrayList<>();
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));
        currentList.add(new EntryShort(1,220,0,"Naruto","TV","Finished Airing",7.82f,null));*/

        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        final ProgressBar progressLoader = (ProgressBar) rootView.findViewById(R.id.progressLoader);
        currentRecycler = (RecyclerView) rootView.findViewById(R.id.currentRecycler);
        /*currentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),currentList,"LIST_CURRENT");

        currentRecycler.setAdapter(adapter);*/
//        updateCurrentDB();



        mRef.child("users").child(uid).child("current_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<EntryShort>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<EntryShort>>() {};
                currentList = dataSnapshot.getValue(genericTypeIndicator);
                currentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),currentList,"LIST_CURRENT");

                currentRecycler.setAdapter(adapter);
                progressLoader.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }


    void updateCurrentDB(){
        mRef.child("users").child(uid).child("current_list").setValue(currentList);
    }

}
