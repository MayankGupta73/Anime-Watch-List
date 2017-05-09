package com.example.mayankgupta.animewatchlist.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


import com.example.mayankgupta.animewatchlist.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class PickerActivity extends AppCompatActivity {

    TextView  tvDate, tvTime;
    Button btnPick, btnSet;
    EditText etAnimeName;
    RadioButton rbNoRepeat, rbDaily, rbWeekly;

    int mHour, mMinute,mYear, mMonth, mDay;
    String mRecurrenceRule;

    Calendar now;
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    DatePickerDialog.OnDateSetListener odl = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            tvDate.setText(mDay+"/"+(mMonth+1)+"/"+mYear);

            tpd.show(getFragmentManager(),"Timepickerdialog");
        }
    };

    TimePickerDialog.OnTimeSetListener otl = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
            mHour = hourOfDay;
            mMinute = minute;

            tvTime.setText(mHour+":"+mMinute);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        getSupportActionBar().setTitle("Set Reminder");

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnPick = (Button) findViewById(R.id.btnPick);
        btnSet = (Button) findViewById(R.id.btnSet);
        etAnimeName = (EditText) findViewById(R.id.etAnimeName);
        rbNoRepeat = (RadioButton) findViewById(R.id.rbNoRepeat);
        rbDaily = (RadioButton) findViewById(R.id.rbDaily);
        rbWeekly = (RadioButton) findViewById(R.id.rbWeekly);


        now = Calendar.getInstance();
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
        mHour =now.get(Calendar.HOUR_OF_DAY);
        mMinute = now.get(Calendar.MINUTE);

        dpd = DatePickerDialog.newInstance(odl,mYear,mMonth,mDay);
        tpd = TimePickerDialog.newInstance(otl,mHour,mMinute,true);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if(mHour > 12){
                    i.putExtra("AP","PM");
                    mHour = mHour - 12;
                }
                else {
                    i.putExtra("AP","AM");
                }
                String hour;
                if(String.valueOf(mHour).length() == 1){
                     hour = "0" + String.valueOf(mHour);
                }
                else  hour = String.valueOf(mHour);
                String minute;
                if(String.valueOf(mMinute).length() == 1){
                    minute = "0" + String.valueOf(mMinute);
                }
                else  minute = String.valueOf(mMinute);
                String time = hour + ":" + minute;
                i.putExtra("Time",time);
                String date = mDay+"/"+(mMonth+1)+"/"+mYear;
                i.putExtra("Date",date);
                i.putExtra("Anime Name",etAnimeName.getText().toString());
                if(rbDaily.isChecked()){
                    mRecurrenceRule = "Repeat Daily";
                }
                else if(rbWeekly.isChecked()){
                    mRecurrenceRule = "Repeat Weekly";
                }
                else mRecurrenceRule = "Do Not Repeat";

                i.putExtra("RRule",mRecurrenceRule);
                i.putExtra("Year",mYear);
                i.putExtra("Month",mMonth);
                i.putExtra("Day",mDay);
                i.putExtra("Hour",mHour);
                i.putExtra("Minute",mMinute);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }
}
