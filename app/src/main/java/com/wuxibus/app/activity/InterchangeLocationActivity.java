package com.wuxibus.app.activity;

import android.app.Activity;
import android.graphics.Point;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeAroundStopAdapter;
import com.wuxibus.app.adapter.InterchangeSearchAdapter;
import com.wuxibus.app.adapter.InterchangeSearchHistoryAdapter;
import com.wuxibus.app.adapter.InterchangeStoreAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.entity.InterchangeSearchHistory;
import com.wuxibus.app.entity.StopMapModel;
import com.wuxibus.app.entity.StopNearby;
import com.wuxibus.app.util.DBUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/10/6.
 */
public class InterchangeLocationActivity extends Activity implements AdapterView.OnItemClickListener,
        View.OnClickListener,TextWatcher{
    public EditText locationEditText;

    PoiSearch poiSearch = PoiSearch.newInstance();
    List<PoiInfo> searchResultList = new ArrayList<PoiInfo>();
    ListView historyListView;
    ListView aroundListView;
    ListView storeListView;
    View mapContainer;
    MapView mapView;
    BaiduMap mBaiduMap;
    Geocoder geocoder;
    RadioButton historyBtn;
    RadioButton aroundBtn;
    RadioButton storeBtn;
    RadioButton mapviewBtn;

    ListView searchResultListView;
    View noSearchContainer;

    TextView cancelTextView;
    //map中心位置
    TextView mapTitleTextView;
    ImageView checkTitleImageView;
    //地图选点，返回值
    LatLng choosePoint;//地图选择的点
    String chooseAddress;//

    //初始化值

    boolean isFromOrigin;//是否是从出发按钮点击跳转过来


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_location);
        locationEditText = (EditText) findViewById(R.id.location_et);
        historyListView = (ListView) findViewById(R.id.interchange_history_lv);
        aroundListView = (ListView) findViewById(R.id.interchange_around_lv);
        storeListView = (ListView) findViewById(R.id.interchange_storage_lv);
        mapContainer = findViewById(R.id.map_container);
        mapView = (MapView) findViewById(R.id.location_mapview);
        historyBtn = (RadioButton) findViewById(R.id.history_btn);
        aroundBtn = (RadioButton) findViewById(R.id.around_btn);
        storeBtn = (RadioButton) findViewById(R.id.store_btn);
        mapviewBtn = (RadioButton) findViewById(R.id.mapview_btn);
        cancelTextView = (TextView)findViewById(R.id.cancel_tv);
        cancelTextView.setOnClickListener(this);

        searchResultListView = (ListView) findViewById(R.id.search_result_listview);
        searchResultListView.setOnItemClickListener(this);
        historyListView.setOnItemClickListener(this);
        aroundListView.setOnItemClickListener(this);

        noSearchContainer = findViewById(R.id.no_search_container);

        mapTitleTextView = (TextView) findViewById(R.id.title_tv);
        checkTitleImageView = (ImageView) findViewById(R.id.check_address_iv);

        checkTitleImageView.setOnClickListener(this);
        storeListView.setOnItemClickListener(this);


        //locationEditText.setOnFocusChangeListener(this);
        locationEditText.addTextChangedListener(this);
        historyBtn.setOnClickListener(this);
        aroundBtn.setOnClickListener(this);
        storeBtn.setOnClickListener(this);
        mapviewBtn.setOnClickListener(this);

        String locaiton = this.getIntent().getStringExtra("location");
        if(locaiton != null && locaiton.equals("origin")){
            locationEditText.setHint("请输入出发地");
            isFromOrigin = true;
        }else if(locaiton !=null && locaiton.equals("destination")){
            locationEditText.setHint("请输入目的地");
            isFromOrigin = false;
        }

        mBaiduMap = mapView.getMap();


        //百度地图当前位置
        initCenter();
       // drawCurrentIcon();

        //必须重新初始化该值
        //InterchangeSearch.isAroundStopBySource = false;
        //InterchangeSearch.isAroundStopByDest = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        DBUtil dbUtil = new DBUtil(this);

        //历史搜索记录
        List<InterchangeSearchHistory> list = dbUtil.queryInterchangeSearchHistory();
        InterchangeSearchHistoryAdapter adapter = new InterchangeSearchHistoryAdapter(list,this);
        historyListView.setAdapter(adapter);

        //查询周围站台，tab2(附近)
        queryAroundStops();

        //tab3 收藏（家，单位地址）
        InterchangeStoreAdapter storeAdapter = new InterchangeStoreAdapter(this);
        storeListView.setAdapter(storeAdapter);

        mapChooseLocation();


    }

    /**
     * 地图选择的点
     */
    public void mapChooseLocation(){
        mBaiduMap.setOnMapStatusChangeListener(
                new BaiduMap.OnMapStatusChangeListener(){

                    @Override

                    public void onMapStatusChangeStart(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {
                        LatLng ll=mapStatus.target;
                        choosePoint = ll;

                        //Log.d("kee", "sts ch fs:" + ll.latitude + "," + ll.longitude + "");

                        reverseGeoCode(ll);

                    }
                }
        );
    }

    /**
     * 解析当前地址
     * @param ll
     */
    private void reverseGeoCode(LatLng ll) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(InterchangeLocationActivity.this, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                }
//                                Toast.makeText(InterchangeLocationActivity.this,
//                                        "位置：" + result.getAddress(), Toast.LENGTH_LONG)
//                                        .show();

                mapTitleTextView.setText(result.getAddress());
                chooseAddress = result.getAddress();

            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
    }

    public void poiSearch(String keyword){

        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(AllConstants.City);// 城市：无锡
        citySearchOption.keyword(keyword);// 关键字
        citySearchOption.pageCapacity(30);// 默认每页10条
        //citySearchOption.pageNum(page);// 分页编号
        // 发起检索请求
        poiSearch.searchInCity(citySearchOption);
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                searchResultList.clear();//清空之前数据

                for (PoiInfo poi : poiResult.getAllPoi()) {
                    InitApplication.appLog.i(poi.name);

                    searchResultList.add(poi);

                }

                //更新listview视图
                InterchangeSearchAdapter adapter = new InterchangeSearchAdapter(searchResultList,
                        InterchangeLocationActivity.this);
                searchResultListView.setAdapter(adapter);



            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });

    }

    public void initCenter(){
        //设定中心点坐标

        //LatLng cenpt = new LatLng(31.565137, 120.288553);
        LatLng cenpt = new LatLng(GPS.latitude, GPS.longitude);
//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(16)
                .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
        mapView.getMap().setMapStatus(mMapStatusUpdate);

        reverseGeoCode(cenpt);//首次进入定位到当前地址信息
        choosePoint = cenpt;
    }

    /**
     * 绘制当前屏幕的位置点
     */
    public void drawCurrentIcon(){

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.interchange_map_pin_from);
            LatLng latlng = new LatLng(GPS.latitude,GPS.longitude);
            OverlayOptions makerOptions = null;
            Marker marker = null;
            makerOptions = new MarkerOptions().position(latlng).icon(icon)
                    .zIndex(7);
            marker = (Marker)mBaiduMap.addOverlay(makerOptions);

            View view = View.inflate(this,R.layout.interchange_baidumap_geocode,null);
        ImageView confirmIV = (ImageView) view.findViewById(R.id.check_address_iv);


        InfoWindow infoWindow;
       // final LatLng ll = marker.getPosition();
