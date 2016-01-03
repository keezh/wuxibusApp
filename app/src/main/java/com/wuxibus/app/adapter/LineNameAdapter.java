package com.wuxibus.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.LineRealActivity;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/12.
 */
public class LineNameAdapter extends BaseAdapter implements View.OnClickListener{
    Activity context;
    List<String> lineNameList;
    String stopName;
    List<Route> routeList;

    public LineNameAdapter(Activity context,List<String> list,String stopName){
        this.context = context;
        this.lineNameList = list;
        this.stopName = stopName;
    }

    @Override
    public int getCount() {
        return lineNameList.size();
    }

    @Override
    public Object getItem(int i) {
        return lineNameList.get(i);
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
            view = View.inflate(context, R.layout.stop_linename_item,null);
            viewHolder.lineNameTextView = (TextView) view.findViewById(R.id.line_name_textview);
            view.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.lineNameTextView.setText(lineNameList.get(i));

        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String lineName = viewHolder.lineNameTextView.getText().toString();
//        Intent intent = new Intent(context, SearchLineResultActivity.class);
//        intent.putExtra("lineName", viewHolder.lineNameTextView.getText().toString());
//        context.startActivity(intent);
//        "line_id": "35",
//                "line_title": "35路",
//                "line_code": "21874403",
//                "line_name": "35路环形",
//                "direction": 0,
//                "stop_start": "新区分公司",
//                "stop_end": "火车站",
//                "time_start_end": ""
        Map<String, String> params = new HashMap<String, String>();
        //m=line_search_info&k=35路
        params.put("m", "line_search_info");
        params.put("k", lineName);
        //params.put("name","zhongke22");
        //测试使用get请求
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(!TextUtils.isEmpty(jsonObject.toString())){
                        String error = jsonObject.getString("error");
                        if(error != null && error.equals("1")){
                            Map<String, String> strMap = JSON.parseObject(jsonObject.toString(),
                                    new TypeReference<LinkedHashMap<String, String>>() {
                                    });
                            routeList = JSON.parseArray(strMap.get("result"), Route.class);
                            if(routeList != null && routeList.size() > 0){
                                Route route = routeList.get(0);
                                Bundle bundle = new Bundle();
                                bundle.putString("line_id",route.getLine_id());
                                bundle.putString("direction",route.getDirection()+"");
                                bundle.putString("line_name",route.getLine_name());
                                bundle.putString("stop_start",route.getStop_start());
                                bundle.putString("stop_end",route.getStop_end());
                                bundle.putString("time_start_end",route.getTime_start_end());

                                bundle.putString("stopName",stopName);
                                Intent intent = new Intent(context, LineRealActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);

                            }




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

        public class ViewHolder {
                public TextView lineNameTextView;
            }
        }
