package com.mayank7319.mayankgupta.otakulist.models;

/**
 * Created by Mayank Gupta on 20-04-2017.
 */

public class Reminder {

    public Reminder(String animeName, String ap, String recurrenceRule, int minute, int hour, int day, int month, int year) {
        this.animeName = animeName;
        this.ap = ap;
        this.recurrenceRule = recurrenceRule;
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;

        reminderId = this.animeName.hashCode();
    }

    int year, month, day, hour, minute;
    String recurrenceRule, ap, animeName;
    int reminderId;

    public Reminder() {
        //Empty Constructor
    }

    public int getReminderId() {

        return reminderId;
    }

    public String getAp() {
        return ap;
    }

    public String getAnimeName() {
        return animeName;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
