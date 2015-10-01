package com.wuxibus.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchStopResultActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.entity.Stop;
import com.wuxibus.app.entity.StopInfo;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.volley.VolleyManager;

import java.util.List;

/**
 * Created by zhongkee on 15/6/21.
 */
public class StopRealItemAdapter extends BaseAdapter {

    Context context;
    List<Stop> stopList;
    int distance;
    StopInfo stopInfo;

    public StopRealItemAdapter(Context context, StopInfo stopInfo, List<Stop> stopList,int distance){
        this.context = context;
        this.stopInfo = stopInfo;
        this.stopList = stopList;
        this.distance = distance;
    }

    @Override
    public int getCount() {
        return stopList.size();
    }

    @Override
    public Object getItem(int i) {
        return stopList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        if(view == null){
            view = View.inflate(context, R.layout.stop_item_activity,null);

            viewHolder = new ViewHolder();
            viewHolder.stopName = (TextView) view.findViewById(R.id.stop_title_textview);
            viewHolder.sortTextView = (TextView) view.findViewById(R.id.stop_seq_state_shape);
            //viewHolder.lineImageView = (ImageView) view.findViewById(R.id.line_imageview);
            viewHolder.adContainer = view.findViewById(R.id.adv_container);
            viewHolder.advTextView = (TextView) view.findViewById(R.id.adv_title);

            viewHolder.liveIconImageView = (ImageView) view.findViewById(R.id.live_icon_imageview);
            viewHolder.stopDetailTextView = (TextView) view.findViewById(R.id.stop_detail_textview);
            viewHolder.howmanyContainer = view.findViewById(R.id.howmany_stops_container);
            viewHolder.howmanyTextView = (TextView) view.findViewById(R.id.howmany_textview);
            viewHolder.stopEndTip = (TextView) view.findViewById(R.id.stop_end_tip);
            viewHolder.stopToStation = (Button) view.findViewById(R.id.stop_to_station);

            viewHolder.lineView = view.findViewById(R.id.line_imageview);
            viewHolder.wifiImageView = (ImageView) view.findViewById(R.id.wifi_4g_imageview);

            view.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        //begin 实时公交页面渲染
        //

        String tipText = "车辆"+stopList.get(i).getBusselfid()+"于"+stopList.get(i).getActdatetime()+"到达此站";
        viewHolder.stopDetailTextView.setText(tipText);

        viewHolder.stopName.setText(stopList.get(i).getStop_name());
        viewHolder.sortTextView.setText(i+1+ "");

        if(stopList.get(i).getHasBus()){
            viewHolder.sortTextView.setVisibility(View.GONE);
            viewHolder.liveIconImageView.setVisibility(View.VISIBLE);
            viewHolder.stopDetailTextView.setVisibility(View.VISIBLE);
            viewHolder.stopName.setTextColor(Color.argb(255, 220, 69, 69));

            String pic = stopList.get(i).getPic();
            if(!pic.equals("")){
                viewHolder.wifiImageView.setVisibility(View.VISIBLE);
                VolleyManager.loadImage(viewHolder.wifiImageView,pic,R.drawable.background_img);
            }else {
                viewHolder.wifiImageView.setVisibility(View.GONE);
            }

            //viewHolder.lineImageView.setMaxHeight(70);


        }else{//此分支必须要，重置viewHolder中view中得值
            viewHolder.sortTextView.setVisibility(View.VISIBLE);
            viewHolder.liveIconImageView.setVisibility(View.GONE);
            viewHolder.stopDetailTextView.setVisibility(View.GONE);
            viewHolder.wifiImageView.setVisibility(View.GONE);
            viewHolder.stopName.setTextColor(Color.GRAY);

        }
        //Android 4.2，4.3，出现bug
        viewHolder.stopToStation.setVisibility(View.GONE);
        viewHolder.stopEndTip.setVisibility(View.GONE);
        viewHolder.howmanyContainer.setVisibility(View.GONE);

        if(stopList.get(i).isSelected()){

            //add by kee，重新计算左侧的高度
            ViewGroup.LayoutParams params = viewHolder.lineView.getLayoutParams();
            params.width = DensityUtil.dip2px(context,2);
            //此处，还不太合适固定数字，会存在适配问题，目前在1080p上是没有问题的
            //px = 330  1080p上
            params.height = DensityUtil.dip2px(context,140);
            viewHolder.lineView.setLayoutParams(params);

            viewHolder.sortTextView.setVisibility(View.GONE);
            viewHolder.liveIconImageView.setVisibility(View.VISIBLE);
            viewHolder.liveIconImageView.setBackgroundResource(R.drawable.route_detail_icon_me);
            viewHolder.stopName.setTextColor(Color.argb(255, 80, 206, 133));
            if(distance > 0){
                viewHolder.howmanyContainer.setVisibility(View.VISIBLE);
                viewHolder.howmanyTextView.setText(distance+"");

            }

            //不仅仅是运营结束判断
            viewHolder.stopToStation.setVisibility(View.VISIBLE);
            if(stopInfo.getInfo().contains("最近一班车") && stopInfo.getStop_limit() == 0){
                viewHolder.stopEndTip.setVisibility(View.VISIBLE);
                viewHolder.stopEndTip.setText(stopInfo.getInfo());
            }else if(stopInfo.getInfo().contains("今日已经结束营运") && stopInfo.getStop_limit() == 0){
                viewHolder.stopEndTip.setVisibility(View.VISIBLE);
                viewHolder.stopEndTip.setText(stopInfo.getInfo());
            }

//            if(stopInfo != null && stopInfo.getStop_limit() == 0){
//                viewHolder.stopEndTip.setVisibility(View.VISIBLE);
//            }

            viewHolder.stopToStation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SearchStopResultActivity.class);
                    intent.putExtra("stopName", viewHolder.stopName.getText().toString());
                    context.startActivity(intent);
                }
            });


        }

        //add adv show
        if(stopList.get(i).getAd_title()!= null){
            viewHolder.adContainer.setVisibility(View.VISIBLE);
            viewHolder.advTextView.setText(stopList.get(i).getAd_title());
            viewHolder.adUrl = stopList.get(i).getAd_url();
            final int tempI = i;
            viewHolder.advTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url",viewHolder.adUrl);
                    intent.putExtra("title",stopList.get(tempI).getAd_title());
                    context.startActivity(intent);
                }
            });
        }else{
            viewHolder.adContainer.setVisibility(View.GONE);
        }





        viewHolder.line_code = stopList.get(i).getLine_code();
        viewHolder.line_seq = stopList.get(i).getStop_seq();

        //view.invalidate();
        //view.requestLayout();

        return view;
    }

    public class ViewHolder{
       // public ImageView lineImageView;
        public View adContainer;
        public TextView advTextView;
        public String adUrl;
        public TextView stopName;
        public String line_code;
        public String line_seq;
        public TextView sortTextView;//序号，1，2，3
        public boolean hasBus;
        public String busSelfId;
        public String actDatetime;

        public ImageView liveIconImageView;
        public TextView stopDetailTextView;
        public View howmanyContainer;
        public TextView howmanyTextView; //距离还有几站
        public TextView stopEndTip;
        public Button stopToStation;//途径站台按钮
        public View lineView;
        public ImageView wifiImageView;

    }
}
