package com.example.mayankgupta.animewatchlist.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mayankgupta.animewatchlist.R;
import com.example.mayankgupta.animewatchlist.ReminderBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 20-04-2017.
 */

public class ReminderRecycler extends RecyclerView.Adapter<ReminderRecycler.ReminderHolder> {
    ArrayList<Reminder> reminders;
    Context ctx;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    public ReminderRecycler(ArrayList<Reminder> reminders, Context ctx) {
        this.reminders = reminders;
        this.ctx = ctx;
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    @Override
    public void onBindViewHolder(ReminderHolder holder, final int position) {
        Reminder currentItem = reminders.get(position);

        holder.tvAnimeName.setText(currentItem.getAnimeName());
        holder.tvAP.setText(currentItem.getAp());
        holder.tvRRule.setText(currentItem.getRecurrenceRule());

        String hour;
        if(String.valueOf(currentItem.getHour()).length() == 1){
            hour = "0" + String.valueOf(currentItem.getHour());
        }
        else  hour = String.valueOf(currentItem.getHour());
        String minute;
        if(String.valueOf(currentItem.getMinute()).length() == 1){
            minute = "0" + String.valueOf(currentItem.getMinute());
        }
        else  minute = String.valueOf(currentItem.getMinute());
        String time = hour + ":" + minute;
        holder.tvTime.setText(time);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder item = reminders.get(position);

                Intent intent = new Intent(ctx,ReminderBroadcastReceiver.class);
                AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                intent.putExtra("Anime Name",item.getAnimeName());
                PendingIntent pi = PendingIntent.getBroadcast(ctx,item.getReminderId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pi);
                reminders.remove(position);
                mRef.child("reminders").setValue(reminders);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.reminder_list_item,parent,false);
        return new ReminderHolder(itemView);
    }

    public class ReminderHolder extends RecyclerView.ViewHolder{
        TextView tvTime, tvAP, tvAnimeName, tvRRule;
        ImageButton btnDelete;

        public ReminderHolder(View itemView) {
            super(itemView);
            tvAnimeName = (TextView) itemView.findViewById(R.id.tvAnimeName);
            tvAP = (TextView) itemView.findViewById(R.id.tvAP);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvRRule = (TextView) itemView.findViewById(R.id.tvRRule);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        }
    }
}
