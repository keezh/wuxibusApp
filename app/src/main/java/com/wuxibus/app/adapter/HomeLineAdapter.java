package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.HomeFavAroundRoute;

import java.util.List;

/**
 * Created by zhongkee on 15/7/18.
 */
public class HomeLineAdapter extends BaseAdapter {
    Context context;
    List<HomeFavAroundRoute> list;
    List<HomeFavAroundRoute> favList;

    public HomeLineAdapter(Context context,List<HomeFavAroundRoute>list,List<HomeFavAroundRoute> favList){
        this.context = context;
        this.list = list;
        this.favList = favList;

    }

    /**
     * 特殊处理一下，如果用户已经收藏了线路，则该列表的最后没有提示图片项，否则该list最后有一项图片帮助项
     * @return
     */
    @Override
    public int getCount() {
        if(favList != null && favList.size() == 0)
            return list.size()+1;
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
       // if(favList != null && favList.size() == 0){
            if(i == list.size()){
                view = View.inflate(context,R.layout.home_fav_tip,null);
                return view;
            }
        //}
        final ViewHolder viewHolder;
        if(view == null || view.getTag() == null){ //view.getTag(),是添加最后一项出现的bug，解决了，具体什么原因，还不知道原理，当i = 5的适合，
            //viewholder 为null
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.home_line_item,null);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title_textview);
            viewHolder.saveImageView = (ImageView) view.findViewById(R.id.home_line_icon);
            viewHolder.endTitleTextView = (TextView) view.findViewById(R.id.line_end_textview);
            viewHolder.distanceStopTextView = (TextView) view.findViewById(R.id.line_distance);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        //后期该字段需要修改为只有数字的
        viewHolder.titleTextView.setText(list.get(i).getLineTitle());
        if(list.get(i).getType() == AllConstants.AROUND_FLAG){
            viewHolder.saveImageView.setImageResource(R.drawable.home_icon_loc);
        }else{
            viewHolder.saveImageView.setImageResource(R.drawable.home_icon_fav);
        }
        if(list.get(i).getType() == AllConstants.FAV_FLAG && !list.get(i).isFavIsHere()){
            view.findViewById(R.id.line_first_container).setVisibility(View.GONE);
            view.findViewById(R.id.line_second_container).setVisibility(View.GONE);
            view.findViewById(R.id.line_not_here).setVisibility(View.VISIBLE);


        }else{
            view.findViewById(R.id.line_first_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_second_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_not_here).setVisibility(View.GONE);

            viewHolder.endTitleTextView.setText(list.get(i).getEndStop());
        }

        //add by kee 无法获取周围线路只有收藏的时候
        if(list.size() == favList.size()){
            view.findViewById(R.id.line_first_container).setVisibility(View.GONE);
            view.findViewById(R.id.line_second_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_not_here).setVisibility(View.GONE);

            viewHolder.endTitleTextView.setText(list.get(i).getEndStop());

        }

        viewHolder.distanceStopTextView.setText(list.get(i).realDistanceStops);

        viewHolder.route = list.get(i);

        return view;
    }

    public class ViewHolder{
        public ImageView saveImageView;
        public TextView titleTextView;
        public TextView distanceStopTextView;
        public TextView endTitleTextView;
        public HomeFavAroundRoute route;
    }
}
