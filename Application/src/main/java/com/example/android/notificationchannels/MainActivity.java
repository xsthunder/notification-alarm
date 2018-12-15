/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.notificationchannels;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NOTI_PRIMARY1 = 1100;

    /*
     * A view model for interacting with the UI elements.
     */
    private MainUi ui;

    /*
     * A
     */
    private NotificationHelper noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noti = new NotificationHelper(this);
        ui = new MainUi(findViewById(R.id.activity_main));
    }

    /**
     * Send activity notifications.
     */
    public void recursiveNotify( int offset){
        class Tmp implements Runnable {
            int counter;

            Tmp(int counter) {
                this.counter = counter;
            }

            @Override
            public void run() {
                int delay = 8000;
                int second = this.counter*delay/1000;
                int minute = second/60;
                second = second%60;
                String content = "after ";
                if(minute>0){
                    content = String.format("%s%d minute%s ", content, minute, minute==1?"":"s");
                }
                content = String.format("%s%d second%s", content , second, second==1?"":"s");
                Notification.Builder nb = noti.getNotification1("wake up!", content);
                if(nb == null)return ;
                noti.notify(NOTI_PRIMARY1, nb);
                Handler handler = new Handler();
                handler.postDelayed(new Tmp(this.counter+1), delay);
                Log.d(TAG, "run: zjj: in recursive");

            }
        }
        if(offset>=0){
            Handler handler = new Handler();
            int os = offset*1000;
            handler.postDelayed(new Tmp(0),os );
        }
    }
    public int getCurrentTimeOffsetInSecond(String hourText, String minuteText){

        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);

        int offset = hour * 60 * 60 + minute * 60;

        Notification.Builder nb = null;
        final String title = "Wake up!";
        final String contentString = String.format("zjj read %d:%d, After %d seconds",hour, minute, offset );
        nb = noti.getNotification1(title, contentString);
        Log.d(TAG, String.format("zjj getCurrentTimeOffsetInSecond: %s",contentString));
        noti.notify(NOTI_PRIMARY1, nb);
        return offset;
    }
    public void sendNotification(int offset) {
        Notification.Builder nb = null;
        final String title = "Wake up!";
        final String content = String.format( "waiting for %d seconds", offset);
        nb = noti.getNotification1(title, content);
        if(nb != null)noti.notify(NOTI_PRIMARY1, nb);
        if(nb != null)recursiveNotify( offset);
    }

    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample
     * seperate.)
     */
    class MainUi implements View.OnClickListener {
        final TextView hour;
        final TextView minute;

        private MainUi(View root) {
            hour = (TextView) root.findViewById(R.id.hour);
            minute = (TextView) root.findViewById(R.id.minute);
            ((ImageButton) root.findViewById(R.id.main_primary_send1)).setOnClickListener(this);
        }

        private String getHourText() {
            if (hour != null) {
                return hour.getText().toString();
            }
            return "";
        }


        private String getMinuteText() {
            if (minute != null) {
                return minute.getText().toString();
            }
            return "";
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_primary_send1:
                    sendNotification(getCurrentTimeOffsetInSecond(getHourText(), getMinuteText()));
                    break;
                default:
                    Log.e(TAG, "Unknown click event.");
                    break;
            }
        }
    }
}
