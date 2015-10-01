package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.SearchHistory;

import java.util.List;

/**
 * Created by zhongkee on 15/7/25.
 */
public class SearchLineAdapter extends BaseAdapter{
    Context context;
    List<SearchHistory> list;

    public SearchLineAdapter(Context context,List<SearchHistory>list){
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
            view = View.inflate(context, R.layout.search_line_history_item,null);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textview);
            viewHolder.startTextView = (TextView) view.findViewById(R.id.start_textview);
            viewHolder.endTextView = (TextView) view.findViewById(R.id.end_textview);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.titleTextView.setText(list.get(i).getLineName());
        viewHolder.startTextView.setText(list.get(i).getStartStop());
        viewHolder.endTextView.setText(list.get(i).getEndStop());
        viewHolder.lineName = list.get(i).getLineName();
        return view;
    }

    public class ViewHolder{
        public TextView titleTextView;
        public TextView startTextView;
        public TextView endTextView;
        public String lineName;
    }
}
