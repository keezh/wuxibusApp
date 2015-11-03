package com.wuxibus.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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
import com.wuxibus.app.adapter.InterchangeSearchAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.HomeCompanyLocation;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.util.StorageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/10/24.
 */
public class MapLocationResetActivity extends Activity implements View.OnClickListener,TextWatcher,AdapterView.OnItemClickListener {

    View mapContainer;
    MapView mapView;

    ListView searchResultListView;

    //map中心位置
    TextView mapTitleTextView;
    ImageView checkTitleImageView;

    String position;

    EditText editText;
    TextView cancelTextView;

    PoiSearch poiSearch = PoiSearch.newInstance();
    List<PoiInfo> searchResultList = new ArrayList<PoiInfo>();
    private BaiduMap mBaiduMap;
    private LatLng choosePoint;
    private String chooseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_location_reset);

        mapContainer = findViewById(R.id.map_container);
        mapView = (MapView) findViewById(R.id.location_mapview);
        searchResultListView = (ListView) findViewById(R.id.search_result_listview);
        searchResultListView.setOnItemClickListener(this);

        mapTitleTextView = (TextView) findViewById(R.id.title_tv);
        checkTitleImageView = (ImageView) findViewById(R.id.check_address_iv);
        checkTitleImageView.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.location_et);
        ;
        cancelTextView = (TextView) findViewById(R.id.cancel_tv);
        cancelTextView.setOnClickListener(this);
        editText.addTextChangedListener(this);
        mBaiduMap = mapView.getMap();

        position = getIntent().getStringExtra("position");

        //百度地图当前位置
        initCenter();

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
        //地图选点
        choosePoint = cenpt;

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
                    Toast.makeText(MapLocationResetActivity.this, "抱歉，未能找到结果",
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

    @Override
    public void onClick(View v) {
        if(v == cancelTextView){
            this.finish();
        }else if(v == checkTitleImageView){
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.location = choosePoint;
            poiInfo.name = chooseAddress;
            if(position.equals("0")){//家

                InterchangeSearch.homePoiInfo = poiInfo;

            }else if(position.equals("1")){
                InterchangeSearch.companyInfo = poiInfo;
            }

            StorageUtil.saveBindDivice(this);

            this.finish();

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(parent == searchResultListView){
            InterchangeSearchAdapter.ViewHolder viewHolder = (InterchangeSearchAdapter.ViewHolder) view.getTag();
            PoiInfo poiInfo = viewHolder.poiInfo;
            if (this.position.equals("0")){
                InterchangeSearch.homePoiInfo = poiInfo;
            }else if(this.position.equals("1")){
                InterchangeSearch.companyInfo = poiInfo;
            }

            StorageUtil.saveBindDivice(this);
            this.finish();
        }

    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String keyword = editText.getText().toString().trim();
        if(keyword.equals("")){
            mapContainer.setVisibility(View.VISIBLE);
            searchResultListView.setVisibility(View.GONE);
        }else {
            mapContainer.setVisibility(View.GONE);
            searchResultListView.setVisibility(View.VISIBLE);
            poiSearch(keyword);
        }
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
                        MapLocationResetActivity.this);
                searchResultListView.setAdapter(adapter);

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


}
