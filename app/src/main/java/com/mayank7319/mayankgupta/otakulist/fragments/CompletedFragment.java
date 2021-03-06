package com.mayank7319.mayankgupta.otakulist.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mayank7319.mayankgupta.otakulist.adapters.AnimeRecyclerAdapter;
import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.models.EntryShort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CompletedFragment extends Fragment {

    RecyclerView completedRecycler;
    ArrayList<EntryShort> animeList;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    SearchView searchView;
    ProgressBar progressLoader;

    public CompletedFragment() {
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
            searchView.setVisibility(View.VISIBLE);
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        }if(searchView!=null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    if(query.isEmpty()){
                       Toast.makeText(getContext(),"Enter a query",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    ArrayList<EntryShort> searchList = new ArrayList<EntryShort>();
                    for(EntryShort entry:animeList){
                        if(query.trim().equalsIgnoreCase(entry.getTitle()) || entry.getTitle().toLowerCase().contains(query.toLowerCase())){
                            searchList.add(entry);
                        }
                    }
                    if(searchList.isEmpty()){
                        Toast.makeText(getContext(),"No items match the query",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    AnimeRecyclerAdapter adapterSearch = new AnimeRecyclerAdapter(getContext(),searchList,"LIST_COMPLETED");
                    completedRecycler.setAdapter(adapterSearch);
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
                    AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),animeList,"LIST_COMPLETED");
                    completedRecycler.setAdapter(adapter);
                    return true;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_completed, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLoader = (ProgressBar) rootView.findViewById(R.id.progressLoader);
        progressLoader.setVisibility(View.VISIBLE);
        completedRecycler = (RecyclerView) rootView.findViewById(R.id.completedRecycler);
        /*completedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),animeList,"LIST_COMPLETED");

        completedRecycler.setAdapter(adapter);*/
//        updateCurrentDB();


        mRef.child("users").child(uid).child("completed_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<EntryShort>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<EntryShort>>() {};
                animeList = dataSnapshot.getValue(genericTypeIndicator);
                if(animeList == null) animeList = new ArrayList<EntryShort>();
                completedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),animeList,"LIST_COMPLETED");

                completedRecycler.setAdapter(adapter);
                progressLoader.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }


    void updateCurrentDB(){
        mRef.child("users").child(uid).child("completed_list").setValue(animeList);
    }

}
