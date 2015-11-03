
package com.wuxibus.app.adapter;

import android.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;



import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {
    private List<ImageView> mImageViews;
    private Fragment fragment;

    public MyViewPagerAdapter(List<ImageView> mImageViews) {
       // super();
        this.mImageViews = mImageViews;//构造方法，参数是我们的页卡，这样比较方便。
       // this.homeFragment = homeFragment;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        if(mImageViews.size()>3){
//            container.removeView(mImageViews.get(position % mImageViews.size()));//删除页卡
//
//        }
     //   container.removeView(mImageViews.get(position % mImageViews.size()));//删除页卡

       // container.removeView((View) object);
    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        if(object instanceof HomeFragment){
//            HomeFragment f = (HomeFragment) object;
//            View v = f.getView();
//            if(v != null){
//                v.bringToFront();
//            }
//        }
//    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡

//        ((ViewPager)container).addView(mImageViews.get(position % mImageViews.size()), 0);
//        return mImageViews.get(position % mImageViews.size());


        //对ViewPager页号求模取出View列表中要显示的项
        if(mImageViews.size() == 0){
            return null;
        }
        position %= mImageViews.size();
        if (position<0){
            position = mImageViews.size()+position;
        }
        ImageView view = mImageViews.get(position % mImageViews.size());
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
        if(mImageViews.size() == 1){
            return  1;
        }
        return Integer.MAX_VALUE;//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;//官方提示这样写
    }
}