package com.wuxibus.app.adapter;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wuxibus.app.fragment.RouteAroundFragment;
import com.wuxibus.app.fragment.RouteHistoryFragment;
import com.wuxibus.app.fragment.RouteStoreFragment;

public class LineViewPagerAdapter extends FragmentPagerAdapter {
    private int size;
    private String titles[];
    private Handler myHandler;

    public LineViewPagerAdapter(FragmentManager fm, Handler myHandler, String titles[]) {
        super(fm);
        this.titles = titles;
        this.size = titles.length;
        this.myHandler = myHandler;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RouteStoreFragment();

            case 1:
                return new RouteAroundFragment();

            case 2:
                return new RouteHistoryFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return size;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
