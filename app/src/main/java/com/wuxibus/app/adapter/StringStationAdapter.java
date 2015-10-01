package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;

import java.util.List;

/**
 * Created by zhongkee on 15/9/21.
 */
public class StringStationAdapter extends BaseAdapter {

    List<String> list;
    Context context;

    public StringStationAdapter(Context context,List<String> list){
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
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.route_search_result,null);
            viewHolder.resultText = (TextView) view.findViewById(R.id.result_textview);
           // viewHolder.resultText.setText(list.get(i));
            //保存值
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.resultText.setText(list.get(i));

        return view;
    }

    public class ViewHolder{
        public TextView resultText;
    }
}
