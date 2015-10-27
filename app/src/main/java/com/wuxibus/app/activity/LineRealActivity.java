package com.wuxibus.app.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.wuxibus.app.R;
import com.wuxibus.app.StopListModel;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.adapter.StopRealItemAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.MyListView;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.LiveStop;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.entity.Stop;
import com.wuxibus.app.entity.StopInfo;
import com.wuxibus.app.sqlite.DBManager;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.util.DeviceTools;
import com.wuxibus.app.util.ListViewHeightUtil;
import com.wuxibus.app.util.Tools;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/6/20.
 * 线路详情页面，实时线路
 */
public class LineRealActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{

    //private static final String PREFS_NAME = "line_save.xml";
    ImageView backImageView;
    ListView stopListView;

    ImageView mapImageView;
    List<Route> routeList;
    //单程的站台列表
    List<Stop> stopList;
    List<LiveStop> liveStops;

    Button reverseButton;
    Button favoriteButton;
    public int routeIndex = 0;

    String lineId;
    String lineTitle;
    String direction;
    List<AdvItem> list;
    String stopName;//站台tab页中传递过来的站台名称
    String stopSeq;//首页直接传递过来当前值
    String lineCode;//首页直接传递过来
    //add by kee 根据不同情况下的stop_seq请求数据,如果没有传值，取最后一站的stop_seq
    //String current_stop_seq;
    //
    StopInfo stopInfo;
//起点
    TextView startTextView;
    //终点
    TextView endTextView;
    //起始点时间
    TextView startEndTextView;

    ViewPager adViewPager;

    ScrollView scrollView;
    //为了计算滚动到站台的位置
    private int currentStopIndex;

    ImageView refreshImageView;

//    String currentStopLineCode;
//    String currentStopSeq;
//    int currentItem;

