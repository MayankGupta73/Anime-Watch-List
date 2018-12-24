package com.mayank7319.mayankgupta.otakulist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mayank7319.mayankgupta.otakulist.R;
import com.mayank7319.mayankgupta.otakulist.fragments.CompletedFragment;
import com.mayank7319.mayankgupta.otakulist.fragments.CurrentFragment;
import com.mayank7319.mayankgupta.otakulist.fragments.HomeFragment;
import com.mayank7319.mayankgupta.otakulist.fragments.OnHoldFragment;
import com.mayank7319.mayankgupta.otakulist.fragments.ReminderFragment;
import com.mayank7319.mayankgupta.otakulist.fragments.StatsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseDatabase mDatabase;
    TextView tvNavName;
    SearchView searchView;
    public static String title;
    static boolean dbCalledAlready = false;

    public static final String DEFAULT_TITLE = "Otaku List";
    public static final String STATS_TITLE = "Statistics";
    public static final String CURRENT_TITLE = "Currently Watching";
    public static final String ON_HOLD_TITLE = "On Hold";
    public static final String COMPLETED_TITLE = "Completed";
    public static final String REMINDER_TITLE = "Anime Reminders";

    FragmentManager fragMan;
    FragmentTransaction fragTxn;
    Fragment fragment = null, prevFragment=null;

    public static final String TAG = "MWL";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if(mDatabase == null) {
//            mDatabase = FirebaseDatabase.getInstance();
//            mDatabase.setPersistenceEnabled(true);
//        }

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            startLogin();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String name = user.getDisplayName();
                    tvNavName.setText((name!=null)?name:"Otaku");
                    initializeFragment(savedInstanceState);
                    return;
                }
                else{
                    startLogin();
                }
            }
        };


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

        fragMan = getSupportFragmentManager();

        if(savedInstanceState == null){
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }


    }

    public void startLogin(){
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
//                    finish();
    }

    void initializeFragment(Bundle savedInstanceState){
        //In case rotated etc.
        fragment = new HomeFragment();
        title = DEFAULT_TITLE;
        if(savedInstanceState == null){
            setFragment(fragment,DEFAULT_TITLE);
            if(!dbCalledAlready) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                dbCalledAlready = true;
            }
        }
        else {
            title = savedInstanceState.getString("title");
            fragment = getFragment(title);
            setFragment(fragment,title);
        }

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
    protected void onResume() {
        super.onResume();
        if(fragment == null) {
            setFragment(getFragment(DEFAULT_TITLE), DEFAULT_TITLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title",title);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        prevFragment = fragment;
        if (id == R.id.nav_home ) {
            fragment = new HomeFragment();
            title = "Anime Watch List";

        } else if(id == R.id.nav_stats) {
            fragment = new StatsActivity();
            title = "Statistics";

        } else if (id == R.id.nav_current) {
            fragment = new CurrentFragment();
            title = "Currently Watching";

        } else if (id == R.id.nav_on_hold) {
            fragment = new OnHoldFragment();
            title = "On Hold";

        } else if (id == R.id.nav_completed) {
            fragment = new CompletedFragment();
            title = "Completed";

        } /*else if (id == R.id.nav_all) {
            fragment = new AnimeListFragment();
            title = "All";

        }*/
        else if(id == R.id.nav_reminder){
            fragment = new ReminderFragment();
            title = "Anime Reminders";

        }else if (id == R.id.nav_logout) {
            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        else /*if(id == R.id.nav_top || id == R.id.nav_popular || id == R.id.nav_seasonal ||id == R.id.nav_upcoming)*/{
            Intent intent = new Intent(MainActivity.this,AnimeListActivity.class);
            intent.putExtra("getData",true);
            switch (id){
                case R.id.nav_search:
                    intent.putExtra("type","Search");
                    break;
                case R.id.nav_top:
                    intent.putExtra("type","Top Anime");
                    break;
                case R.id.nav_popular:
                    intent.putExtra("type","Popular");
                    break;
                case R.id.nav_seasonal:
                    intent.putExtra("type","Seasonal Chart");
                    break;
                case R.id.nav_upcoming:
                    intent.putExtra("type","Upcoming");
                    break;
            }
            startActivity(intent);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        setFragment(fragment,title);
        //Do stuff to change fragments here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(Fragment fragmentSwap,String title){
        //fragmentSwap = fragment;
        //clearBackStack();
        fragTxn =fragMan.beginTransaction();
        if(prevFragment!=null)
        fragTxn.remove(prevFragment);

        fragTxn.replace(R.id.navFragment,fragmentSwap);
        fragTxn.commit();
        getSupportActionBar().setTitle(title);
    }

    Fragment getFragment(String type){
        switch (type){
            case STATS_TITLE:
                return new StatsActivity();
            case CURRENT_TITLE:
                return new CurrentFragment();
            case ON_HOLD_TITLE:
                return new OnHoldFragment();
            case COMPLETED_TITLE:
                return new CompletedFragment();
            case REMINDER_TITLE:
                return new ReminderFragment();
            default: return new HomeFragment();
        }
    }

}
