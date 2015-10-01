package com.wuxibus.app.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.StopListModel;
import com.wuxibus.app.entity.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/7/13.
 * 以下是
 * 第一步，发起POI检索，获取相应线路的UID；
 //以城市内检索为例，详细方法请参考POI检索部分的相关介绍
 mSearch.searchInCity((new PoiCitySearchOption())
 .city(“北京”)
 .keyword(“717”);
 第二步，在POI检索结果中判断该POI类型是否为公交信息；
 public void onGetPoiResult(PoiResult result) {
 if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
 return;
 }
 //遍历所有POI，找到类型为公交线路的POI
 for (PoiInfo poi : result.getAllPoi()) {
 if (poi.type == PoiInfo.POITYPE.BUS_LINE ||poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
 //说明该条POI为公交信息，获取该条POI的UID
 busLineId = poi.uid;
 break;
 }
 }
 }
 第三步，定义并设置公交信息结果监听者（与POI类似），并发起公交详情检索；
 //如下代码为发起检索代码，定义监听者和设置监听器的方法与POI中的类似
 mBusLineSearch.searchBusLine((new BusLineSearchOption()
 .city(“北京”)
 .uid(busLineId)));
 */
public class LineMapActivity extends Activity implements OnGetPoiSearchResultListener,
        OnGetBusLineSearchResultListener, BDLocationListener {
    PoiSearch mPoiSearch = PoiSearch.newInstance();
    BusLineSearch busLineSearch;
    private String busLineId;
    List<List<BusLineResult.BusStation>> twoStations = new ArrayList<List<BusLineResult.BusStation>>();

    MapView mapView;
    private BaiduMap mBaiduMap;

    String lineTitle;
    private boolean hasFoundLine = false;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener;

    ImageView backImageView;

    //线路名称
    TextView titleTextView;
    TextView startTextView;
    TextView endTextView;
    TextView startEndTimeTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_map);
        mapView = (MapView) findViewById(R.id.bmapView);
        //百度接口
        busLineSearch = BusLineSearch.newInstance();
        lineTitle = this.getIntent().getStringExtra("lineTitle");//11路，12路，35路返回数据有异常

        mPoiSearch.searchInCity(new PoiCitySearchOption().city("无锡").keyword(lineTitle));
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        busLineSearch.setOnGetBusLineSearchResultListener(this);

        myListener = new MyLocationListener();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initMapView();

        initViews();


    }

    public void initViews(){
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        startTextView = (TextView) findViewById(R.id.start_textview);
        endTextView = (TextView) findViewById(R.id.end_textview);
        startEndTimeTextView = (TextView) findViewById(R.id.start_end_textview);
        titleTextView.setText(this.getIntent().getExtras().getString("lineTitle"));
        startTextView.setText(this.getIntent().getExtras().getString("startStop"));
        endTextView.setText(this.getIntent().getExtras().getString("endStop"));
        startEndTimeTextView.setText(this.getIntent().getExtras().getString("startEndTime"));

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LineMapActivity.this.finish();
            }
        });


    }

    public void initCenter(){
        List<Stop> stops = StopListModel.newInstance().currentLiveStops;
        int size = stops.size();
        LatLng cenpt = new LatLng(Double.parseDouble(stops.get(size - 1).getLatitude_baidu()),
                Double.parseDouble(stops.get(size - 1).getLongitude_baidu()));
        //设定中心点坐标
       // LatLng cenpt = new LatLng(31.565137, 120.288553);
//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(14)
                .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
        mapView.getMap().setMapStatus(mMapStatusUpdate);
    }


    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String city = bdLocation.getCity();
            InitApplication.appLog.i("kee city = " + city);
        }
    }


    public void initMapView(){
        // 开启定位图层
        mBaiduMap = mapView.getMap();
        mapView.getMap().setMyLocationEnabled(true);
        initCenter();

        drawLineInfo();




//// 构造定位数据
//        //31.565137, 120.288553
//        MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(500)
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(100).latitude(31.565137)
//                .longitude(120.288553).build();
//// 设置定位数据
//        mapView.getMap().setMyLocationData(locData);
//// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geo);
//        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//        mapView.getMap().setMyLocationConfiguration(config);
//// 当不需要定位图层时关闭定位图层
//        //mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        //遍历所有POI，找到类型为公交线路的POI  
        for (PoiInfo poi : result.getAllPoi()) {
            if (poi.type == PoiInfo.POITYPE.BUS_LINE ||poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                //说明该条POI为公交信息，获取该条POI的UID
                busLineId = poi.uid;
                //break;
                //如下代码为发起检索代码，定义监听者和设置监听器的方法与POI中的类似
                busLineSearch.searchBusLine((new BusLineSearchOption()
                        .city("无锡")
                        .uid(busLineId)));
            }
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetBusLineResult(BusLineResult busLineResult) {
        //String lineName = busLineResult.getBusLineName();
        List<BusLineResult.BusStation> busStationList = busLineResult.getStations();
        if(busStationList == null){//release版本，快2，快3，崩溃问题
            return;
        }
        //此段算法参考ios,匹配用户选中的线路
        if(hasFoundLine){//只要找到一条线路，就不用再次匹配
            return;
        }
        int checkCount = 0;
        List<Stop> stops = StopListModel.newInstance().currentLiveStops;
        if(busStationList.size() == stops.size()){
            for (int i = 0;i<busStationList.size();i++){
                String baiduTitle = busStationList.get(i).getTitle();
                String wuxiStopName = stops.get(i).getStop_name();
                if(baiduTitle.equals(wuxiStopName) || baiduTitle.indexOf(wuxiStopName) >= 0){
                    checkCount++;
                }

            }
        }

        if(checkCount > busStationList.size()*0.6){

            hasFoundLine = true;
            List<BusLineResult.BusStep> stepList = busLineResult.getSteps();
            //step 1,画线路

            for (int i = 0;i<stepList.size();i++){

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.argb(150,0,0,255));
                polylineOptions.width(10);
                List<LatLng> listPoints = stepList.get(i).getWayPoints();
                polylineOptions.points(listPoints);

                mBaiduMap.addOverlay(polylineOptions);

            }
            //step 2:绘制圆形,
//            for (int i = 0; i < stops.size(); i++) {
//                LatLng latLng = new LatLng(Double.parseDouble(stops.get(i).getLatitude_baidu()),
//                        Double.parseDouble(stops.get(i).getLongitude_baidu()));
//                CircleOptions circleOptions = new CircleOptions();
//                circleOptions.center(latLng);
//                circleOptions.radius(5);
//                circleOptions.fillColor(Color.WHITE);
//                //2 width,Color.BLUE边的颜色
//                Stroke stroke = new Stroke(2,Color.BLUE);
//                circleOptions.stroke(stroke);
//                mBaiduMap.addOverlay(circleOptions);
//
//            }




        }
    }

    public void drawLineInfo(){

        List<Stop> stops = StopListModel.newInstance().currentLiveStops;

        //step 2: 绘制站点位置，点击站点显示站点名称
        for (int i = 0; i < stops.size(); i++) {
            BitmapDescriptor circle = BitmapDescriptorFactory.fromResource(R.drawable.route_map_spot);
            LatLng latlng = new LatLng(Double.parseDouble(stops.get(i).getLatitude_baidu()),
                    Double.parseDouble(stops.get(i).getLongitude_baidu()));
            OverlayOptions circleMarker = new MarkerOptions().position(latlng).icon(circle)
                    .zIndex(5);
            //mBaiduMap.addOverlay(circleMarker);
            Marker marker = (Marker) (mBaiduMap.addOverlay(circleMarker));
            Bundle bundle = new Bundle();
            bundle.putSerializable("stop", stops.get(i));
            marker.setExtraInfo(bundle);

        }

        //step 3,绘制实时公交图标

        for(int i= 0;i< stops.size();i++){
            BitmapDescriptor selectBusIcon = BitmapDescriptorFactory.fromResource(R.drawable.route_map_pin_me);
            BitmapDescriptor hasBusIcon = BitmapDescriptorFactory.fromResource(R.drawable.route_map_pin_bus);
            LatLng latlng = new LatLng(Double.parseDouble(stops.get(i).getLatitude_baidu()),
                    Double.parseDouble(stops.get(i).getLongitude_baidu()));
            OverlayOptions makerOptions = null;
            Marker marker = null;
            if(stops.get(i).isSelected()){
                makerOptions = new MarkerOptions().position(latlng).icon(selectBusIcon)
                        .zIndex(7);
                marker = (Marker)mBaiduMap.addOverlay(makerOptions);
            }else if(stops.get(i).getHasBus()){
                makerOptions = new MarkerOptions().position(latlng).icon(hasBusIcon)
                        .zIndex(7);
                marker = (Marker)mBaiduMap.addOverlay(makerOptions);
            }

            if(makerOptions != null && marker != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("stop",stops.get(i));
                bundle.putString("flag","pinIcon");
                marker.setExtraInfo(bundle);
            }


        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {

                InfoWindow infoWindow;
                View view;
                //判断点击的是否是pin图标
                if(marker.getExtraInfo().getString("flag")!= null){

                    Stop stop = (Stop)marker.getExtraInfo().get("stop");


                    view = View.inflate(LineMapActivity.this,R.layout.map_pin_item,null);
                    TextView stopName = (TextView) view.findViewById(R.id.stop_name_textview);
                    TextView tip = (TextView) view.findViewById(R.id.tip_textview);
                    stopName.setText(stop.getStop_name());
                    if(stop.getHasBus() && !stop.isSelected()){
                        tip.setText("车辆"+stop.getBusselfid()+"于"+stop.getActdatetime()+"到达此站");
                    }else {
                        tip.setText("最近一班车距离您还有"+StopListModel.newInstance().distance+"站");
                    }


                    return false;

                }else{
                    Stop stop = (Stop)marker.getExtraInfo().get("stop");

                    TextView stopInfo = new TextView(getApplicationContext());
                    stopInfo.setBackgroundResource(R.drawable.map_tooltip);
                    stopInfo.setText(stop.getStop_name());
                    //stopInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    view = stopInfo;
                }



                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 10;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                //为弹出的InfoWindow添加点击事件
                infoWindow = new InfoWindow(view,llInfo,0);
                //显示InfoWindow
                mBaiduMap.showInfoWindow(infoWindow);
                //设置详细信息布局为可见

                return false;
            }
        });
    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null)
            return ;
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation){
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
        }

        InitApplication.appLog.i(sb.toString());

    }
}
