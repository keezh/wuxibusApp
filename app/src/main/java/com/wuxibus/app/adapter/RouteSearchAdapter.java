package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.Route;

import java.util.List;

/**
 * Created by zhongkee on 15/6/20.
 */
public class RouteSearchAdapter extends BaseAdapter {

    Context context;
    List<Route> routeList;

    public RouteSearchAdapter(Context context,List<Route> routeList){
        this.context = context;
        this.routeList = routeList;
    }
    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Object getItem(int i) {
        return routeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = View.inflate(context, R.layout.route_line_search_item,null);
            TextView title_textview = (TextView) view.findViewById(R.id.line_title_textview);
            TextView stop_start_textview = (TextView) view.findViewById(R.id.stop_start_textview);
            TextView stop_end_textview = (TextView) view.findViewById(R.id.stop_end_textview);

            title_textview.setText(routeList.get(i).getLine_title());
            stop_start_textview.setText(routeList.get(i).getStop_start());
            stop_end_textview.setText(routeList.get(i).getStop_end());

            view.setTag(routeList.get(i));
        }else {

        }
        return view;
    }
}
