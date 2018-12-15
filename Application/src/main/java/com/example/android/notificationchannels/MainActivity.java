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
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

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


    public int getCurrentTimeOffsetInSecond(String hourText, String minuteText){
        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);

        int offset = hour * 60 * 60 + minute * 60;

        return offset;
    }

    class Alarm{
        int offset;
        MainUi ui;
        Alarm(int offset, MainUi ui){
            this.offset = offset;
            this.ui = ui;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss //获取当前时间
            String then = simpleDateFormat.format(new Date(System.currentTimeMillis() + offset*1000));
            final String contentString = String.format("ring at %s", then);
            ui.log(contentString);
            recursiveNotify();
        }
        /**
         * Send activity notifications.
         */
        public void recursiveNotify(){
            class Tmp implements Runnable {
                int counter;
                MainUi ui;
                Alarm alarm;
                Tmp(int counter, MainUi ui, Alarm alarm) {
                    this.counter = counter;
                    this.ui = ui;
                    this.alarm = alarm;
                }

                @Override
                public void run() {
                    if(ui.alarm != alarm){
                        return;
                    }
                    if(counter==0){
                        ui.log("start ringing");
                    }
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
                    handler.postDelayed(new Tmp(counter+1, ui, alarm), delay);
                    Log.d(TAG, "run: zjj: in recursive");
                }
            }
            if(offset>=0){
                Handler handler = new Handler();
                int os = offset*1000;
                handler.postDelayed(new Tmp(0, ui, this),os );
            }
        }
    }
    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample
     * seperate.)
     */
    class MainUi implements View.OnClickListener {
        Alarm alarm = null;
        final TextView hour;
        final TextView minute;
        final TextView log;

        private MainUi(View root) {
            hour = (TextView) root.findViewById(R.id.hour);
            minute = (TextView) root.findViewById(R.id.minute);
            log = (TextView) root.findViewById(R.id.log);
            ((ImageButton) root.findViewById(R.id.main_primary_send1)).setOnClickListener(this);
        }

        public void log(String s){
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss //获取当前时间
            final String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            final String content = String.format("%s:%s\n", now,s);
            this.log.append(content);
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
                    int offset = getCurrentTimeOffsetInSecond(getHourText(), getMinuteText());
                    if(this.alarm!=null){
                        this.log("stop last alarm");
                        this.alarm=null;
                        return;
                    }
                    this.alarm = new Alarm(offset, this);
                    break;
                default:
                    Log.e(TAG, "Unknown click event.");
                    break;
            }
        }
    }
}
