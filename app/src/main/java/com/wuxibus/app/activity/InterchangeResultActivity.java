package com.wuxibus.app.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeResultAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.InterchangeRoutes;
import com.wuxibus.app.entity.InterchangeScheme;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.entity.InterchangeStep;
import com.wuxibus.app.entity.InterchangeStepLocation;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
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
 *
 * http://api.map.baidu.com/direction/v1?
 * ak=RUC7z2D4GYlihrOhwwOC76w5&
 * destination=31.577102304812%2C120.28583936828&
 * mode=transit&
 * origin=31.5735226816477%2C120.292504041863&
 * output=json&region=%E6%97%A0%E9%94%A1
 *
 *
 */
public class InterchangeResultActivity extends Activity implements OnClickListener{

    private ImageView backImageView;
    List<AdvItem> list;
    ViewPager advertiseViewPager;

    List<InterchangeScheme> schemeList = new ArrayList<InterchangeScheme>();

    ListView recommendListView;
    ListView fastListView;
    ListView lessChangeListView;
    ListView lessStepListView;

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
        advertiseViewPager = (ViewPager)findViewById(R.id.adv_viewpager);

        recommendListView = (ListView) findViewById(R.id.recommend_lv);
        fastListView = (ListView) findViewById(R.id.fast_lv);
        lessChangeListView = (ListView) findViewById(R.id.less_change_lv);
        lessStepListView = (ListView) findViewById(R.id.less_step_lv);

        queryAdvList();//广告接口
        queryRoutes();//
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
       // String orign = URLEncoder.encode("金科米兰花园","UTF-8");
       // String destination = URLEncoder.encode("无锡东站","UTF-8");
        String region = URLEncoder.encode("无锡","UTF-8");

//            url = AllConstants.InterchangeUrl+"?ak="+AllConstants.Baidu_ak+"&origin="+orign+
//                "&destination="+destination+"&mode=transit&output=json&region="+region;
        String orignLatLng = InterchangeSearch.sourceInfo.location.latitude + ","+ InterchangeSearch.sourceInfo.location.longitude;
        String destinationLatLng = InterchangeSearch.destinationInfo.location.latitude + ","+InterchangeSearch.destinationInfo.location.longitude;
        orignLatLng = URLEncoder.encode(orignLatLng,"UTF-8");
        destinationLatLng = URLEncoder.encode(destinationLatLng,"UTF-8");
            url = AllConstants.InterchangeUrl+"?ak="+AllConstants.Baidu_ak+"&destination="+destinationLatLng+
                    "&origin="+orignLatLng+"&mode=transit&output=json&region="+region;

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
                        //String result = jsonObject.getString("result");
                        String routesStr = jsonObject.getJSONObject("result").getString("routes");

                        JSONArray routeArray = jsonObject.getJSONObject("result").getJSONArray("routes");

                        for (int i = 0; i < routeArray.length(); i++) {
                            JSONObject schemeObject = routeArray.getJSONObject(i);
                            JSONArray  schemeItemArray = schemeObject.getJSONArray("scheme");
                            JSONObject schemeReal = schemeItemArray.getJSONObject(0);
                            //InterchangeScheme interchangeScheme = JSON.parseObject(schemeReal.toString(),InterchangeScheme.class);
                            //InterchangeScheme interchangeScheme = jsonToObject(schemeReal);

                            String tempScheme = schemeItemArray.getString(0);
                            InterchangeScheme interchangeScheme = JSON.parseObject(tempScheme,InterchangeScheme.class);

                            schemeList.add(interchangeScheme);

                        }

                        //推荐UI
                        InterchangeResultAdapter adapter = new InterchangeResultAdapter(InterchangeResultActivity.this,schemeList);
                        recommendListView.setAdapter(adapter);


                        //InitApplication.appLog.i(schemes.size());

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        },null);
    }

    /**
     * json解析，使用原始解析方法
     * @param obj
     * @return
     */
    public InterchangeScheme jsonToObject(JSONObject obj){
        try {
            InterchangeScheme scheme = new InterchangeScheme();
            scheme.setDistance(obj.getInt("distance"));
            scheme.setDuration(obj.getInt("duration"));
            JSONArray stepJsonArray = obj.getJSONArray("steps").getJSONArray(0);
            for (int i = 0; i < stepJsonArray.length(); i++) {
                JSONObject stepObj = stepJsonArray.getJSONObject(i);
                InterchangeStep step = new InterchangeStep();
                step.setDuration(stepObj.getInt("duration"));
                step.setDistance(stepObj.getInt("distance"));
                step.setType(stepObj.getInt("type"));
                InterchangeStepLocation orignLoc = JSON.parseObject(stepObj.getString("stepOriginLocation"), InterchangeStepLocation.class);
                step.setStepOriginLocation(orignLoc);

            }
            //List<InterchangeStep> stepList =  JSON.parseArray(tempJsonString,InterchangeStep.class);
            //scheme.setSteps(stepList);
            return scheme;

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void onClick(View v) {
        if(v == backImageView){
            finish();
        }
    }

    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","transfer_search");

        //服务器端程序返回接口不统一
        paras = AES7PaddingUtil.toAES7Padding(paras);
        VolleyManager.getJsonArray(url, paras, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                list = JSON.parseArray(jsonArray.toString(), AdvItem.class);
                List<ImageView> imageList = new ArrayList<ImageView>();
                int length = 0;
                if (list != null) {
                    length = list.size();
                    if (length == 2 || length == 3) {
                        length = length * 2;
                    }
                }
                for (int i = 0; list != null && i < length; i++) {
                    SmartImageView imageView = new SmartImageView(InterchangeResultActivity.this);
                    imageList.add(imageView);
                    //imageView.setRatio(720.0f/200);
                    String imgUrl = list.get(i % list.size()).getIndex_pic();
                    //比例：16：9
                    String width = AllConstants.Width + "";
                    int height = DensityUtil.dip2px(InterchangeResultActivity.this, 110);
                    //xml布局高度设定了
                    imgUrl += "/" + width + "x" + height;

                    boolean flag = BitmapCache.getInstern().getSDCardBitmap(imgUrl, imageView, new BitmapCache.CallBackSDcardImg() {
                        @Override
                        public void setImgToView(Bitmap bitmap, ImageView imgView) {
                            new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                            imgView.setImageBitmap(bitmap);
                        }
                    });
                    if (!flag) {
                        VolleyManager.loadImage(imageView, imgUrl, R.drawable.background_img);
                    }
                    //VolleyManager.loadImage(imageView, imgUrl, R.drawable.advertise_test);
                    final int tempI = i % list.size();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(InterchangeResultActivity.this, WebViewActivity.class);
                            intent.putExtra("url", list.get(tempI).getUrl());
                            intent.putExtra("title", list.get(tempI).getTitle());
                            startActivity(intent);

                        }
                    });

                }
                if (imageList.size() > 0) {
                    advertiseViewPager.setVisibility(View.VISIBLE);
                    advertiseViewPager.setAdapter(new MyViewPagerAdapter(imageList));
                    advertiseViewPager.setCurrentItem(list.size() * 100);
                } else {
                    advertiseViewPager.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }
}
