package com.drigio.labs.callblock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//Adapter class for implementing view pager

public class PageAdapter extends FragmentStatePagerAdapter {

    //Declare your class variables here
    int noOfTabs;
    String[] tabTitles;

    public PageAdapter(FragmentManager fm, int noOfTabs, Context context) {
        super(fm);
        this.noOfTabs = noOfTabs;
        tabTitles = new String[]{
                context.getString(R.string.drive_mode_tab),
                context.getString(R.string.auto_reply_tab),
                context.getString(R.string.call_logs_tab)
        };
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                DriveModeFragment driveModeFragment = new DriveModeFragment();
                return driveModeFragment;
            case 1 :
                AutoReplyFragment autoReplyFragment = new AutoReplyFragment();
                return autoReplyFragment;
            case 2 :
                CallLogsFragment callLogsFragment = new CallLogsFragment();
                return callLogsFragment;
            default :
                    return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
