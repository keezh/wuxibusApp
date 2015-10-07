package com.wuxibus.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.*;

/**
 * Created by zhongkee on 15/10/4.
 */
public class InterchangeResultActivity extends Activity implements OnClickListener{

    private ImageView backImageView;




    /**
     * http://api.map.baidu.com/direction/v1?ak=RUC7z2D4GYlihrOhwwOC76w5&
     * destination=%E6%97%A0%E9%94%A1%E4%B8%9C%E7%AB%99&mode=transit&
     * origin=%E9%87%91%E7%A7%91%E7%B1%B3%E5%85%B0%E7%B1%B3%E5%85%B0%E8%8A%B1%E5%9B%AD
     * &output=json&region=%E6%97%A0%E9%94%A1
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_result);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        backImageView.setOnClickListener(this);

       // queryRoutes();


    }





    public void queryRoutes(){
        Map<String,String> params = new HashMap<String,String>();
        params.put("ak","RUC7z2D4GYlihrOhwwOC76w5");
        params.put("destination","无锡东站");
        params.put("mode","transit");
        params.put("origin","金科米兰花园");
        params.put("output","json");
        params.put("region","无锡");
        String url = "";
        try {
        String orign = URLEncoder.encode("金科米兰花园","UTF-8");
        String destination = URLEncoder.encode("无锡东站","UTF-8");
        String region = URLEncoder.encode("无锡","UTF-8");
            url = AllConstants.InterchangeUrl+"?ak="+AllConstants.Baidu_ak+"&origin="+orign+
                "&destination="+destination+"&mode=transit&output=json&region="+region;


            //url = URLEncoder.encode(url, "UTF-8");
        }catch (UnsupportedEncodingException e){

        }


        VolleyManager.getJson(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int type = jsonObject.getInt("type");
                    if(type == 1){//再起发起请求

                    }else if(type == 2){//获得结果
                        String result = jsonObject.getString("result");
                        InitApplication.appLog.i(result);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        },null);
    }

    @Override
    public void onClick(View v) {
        if(v == backImageView){
            finish();
        }
    }
}
