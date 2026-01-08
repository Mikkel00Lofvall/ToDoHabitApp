package com.example.todo.habit;

import android.os.Handler;
import android.os.Looper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitItem implements Serializable {

    private static final ExecutorService habitExecutorService =
            Executors.newSingleThreadExecutor();

    private String title;
    private String creationTime;

    private String frequency;
    private String goal;

    private boolean isDone;

    private String lastActivated = null;
    private int streak = 0;

    private boolean streakHasAlreadyBeenClaimed = false;

    public HabitItem(String title, String frequency, String creationTime, String goal) {
        this.title = title;
        this.frequency = frequency;
        this.creationTime = creationTime;
        this.goal = goal;
    }

    public void updateStreak() {
        if (frequency == null) return;

        String freq = frequency.toLowerCase(Locale.ROOT);
        boolean intervalPassed = false;
        boolean missedInterval = false;

        switch (freq) {
            case "daily":
                intervalPassed = !SameDate();
                missedInterval = intervalPassed && !isYesterday();
                break;
            case "weekly":
                intervalPassed = isWeekPassed();
                missedInterval = intervalPassed && !isLastWeek();
                break;
            default:
                return;
        }

        if (missedInterval) {
            streak = 0;
            streakHasAlreadyBeenClaimed = false;
            isDone = false;
        } else if (intervalPassed) {
            isDone = false;
            streakHasAlreadyBeenClaimed = false;
        }

        if (isDone && !streakHasAlreadyBeenClaimed) {
            streak++;
            streakHasAlreadyBeenClaimed = true;
        }
    }


    private boolean isYesterday() {
        if (lastActivated == null) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
        try {
            Date lastDate = sdf.parse(lastActivated);
            Calendar last = Calendar.getInstance();
            last.setTime(lastDate);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);

            return last.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                    && last.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isLastWeek() {
        if (lastActivated == null) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
        try {
            Date lastDate = sdf.parse(lastActivated);
            Calendar last = Calendar.getInstance();
            last.setTime(lastDate);

            Calendar lastWeek = Calendar.getInstance();
            lastWeek.add(Calendar.DAY_OF_YEAR, -7);

            return last.get(Calendar.YEAR) == lastWeek.get(Calendar.YEAR)
                    && last.get(Calendar.WEEK_OF_YEAR) == lastWeek.get(Calendar.WEEK_OF_YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isWeekPassed() {
        if (this.lastActivated == null) return false;

        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());

        try {
            Date lastDate = sdf.parse(this.lastActivated);

            Calendar last = Calendar.getInstance();
            last.setTime(lastDate);

            Calendar today = Calendar.getInstance();

            long diffMillis = today.getTimeInMillis() - last.getTimeInMillis();
            long diffDays = diffMillis / (1000L * 60 * 60 * 24);

            return diffDays >= 7;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean SameDate() {
        if (this.lastActivated == null) return false;

        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());

        try {
            Date lastDate = sdf.parse(this.lastActivated);
            Calendar last = Calendar.getInstance();
            last.setTime(lastDate);

            Calendar today = Calendar.getInstance();

            if (last.get(Calendar.YEAR) == today.get(Calendar.YEAR) && last.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }

            return false;
        }

        catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getTitle() { return this.title; }
    public String getCreationTime() { return this.creationTime; }
    public String getFrequency() { return this.frequency; }
    public String getGoal() { return this.goal; }
    public boolean getIsDone() { return this.isDone; }
    public void setDone(boolean done) { this.isDone = done; }

    public void setStreak(int newStreak) { this.streak = newStreak; }
    public void clearStreak() { this.streak = 0; }
    public void setLastActivated(String time) { this.lastActivated = time; }
    public String getLastActivated() { return this.lastActivated; }
    public int getStreak() { return this.streak; }

}
