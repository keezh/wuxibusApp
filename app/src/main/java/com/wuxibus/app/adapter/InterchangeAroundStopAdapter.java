package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.wuxibus.app.R;
import com.wuxibus.app.entity.StopNearby;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhongkee on 15/10/14.
 */
public class InterchangeAroundStopAdapter extends BaseAdapter{
    Context context;
    List<StopNearby> list;

    public InterchangeAroundStopAdapter(Context context, List<StopNearby> list) {
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
            convertView = View.inflate(context, R.layout.interchange_around_stop,null);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_tv);
            viewHolder.distanceTextView = (TextView) convertView.findViewById(R.id.distance_stop_tv);
            viewHolder.aroundStopsTextView = (TextView) convertView.findViewById(R.id.around_stops_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titleTextView.setText(list.get(position).getStop_name());
        viewHolder.distanceTextView.setText(list.get(position).getRemoving()+"米");
        StringBuilder temp = new StringBuilder();
        List<String> line_info = list.get(position).getLine_info();
        for(int k = 0;line_info!= null && k<line_info.size() - 1;k++){
            temp.append(line_info.get(k)+"，");

        }
        temp.append(line_info.get(line_info.size()-1));
        viewHolder.aroundStopsTextView.setText(temp.toString());
        return convertView;
    }

    public class ViewHolder{
        public TextView titleTextView;
        public TextView distanceTextView;
        public TextView aroundStopsTextView;

    }
}
