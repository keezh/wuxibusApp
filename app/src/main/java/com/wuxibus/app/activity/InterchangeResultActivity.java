package com.wuxibus.app.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.wuxibus.app.InterchangeModel;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeResultAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.InterchangeResultType;
import com.wuxibus.app.entity.InterchangeScheme;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.entity.InterchangeStep;
import com.wuxibus.app.entity.InterchangeStepLocation;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DBUtil;
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
public class InterchangeResultActivity extends Activity implements OnClickListener,AdapterView.OnItemClickListener{

    private ImageView backImageView;
    List<AdvItem> list;
    ViewPager advertiseViewPager;

    //推荐线路
    List<InterchangeScheme> schemeList = new ArrayList<InterchangeScheme>();
    List<InterchangeScheme> sortList = new ArrayList<InterchangeScheme>();
//    List<InterchangeScheme> lessInterchangeList = new ArrayList<InterchangeScheme>();
//    List<InterchangeScheme> lessStepList = new ArrayList<InterchangeScheme>();

    ListView recommendListView;
    ListView fastListView;
    ListView lessChangeListView;
    ListView lessStepListView;
    RadioButton recommentBtn;
    RadioButton fastBtn;
    RadioButton lessInterchangeBtn;
    RadioButton lessStepBtn;

    View hasRoutesContainer;
    View noRoutesTip;

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

        recommentBtn = (RadioButton) findViewById(R.id.comment_btn);
        fastBtn = (RadioButton) findViewById(R.id.fast_btn);
        lessInterchangeBtn = (RadioButton) findViewById(R.id.less_interchange_btn);
        lessStepBtn = (RadioButton) findViewById(R.id.less_step_btn);

        recommentBtn.setOnClickListener(this);
        fastBtn.setOnClickListener(this);
        lessInterchangeBtn.setOnClickListener(this);
        lessStepBtn.setOnClickListener(this);

        recommendListView.setOnItemClickListener(this);
        fastListView.setOnItemClickListener(this);
        lessChangeListView.setOnItemClickListener(this);
        lessStepListView.setOnItemClickListener(this);

        hasRoutesContainer = findViewById(R.id.hasRoutesContainer);
        noRoutesTip = findViewById(R.id.noRoutesTip);

