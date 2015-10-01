package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.SearchPlace;

import java.util.List;

/**
 * Created by zhongkee on 15/7/26.
 */
public class SearchPlaceAdapter extends BaseAdapter {
    Context context;
    List<SearchPlace> list;
    public SearchPlaceAdapter(Context context,List<SearchPlace> list){
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
            view = View.inflate(context, R.layout.search_place_item,null);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.result_textview);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.titleTextView.setText(list.get(i).getTitle());
        viewHolder.lat = list.get(i).getLat();
        viewHolder.lng = list.get(i).getLng();

        return view;
    }

    public class ViewHolder{
        public TextView titleTextView;
        public String lat;
        public String lng;
    }
}
