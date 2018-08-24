package com.drigio.labs.callblock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Declare all the class variables here
    protected static final String PREFNAME = "Settings";
    protected static final String DEFAULT_AUTO_REPLY = "I'm currently driving. Please try calling again later.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected static boolean isEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Blocker.PREFNAME,MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("isEnabled",false);
        return isEnabled;
    }

    //Get the value that is stored in sharedPrefs
    protected static boolean isAutoEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        boolean isAutoEnabled = sharedPreferences.getBoolean("isAutoEnabled",false);
        return isAutoEnabled;
    }

    protected static void saveAutoReplyMsg(EditText autoReplyMsg, Context context) {
        String msg = autoReplyMsg.getText().toString();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoReplyMsg",msg);
        editor.apply();
        Toast.makeText(context,"Auto Reply Text Message Changed!",Toast.LENGTH_LONG).show();
    }

    protected static String getAutoReplyMsg(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getString("autoReplyMsg",DEFAULT_AUTO_REPLY);
    }

    protected static boolean isDefaultAutoReplyEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isDefaultAutoReplyText",true);
    }

    public static boolean isAutoReplyEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isAutoReplyEnabled",true);
    }
}
