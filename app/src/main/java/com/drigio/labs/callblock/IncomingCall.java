package com.drigio.labs.callblock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//BroadCast Receiver Class to Receive notifications about incoming calls and then Block Them.

public class IncomingCall extends BroadcastReceiver {

    //Declare all the class variables here
    private static final String TAG = "IncomingCall";

    //State What to do onReceiving the notification about an Incoming Call
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){  //check if there is an incoming call
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); //get the incoming number
                Log.d("Incoming Call", "incoming number : " + number);
                declinePhone(context); //Actual Implementation of Blocking Calls
                Log.d("Incoming Call","Call Cut Ringing");
                //Get the contact details from Contacts if present else return "Unknown number"
                String name = getContactDetails(number,context);
                Log.d(TAG,name);
                //Save the log to SQLite
                saveCallLogs(name,number,context);
                //Send a msg to the caller if AutoReply is Enabled
                if(MainActivity.isAutoReplyEnabled(context)) {
                    sendSMS(number,context);
                }
                //Check if Priority Contact Call
                SharedPreferences sharedPreferences = context.getSharedPreferences(PriorityCallFragment.PREFNAME,Context.MODE_PRIVATE);
                String[] priorityNumbers = sharedPreferences.getString("priorityNumbers",PriorityCallFragment.DEFAULT_PRIORITY_NOS).split(",");
                for(String no : priorityNumbers) {
                    Log.e(TAG,no + " " + number);
                    if("+91".concat(no).equals(number)){
                        Log.d(TAG,"Priority Call " + no);
                        Intent intent1 = new Intent(context,VoiceOverService.class);
                        intent1.putExtra("voiceOver",VoiceOverService.PRIORITY_CONTACT);
                        context.startService(intent1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Actual Method to cut the incoming calls
    private void declinePhone(Context context) throws Exception {

        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Cant disconnect call....");
        }
    }

    private String getContactDetails(String number,Context context){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactsLookup = contentResolver.query(uri,null,null,null,null);

        try{
            if(contactsLookup != null && contactsLookup.getCount() > 0) {
                contactsLookup.moveToNext();
                name = contactsLookup.getString(contactsLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }else {
                name = "Unknown Number";
            }
        }finally {
            if(contactsLookup != null) {
                contactsLookup.close();
            }
        }
        return name;
    }

    private void saveCallLogs(String name,String number,Context context) {
        CallLogsDBHelper callLogsDBHelper = new CallLogsDBHelper(context);
        SQLiteDatabase sqLiteDatabase = callLogsDBHelper.getWritableDatabase();

        //Get current time when call came in
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        callLogsDBHelper.addCallLog(name,number,date,sqLiteDatabase);
        callLogsDBHelper.close();
    }

    protected static void sendSMS(String phoneNo,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFNAME,Context.MODE_PRIVATE);
        String msg = sharedPreferences.getString("autoReplyMsg",MainActivity.DEFAULT_AUTO_REPLY)
                + " Reply with 'emergency' for call back"
                + System.getProperty("line.separator")
                + "Sent using SafeDriveDroid";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.d(TAG,"Message sent to the caller");
        } catch (Exception ex) {
            Log.d(TAG,"Error : Message not sent to the caller");
            ex.printStackTrace();
        }
    }

}
