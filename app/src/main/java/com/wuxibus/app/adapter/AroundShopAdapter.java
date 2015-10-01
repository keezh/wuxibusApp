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
import com.wuxibus.app.entity.Shop;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;


import java.util.List;

/**
 * Created by zhongkee on 15/7/12.
 */
public class AroundShopAdapter extends BaseAdapter {
    Context context;
    List<Shop> shopList;

    public AroundShopAdapter(Context context,List<Shop> shopList){
        this.context = context;
        this.shopList = shopList;
    }

    @Override
    public int getCount() {
        return shopList.size();
    }

    @Override
    public Object getItem(int i) {
        return shopList.get(i);
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
            view = View.inflate(context, R.layout.stop_around_shop,null);
            viewHolder.thumbnailImageView = (ImageView) view.findViewById(R.id.thumbnail_imageview);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textview);
            viewHolder.addressTextView = (TextView) view.findViewById(R.id.address_textview);
            viewHolder.flagsTextView = (TextView) view.findViewById(R.id.flags_textview);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.titleTextView.setText(shopList.get(i).getTitle());
        viewHolder.addressTextView.setText(shopList.get(i).getSjadd());
        viewHolder.url = shopList.get(i).getUrl();
        String flags="";
        for (int j = 0; j < shopList.get(i).getFlags().size(); j++) {
            flags = flags + shopList.get(i).getFlags().get(j) + " ";
        }
        viewHolder.flagsTextView.setText(flags);

        String index_pic = shopList.get(i).getIndex_pic() + "/200x150";

        boolean flag = BitmapCache.getInstern().getSDCardBitmap(index_pic, viewHolder.thumbnailImageView, new BitmapCache.CallBackSDcardImg() {
            @Override
            public void setImgToView(Bitmap bitmap, ImageView imgView) {
                new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                imgView.setImageBitmap(bitmap);
            }
        });
        if (!flag) {
            VolleyManager.loadImage(viewHolder.thumbnailImageView, index_pic, R.drawable.background_img);
        }

       // VolleyManager.loadImage(viewHolder.thumbnailImageView,index_pic,R.drawable.background_img);

        return view;
    }

    public class ViewHolder{
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView addressTextView;
        public TextView flagsTextView;
        public String url;

    }
}
