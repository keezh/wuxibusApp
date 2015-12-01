package com.wuxibus.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchStopResultActivity;
import com.wuxibus.app.adapter.AroundStopAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.StopNearby;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;

/**
 * Created by zhongkee on 15/7/23.
 * 该类没有被使用
 */
public class StationAroundFragment extends Fragment implements  AdapterView.OnItemClickListener{

    ListView around_stops_listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.station_aound_fragment, null);
        around_stops_listview = (ListView)view.findViewById(R.id.around_stops_listview);
        around_stops_listview.setOnItemClickListener(this);
        //查询附近线路
        queryAroundStops();

        around_stops_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));
            }
        });
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == around_stops_listview){
            AroundStopAdapter.ViewHolder viewHolder = (AroundStopAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(this.getActivity(), SearchStopResultActivity.class);
            intent.putExtra("stopName",viewHolder.stopNameTextView.getText().toString());
            startActivity(intent);

        }
    }

    public void queryAroundStops(){
//radius=500&longitude=120.303283&latitude=31.569154
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","stop_nearby");
        params.put("radius","500");
        params.put(AllConstants.LongitudeBaidu, GPS.longitude+"");
        params.put(AllConstants.LatitudeBaidu,GPS.latitude+"");


        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getString("error").equals("1")) {
                        List<StopNearby> list = JSON.parseArray(jsonObject.getString("result"), StopNearby.class);
                        AroundStopAdapter aroundStopAdapter = new AroundStopAdapter(StationAroundFragment.this.getActivity(), list);
                        around_stops_listview.setAdapter(aroundStopAdapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

}
