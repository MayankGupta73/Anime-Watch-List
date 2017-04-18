package com.example.mayankgupta.animewatchlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    TextView tvNavName;
    SearchView searchView;

    FragmentManager fragMan;
    FragmentTransaction fragTxn;
    Fragment fragment = null, prevFragment=null;

    public static final String TAG = "MWL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragMan = getSupportFragmentManager();
        if(fragment == null){
            fragment = new HomeFragment();
            setFragment(fragment);
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: logged in");
                    String name = user.getDisplayName();
                    Log.d(TAG, "onAuthStateChanged: username = "+name);
                    tvNavName.setText((name!=null)?name:"Otaku");
                    return;
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: not logged in");
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        searchView = (SearchView) findViewById(R.id.search_view);

        /*searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(),"Search Stuff",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvNavName = (TextView) header.findViewById(R.id.tvNavName);

        if(savedInstanceState == null){
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }

        String sampleXml = "<entry><id>20</id><title>Naruto</title><english>Naruto</english><synonyms>NARUTO</synonyms><episodes>220</episodes><score>7.82</score><type>TV</type><status>Finished Airing</status><start_date>2002-10-03</start_date><end_date>2007-02-08</end_date><synopsis>Moments prior to Naruto Uzumaki&#039;s birth, a huge demon known as the Kyuubi, the Nine-Tailed Fox, attacked Konohagakure, the Hidden Leaf Village, and wreaked havoc. In order to put an end to the Kyuubi&#039;s rampage, the leader of the village, the Fourth Hokage, sacrificed his life and sealed the monstrous beast inside the newborn Naruto.<br />\n" +
                "<br />\n" +
                "Now, Naruto is a hyperactive and knuckle-headed ninja still living in Konohagakure. Shunned because of the Kyuubi inside him, Naruto struggles to find his place in the village, while his burning desire to become the Hokage of Konohagakure leads him not only to some great new friends, but also some deadly foes.<br />\n" +
                "<br />\n" +
                "[Written by MAL Rewrite]</synopsis><image>https://myanimelist.cdn-dena.com/images/anime/13/17405.jpg</image></entry>";

        /*JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(sampleXml);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }*/

        /*Log.d(TAG, sampleXml);

        Log.d(TAG, jsonObj.toString());*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setVisibility(View.INVISIBLE);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id== R.id.action_search) {

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home ) {
            prevFragment = fragment;
            fragment = new HomeFragment();
            getSupportActionBar().setTitle("Anime Watch List");

        } else if (id == R.id.nav_current) {
            prevFragment = fragment;
            fragment = new CurrentFragment();
            getSupportActionBar().setTitle("Currently Watching");

        } else if (id == R.id.nav_on_hold) {
            prevFragment = fragment;
            fragment = new OnHoldFragment();
            getSupportActionBar().setTitle("On Hold");

        } else if (id == R.id.nav_completed) {
            prevFragment = fragment;
            fragment = new CompletedFragment();
            getSupportActionBar().setTitle("Completed");

        } else if (id == R.id.nav_all) {
            prevFragment = fragment;
            fragment = new AnimeListFragment();
            getSupportActionBar().setTitle("All");

        }else if(id == R.id.nav_reminder){
            getSupportActionBar().setTitle("Anime Reminders");

        }else if (id == R.id.nav_logout) {
            Log.d(TAG, "onOptionsItemSelected: logout");
            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        setFragment(fragment);
        //Do stuff to change fragments here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setFragment(Fragment fragmentSwap){
        //fragmentSwap = fragment;
        //clearBackStack();
        fragTxn =fragMan.beginTransaction();
        if(prevFragment!=null)
        fragTxn.remove(prevFragment);

        fragTxn.add(R.id.navFragment,fragmentSwap).commit();
    }
    void clearBackStack(){
        if (fragMan.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragMan.getBackStackEntryAt(0);
            fragMan.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
