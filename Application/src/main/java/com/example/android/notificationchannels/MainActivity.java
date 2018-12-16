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
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTI_PRIMARY1 = 1100;

    private MainUi ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ui = new MainUi(findViewById(R.id.activity_main));
    }


    public int getCurrentTimeOffsetInSecond(String hourText, String minuteText){
        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);
        int offset = hour * 60 * 60 + minute * 60;
        return offset;
    }
    public static class Util{
        static private final String SPName = "Util";
        static private final String SPkey = "alarm";
        static private final String SPValue = "a";
        static private final String SPEmptyValue = null;
        static public final int defaultOffset = 7000;

        public static void waitAndAlarm(Context context){
            waitAndAlarm(context, defaultOffset);
        }
        private static AlarmManager getAlarmManager(Context context){
            AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
            return am;
        }
        public static void waitAndAlarm(Context context, int offset){
            // 获取闹铃管理
            PendingIntent pi = buildPendingIntent(context);
            getAlarmManager(context).set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+offset, pi);
        }
        private static PendingIntent buildPendingIntent(Context context){
            // see [android——闹铃不准的解决](https://blog.csdn.net/guduyishuai/article/details/54946179)
            Intent intent = new Intent(context, AlarmReceiver.class);
            // 设置intent的动作,识别当前设置的是哪一个闹铃,有利于管理闹铃的关闭
            intent.setAction("a");
            // 用广播管理闹铃
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            return pi;
        }
        private static void cancelAlarm(Context context){
            getAlarmManager(context).cancel(buildPendingIntent(context));
        }
        public static void setOneAlarm(Context ctx, MainUi ui, int offset){
            // SharedPreferences see https://www.jianshu.com/p/d2a12f531d97
            SharedPreferences diySP = ctx.getSharedPreferences(SPName,MODE_PRIVATE) ;
            String s = diySP.getString(SPkey, SPEmptyValue);
            if(s != SPEmptyValue){
                ui.log("stop last alarm");
                diySP.edit().putString(SPkey, SPEmptyValue).apply();
                cancelAlarm(ctx);
                return;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss //获取当前时间
            String then = simpleDateFormat.format(new Date(System.currentTimeMillis() + offset));
            final String contentString = String.format("ring at %s", then);
            ui.log(contentString);
            diySP.edit().putString(SPkey, SPValue).apply();
            waitAndAlarm(ctx, offset);
        }
    }
    public static class AlarmReceiver  extends BroadcastReceiver {
        // see http://www.voidcn.com/article/p-risqesev-tg.html
        @Override
        public void onReceive(Context context, Intent intent2) {
            // TODO Auto-generated method stub
            NotificationHelper noti = new NotificationHelper(context);
            Notification.Builder nb = noti.getNotification1("wake up!", "a");
            noti.notify(NOTI_PRIMARY1, nb);
            Log.d(TAG, String.format("onReceive: zjj: %s", "ringing"));
            Util.waitAndAlarm(context);
        }
    }
    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample
     * seperate.)
     */
    class MainUi implements View.OnClickListener {
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
                    int offset = getCurrentTimeOffsetInSecond(getHourText(), getMinuteText())*1000;
                    Util.setOneAlarm(MainActivity.this, ui, offset);
                    break;
                default:
                    Log.e(TAG, "Unknown click event.");
                    break;
            }
        }
    }
}
