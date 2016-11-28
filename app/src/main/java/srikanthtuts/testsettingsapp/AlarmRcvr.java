package srikanthtuts.testsettingsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Buchale.Reddy on 03-09-2016.
 */
public class AlarmRcvr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Alarm Recevied",Toast.LENGTH_SHORT).show();
        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(context, notification);
        mp.start();*/
        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        context.startService(startIntent);
    }
}
