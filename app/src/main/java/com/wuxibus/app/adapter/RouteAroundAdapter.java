package com.wuxibus.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.AroundRoute;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/7.
 */
public class RouteAroundAdapter extends BaseAdapter{
    Context context;
    List<AroundRoute> aroundRouteList;

    public RouteAroundAdapter(Context contex,List<AroundRoute> aroundRouteList){
        this.context = contex;
        this.aroundRouteList = aroundRouteList;
    }

    @Override
    public int getCount() {
        return aroundRouteList.size();
    }

    @Override
    public Object getItem(int i) {
        return aroundRouteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.route_around_item,null);
            viewHolder.lineNameTextView = (TextView) view.findViewById(R.id.line_name_textview);
            viewHolder.routeReverseImageView = (ImageView) view.findViewById(R.id.route_reverse_img);
            viewHolder.startTextView = (TextView) view.findViewById(R.id.stop_start_textview);
            viewHolder.endTextView = (TextView) view.findViewById(R.id.stop_end_textview);
            viewHolder.distanceTextView = (TextView) view.findViewById(R.id.around_distance_textview);
            viewHolder.distanceStopTextView = (TextView) view.findViewById(R.id.around_distance_stop_textview);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        int currentIndex = aroundRouteList.get(i).getCurrentIndex();
        String lineTitle = aroundRouteList.get(i).getLine_title();
        viewHolder.lineNameTextView.setText(lineTitle);
        viewHolder.startTextView.setText(aroundRouteList.get(i).getStop_list().get(currentIndex).getStop_start());
        viewHolder.endTextView.setText(aroundRouteList.get(i).getStop_list().get(currentIndex).getStop_end());
        String stopName = aroundRouteList.get(i).getStop_list().get(currentIndex).getStop_name();
        String distance = aroundRouteList.get(i).getStop_list().get(currentIndex).getDistance();
        String tempStr = stopName + "站离您最近("+distance+"米)";
        viewHolder.distanceTextView.setText(tempStr);

        String line_code = aroundRouteList.get(i).getStop_list().get(currentIndex).getLine_code();
        String stop_seq = aroundRouteList.get(i).getStop_list().get(currentIndex).getStop_seq();
        final String temp = "最近一辆车离"+stopName+"还有";

        //viewHolder.distanceStopTextView.setText(temp+"站");
        viewHolder.distanceStopTextView.setText(aroundRouteList.get(i).getDistanceStopInfo());
        viewHolder.timeStartEnd = aroundRouteList.get(i).getStop_list().get(currentIndex).getTime_start_end();
        viewHolder.lineId = aroundRouteList.get(i).getLine_id();
        viewHolder.direction = aroundRouteList.get(i).getStop_list().get(currentIndex).getDirection();

        viewHolder.aroundRoute = aroundRouteList.get(i);

        final int tempI = i;
        viewHolder.routeReverseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempCurrentIndex = aroundRouteList.get(tempI).getCurrentIndex();
                if( tempCurrentIndex == 0){
                    aroundRouteList.get(tempI).setCurrentIndex(1);
                    tempCurrentIndex = 1;
                }else{
                    aroundRouteList.get(tempI).setCurrentIndex(0);
                    tempCurrentIndex = 0;
                }

                viewHolder.startTextView.setText(aroundRouteList.get(tempI).getStop_list().get(tempCurrentIndex).getStop_start());
                viewHolder.endTextView.setText(aroundRouteList.get(tempI).getStop_list().get(tempCurrentIndex).getStop_end());
                String stopName = aroundRouteList.get(tempI).getStop_list().get(tempCurrentIndex).getStop_name();
                String distance = aroundRouteList.get(tempI).getStop_list().get(tempCurrentIndex).getDistance();
                String tempStr = stopName + "站离您最近("+distance+"米)";
                viewHolder.distanceTextView.setText(tempStr);

               // viewHolder.distanceStopTextView.setText(temp+"站");
                viewHolder.distanceStopTextView.setText(aroundRouteList.get(tempI).getDistanceStopInfo());
                viewHolder.timeStartEnd = aroundRouteList.get(tempI).getStop_list().get(tempCurrentIndex).getTime_start_end();

            }
        });



        //还有多少站，还必须重新发起网络请求，获得,
        //放在组件之外，否则用户滚动视图会不停的请求网络

//        Map<String,String> map = new HashMap<String,String>();
//        map.put("m","bus_live");
//        map.put("line_code",line_code);
//        map.put("stop_seq",stop_seq);
//        map = AES7PaddingUtil.toAES7Padding(map);
//        VolleyManager.getJson(AllConstants.ServerUrl, map, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                try{
//                    String stops = jsonObject.getJSONObject("stop_info").getString("stop_limit");
//                    viewHolder.distanceStopTextView.setText(temp+stops+"站");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });

        return view;
    }

    public class ViewHolder{

        public TextView lineNameTextView;
        public ImageView routeReverseImageView;
        public TextView startTextView;
        public TextView endTextView;
        public TextView distanceTextView;
        public TextView distanceStopTextView;
        public String timeStartEnd;
        public String lineId;
        public String direction;
        public AroundRoute aroundRoute;

    }
}