    Animation freshAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_real_activity);
        stopListView = (ListView) findViewById(R.id.stop_listview);
        stopListView.setOnItemClickListener(this);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        backImageView.setOnClickListener(this);
        reverseButton = (Button) findViewById(R.id.route_reverse_btn);
        favoriteButton = (Button) findViewById(R.id.route_favorite_btn);
        mapImageView = (ImageView) findViewById(R.id.map_imageview);
        adViewPager = (ViewPager) findViewById(R.id.ad_viewpage);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        refreshImageView = (ImageView) findViewById(R.id.route_refresh_iv);

        freshAnim = AnimationUtils.loadAnimation(this,R.anim.route_refresh);

        refreshImageView.setOnClickListener(this);
        mapImageView.setOnClickListener(this);

        reverseButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        //只有当网络数据返回，才让用户有点击事件
        reverseButton.setEnabled(false);

        initHeadView();
        queryByLineId();
        initReadFavorite();
        queryAdvList();

    }

    public void initHeadView(){
        Bundle bundle = this.getIntent().getExtras();
        TextView titleTextView = (TextView) findViewById(R.id.title_textview);
        startTextView = (TextView) findViewById(R.id.stop_start_textview);
        endTextView = (TextView) findViewById(R.id.stop_end_textview);
        startEndTextView = (TextView) findViewById(R.id.start_end_textview);

        lineTitle = bundle.getString("line_title");
        titleTextView.setText(bundle.getString("line_title"));
        startTextView.setText(bundle.getString("stop_start"));
        endTextView.setText(bundle.getString("stop_end"));
        startEndTextView.setText(bundle.getString("time_start_end"));
        lineId = bundle.getString("line_id");
        direction = bundle.getString("direction");
        stopName = bundle.getString("stopName");
        stopSeq = bundle.getString("stop_seq");
        lineCode = bundle.getString("line_code");

    }


    /**
     * 根据点击站台名称，或者从站台tab页传递过来stopName,获取当前要实时信息
     * @param stopName
     * @return
     */
    public Stop getCurrentStopSeq(String stopName){

        for (int i = 0; stopList !=null && i < stopList.size(); i++) {
            if(stopList.get(i).getStop_name().equals(stopName)){
                this.currentStopIndex = i;
                return stopList.get(i);
            }

        }
        return null;

    }


    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","line_list");
        paras.put("k",lineTitle);//bug fix
        //暂时使用不加密接口，服务器好像没有进行加密处理
        paras = AES7PaddingUtil.toAES7Padding(paras);
        VolleyManager.getJsonArray(url, paras, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                list = JSON.parseArray(jsonArray.toString(), AdvItem.class);
                List<ImageView> imageList = new ArrayList<ImageView>();
                int length = 0;
                if(list != null){
                    length = list.size();
                    if(length == 2 || length == 3){
                        length = length * 2;
                    }
                }
                for (int i = 0; list != null && i < length; i++) {
                    SmartImageView imageView = new SmartImageView(LineRealActivity.this);
                    imageList.add(imageView);
                    //imageView.setRatio(720.0f / 200);
                    String imgUrl = list.get(i % list.size()).getIndex_pic();
                    //比例：16：9
                    String width = AllConstants.Width+"";
                    int height = DensityUtil.dip2px(LineRealActivity.this, 110);
                    //xml布局高度设定了
                    imgUrl += "/"+width+"x"+height;
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
                            Intent intent = new Intent(LineRealActivity.this, WebViewActivity.class);
                            intent.putExtra("url", list.get(tempI).getUrl());
                            intent.putExtra("title", list.get(tempI).getTitle());
                            startActivity(intent);

                        }
                    });

                }


                if(list.size() == 0){//获取不到广告，就隐藏该栏目
                    adViewPager.setVisibility(View.GONE);
                }else {
                    adViewPager.setVisibility(View.VISIBLE);
                    adViewPager.setAdapter(new MyViewPagerAdapter(imageList));
                    adViewPager.setCurrentItem(list.size() * 100);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    public void queryByLineId(){

//        String url = AllConstants.ServerUrl;
//        url +="/?m=line_stops&line_id="+lineId+"&direction="+direction;
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","line_stops");
        params.put("line_id",lineId);
        params.put("direction",direction);
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (!TextUtils.isEmpty(jsonObject.toString())) {
                        String error = jsonObject.getString("error");
                        if (error != null && error.equals("1")) {
                            Map<String, String> strMap = JSON.parseObject(jsonObject.toString(),
                                    new TypeReference<LinkedHashMap<String, String>>() {
                                    });

                            //2 size
                            routeList = JSON.parseArray(strMap.get("result"),Route.class);

                            if(routeList!= null && routeList.size()>0){
                                stopList = routeList.get(routeIndex).getStops();
                                StopRealItemAdapter stopItemAdapter = new StopRealItemAdapter(LineRealActivity.this,stopInfo,stopList,-1);
                                stopListView.setAdapter(stopItemAdapter);
                                //ScrollView 与ListView冲突问题，解决方案
                                ListViewHeightUtil.setListViewHeightBasedOnChildren(stopListView);

                                deepCopyArray(stopList);

                                //设置合适的当前站台
                                checkCurrentStop();
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


    public void checkCurrentStop(){
        //默认把实时公交请求 add by kee 9-4
//        if(stopSeq != null && lineCode != null){//首页进入
//            busLive(stopList.size() - 1,lineCode,stopSeq);
//
//        }else
        if(stopName == null || getCurrentStopSeq(stopName) == null) {//站台进入
            String line_code = routeList.get(routeIndex).getLine_code();
            String stop_seq = stopList.get(stopList.size() - 1).getStop_seq();
            busLive(stopList.size() - 1, line_code, stop_seq);
        }else{
            Stop stop = getCurrentStopSeq(stopName);
            busLive(stopList.size() - 1, stop.getLine_code(), stop.getStop_seq());

        }
    }

    @Override
    public void onClick(View view) {
        if(view == backImageView){
            finish();
        }else if(view == reverseButton){
           routeReverse();
        }else if(view == favoriteButton){
            routeFavorite();
        }else if(view == mapImageView){
            String lineTitle = this.getIntent().getExtras().getString("line_title");
            String startStop = routeList.get(routeIndex).getStop_start();
            String endStop = routeList.get(routeIndex).getStop_end();
            String startEndTime = routeList.get(routeIndex).getTime_start_end();

            Intent intent = new Intent(this,LineMapActivity.class);
            intent.putExtra("lineTitle",lineTitle);
            intent.putExtra("startStop",startStop);
            intent.putExtra("endStop",endStop);
            intent.putExtra("startEndTime",startEndTime);
            startActivity(intent);

        }else if(view == refreshImageView){

           // if(currentStopIndex )
            if(stopName != null){//从站台进入，直接刷新
                Stop stop = getCurrentStopSeq(stopName);
                busLive(stopList.size() - 1,stop.getLine_code(),stop.getStop_seq());
            }else if(stopSeq == null){
                String line_code = routeList.get(routeIndex).getLine_code();
                String stop_seq = stopList.get(stopList.size() - 1).getStop_seq();
                busLive(stopList.size() - 1, line_code, stop_seq);
            }else{
                busLive(currentStopIndex,lineCode,stopSeq);
            }



        }
    }



    private void initReadFavorite(){
        DBManager dbManager = new DBManager(this);

        boolean isSave = dbManager.get(lineId);
            if(isSave){
                favoriteButton.setBackgroundResource(R.drawable.route_btn_favorite_active);
            }else{
                favoriteButton.setBackgroundResource(R.drawable.route_btn_favorite);
            }

    }

    //保存线路
    private void routeFavorite() {
        final DBManager dbManager = new DBManager(this);
        boolean isSave = dbManager.get(lineId);
        if(isSave){//已经是收藏了，取消收藏
//            String url = AllConstants.ServerUrl;
//            url = url + "/?m=line_unfav&line_id="+lineId+"&device_token="+ DeviceTools.getDeviceIMEI(this);
            Map<String,String> params = new HashMap<String,String>();
            params.put("m","line_unfav");
            params.put("line_id",lineId);
            params.put("device_token",DeviceTools.getDeviceIMEI(this));
            params = AES7PaddingUtil.toAES7Padding(params);
            VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try{
                                if(jsonObject.getString("error").equals("1")){
                                    dbManager.deleteLine(lineId);
                                    favoriteButton.setBackgroundResource(R.drawable.route_btn_favorite);
                                    Tools.showToast(LineRealActivity.this,"取消收藏成功！", Toast.LENGTH_SHORT);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }

            );

        }else{
//            String url = AllConstants.ServerUrl;
//            url = url + "/?m=line_fav&line_id="+lineId+"&device_token="+ DeviceTools.getDeviceIMEI(this);
            Map<String,String> params= new HashMap<String,String>();
            params.put("m","line_fav");
            params.put("line_id",lineId);
            params.put("device_token",DeviceTools.getDeviceIMEI(this));
            params = AES7PaddingUtil.toAES7Padding(params);
            VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try{
                                if(jsonObject.getString("error").equals("1")){
                                    dbManager.add(routeList.get(routeIndex).getLine_id(),
                                            routeList.get(routeIndex).getLine_title(),routeList.get(routeIndex).getLine_name(),
                                            routeList.get(routeIndex).getDirection()+"",
                                            routeList.get(routeIndex).getStop_start(),routeList.get(routeIndex).getStop_end()
                                    ,routeList.get(routeIndex).getTime_start_end(),routeList.get(routeIndex).getStopName());
                                    favoriteButton.setBackgroundResource(R.drawable.route_btn_favorite_active);
                                    Tools.showToast(LineRealActivity.this,"收藏成功！", Toast.LENGTH_SHORT);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }

            );

        }




    }

    private void routeReverse() {
        if(routeIndex == 0){
            stopList = routeList.get(1).getStops();
            StopRealItemAdapter stopItemAdapter = new StopRealItemAdapter(LineRealActivity.this,stopInfo,stopList,-1);
            stopListView.setAdapter(stopItemAdapter);
            ListViewHeightUtil.setListViewHeightBasedOnChildren(stopListView);
            routeIndex = 1;//设置当前状态

        }else {
            stopList = routeList.get(0).getStops();
            StopRealItemAdapter stopItemAdapter = new StopRealItemAdapter(LineRealActivity.this,stopInfo,stopList,-1);
            stopListView.setAdapter(stopItemAdapter);
            ListViewHeightUtil.setListViewHeightBasedOnChildren(stopListView);
            routeIndex = 0;
        }
//重置标题
        startTextView.setText(routeList.get(routeIndex).getStop_start());
        endTextView.setText(routeList.get(routeIndex).getStop_end());
        startEndTextView.setText(routeList.get(routeIndex).getTime_start_end());

        //同时更新当前实时数据
        deepCopyArray(stopList);

        checkCurrentStop();


    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

        //http://api.wxbus.com.cn/api/?m=bus_live&line_code=334423088&stop_seq=1

        final StopRealItemAdapter.ViewHolder viewHolder = (StopRealItemAdapter.ViewHolder) view.getTag();

//        String url = AllConstants.ServerUrl;
//        url += "/?m=bus_live&line_code="+viewHolder.line_code+"&stop_seq="+viewHolder.line_seq;

        //?m=bus_live&line_code=33442308&stop_seq=30

        stopName = viewHolder.stopName.getText().toString();

        getCurrentStopSeq(stopName);//currentIndex
        busLive(i, viewHolder.line_code,viewHolder.line_seq);

        //refresh imageview
        this.currentStopIndex = i;
        this.lineCode = viewHolder.line_code;
        this.stopSeq = viewHolder.line_seq;

    }

    /**
     *
     * @param i
     * @param line_code
     * @param line_seq
     */
    private void busLive(final int i, String line_code,final String line_seq) {

        refreshImageView.startAnimation(freshAnim);

        Map<String,String> map = new HashMap<String,String>();
        map.put("m","bus_live");
        map.put("line_code",line_code);
        map.put("stop_seq",line_seq);
        //实时公交接口
        map = AES7PaddingUtil.toAES7Padding(map);
        VolleyManager.getJson(AllConstants.ServerUrl, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (!TextUtils.isEmpty(jsonObject.toString())) {
                        Map<String, String> strMap = JSON.parseObject(jsonObject.toString(),
                                new TypeReference<LinkedHashMap<String, String>>() {
                                });
                        String stopInfoStr = strMap.get("stop_info");
                        stopInfo = JSON.parseObject(stopInfoStr, StopInfo.class);
                        String jsonStops = strMap.get("list");
                        liveStops = JSON.parseArray(jsonStops, LiveStop.class);
                        //合并数据，更新当前实体公交车辆信息，即改变currentLiveStops信息
                        int distance = updateRouteList(routeList, liveStops, line_seq);
                        StopListModel.newInstance().distance = distance;
                        //更新模型数据,更新listview组件

                        StopRealItemAdapter stopItemAdapter = new StopRealItemAdapter(LineRealActivity.this, stopInfo, StopListModel.newInstance().currentLiveStops, distance);
                        stopListView.setAdapter(stopItemAdapter);
                        stopListView.requestLayout();
                        stopListView.invalidate();
                        //重新计算listview的高度
                        final int totalHeight = ListViewHeightUtil.setListViewHeightBasedOnChildren(stopListView);

                        //网络返回后，回复reverseButton点击事件
                        reverseButton.setEnabled(true);
                        //stopListView.setSelection(i);

                        final int currentStopScrollY = (int)(totalHeight * ((float)currentStopIndex - 2)/stopList.size());
//                        if(currentStopIndex == stopList.size() - 1){//最后一项
//                            currentStopScrollY = totalHeight;
//
//                        }

                        //发现上面代码无效，通过scrollview来实现滚动效果
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0,currentStopScrollY);

                            }
                        });



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

    //复制一个数组，用于用户每次点击更新该实时数组
    public void deepCopyArray(List<Stop> stopList){
        if(!StopListModel.newInstance().currentLiveStops.isEmpty()){
            StopListModel.newInstance().currentLiveStops.clear();
        }
        for(int i = 0;stopList != null && i < stopList.size();i++){
            Stop stop = stopList.get(i).clone();
            StopListModel.newInstance().currentLiveStops.add(stop);
        }
        //return tempList;
    }

    /**
     * 更新当前点击站台之前的有车站台信息
     * 返回值为点击与实时车辆的距离站台数
     * 该函数主要是动态更新数据到StopListModel.newInstance().currentLiveStops
     * @param routeList
     * @param liveStops
     */
    private int updateRouteList(List<Route> routeList, List<LiveStop> liveStops,String click_stop_seq) {
        //存放点击站台之前的实时车辆信息
        List<LiveStop> beforeLiveStops = new ArrayList<LiveStop>();

        //StopListModel.newInstance().currentLiveStops 被赋值
        deepCopyArray(stopList);

        StopListModel.newInstance().click_stop_seq = click_stop_seq;


        //遍历一次，查找到所有点击之前的车辆实时
        for(int i = 0;i< liveStops.size();i++){
            int tempSeq = Integer.parseInt(liveStops.get(i).getStop_seq());
            int clickSeq = Integer.parseInt(click_stop_seq);
            if(tempSeq <= clickSeq){
                LiveStop liveStop = liveStops.get(i);
                beforeLiveStops.add(liveStop);
            }
        }

        //
       // StopListModel.newInstance().beforeLiveStops = beforeLiveStops;

        int result = -1;
        if(beforeLiveStops.size() > 0){
            int beforSeq = Integer.parseInt(beforeLiveStops.get(0).getStop_seq());
            result = Integer.parseInt(click_stop_seq) - beforSeq;
        }

        //临时变量，实际数据存在StopListModel中
        List<Stop> currentLiveStops = StopListModel.newInstance().currentLiveStops;

        for(int i = 0;i<beforeLiveStops.size();i++){
            String currentStopSeq = beforeLiveStops.get(i).getStop_seq();
            for(int j = 0;j<currentLiveStops.size();j++){
                if(currentStopSeq.equals(StopListModel.newInstance().currentLiveStops.get(j).getStop_seq())){
                    currentLiveStops.get(j).setHasBus(true);
                    currentLiveStops.get(j).setBusselfid(beforeLiveStops.get(i).getBusselfid());
                    currentLiveStops.get(j).setProductid(beforeLiveStops.get(i).getProductid());
                    currentLiveStops.get(j).setActdatetime(beforeLiveStops.get(i).getActdatetime());
                    currentLiveStops.get(j).setPic(beforeLiveStops.get(i).getPic());

                    break;
                }
            }
        }

        //设置当前选择车辆标识
        for (int i = 0;i<currentLiveStops.size();i++){
            if(currentLiveStops.get(i).getStop_seq().equals(click_stop_seq)){
                currentLiveStops.get(i).setIsSelected(true);
                break;
            }
        }

        return result;

    }
}
