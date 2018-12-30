package com.mayank7319.mayankgupta.otakulist.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mayank7319.mayankgupta.otakulist.R;

/**
 * Created by Mayank Gupta on 30-12-2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    DatabaseReference mRef;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference preference = findPreference("delete_db");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("This will delete all user data including any Anime progress.")
                        .setTitle("Erase user data?");

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        mRef = FirebaseDatabase.getInstance().getReference();
                        mRef.child("users").child(uid).removeValue();
                        Toast.makeText(getContext(), "Delete operation complete.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                return true;
            }
        });
    }

}
