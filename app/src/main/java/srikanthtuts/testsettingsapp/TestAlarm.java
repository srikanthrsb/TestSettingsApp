package srikanthtuts.testsettingsapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TestAlarm extends AppCompatActivity {

    TextView tvAlarmStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_alarm);
        tvAlarmStatus = (TextView)findViewById(R.id.tvAlarmStatus);
        setButtonClicks();
    }

    private void setButtonClicks() {

        findViewById(R.id.btnStartAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(TestAlarm.this,AlarmRcvr.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1234,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                long when = Calendar.getInstance().getTimeInMillis()+1*1000;
                alarmManager.set(AlarmManager.RTC_WAKEUP,when,pendingIntent);
                tvAlarmStatus.setText("Alarm Started!");
            }
        });


        findViewById(R.id.btnCancelAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestAlarm.this, AlarmRcvr.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1234, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                mp.stop();*/
                Intent stopIntent = new Intent(TestAlarm.this, RingtonePlayingService.class);
                getApplicationContext().stopService(stopIntent);
                tvAlarmStatus.setText("Alarm Canceled!");
            }
        });
    }



}