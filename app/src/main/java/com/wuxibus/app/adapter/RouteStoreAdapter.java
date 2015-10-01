package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.FavoriteRoute;

import java.util.List;

/**
 * Created by zhongkee on 15/6/18.
 */
public class RouteStoreAdapter extends BaseAdapter{
    private Context context;
    private List<FavoriteRoute> favList;

    public RouteStoreAdapter(Context contex){
        this.context = contex;
    }

    public RouteStoreAdapter(Context context,List<FavoriteRoute> favList){
        this.context = context;
        this.favList = favList;
    }

    @Override
    public int getCount() {
        return favList.size();
    }

    @Override
    public Object getItem(int i) {
        return favList.get(i);
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
            view = View.inflate(context, R.layout.route_store_item, null);
            viewHolder.lineNameTextView = (TextView) view.findViewById(R.id.fav_line_title_textview);
            viewHolder.startTextView = (TextView) view.findViewById(R.id.fav_start_textview);
            viewHolder.endTextView = (TextView) view.findViewById(R.id.fav_end_textview);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)view.getTag();

        }

        viewHolder.lineNameTextView.setText(favList.get(i).getLineTitle());
        viewHolder.startTextView.setText(favList.get(i).getStop_start());
        viewHolder.endTextView.setText(favList.get(i).getStop_end());
        viewHolder.lineTitle = favList.get(i).getLineTitle();
        viewHolder.favoriteRoute = favList.get(i);


        return view;
    }

    public class ViewHolder{
        public TextView lineNameTextView;
        public TextView startTextView;
        public TextView endTextView;
        public String lineTitle;
        public FavoriteRoute favoriteRoute;

    }
}
