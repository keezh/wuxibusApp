package com.wuxibus.app.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.wuxibus.app.R;
import com.wuxibus.app.entity.NewsDetail;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import java.util.List;

/**
 * Created by zhongkee on 15/7/18.
 */
public class HomeNewsAdapter extends BaseAdapter {
    Context context;
    List<NewsDetail> list;

    public HomeNewsAdapter(Context context,List<NewsDetail> list){
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
        final ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.home_news_item,null);
            viewHolder.thumbnailImageView = (ImageView) view.findViewById(R.id.news_thumbnail_pic);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.news_title);
            viewHolder.classTextView = (TextView) view.findViewById(R.id.news_class);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.titleTextView.setText(list.get(i).getTitle());
        viewHolder.classTextView.setText(list.get(i).getClass_name());
        viewHolder.url = list.get(i).getUrl();
        viewHolder.indexUrl = list.get(i).getIndex_pic();
        String indexUrl = viewHolder.indexUrl+"/200x150";
        viewHolder.thumbnailImageView.setTag(indexUrl);

        boolean flag = BitmapCache.getInstern().getSDCardBitmap(indexUrl, viewHolder.thumbnailImageView,
                new BitmapCache.CallBackSDcardImg() {
            @Override
            public void setImgToView(Bitmap bitmap, ImageView imgView) {
                new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                imgView.setImageBitmap(bitmap);
            }
        });
        if (!flag) {
            //VolleyManager.loadImage(viewHolder.thumbnailImageView, indexUrl, R.drawable.background_img);
            VolleyManager.loadImage(viewHolder.thumbnailImageView, indexUrl, R.drawable.background_img, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

                    Bitmap resBitmap =  imageContainer.getBitmap();
                    String tag = (String)viewHolder.thumbnailImageView.getTag();
                    if(tag.equals(imageContainer.getRequestUrl())){
                        viewHolder.thumbnailImageView.setImageBitmap(resBitmap);
                    }

                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }



        return view;
    }

    public class ViewHolder{
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView classTextView;
        public String indexUrl;
        public String url;
    }
}
