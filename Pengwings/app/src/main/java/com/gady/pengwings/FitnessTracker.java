package com.gady.pengwings;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gordontang on 2015-02-21.
 */
public class FitnessTracker {

    //tag for logcat output (e.g. console output)
    private static final String TAG = FitnessTracker.class.getSimpleName();

    static int[] stepsArray = new int[30]; // data stored for 2 days
    static Date dailyArrayUpdate = new Date();
    static Boolean recordedToday = false;

    // Fitness tool to be called in Main
    public FitnessTracker (Context context) {
        init(context);

    }

    // initialize fitness data
    public void init(Context context) {
        stepsArray = initializeStepsArray(stepsArray);
    }

    // initialize array for steps per day in last 30 days
    private int[] initializeStepsArray (int[] stepsArray) {
        for (int i=0; i<stepsArray.length; i++) {
            stepsArray[i] = -1;
        }
        return stepsArray;
    }

    // update dates associated with step data
    public void updateDailyStepsArray () {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date d1 = c.getTime();

        SimpleDateFormat md =
                new SimpleDateFormat ("MM.dd");

        // if - last update was today, then update today's step count
        // else - last update was not today (e.g. yesterday, day before that), then
            // - subif - all elements have been filled, shift all array elements by one
            // and update last element (at end of array) to today's step count
            // - subelse - find last element used and update it
        if (md.format(dailyArrayUpdate).equals(md.format(d1))){
            recordSteps(stepsArray);
        }
        else {
            if (stepsArray[stepsArray.length-1] != -1) { // all elements used
                // shift all array elements by one element toward i=0 (i=0 is erased)
                for (int i = 0; i < stepsArray.length; i++) {
                    if (i != (stepsArray.length - 1)) {
                        stepsArray[i] = stepsArray[i + 1];
                    } else { // at last element of array
                        stepsArray[i] = -1;
                    }
                }
            }
            else { // not all elements used

            }
        }
    }

    // record Step Counter data from phone
    private void recordSteps (int[] stepsArray) {
        // find spot in array to record data
        int i = findLatestStepData(stepsArray);

        // place current step data in array
        if (recordedToday) //if data entered today, then record in latest element
            stepsArray[i] = Sensor.TYPE_STEP_COUNTER;
        else // if not, enter in newest element
            stepsArray[i+1] = Sensor.TYPE_STEP_COUNTER;

    }

    // assign a tier to the average steps (Fitness score) based on rolling 2-day average
    private int assignTier(int[] stepsArray, Date dailyUpdate) {
        int tier = 0; // 0 is neutral, 1 is good, -1 is bad

        int i = findLatestStepData(stepsArray);

        // average last 2 days' steps
        int stepAvg = stepsArray[i] + stepsArray[i-1];

        // assign tier based on average step #
        if (stepAvg >= 9000)
            tier = 1;
        else if (stepAvg >= 5000)
            tier = 0;
        else
            tier = -1;

        Log.d(TAG,"tier = "+tier);
        return tier;
    }

    private int findLatestStepData (int[] stepsArray) {
        // find last 2 days' steps
        int i = stepsArray.length-1; //start at end of array
        while (stepsArray[i] == -1) {
            i--;
        };
        Log.d(TAG,"i = "+i);
        return i;
    }

}
