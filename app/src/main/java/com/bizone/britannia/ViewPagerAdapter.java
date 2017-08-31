package com.bizone.britannia;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.logreports.Logger;

import java.util.ArrayList;

/**
 * Created by siddhesh on 7/26/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG=ViewPagerAdapter.class.getSimpleName();
    private ArrayList<RoutePlanEntity>list;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<RoutePlanEntity>list) {
        super(fm);
        Logger.d(TAG,"inside ViewPagerAdapter");
        this.list=list;
        this.count=list.size();
        Log.d(TAG,"list="+list);
        Logger.d(TAG,"exitting ViewPagerAdapter");
    }

    @Override
    public Fragment getItem(int index) {

        Log.d(TAG,"inside getItem: index="+index);

        Logger.d(TAG,"exitting getItem");
        return FragmentOne.newInstance(list.get(index));

    }
    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        Logger.d(TAG,"inside getPageTitle");
        String dt=Commons.milliToDate(list.get(position).date);
        Logger.d(TAG,"exitting getPageTitle : pageTitle= "+dt);
        return dt;
    }

    int count=-1;
    @Override
    public int getCount() {
        Logger.d(TAG,"inside getCount");
        // get item count - equal to number of tabs
        Logger.d(TAG,"exitting getCount : listSize= " +count);
        return count;
    }

}


