package com.wuxibus.app.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.HomeClass;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import java.util.List;

/**
 * Created by zhongkee on 15/7/18.
 */
public class HomeClassAdapter extends BaseAdapter {

    Context context;
    List<HomeClass> list;

    public HomeClassAdapter(Context context,List<HomeClass> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.home_class,null);
            viewHolder.iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textview);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //必须要加个默认图片，否则UI显示换乱，这个bug找了好久
       // VolleyManager.loadImage(viewHolder.iconImageView,list.get(i).getIndex_pic(),R.drawable.background_img);
        String imgUrl = list.get(i).getIndex_pic();
        boolean flag = BitmapCache.getInstern().getSDCardBitmap(imgUrl, viewHolder.iconImageView, new BitmapCache.CallBackSDcardImg() {
            @Override
            public void setImgToView(Bitmap bitmap, ImageView imgView) {
                new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                imgView.setImageBitmap(bitmap);
            }
        });
        if (!flag) {
            VolleyManager.loadImage(viewHolder.iconImageView, imgUrl, R.drawable.background_img);
        }
        viewHolder.titleTextView.setText(list.get(i).getTitle());
        viewHolder.url = list.get(i).getUrl();
        return view;
    }

    public class ViewHolder{
        public ImageView iconImageView;
        public TextView titleTextView;
        public String url;

    }
}
