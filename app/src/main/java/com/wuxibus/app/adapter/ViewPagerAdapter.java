package com.wuxibus.app.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.wuxibus.app.R;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import java.util.List;

/**
 * Created by KIM on 2015/7/14 0014.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<String> pList;
    private Context context;
    private int p;

    public ViewPagerAdapter(Context context, List<String> pList) {
        this.context = context;
        this.pList = pList;
    }

    @Override
    public int getCount() {
        return pList.size() == 1 ? 1 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        p = position % pList.size();
        String imgurl = pList.get(p);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView touchNoNetImageView = new ImageView(context);
        touchNoNetImageView.setImageResource(R.color.view_bg_color);
        boolean flag = BitmapCache.getInstern().getSDCardBitmap(imgurl, touchNoNetImageView, new BitmapCache.CallBackSDcardImg() {
            @Override
            public void setImgToView(Bitmap bitmap, ImageView imgView) {
                new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                imgView.setImageBitmap(bitmap);
            }
        });
        if (!flag) {
            VolleyManager.loadImage(touchNoNetImageView, imgurl, R.drawable.background_img);
        }
        touchNoNetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        touchNoNetImageView.setLayoutParams(mParams);
        container.addView(touchNoNetImageView);

        return touchNoNetImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
