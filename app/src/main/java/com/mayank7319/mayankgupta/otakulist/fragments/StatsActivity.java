package com.mayank7319.mayankgupta.otakulist.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mayank7319.mayankgupta.otakulist.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StatsActivity extends Fragment {

    PieChart pieChartMain;
    TextView tvCurrent, tvOnHold, tvCompleted;
    ProgressBar progressLoader;
    Context ctx;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
    float numCurrent, numOnHold, numCompleted;

    public StatsActivity() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats,container,false);

        ctx = getActivity();
        progressLoader = (ProgressBar) rootView.findViewById(R.id.progressLoader);
        progressLoader.setVisibility(View.VISIBLE);

        tvCurrent = (TextView) rootView.findViewById(R.id.tvCurrent);
        tvOnHold = (TextView) rootView.findViewById(R.id.tvOnHold);
        tvCompleted = (TextView) rootView.findViewById(R.id.tvCompleted);

        pieChartMain = (PieChart) rootView.findViewById(R.id.pieChartMain);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numCurrent = dataSnapshot.child("current_list").getChildrenCount();
                numCompleted = dataSnapshot.child("completed_list").getChildrenCount();
                numOnHold = dataSnapshot.child("on_hold_list").getChildrenCount();

//                float numTotal = (numCurrent+numCompleted+numOnHold);
//                numCurrent = (numCurrent/numTotal)*100;
//                numCompleted = (numCompleted/numTotal)*100;
//                numOnHold = (numOnHold/numTotal)*100;

                List<PieEntry> entries = new ArrayList<PieEntry>();
                entries.add(new PieEntry(numCurrent,"Current"));
                entries.add(new PieEntry(numOnHold,"On Hold"));
                List<PieEntry> entriesSecond = entries;
                entries.add(new PieEntry(numCompleted,"Completed"));

                PieDataSet set = new PieDataSet(entries,"Series Status");
                set.setColors(new int[] { R.color.blue, R.color.red, R.color.green}, ctx);
                PieData data = new PieData(set);
                pieChartMain.setData(data);
                pieChartMain.setCenterText("Series Stats");
                Description desc = pieChartMain.getDescription();
                desc.setText("Percentage wise values of the Anime Series Status");
                pieChartMain.setDescription(desc);
                pieChartMain.setUsePercentValues(true);
                pieChartMain.invalidate();

                tvCurrent.setText("Currently Watching: "+String.valueOf((int)numCurrent)+" series");
                tvOnHold.setText("On Hold: "+String.valueOf((int)numOnHold)+" series");
                tvCompleted.setText("Completed: "+String.valueOf((int)numCompleted)+" series");

                progressLoader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }
}
