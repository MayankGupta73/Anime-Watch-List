package com.mayank7319.mayankgupta.otakulist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mayank7319.mayankgupta.otakulist.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