        queryAdvList();//广告接口
//        if(InterchangeSearch.isAroundStopByDest || InterchangeSearch.isAroundStopBySource){
//            queryRoutesByStopName();
//        }else{
            queryRoutes();//
//        }
    }



    public void queryRoutes(){
        String url = "";
        try {
        String region = URLEncoder.encode("无锡","UTF-8");

            //当是从附近站台查询过来，只能使用地点名称查询，比较麻烦
            String sourceOrign = "";
            String dest = "";
            if (InterchangeSearch.isAroundStopBySource){
                sourceOrign = URLEncoder.encode(InterchangeSearch.sourceInfo.name,"UTF-8");
            }else{
                sourceOrign = InterchangeSearch.sourceInfo.location.latitude + ","+ InterchangeSearch.sourceInfo.location.longitude;
            }

            if (InterchangeSearch.isAroundStopByDest){
                dest = URLEncoder.encode(InterchangeSearch.destinationInfo.name,"UTF-8");
            }else{
                dest = InterchangeSearch.destinationInfo.location.latitude + ","+InterchangeSearch.destinationInfo.location.longitude;
            }

            if(InterchangeSearch.isAroundStopByDest || InterchangeSearch.isAroundStopBySource){
                url = AllConstants.InterchangeUrl+"?ak="+AllConstants.Baidu_ak+"&destination="+dest+
                        "&origin="+sourceOrign+"&mode=transit&output=json&region="+region;
            }else{
                String orignLatLng = InterchangeSearch.sourceInfo.location.latitude + ","+ InterchangeSearch.sourceInfo.location.longitude;
                String destinationLatLng = InterchangeSearch.destinationInfo.location.latitude + ","+InterchangeSearch.destinationInfo.location.longitude;
                orignLatLng = URLEncoder.encode(orignLatLng,"UTF-8");
                destinationLatLng = URLEncoder.encode(destinationLatLng,"UTF-8");
                url = AllConstants.InterchangeUrl+"?ak="+AllConstants.Baidu_ak+"&destination="+destinationLatLng+
                        "&origin="+orignLatLng+"&mode=transit&output=json&region="+region;
            }

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
                       // String routesStr = jsonObject.getJSONObject("result").getString("routes");

                        JSONArray routeArray = jsonObject.getJSONObject("result").getJSONArray("routes");//当没有换乘方案时，会抛出异常

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
                        InterchangeResultAdapter adapter = new InterchangeResultAdapter(InterchangeResultActivity.this,
                                schemeList, InterchangeResultType.Default);
                        adapter.initTotalParameters();//计算
                        recommendListView.setAdapter(adapter);

                        sortList = deepCopyArray(schemeList);

                        //保存该引用
                        InterchangeModel.getInstance().schemeList = schemeList;
                        InterchangeModel.getInstance().sortList = sortList;

                        //保存搜索历史记录
                        if (schemeList != null && schemeList.size() > 0){
                            double srcLat = jsonObject.getJSONObject("result").getJSONObject("origin").getJSONObject("originPt").getDouble("lat");
                            double srcLng = jsonObject.getJSONObject("result").getJSONObject("origin").getJSONObject("originPt").getDouble("lng");
                            double destLat = jsonObject.getJSONObject("result").getJSONObject("destination").getJSONObject("destinationPt").getDouble("lat");
                            double destLng = jsonObject.getJSONObject("result").getJSONObject("destination").getJSONObject("destinationPt").getDouble("lng");
                            InterchangeSearch.sourceInfo.location = new LatLng(srcLat,srcLng);
                            InterchangeSearch.destinationInfo.location = new LatLng(destLat,destLng);

                            saveSearchFullHistory();
                        }



                    }
                }catch (Exception e){
                    //没有换乘信息提示
                    if(schemeList == null || schemeList.size() == 0){
                        hasRoutesContainer.setVisibility(GONE);
                        noRoutesTip.setVisibility(VISIBLE);
                    }

                    e.printStackTrace();
                }

            }
        },null);
    }

    public void saveSearchFullHistory(){

        DBUtil dbUtil = new DBUtil(this);
        String sourceName = InterchangeSearch.sourceInfo.name;
        String sourceLatitude = "";
        String sourceLongitude = "";
        String destLatitude = "";
        String destLongitude = "";

        if (InterchangeSearch.sourceInfo.location != null){
            sourceLatitude = InterchangeSearch.sourceInfo.location.latitude+"";
            sourceLongitude = InterchangeSearch.sourceInfo.location.longitude+"";
        }

        String destName = InterchangeSearch.destinationInfo.name;
        if (InterchangeSearch.destinationInfo.location != null){
            destLatitude = InterchangeSearch.destinationInfo.location.latitude+"";
            destLongitude = InterchangeSearch.destinationInfo.location.longitude+"";
        }

        dbUtil.insertInterchangeSearchFull(sourceName,sourceLatitude,sourceLongitude,destName,destLatitude,destLongitude);

    }

    public List<InterchangeScheme> deepCopyArray(List<InterchangeScheme> list){
        List<InterchangeScheme> resultList = new ArrayList<InterchangeScheme>();
        for (int i = 0; list!= null &&i < list.size(); i++) {
            InterchangeScheme scheme = list.get(i).clone();
            resultList.add(scheme);
        }
        return resultList;
    }


    @Override
    public void onClick(View v) {
        if(v == backImageView){
            finish();
        }else if(v == recommentBtn){
            showView(InterchangeResultType.Default);
        }else if(v == fastBtn){
            showView(InterchangeResultType.Fast);
        }else if(v == lessInterchangeBtn){
            showView(InterchangeResultType.LessInterchange);
        }else if(v == lessStepBtn){
            showView(InterchangeResultType.LessStep);
        }
    }

    public void showView(int type){
        if(type == InterchangeResultType.Default){
            recommendListView.setVisibility(VISIBLE);
            fastListView.setVisibility(GONE);
            lessChangeListView.setVisibility(GONE);
            lessStepListView.setVisibility(GONE);
        }else if(type == InterchangeResultType.Fast){
            InterchangeResultAdapter fastAdapter = new InterchangeResultAdapter(InterchangeResultActivity.this,
                    sortList, InterchangeResultType.Fast);
            fastAdapter.sort();
            fastListView.setAdapter(fastAdapter);

            fastListView.setVisibility(VISIBLE);
            recommendListView.setVisibility(GONE);
            lessChangeListView.setVisibility(GONE);
            lessStepListView.setVisibility(GONE);

        }else if(type == InterchangeResultType.LessInterchange){
            InterchangeResultAdapter lessInterchangeAdapter = new InterchangeResultAdapter(InterchangeResultActivity.this,
                    sortList, InterchangeResultType.LessInterchange);
            lessInterchangeAdapter.sort();
            lessChangeListView.setAdapter(lessInterchangeAdapter);

            lessChangeListView.setVisibility(VISIBLE);
            fastListView.setVisibility(GONE);
            recommendListView.setVisibility(GONE);
            lessStepListView.setVisibility(GONE);
        }else if(type == InterchangeResultType.LessStep){
            InterchangeResultAdapter lessStepAdapter = new InterchangeResultAdapter(InterchangeResultActivity.this,
                    sortList, InterchangeResultType.LessStep);
            lessStepAdapter.sort();
            lessStepListView.setAdapter(lessStepAdapter);

            lessStepListView.setVisibility(VISIBLE);
            lessChangeListView.setVisibility(GONE);
            fastListView.setVisibility(GONE);
            recommendListView.setVisibility(GONE);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(parent == recommendListView){
            Intent intent = new Intent(this,InterchangeResultDetailActivity.class);
            intent.putExtra("isRecommend",true);
            intent.putExtra("currentIndex",position);
            startActivity(intent);
        }else if(parent == fastListView || parent == lessChangeListView || parent == lessStepListView){
            Intent intent = new Intent(this,InterchangeResultDetailActivity.class);
            intent.putExtra("isRecommend",false);
            intent.putExtra("currentIndex",position);
            startActivity(intent);
        }

    }
}
