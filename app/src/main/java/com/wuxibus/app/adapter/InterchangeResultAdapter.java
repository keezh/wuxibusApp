package com.wuxibus.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuxibus.app.R;
import com.wuxibus.app.entity.InterchangeScheme;
import com.wuxibus.app.entity.InterchangeStep;
import com.wuxibus.app.entity.InterchangeVehicle;
import com.wuxibus.app.util.DensityUtil;

import org.bouncycastle.jce.provider.symmetric.ARC4;

import java.util.List;

/**
 * Created by zhongkee on 15/10/25.
 */
public class InterchangeResultAdapter extends BaseAdapter {
    Context context;
    List<InterchangeScheme> schemeList;

    public int type;

    public InterchangeResultAdapter(Context context,List<InterchangeScheme> schemeList){
        this.context = context;
        this.schemeList = schemeList;

        initTotalParameters();

    }

    /**
     * 计算属性，只需计算一次
     */
    public void initTotalParameters(){

        for (int i = 0; i < schemeList.size(); i++) {
            List<List<InterchangeStep>> steps = schemeList.get(i).getSteps();
            int minutes = schemeList.get(i).getDuration()/60;
            String time = getTimes(minutes);//计算时间
            //List<InterchangeStep> stepList = steps.get(0);
            int nums = getTotalNums(steps);
            int meters = getTotalMeters(steps);

            schemeList.get(i).totalTime = minutes;
            schemeList.get(i).totalStops = nums;
            schemeList.get(i).totalMeters = meters;
        }

    }

    @Override
    public int getCount() {
        return schemeList.size();
    }

    @Override
    public Object getItem(int position) {
        return schemeList.get(position);
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
            convertView = View.inflate(context, R.layout.interchange_result_item,null);
            viewHolder.titleContainer = (LinearLayout) convertView.findViewById(R.id.title_container);
            viewHolder.detailTextView = (TextView) convertView.findViewById(R.id.detail_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        List<List<InterchangeStep>> steps = schemeList.get(position).getSteps();

//        int minutes = schemeList.get(position).getDuration()/60;
//        String time = getTimes(minutes);//计算时间
//        //List<InterchangeStep> stepList = steps.get(0);
//        int nums = getTotalNums(steps);
//        int meters = getTotalMeters(steps);

        int time = schemeList.get(position).totalTime;
        int nums = schemeList.get(position).totalStops;
        int meters = schemeList.get(position).totalMeters;

        viewHolder.detailTextView.setText(time+" | "+nums+"站"+" | 步行"+meters+"米");

        viewHolder.titleContainer.removeAllViews();
        layoutTitleContainer(steps, viewHolder.titleContainer);


        return convertView;
    }

    /**
     * 计算时间
     * @param minutes
     * @return
     */
    public String getTimes(int minutes){
        String durationStr = "";
        int hour = 0;
        if(minutes >= 60){
            hour = minutes/60;
        }
        int leftMinuters = minutes - hour * 60;
        if(hour > 0){
            durationStr = hour+"小时"+leftMinuters+"分钟";
        }else {
            durationStr = leftMinuters+"分钟";
        }

        return durationStr;
    }

    //总站数
    public int getTotalNums(List<List<InterchangeStep>> stepList){
        int nums = 0;
        for (int i = 0; i < stepList.size(); i++) {

            List<InterchangeStep> steps = stepList.get(i);

            InterchangeVehicle vehicle = steps.get(0).getVehicle();
            if(vehicle != null){
                nums = nums + vehicle.getStop_num();
            }
        }

        return nums;
    }

    /**
     * 总步行数
     * @param stepList
     * @return
     */
    public int getTotalMeters(List<List<InterchangeStep>> stepList){
        int meters = 0;
        for (int i = 0; i < stepList.size(); i++) {

            List<InterchangeStep> steps = stepList.get(i);

            InterchangeVehicle vehicle = steps.get(0).getVehicle();
            if(vehicle == null){
                meters = meters + steps.get(0).getDistance();
            }
        }

        return meters;
    }

    /**
     * 动态添加头
     * @param stepList
     * @param container
     */
    public void layoutTitleContainer(List<List<InterchangeStep>> stepList,LinearLayout container){

        ImageView nextImageView = null;
        int margin = DensityUtil.dip2px(context,5);//5dp

        for (int i = 0; i < stepList.size(); i++) {
            List<InterchangeStep> steps = stepList.get(i);

            InterchangeVehicle vehicle = steps.get(0).getVehicle();
            if(vehicle != null){
                TextView titleView = new TextView(context);
                titleView.setTextColor(Color.BLACK);
                titleView.setTextSize(18);
                titleView.setText(vehicle.getName());
                nextImageView = new ImageView(context);
                nextImageView.setImageResource(R.drawable.interchange_plan_icon_next);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(margin, margin, margin, margin);
                nextImageView.setLayoutParams(lp);
                container.addView(titleView);
                container.addView(nextImageView);
            }
        }
        //移除最后一个view
        container.removeView(nextImageView);
        container.requestLayout();
    }

    public class ViewHolder{
       public LinearLayout titleContainer;
        public TextView detailTextView;//1小时15分钟|13站|不行1200米
        public TextView stopInfoTextView;//已停运
        public ViewGroup lineWrapLayout;//step详情

    }
}
