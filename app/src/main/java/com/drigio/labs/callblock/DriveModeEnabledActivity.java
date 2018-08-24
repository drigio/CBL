package com.drigio.labs.callblock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class DriveModeEnabledActivity extends AppCompatActivity {

    //Declare all the class variables here
    Button disableDriveMode;
    private static final String TAG = "DMEnabledActivity"; //Tag to identify in the Logs
    DevicePolicyManager devicePolicyManager;
    ComponentName DPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LaunchActivity.setLocale(LaunchActivity.getLocale(DriveModeEnabledActivity.this),DriveModeEnabledActivity.this); //Set the default locale
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_mode_enabled);

        //Make the activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Find all The GUI Elements
        disableDriveMode = findViewById(R.id.disableDriveMode);
        PulsatorLayout pulsator = findViewById(R.id.pulsator);

        //Start the pulsing of the disable DriveMode button
        pulsator.start();

        //Set the function of disableDriveMode button on long clicking it
        //So that this driveMode wont be disabled by mistakenly touching it
        disableDriveMode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Stop the DriveModeEnabledService
                stopService(new Intent(DriveModeEnabledActivity.this,DriveModeEnabledService.class));
                //Give a notification stating the number of calls that were blocked and
                //On Clicking the notification take them to the CallsBlocked Activity
                showCallLogs();
                //Unpin the app
                /*if(devicePolicyManager.isDeviceOwnerApp(getPackageName())) {
                    stopLockTask();
                }*/
                //Return to the Main Screen
                Intent mainScreen = new Intent(DriveModeEnabledActivity.this,LaunchActivity.class);
                startActivity(mainScreen);
                finish();
                return true;
            }
        });

        //Start the DriveModeEnabledService
        if(isEnabled()){
            //Do Nothing
        }else
        {
            //Stop the SpeedUpdatesService
            if(isAutoEnabled()) {
                stopService(new Intent(this,SpeedUpdates.class));

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFNAME,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAutoEnabled",false);
                editor.commit();
            }

            //Start DriveModeEnabled Service
            startService(new Intent(this,DriveModeEnabledService.class));
        }

    }



    private void showCallLogs() {
        //Start a Notification stating that Drive Mode has been enabled!
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String channelId = "DriveModeEnabled";
        final String channelName = "Drive Mode Enabled";
        final int notificationID = 1016;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        //Check if Api >= 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //On Clicking the notification take the user to CallLogsFragment Activity
        Intent intent = new Intent(this,LaunchActivity.class);
        intent.putExtra("showCallLogs",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification for all SDK Versions
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("Drive Mode Disabled")
                .setContentText("Touch to Check If People Called You!")
                .setSmallIcon(R.drawable.car)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        //Send the notification
        notificationManager.notify(notificationID,notification);
    }

    private boolean isEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences(Blocker.PREFNAME, MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("isEnabled", false);
        return isEnabled;
    }

    //Get the value that is stored in sharedPrefs
    private boolean isAutoEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFNAME,MODE_PRIVATE);
        boolean isAutoEnabled = sharedPreferences.getBoolean("isAutoEnabled",false);
        return isAutoEnabled;
    }

}
