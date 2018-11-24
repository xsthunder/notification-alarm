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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;

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
     *
     * @param id The ID of the notification to create
     * @param offset The offset in second for notification
     */
    public void loopAnHour(int id, Notification.Builder nb, int offset){
        if(nb == null){
            return;
        }
        class Tmp implements Runnable {
            int id;
            Notification.Builder nb;
            Tmp(int id, Notification.Builder nb){
                this.id = id;
                this.nb = nb;
            }
            @Override
            public void run() {
                noti.notify(id, nb);
            }
        }
        Handler handler = new Handler();
        for(int i = 0;i<3600;i++){
            handler.postDelayed(new Tmp(id, nb), 1800*i+offset*1000);
        }
    }
    public int getCurrentTimeOffsetInSecond(String hourText, String minuteText){

        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);

        int offset = hour * 60 * 60 + minute * 60;


        Notification.Builder nb = null;
        final String title = "Wake up!";
        final String contentString = String.format("read %d:%d After %d seconds",hour, minute, offset );
        nb = noti.getNotification1(title, contentString);
        Log.d(TAG, String.format("getCurrentTimeOffsetInSecond: %s",contentString));
        noti.notify(NOTI_PRIMARY1, nb);
        return offset;
    }
    public void sendNotification(int id, int offset) {
        Notification.Builder nb = null;
        final String title = "Wake up!";
        switch (id) {
            case NOTI_PRIMARY1:
                nb = noti.getNotification1(title, getString(R.string.primary1_body));
                break;

            case NOTI_PRIMARY2:
                nb = noti.getNotification1(title, getString(R.string.primary2_body));
                break;

            case NOTI_SECONDARY1:
                nb = noti.getNotification2(title, getString(R.string.secondary1_body));
                break;

            case NOTI_SECONDARY2:
                nb = noti.getNotification2(title, getString(R.string.secondary2_body));
                break;
        }
        if (nb != null) {
            loopAnHour(id, nb, offset);
        }
    }

    /**
     * Send Intent to load system Notification Settings for this app.
     */
    public void goToNotificationSettings() {
        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(i);
    }

    /**
     * Send intent to load system Notification Settings UI for a particular channel.
     *
     * @param channel Name of channel to configure
     */
    public void goToNotificationSettings(String channel) {
        Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        i.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
        startActivity(i);
    }

    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample
     * seperate.)
     */
    class MainUi implements View.OnClickListener {
        final TextView titlePrimary;
        final TextView titleSecondary;
        final TextView hour;
        final TextView minute;

        private MainUi(View root) {
            titlePrimary = (TextView) root.findViewById(R.id.hour);
            hour = (TextView) root.findViewById(R.id.hour);
            minute = (TextView) root.findViewById(R.id.minute);
            ((Button) root.findViewById(R.id.main_primary_send1)).setOnClickListener(this);
            ((Button) root.findViewById(R.id.main_primary_send2)).setOnClickListener(this);
            ((ImageButton) root.findViewById(R.id.main_primary_config)).setOnClickListener(this);

            titleSecondary = (TextView) root.findViewById(R.id.main_secondary_title);
            ((Button) root.findViewById(R.id.main_secondary_send1)).setOnClickListener(this);
            ((Button) root.findViewById(R.id.main_secondary_send2)).setOnClickListener(this);
            ((ImageButton) root.findViewById(R.id.main_secondary_config)).setOnClickListener(this);

            ((Button) root.findViewById(R.id.btnA)).setOnClickListener(this);
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

        private String getTitleSecondaryText() {
            if (titlePrimary != null) {
                return titleSecondary.getText().toString();
            }
            return "";
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_primary_send1:
                    sendNotification(NOTI_PRIMARY1, getCurrentTimeOffsetInSecond(getHourText(), getMinuteText()));
                    break;
//                case R.id.main_primary_send2:
//                    sendNotification(NOTI_PRIMARY2, getCurrentTimeOffsetInSecond());
//                    break;
//                case R.id.main_primary_config:
//                    goToNotificationSettings(NotificationHelper.PRIMARY_CHANNEL);
//                    break;

//                case R.id.main_secondary_send1:
//                    sendNotification(NOTI_SECONDARY1, getTitleSecondaryText());
//                    break;
//                case R.id.main_secondary_send2:
//                    sendNotification(NOTI_SECONDARY2, getTitleSecondaryText());
//                    break;
//                case R.id.main_secondary_config:
//                    goToNotificationSettings(NotificationHelper.SECONDARY_CHANNEL);
//                    break;
//                case R.id.btnA:
//                    goToNotificationSettings();
//                    break;
                default:
                    Log.e(TAG, "Unknown click event.");
                    break;
            }
        }
    }
}