//        Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//        p.y -= 100;
//        //p.x -= 20;
//        LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
        //为弹出的InfoWindow添加点击事件
        //0.002 也是进过测试，估算出来的一个数值
        LatLng windowLatLng = new LatLng(GPS.latitude + 0.002,GPS.longitude);
        infoWindow = new InfoWindow(view,windowLatLng,0);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(infoWindow);


    }


    @Override
    public void onClick(View v) {
        if(v == historyBtn){
            historyListView.setVisibility(View.VISIBLE);
            aroundListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            mapContainer.setVisibility(View.GONE);

        }else if(v == aroundBtn){
            historyListView.setVisibility(View.GONE);
            aroundListView.setVisibility(View.VISIBLE);
            storeListView.setVisibility(View.GONE);
            mapContainer.setVisibility(View.GONE);

        }else if(v == storeBtn){
            historyListView.setVisibility(View.GONE);
            aroundListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.GONE);

        }else if(v == mapviewBtn){
            historyListView.setVisibility(View.GONE);
            aroundListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            mapContainer.setVisibility(View.VISIBLE);


        }

        if(v == cancelTextView){
            this.finish();
        }

        if(v == checkTitleImageView){//map 确认选中的点
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.location = choosePoint;
            poiInfo.name = chooseAddress;
            if(isFromOrigin){
                InterchangeSearch.sourceInfo = poiInfo;
            }else{
                InterchangeSearch.destinationInfo = poiInfo;
            }

            this.finish();


        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
       // if(v == locationEditText){
            String keyword = locationEditText.getText().toString().trim();
            if(keyword.equals("")){
                noSearchContainer.setVisibility(View.VISIBLE);
                searchResultListView.setVisibility(View.GONE);
            }else {
                noSearchContainer.setVisibility(View.GONE);
                searchResultListView.setVisibility(View.VISIBLE);
                poiSearch(keyword);
            }


       // }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == searchResultListView){
            InterchangeSearchAdapter.ViewHolder viewHolder = (InterchangeSearchAdapter.ViewHolder) view.getTag();

            PoiInfo poiInfo = viewHolder.poiInfo;
            DBUtil dbUtil = new DBUtil(this);
            dbUtil.insertInterchangeSearch(poiInfo.name,poiInfo.location.latitude+"",poiInfo.location.longitude+"");

            if(isFromOrigin){
                InterchangeSearch.sourceInfo = poiInfo;
            }else{
                InterchangeSearch.destinationInfo = poiInfo;
            }

            this.finish();

        }else if(parent == historyListView){
            InterchangeSearchHistoryAdapter.ViewHolder viewHolder = (InterchangeSearchHistoryAdapter.ViewHolder)view.getTag();
            PoiInfo poiInfo = new PoiInfo();
            LatLng latLng;
            if (viewHolder == null){//我的位置项
                poiInfo.name = "我的位置";
                 latLng = new LatLng(GPS.latitude,GPS.longitude);

            }else{
                poiInfo.name = viewHolder.titleTextView.getText().toString();
                 latLng = new LatLng(Double.parseDouble(viewHolder.latitude),Double.parseDouble(viewHolder.longitude));
            }

            poiInfo.location = latLng;

            if(isFromOrigin){
                InterchangeSearch.sourceInfo = poiInfo;
            }else{
                InterchangeSearch.destinationInfo = poiInfo;
            }
            this.finish();

        }else if(parent == storeListView){
            InterchangeStoreAdapter.ViewHolder viewHolder = (InterchangeStoreAdapter.ViewHolder) view.getTag();
            PoiInfo poiInfo = viewHolder.poiInfo;
            if(poiInfo.name.equals("未设定")){
                return;
            }else {
                if(isFromOrigin){
                    InterchangeSearch.sourceInfo = poiInfo;
                }else{
                    InterchangeSearch.destinationInfo = poiInfo;
                }
                this.finish();
            }

        }else if(parent == aroundListView){
            InterchangeAroundStopAdapter.ViewHolder viewHolder = (InterchangeAroundStopAdapter.ViewHolder) view.getTag();
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.name = viewHolder.titleTextView.getText().toString();
            if(isFromOrigin){
                InterchangeSearch.sourceInfo = poiInfo;
                InterchangeSearch.isAroundStopBySource = true;
            }else{
                InterchangeSearch.destinationInfo = poiInfo;
                InterchangeSearch.isAroundStopByDest = true;
            }
            this.finish();

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
                        //AroundStopAdapter aroundStopAdapter = new AroundStopAdapter(StationAroundFragment.this.getActivity(), list);
                        InterchangeAroundStopAdapter adapter = new InterchangeAroundStopAdapter(InterchangeLocationActivity.this,list);
                        aroundListView.setAdapter(adapter);
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
