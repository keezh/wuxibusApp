package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.MyItem;

import java.util.List;

/**
 * Created by zhongkee on 15/8/6.
 */
public class MyItemAdapter extends BaseAdapter {

    Context context;
    List<MyItem> list;

    public MyItemAdapter(Context context,List<MyItem> list){
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
        if(view == null){
            view = View.inflate(context, R.layout.fragment_my_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon_imageview);
            TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
            imageView.setImageResource(list.get(i).getImgResId());
            titleTextView.setText(list.get(i).getTitle());

        }

        view.setTag(list.get(i));
        return view;
    }
}
