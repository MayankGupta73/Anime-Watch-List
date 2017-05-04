package com.example.mayankgupta.animewatchlist.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mayankgupta.animewatchlist.activities.MainActivity;
import com.example.mayankgupta.animewatchlist.activities.PickerActivity;
import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.ReminderBroadcastReceiver;
import com.example.mayankgupta.animewatchlist.models.Reminder;
import com.example.mayankgupta.animewatchlist.adapters.ReminderRecycler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderFragment extends Fragment {

    FloatingActionButton fabReminder;
    ArrayList<Reminder> reminders;
    RecyclerView reminderRecycler;
    ReminderRecycler adapter;
    Context ctx;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    public static final String TAG = "RM";

    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder, container, false);
        fabReminder = (FloatingActionButton) rootView.findViewById(R.id.fabReminder);
        fabReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),PickerActivity.class);
                startActivityForResult(i,123);
            }
        });
        reminderRecycler = (RecyclerView) rootView.findViewById(R.id.reminderRecycler);
        reminderRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ctx = getActivity();

        mRef.child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    reminders = new ArrayList<Reminder>();
                    Toast.makeText(ctx,"No reminders",Toast.LENGTH_SHORT).show();
                }
                else {
                    GenericTypeIndicator<ArrayList<Reminder>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Reminder>>() {};
                    reminders = dataSnapshot.getValue(genericTypeIndicator);

                    Calendar now = Calendar.getInstance();
                    boolean flag = false;
                    for(Reminder reminder:reminders){
                        Calendar remTime = Calendar.getInstance();
                        String AP = reminder.getAp();
                        int hour = reminder.getHour();
                        hour = AP.equals("PM")? hour+12:hour;
                        remTime.set(reminder.getYear(),reminder.getMonth(),reminder.getDay(),hour,reminder.getMinute());
                        if(remTime.before(now)){
                            flag = true;
                            reminders.remove(reminder);
                        }
                    }

                    if(flag){
                        mRef.child("reminders").setValue(reminders);
                    }
                }
                adapter = new ReminderRecycler(reminders,getContext());
                reminderRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 123){
            if(resultCode == RESULT_OK){
                Calendar time = Calendar.getInstance();
                int year , month, day, hour, minute ;

                year = data.getIntExtra("Year",0);
                month = data.getIntExtra("Month",0);
                day = data.getIntExtra("Day",0);
                hour = data.getIntExtra("Hour",0);
                minute = data.getIntExtra("Minute",0);

                String animeName = data.getStringExtra("Anime Name");
                String recurrenceRule = data.getStringExtra("RRule");
                String AP = data.getStringExtra("AP");

                Reminder newRem = new Reminder(animeName,AP,recurrenceRule,minute,hour,day,month,year);
                reminders.add(newRem);
                mRef.child("reminders").setValue(reminders);
                adapter.notifyDataSetChanged();

                hour = AP.equals("PM")? hour+12:hour;
                time.set(year,month,day,hour,minute);

                int id = newRem.getReminderId();
                Toast.makeText(ctx,"Alarm set for "+data.getStringExtra("Date")+" at "+data.getStringExtra("Time"),Toast.LENGTH_SHORT).show();
                AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(),ReminderBroadcastReceiver.class);
                intent.putExtra("Anime Name",animeName);


                PendingIntent pi = PendingIntent.getBroadcast(getContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);

                switch(recurrenceRule){
                    case "Do Not Repeat":
                        am.set(AlarmManager.RTC_WAKEUP,time.getTimeInMillis(),pi);
                        break;
                    case "Repeat Daily":
                        am.setRepeating(AlarmManager.RTC_WAKEUP,time.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
                        break;
                    case "Repeat Weekly":
                        am.setRepeating(AlarmManager.RTC_WAKEUP,time.getTimeInMillis(),(AlarmManager.INTERVAL_DAY*7),pi);
                        break;
                }

            }
        }

    }
}
