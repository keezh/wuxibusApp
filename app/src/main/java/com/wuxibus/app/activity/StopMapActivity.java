package com.wuxibus.app.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.wuxibus.app.R;
import com.wuxibus.app.entity.StopInfoMap;
import com.wuxibus.app.entity.StopMapModel;

import java.util.List;

/**
 * Created by zhongkee on 15/8/29.
 */
public class StopMapActivity extends Activity implements View.OnClickListener{

    public TextView titleTextView;
    public ImageView backImageView;
    MapView mapView;
    private BaiduMap mBaiduMap;
    List<StopInfoMap> list;
    String stopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_map);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        backImageView.setOnClickListener(this);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        stopName = this.getIntent().getExtras().getString("stopName");
        titleTextView.setText(stopName);
        mapView = (MapView) findViewById(R.id.bmapView);

        initMapView();
        initCenter();
        drawStopIcon();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoWindow infoWindow;
                View view = View.inflate(getApplicationContext(),R.layout.stop_map_info,null);
                String info = marker.getExtraInfo().getString("info");
                TextView stopTitle = (TextView) view.findViewById(R.id.stop_title);
                TextView lineInfo = (TextView) view.findViewById(R.id.line_info);
                stopTitle.setText(stopName);
                lineInfo.setText(info);
//                TextView stopInfo = new TextView(getApplicationContext());
//                stopInfo.setBackgroundResource(R.drawable.map_tooltip);
//                stopInfo.setText(info);

//                view = stopInfo;

                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 100;
                //p.x -= 20;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                //为弹出的InfoWindow添加点击事件
                infoWindow = new InfoWindow(view,llInfo,0);
                //显示InfoWindow
                mBaiduMap.showInfoWindow(infoWindow);
                return false;
            }
        });

    }

    public void initMapView() {
        // 开启定位图层
        mBaiduMap = mapView.getMap();
        mapView.getMap().setMyLocationEnabled(true);
    }

    public void initCenter(){
        //设定中心点坐标
        list = StopMapModel.getInstance().getList();
        double lat = 31.565137;
        double lng = 120.288553;
        if(list != null && list.size()>=1){
            lat = Double.parseDouble(list.get(0).getBlatitude());
            lng = Double.parseDouble(list.get(0).getBlongitude());

        }
        //LatLng cenpt = new LatLng(31.565137, 120.288553);
        LatLng cenpt = new LatLng(lat, lng);
//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
        mapView.getMap().setMapStatus(mMapStatusUpdate);
    }

    public void drawStopIcon(){

        for (int i = 0; list != null && i < list.size(); i++) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.route_map_pin_bus);
            LatLng latlng = new LatLng(Double.parseDouble(list.get(i).getBlatitude()),
                    Double.parseDouble(list.get(i).getBlongitude()));
            OverlayOptions makerOptions = null;
            Marker marker = null;
            makerOptions = new MarkerOptions().position(latlng).icon(icon)
                    .zIndex(7);
            marker = (Marker)mBaiduMap.addOverlay(makerOptions);
            Bundle bundle = new Bundle();
            bundle.putString("info",list.get(i).getLine_info());
            marker.setExtraInfo(bundle);
        }

    }


    @Override
    public void onClick(View v) {
        if(v == backImageView){
            this.finish();
        }
    }
}
