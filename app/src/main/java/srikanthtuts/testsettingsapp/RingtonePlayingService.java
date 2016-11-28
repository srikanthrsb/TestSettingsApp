package srikanthtuts.testsettingsapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

/**
 * Created by Buchale.Reddy on 03-09-2016.
 */
public class RingtonePlayingService extends Service
{
    private Ringtone ringtone;
    MediaPlayer mp;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();
        /*
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(this, notification);
        mp.setVolume(10f,20f);
        mp.start();*/

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        ringtone.stop();
        //mp.stop();
    }
}
