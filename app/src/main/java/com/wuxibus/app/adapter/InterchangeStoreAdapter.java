package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.wuxibus.app.R;
import com.wuxibus.app.entity.InterchangeSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/10/15.
 */
public class InterchangeStoreAdapter extends BaseAdapter {
    List<PoiInfo> list = new ArrayList<PoiInfo>();
    Context context;

    public InterchangeStoreAdapter(Context context){
        this.context = context;
        initList();
    }

    public void initList(){
        if (InterchangeSearch.homePoiInfo == null){
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.name = "未设定";
            list.add(poiInfo);
        }else{
            list.add(InterchangeSearch.homePoiInfo);
        }

        if(InterchangeSearch.companyInfo == null){
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.name = "未设定";
            list.add(poiInfo);
        }else{
            list.add(InterchangeSearch.companyInfo);
        }
    }

    @Override
    public int getCount() {
        return 2;
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
            convertView = View.inflate(context, R.layout.interchange_store, null);
            viewHolder.addressTextView = (TextView) convertView.findViewById(R.id.address_tv);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.addressTextView.setText(list.get(position).name);
        if (position == 0){
            viewHolder.titleTextView.setText("家");
        }else{
            viewHolder.titleTextView.setText("单位");
        }
        viewHolder.poiInfo = list.get(position);

        return convertView;
    }

    public class ViewHolder{
        public TextView addressTextView;
        public TextView titleTextView;
        public PoiInfo poiInfo;
    }
}
