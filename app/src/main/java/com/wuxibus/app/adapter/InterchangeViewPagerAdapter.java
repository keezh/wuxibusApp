package com.wuxibus.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by zhongkee on 15/11/1.
 */
public class InterchangeViewPagerAdapter extends PagerAdapter {
    private List<View> pageViews;

    public InterchangeViewPagerAdapter(List<View> pageViews){
        this.pageViews = pageViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(pageViews.size() == 0){
            return null;
        }
        position %= pageViews.size();
        if (position<0){
            position = pageViews.size()+position;
        }
        View view = pageViews.get(position % pageViews.size());
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        //add listeners here if necessary
        return view;
    }


    @Override
    public int getCount() {
//        if(pageViews.size() == 1){
//            return  1;
//        }
        return pageViews.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
