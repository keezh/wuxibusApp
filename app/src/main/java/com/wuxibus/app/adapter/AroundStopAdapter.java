package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.StopNearby;

import java.util.List;

/**
 * Created by zhongkee on 15/7/9.
 */
public class AroundStopAdapter extends BaseAdapter {
    Context context;
    List<StopNearby> stopNearbyList;

    public AroundStopAdapter(Context context,List<StopNearby> stopNearbyList){
        this.context = context;
        this.stopNearbyList = stopNearbyList;
    }

    @Override
    public int getCount() {
        return stopNearbyList.size();
    }

    @Override
    public Object getItem(int i) {
        return stopNearbyList.get(i);
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
            view = View.inflate(context, R.layout.stop_nearby_item,null);
            viewHolder.stopNameTextView = (TextView) view.findViewById(R.id.stop_name_textview);
            viewHolder.distanceTextView = (TextView)view.findViewById(R.id.distance_textview);
            viewHolder.linesTextView = (TextView) view.findViewById(R.id.lines_textview);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.stopNameTextView.setText(stopNearbyList.get(i).getStop_name());
        viewHolder.distanceTextView.setText(stopNearbyList.get(i).getRemoving()+"米");
        StringBuilder temp = new StringBuilder();
        List<String> line_info = stopNearbyList.get(i).getLine_info();
        for(int k = 0;line_info!= null && k<line_info.size();k++){
            temp.append(line_info.get(k)+"  ");

        }
        viewHolder.linesTextView.setText(temp.toString());

        return view;
    }

    public class ViewHolder{
        public TextView stopNameTextView;
        public TextView distanceTextView;
        public TextView linesTextView;
    }
}
