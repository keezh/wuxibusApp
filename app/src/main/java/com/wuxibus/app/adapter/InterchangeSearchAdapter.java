package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.wuxibus.app.R;

import java.util.List;

/**
 * Created by zhongkee on 15/10/8.
 */
public class InterchangeSearchAdapter extends BaseAdapter {

    List<PoiInfo> list;
    Context context;

    public InterchangeSearchAdapter(List<PoiInfo> list,Context context){
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.interchange_search,null);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textview);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.titleTextView.setText(list.get(position).name);
        viewHolder.poiInfo = list.get(position);

        return view;
    }

    public class ViewHolder{

        public TextView titleTextView;
        public PoiInfo poiInfo;
    }
}
