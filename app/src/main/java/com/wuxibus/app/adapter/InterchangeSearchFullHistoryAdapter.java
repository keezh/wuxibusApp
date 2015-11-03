package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.InterchangeSearchFullHistory;

import java.util.List;

/**
 * Created by zhongkee on 15/10/31.
 */
public class InterchangeSearchFullHistoryAdapter extends BaseAdapter {
    Context context;
    List<InterchangeSearchFullHistory> list;

    public InterchangeSearchFullHistoryAdapter(Context context,List<InterchangeSearchFullHistory> list){
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.interchange_search_full_history,null);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_tv);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.sourceName = list.get(position).getSourceName();
        viewHolder.sourceLatitude = list.get(position).getSourceLatitude();
        viewHolder.sourceLongitude = list.get(position).getSourceLongitude();
        viewHolder.destName = list.get(position).getDestinationName();
        viewHolder.destLatitude = list.get(position).getDestinationLatitude();
        viewHolder.destLongitude = list.get(position).getDestinationLongitude();
        viewHolder.titleTextView.setText(viewHolder.sourceName + " - "+viewHolder.destName);

        return convertView;
    }

    public class ViewHolder{
        public TextView titleTextView;
        public String sourceName;
        public String destName;
        public String sourceLatitude;
        public String sourceLongitude;
        public String destLatitude;
        public String destLongitude;
    }
}
