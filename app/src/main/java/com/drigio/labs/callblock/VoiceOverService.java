package com.drigio.labs.callblock;

// Class where all the background voice over music wil be played
// 1) For notifying Emergency calls
// 2) For notifying priority contact calls

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class VoiceOverService extends Service {

    //Declare all your class variables here
    private static final String PRIORITY_VOICE_OVER = "priority";
    private static final String TAG = "VoiceOverService";
    protected static final String SPEED_LIMIT_EXCEEDED = "speedlimit";
    protected static final String SPEED_FLUCTS = "speedFlucts";
    protected static final String PRIORITY_CONTACT = "priority";
    protected static final String DISTRESS = "distress";
    MediaPlayer player;
    Bundle bundle;
    String voiceOver;
    int count;

    public VoiceOverService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        bundle = intent.getExtras();
        if(bundle != null) {
            voiceOver = bundle.getString("voiceOver");

            switch (voiceOver) {
                case EmergencySMS.EMERGENCY_KEYWORD :
                    //Ask media player to play Emergency Voice over
                    playEmergency();
                    break;
                case PRIORITY_VOICE_OVER :
                    //Ask media player to play Priority Voice over
                    playPriority();
                    break;
                case SPEED_LIMIT_EXCEEDED :
                    //Ask media player to play Speed Limit Voice over
                    playSpeedLimit();
                    break;
                case SPEED_FLUCTS :
                    //Ask media player to play Speed Limit Voice over
                    playSpeedFlucts();
                    break;
                case  DISTRESS :
                    //Ask media player to play Speed Limit Voice over
                    playDistress();
                    break;
                default :
                    Log.d(TAG,"Error : Not received any of the voice overs");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //Plays the voice over for 5 times and then destroys the service
    private void playEmergency() {
        Log.d(TAG,"Playing Emergency Voice Over");
        player = MediaPlayer.create(this,R.raw.emergency_call);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 4;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    private void playPriority() {
        Log.d(TAG,"Playing Priority Contact Voice Over");
        player = MediaPlayer.create(this,R.raw.priority);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 2;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    private void playSpeedLimit() {
        Log.d(TAG,"Playing Speed Limit Voice Over");
        player = MediaPlayer.create(this,R.raw.speed_limit_exceeded);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 1;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    private void playDistress() {
        Log.d(TAG,"Playing Distress Voice Over");
        player = MediaPlayer.create(this,R.raw.beep);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 9;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    private synchronized void playSpeedFlucts() {
        Log.d(TAG,"Playing Speed Limit Voice Over");
        player = MediaPlayer.create(this,R.raw.acceleration_flucts);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 1;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Release the player
        if(player != null) {
            player.stop();
            player.reset();
            player.release();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
