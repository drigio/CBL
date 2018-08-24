package com.drigio.labs.callblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AutoReplyFragment extends Fragment {

    //Declare all your class variables here
    View view;
    Button setAutoReply;
    CheckBox setDefaultAutoReplyText,enableAutoReply;
    EditText autoReplyMsg;
    Context context;

    public AutoReplyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_auto_reply, container, false);
        context = getContext();

        //Find all the views
        autoReplyMsg = view.findViewById(R.id.autoReplyMsg);
        setAutoReply = view.findViewById(R.id.setAutoReply);
        setDefaultAutoReplyText = view.findViewById(R.id.setDefaultAutoReply);
        enableAutoReply = view.findViewById(R.id.enableAutoReply);

        //Set the enableAutoReply Checkbox to whatever the user already saved
        if(!MainActivity.isAutoReplyEnabled(context)) {
            //If it is not enabled
            enableAutoReply.setChecked(false);
            autoReplyMsg.setEnabled(false);
            setDefaultAutoReplyText.setEnabled(false);
            setAutoReply.setEnabled(false);
        }else {
            //If it is enabled
            enableAutoReply.setChecked(true);
            //in the beginning keep it disabled
            autoReplyMsg.setEnabled(!MainActivity.isDefaultAutoReplyEnabled(context));
            setAutoReply.setEnabled(!MainActivity.isDefaultAutoReplyEnabled(context));
        }

        //Set the state of setDefaultAutoReplyText to true if already set
        setDefaultAutoReplyText.setChecked(MainActivity.isDefaultAutoReplyEnabled(context));

        //Set the autoReplyMsg EditText field to whatever the user already saved
        autoReplyMsg.setText(MainActivity.getAutoReplyMsg(context));

        //Set the function of enableAutoReply CheckBox on clicking it
        enableAutoReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAutoReplyEnabled = ((CheckBox) view).isChecked();
                //Store the value in sharePrefs to access them later
                SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFNAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAutoReplyEnabled",isAutoReplyEnabled);
                editor.commit();

                //Enable/Disable the setDefaultAutoReplyText CheckBox and autoReplyMsg EditText
                if(isAutoReplyEnabled) {
                    setDefaultAutoReplyText.setEnabled(true);
                    autoReplyMsg.setEnabled(!MainActivity.isDefaultAutoReplyEnabled(context));
                    setAutoReply.setEnabled(!MainActivity.isDefaultAutoReplyEnabled(context));
                }else {
                    setDefaultAutoReplyText.setEnabled(false);
                    autoReplyMsg.setEnabled(false);
                    setAutoReply.setEnabled(false);
                }
            }
        });

        //Set the function of Default Auto reply text msg
        setDefaultAutoReplyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDefaultAutoReplyText = ((CheckBox) view).isChecked();
                //Store the value in sharedPrefs to access them later
                SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFNAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isDefaultAutoReplyText",isDefaultAutoReplyText);
                editor.putString("autoReplyMsg",MainActivity.DEFAULT_AUTO_REPLY);
                editor.commit();

                //Enable/Disable the autoReplyMsg EditText and setAutoReply Button
                if(isDefaultAutoReplyText) {
                    autoReplyMsg.setEnabled(false);
                    autoReplyMsg.setText(MainActivity.getAutoReplyMsg(context));
                    setAutoReply.setEnabled(false);
                }else {
                    autoReplyMsg.setEnabled(true);
                    setAutoReply.setEnabled(true);
                }
            }
        });

        //Set what to do when clicked on setAutoReply button
        setAutoReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.saveAutoReplyMsg(autoReplyMsg,context);
            }
        });


        return view;
    }

}
