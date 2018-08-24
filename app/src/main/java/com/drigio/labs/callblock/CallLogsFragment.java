package com.drigio.labs.callblock;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CallLogsFragment extends Fragment {

    //Declare all your class variables here
    View view;
    Context context;
    private CallLogsDBHelper callLogsDBHelper;
    private ListView listView;

    public CallLogsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_call_logs, container, false);
        context = getContext();

        listView = view.findViewById(R.id.callLogsListView);
        callLogsDBHelper = new CallLogsDBHelper(context);

        populateListView();

        return view;
    }

    private void populateListView() {
        SQLiteDatabase sqLiteDatabase = callLogsDBHelper.getReadableDatabase();

        Cursor data = callLogsDBHelper.showCallLogs(sqLiteDatabase);

        if(data != null && data.getCount() > 0) {
            String columns[] = new String[] {
                    CallLogsContract.CallLogsEntry.CONTACT_NAME,
                    CallLogsContract.CallLogsEntry.CONTACT_NUMBER,
                    CallLogsContract.CallLogsEntry.DATE
            };

            int boundTo[] = new int[] {
                    R.id.callLogName,
                    R.id.callLogNumber,
                    R.id.callLogDate
            };

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context,R.layout.call_log_item,data,columns,boundTo,0);
            listView.setAdapter(simpleCursorAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        callLogsDBHelper.close();
    }
}
