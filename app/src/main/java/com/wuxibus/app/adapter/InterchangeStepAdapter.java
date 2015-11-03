package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wuxibus.app.entity.InterchangeStep;

import java.util.List;

/**
 * Created by zhongkee on 15/11/2.
 */
public class InterchangeStepAdapter extends BaseAdapter {

    List<List<InterchangeStep>> steps;
    Context context;
    public InterchangeStepAdapter(Context context,List<List<InterchangeStep>> steps){
        this.context = context;
        this.steps = steps;
    }

    @Override
    public int getCount() {
        return steps.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == steps.size()){
           return "";
        }
        return steps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){

        }
        return null;
    }

    public class ViewHolder{

    }
}
