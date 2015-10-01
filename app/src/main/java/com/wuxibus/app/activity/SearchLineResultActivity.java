package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.RouteSearchAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DBUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/6/20.
 */
public class SearchLineResultActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{

    ListView listView;
    private List<Route> routeList;
    ImageView back;
    String lineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_line_search);

        listView = (ListView) findViewById(R.id.line_name_listview);
        listView.setOnItemClickListener(this);
        back = (ImageView) findViewById(R.id.back_imageview);
        back.setOnClickListener(this);

        Bundle bundle = this.getIntent().getExtras();
        lineName = (String) bundle.get("lineName");
        queryByLineName(lineName);
    }



    private  void queryByLineName(String lineName){
        String url = AllConstants.ServerUrl;
                //http://www.tenjeu.com/new_bus/api/?m=line_search_info&k=35%E8%B7%AF
       // url += "?line_search_info&k="+lineName;
        Map<String,String> map = new HashMap<String,String>();
        map.put("m","line_search_info");
        map.put("k",lineName);
        map = AES7PaddingUtil.toAES7Padding(map);
        VolleyManager.getJson(url,map, new Response.Listener<JSONObject>(){

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
                                    RouteSearchAdapter routeSearchAdapter = new RouteSearchAdapter(SearchLineResultActivity.this,routeList);
                                    listView.setAdapter(routeSearchAdapter);

                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

            }
        }

        );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Route route = (Route) view.getTag();
        Bundle bundle = new Bundle();
        bundle.putString("line_id",route.getLine_id());
        bundle.putString("direction",route.getDirection()+"");
        bundle.putString("line_title",route.getLine_title());
        bundle.putString("stop_start",route.getStop_start());
        bundle.putString("stop_end",route.getStop_end());
        bundle.putString("line_code",route.getLine_code());
        bundle.putString("time_start_end",route.getTime_start_end());
        ///
        DBUtil dbUtil = new DBUtil(this);
        dbUtil.saveDB(lineName,route.getLine_title(),route.getStop_start(),route.getStop_end(),AllConstants.SEARCH_LINE_TYPE);
        Intent intent = new Intent(this,LineRealActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        if(view == back){
            finish();
        }
    }
}
