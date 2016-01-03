package com.wuxibus.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.LineRealActivity;
import com.wuxibus.app.activity.MainActivity;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.adapter.RouteAroundAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.AroundRoute;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;

/**
 * Created by zhongkee on 15/6/17.
 */
public class RouteAroundFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    ListView lineAroundListView;
    View tipView;
    //下拉刷新控件
    SwipeRefreshLayout swipeRefreshLayout;

    Button refreshGpsBtn;

    public RouteAroundFragment(){

    }

//    public RouteAroundFragment(Handler myHandler) {
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_around,null);
        lineAroundListView = (ListView) view.findViewById(R.id.line_around_listview);
        tipView = view.findViewById(R.id.route_none_container);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshGpsBtn = (Button) view.findViewById(R.id.refresh_gps_btn);
        lineAroundListView.setOnItemClickListener(this);
        refreshGpsBtn.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ((MainActivity)RouteAroundFragment.this.getActivity()).updateBaiduGps();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        queryRouteAround();
                    }
                }, 500);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        lineAroundListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (lineAroundListView.getLastVisiblePosition() == (lineAroundListView.getCount() - 1)) {
                        }
                        // 判断滚动到顶部

                        if(lineAroundListView.getFirstVisiblePosition() == 0){
                            swipeRefreshLayout.setEnabled(true);
                        }else{
                            swipeRefreshLayout.setEnabled(false);
                        }

                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));
            }
        });

        queryRouteAround();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //queryRouteAround();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        RouteAroundAdapter.ViewHolder viewHolder = (RouteAroundAdapter.ViewHolder)view.getTag();

        String lineName = viewHolder.lineNameTextView.getText().toString();

//        Bundle bundle = new Bundle();
//        bundle.putString("lineName",lineName);
//        Intent intent = new Intent(this.getActivity(),SearchLineResultActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putString("line_name",lineName);
        bundle.putString("stop_start",viewHolder.startTextView.getText().toString());
        bundle.putString("stop_end",viewHolder.endTextView.getText().toString());
        bundle.putString("time_start_end",viewHolder.timeStartEnd);
        bundle.putString("line_id",viewHolder.lineId);
        bundle.putString("direction",viewHolder.direction);
        int currentIndex = viewHolder.aroundRoute.getCurrentIndex();
        bundle.putString("stopName",viewHolder.aroundRoute.getStop_list().get(currentIndex).getStop_name());

        Intent intent = new Intent(this.getActivity(), LineRealActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void showBgImageView(){

    }

    /**
     * 网络请求，这边线路
     * 周边的线路，radius 是半径，单位米
     http://api.wxbus.com.cn/api/?latitude=31.5611&longitude=120.277&m=stop_nearby&radius=500
     latitude=31.5611&longitude=120.277&m=line_nearby&radius=500

     */
    public void queryRouteAround(){
        String url = AllConstants.ServerUrl;
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","line_nearby");
        params.put("radius","500");
//        params.put("latitude","31.5611");
//        params.put("longitude","120.277");
        params.put(AllConstants.LatitudeBaidu, GPS.latitude+"");
        params.put(AllConstants.LongitudeBaidu, GPS.longitude+"");

        params = AES7PaddingUtil.toAES7Padding(params);

        VolleyManager.getJson(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(jsonObject.getString("error").equals("1")){
                        List<AroundRoute> aroundRouteList = JSON.parseArray(jsonObject.getString("result"),AroundRoute.class);
                        if(aroundRouteList != null && aroundRouteList.size() > 0){
                            RouteAroundAdapter routeAroundAdapter = new RouteAroundAdapter(RouteAroundFragment.this.getActivity(),aroundRouteList);
                            lineAroundListView.setAdapter(routeAroundAdapter);
                            lineAroundListView.setVisibility(View.VISIBLE);
                            //查询距离多少站提示信息
                            queryRealStop(routeAroundAdapter,aroundRouteList);
                            tipView.setVisibility(View.GONE);
                        }else{
                            tipView.setVisibility(View.VISIBLE);
                            lineAroundListView.setVisibility(View.GONE);
                        }


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

    }


    public void queryRealStop(RouteAroundAdapter adapter,List<AroundRoute>list){
        for (int i = 0; list!= null && i < list.size(); i++) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("m","bus_live");
            int currentIndex = list.get(i).getCurrentIndex();
            map.put("line_code",list.get(i).getStop_list().get(currentIndex).getLine_code());
            map.put("stop_seq",list.get(i).getStop_list().get(currentIndex).getStop_seq());
            list.get(i).adapter = adapter;
            map = AES7PaddingUtil.toAES7Padding(map);

            //具体回调在模型AroundRoute中
            VolleyManager.getJson(AllConstants.ServerUrl, map, list.get(i), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });


        }

    }

    //重新定位
    @Override
    public void onClick(View v) {
        if(v == refreshGpsBtn){

            MainActivity activity = (MainActivity)this.getActivity();
            activity.updateBaiduGps();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryRouteAround();
                }
            },1000);


        }
    }
}
