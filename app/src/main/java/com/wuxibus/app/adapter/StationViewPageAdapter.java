package com.wuxibus.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.wuxibus.app.fragment.StationAroundFragment;
import com.wuxibus.app.fragment.StationHistoryFragment;

/**
 * Created by zhongkee on 15/7/23.
 */
public class StationViewPageAdapter extends FragmentPagerAdapter {

    private int size;
    private String titles[];

    public StationViewPageAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new StationAroundFragment();
            case 1:
                return new StationHistoryFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    //bug

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f =  (Fragment)super.instantiateItem(container, position);

        //String title = titles[position];
       // f.setTitle(title);
        return f;
    }

    @Override
    public int getItemPosition(Object object) {
        //return super.getItemPosition(object);
        return PagerAdapter.POSITION_NONE;
    }
}
