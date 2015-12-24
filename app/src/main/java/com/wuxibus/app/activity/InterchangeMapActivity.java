package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.InterchangeModel;
import com.wuxibus.app.R;
import com.wuxibus.app.StopListModel;
import com.wuxibus.app.entity.InterchangeScheme;
import com.wuxibus.app.entity.InterchangeStep;
import com.wuxibus.app.entity.InterchangeStepLocation;
import com.wuxibus.app.entity.InterchangeVehicle;
import com.wuxibus.app.entity.Stop;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/12/2.
 */
public class InterchangeMapActivity extends Activity implements View.OnClickListener {

    ImageView backImageView;
    int currentIndex;
    MapView mapView;
    private BaiduMap mBaiduMap;
    TextView titleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interchange_map);
        mapView = (MapView) findViewById(R.id.bmapView);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        backImageView.setOnClickListener(this);
        titleTextView = (TextView) findViewById(R.id.title_tv);
        currentIndex = getIntent().getIntExtra("currentIndex", 0);//传递过来的值
        titleTextView.setText("方案"+(currentIndex+1));
        initTipInfo();

        initMapView();
        initCenter();
        dropIconInfo();
    }

    public void dropIconInfo(){
        List<InterchangeScheme> schemeList = InterchangeModel.getInstance().schemeList;
        List<List<InterchangeStep>> steps = schemeList.get(currentIndex).getSteps();
        for (int i = 0; i < steps.size(); i++) {
            InterchangeStep step = steps.get(i).get(0);
            Marker marker = null;
            if (i == 0){
                BitmapDescriptor pinFrom = BitmapDescriptorFactory.fromResource(R.drawable.interchange_map_pin_from);
                LatLng latlng = new LatLng(step.getStepOriginLocation().getLat(),step.getStepOriginLocation().getLng());
                List<LatLng>footList = convertToLatLng(step.getPath());
                if (footList!= null && footList.size()>0){
                    latlng = footList.get(0);
                }
               // LatLng latLng =

                OverlayOptions circleMarker = new MarkerOptions().position(latlng).icon(pinFrom)
                        .zIndex(5);
                //mBaiduMap.addOverlay(circleMarker);
                marker = (Marker) (mBaiduMap.addOverlay(circleMarker));
            }else if(i == steps.size() - 1){
                BitmapDescriptor pinFrom = BitmapDescriptorFactory.fromResource(R.drawable.interchange_map_pin_to);
                LatLng latlng = new LatLng(step.getStepOriginLocation().getLat(),step.getStepOriginLocation().getLng());
                OverlayOptions circleMarker = new MarkerOptions().position(latlng).icon(pinFrom)
                        .zIndex(5);
                 marker = (Marker) (mBaiduMap.addOverlay(circleMarker));
            }

            if (step.getType() == 5){//步行图标
                BitmapDescriptor circle = BitmapDescriptorFactory.fromResource(R.drawable.interchange_map_spot_walk);
                LatLng latlng = new LatLng(step.getStepOriginLocation().getLat(),step.getStepOriginLocation().getLng());
                List<LatLng>footList = convertToLatLng(step.getPath());
                if (footList!= null && footList.size()>0){
                    latlng = footList.get(0);
                }
                OverlayOptions circleMarker = new MarkerOptions().position(latlng).icon(circle)
                        .zIndex(5);
                //mBaiduMap.addOverlay(circleMarker);
                 marker = (Marker) (mBaiduMap.addOverlay(circleMarker));
            }

            if (step.getType() == 3){//bus图标
                BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.interchange_map_spot_bus);
                LatLng latlng = new LatLng(step.getStepOriginLocation().getLat(),step.getStepOriginLocation().getLng());
                List<LatLng>footList = convertToLatLng(step.getPath());
                if (footList!= null && footList.size()>0){
                    latlng = footList.get(0);
                }
                OverlayOptions circleMarker = new MarkerOptions().position(latlng).icon(busIcon)
                        .zIndex(5);
                //mBaiduMap.addOverlay(circleMarker);
                 marker = (Marker) (mBaiduMap.addOverlay(circleMarker));
            }

            //xian
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.argb(150, 0, 0, 255));
            polylineOptions.width(10);
            List<LatLng> listPoints = convertToLatLng(step.getPath());
            //火车站---》宜家商场  crashbug
            if (listPoints != null && listPoints.size() < 2){
                continue;
            }
            polylineOptions.points(listPoints);

            mBaiduMap.addOverlay(polylineOptions);

            if(step.getType() == 5 && marker != null){
                Bundle bundle = new Bundle();
                bundle.putInt("type", step.getType());
                bundle.putString("footInfo",step.getStepInstruction());
                marker.setExtraInfo(bundle);
            }else if(step.getType() == 3 && marker != null){
                Bundle bundle = new Bundle();
                bundle.putInt("type",step.getType());
                bundle.putString("lineName", step.getVehicle().getName());
                bundle.putString("stopName",step.getVehicle().getStart_name());
                marker.setExtraInfo(bundle);
            }


            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener(){

                @Override
                public boolean onMarkerClick(Marker marker) {
                    Bundle bundle = marker.getExtraInfo();

                    InfoWindow infoWindow;
                    View view;
                    view = View.inflate(getApplicationContext(),R.layout.interchange_map_info,null);
                    TextView title001 = (TextView) view.findViewById(R.id.title001);
                    TextView title002 = (TextView) view.findViewById(R.id.title002);
                    if(bundle == null){
                        view.setVisibility(View.GONE);
                        return false;
                    }
                    if(bundle.getInt("type") == 5){
                        title002.setVisibility(View.GONE);
                        title001.setText(bundle.getString("footInfo"));

                    }else if(bundle.getInt("type")==3){
                        title002.setVisibility(View.VISIBLE);
                        title001.setText(bundle.getString("lineName"));
                        title002.setText(bundle.getString("stopName"));

                    }

                    final LatLng ll = marker.getPosition();
                    Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                    p.y -= 10;
                    LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                    //为弹出的InfoWindow添加点击事件
                    infoWindow = new InfoWindow(view,llInfo,0);
                    //显示InfoWindow
                    mBaiduMap.showInfoWindow(infoWindow);

                    return false;

                }});


        }
    }



    /**
     * "path": "120.37036916347,31.57171524566;120.37062967207,31.571438358723;
     * 120.37080035011,31.571269149631;120.3709620451,31.571099940229;120.37109679093,31.570946113231;
     * 120.37173458784,31.57026157998;
     * 120.37159085896,31.570215431488;120.37136628258,31.570146208708;120.37091712983,31.570000071556;
     * 120.37080035011,31.569961614372;120.37048594318,31.56986162562;120.37014458709,31.569753945304;
     * 120.36961458684,31.569569350184;120.36863543383,31.569253999334;120.36829407774,31.569184775832;
     * 120.3682222133,31.569138626802;120.3684827219,31.568915572833"
     * @param path
     * @return
     */
    public List<LatLng> convertToLatLng(String path){
        List<LatLng> list = new ArrayList<LatLng>();
        String allPoints[] = path.split(";");
        for (int i = 0; i < allPoints.length; i++) {
            String point = allPoints[i];
            String latLng[] = point.split(",");
            LatLng latLngBaidu = new LatLng(Double.parseDouble(latLng[1]),Double.parseDouble(latLng[0]));
            list.add(latLngBaidu);
        }

        return list;
    }

    public void initMapView() {
        // 开启定位图层
        mBaiduMap = mapView.getMap();
        mapView.getMap().setMyLocationEnabled(true);
    }

    public void initCenter(){
        List<InterchangeScheme> schemeList = InterchangeModel.getInstance().schemeList;
        InterchangeStepLocation originLocation = schemeList.get(currentIndex).getSteps().get(0).get(0).getStepOriginLocation();
        LatLng cenpt = new LatLng(originLocation.getLat(),originLocation.getLng());

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

    public void initTipInfo(){
        List<InterchangeScheme> schemeList = InterchangeModel.getInstance().schemeList;
        LinearLayout container = (LinearLayout)findViewById(R.id.title_container);
        TextView detailTextView = (TextView) findViewById(R.id.detail_tv);
        List<List<InterchangeStep>> steps = schemeList.get(currentIndex).getSteps();
        layoutTitleContainer(steps, container);
        int time = schemeList.get(currentIndex).totalTime;
        int nums = schemeList.get(currentIndex).totalStops;
        int meters = schemeList.get(currentIndex).totalMeters;

        detailTextView.setText(Tools.getTimes(time) + "  |  " + nums + "站" + "  |  步行" + meters + "米");
    }

    /**
     * 动态添加头
     * @param stepList
     * @param container
     */
    public void layoutTitleContainer(List<List<InterchangeStep>> stepList,LinearLayout container){

        TextView nextImageView = null;
        int margin = DensityUtil.dip2px(this, 5);//5dp

        for (int i = 0; i < stepList.size(); i++) {
            List<InterchangeStep> steps = stepList.get(i);

            InterchangeVehicle vehicle = steps.get(0).getVehicle();
            if(vehicle != null){
                TextView titleView = new TextView(this);
                titleView.setTextColor(Color.BLACK);
                titleView.setTextSize(18);
                titleView.setText(vehicle.getName());
                nextImageView = new TextView(this);
                nextImageView.setTextSize(18);
                nextImageView.setText(" → ");
                // nextImageView.setImageResource(R.drawable.interchange_plan_icon_next);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(margin, margin, margin, margin);
                nextImageView.setLayoutParams(lp);
                container.addView(titleView);
                container.addView(nextImageView);
            }
        }
        //移除最后一个view
        container.removeView(nextImageView);
        container.requestLayout();
    }

    @Override
    public void onClick(View v) {
        if(v == backImageView){
            finish();
        }
    }


}
