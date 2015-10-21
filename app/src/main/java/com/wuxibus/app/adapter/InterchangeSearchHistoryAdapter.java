package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.InterchangeSearchHistory;

import java.util.List;

/**
 * Created by zhongkee on 15/10/13.
 */
public class InterchangeSearchHistoryAdapter extends BaseAdapter{
    List<InterchangeSearchHistory> list;
    Context context;

    public InterchangeSearchHistoryAdapter(List<InterchangeSearchHistory> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(list.size() == 0){
            return  1;
        }
        return list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0){
            return new InterchangeSearchHistory();
        }
        return list.get(position + 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (position == 0) {
            convertView = View.inflate(context, R.layout.interchange_search_first, null);
            return convertView;

        }

            if(convertView == null || convertView.getTag() == null){
            viewHolder = new ViewHolder();
                convertView = View.inflate(context, R.layout.interchange_search_item,null);
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.result_textview);
                convertView.setTag(viewHolder);

            }else{
            viewHolder = (ViewHolder)convertView.getTag();
             }
            viewHolder.titleTextView.setText(list.get(position - 1).getName());
            viewHolder.latitude = list.get(position - 1).getLatitude();
            viewHolder.longitude = list.get(position -1).getLongitude();


        return convertView;
    }

    public class ViewHolder{

        public TextView titleTextView;
        public String latitude;
        public String longitude;

    }
}
