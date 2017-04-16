package com.example.mayankgupta.animewatchlist;


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
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.models.EntryShort;

import java.util.ArrayList;


public class AnimeListFragment extends Fragment {

    RecyclerView animeRecycler;
    ArrayList<EntryShort> animeList;
    SearchView searchView;

    public AnimeListFragment() {
        //No args in constructor
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
                    animeRecycler.setAdapter(adapterSearch);
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
                    animeRecycler.setAdapter(adapter);
                    return true;
                }
            });
        }
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

        View rootView = inflater.inflate(R.layout.fragment_anime_list, container, false);
        animeRecycler = (RecyclerView) rootView.findViewById(R.id.animeRecycler);
        animeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AnimeRecyclerAdapter adapter = new AnimeRecyclerAdapter(getContext(),animeList,"LIST_ALL");

        animeRecycler.setAdapter(adapter);
        return rootView;
    }

}
