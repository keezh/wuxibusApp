package com.wuxibus.app.activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.util.Tools;

/**
 * Created by zhongkee on 15/7/15.
 */
public class MyLocationListener implements com.baidu.location.BDLocationListener {

    Context context;

    public MyLocationListener(Context context){
        this.context = context;
    }
    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null)
            return ;
//        StringBuffer sb = new StringBuffer(256);
//        sb.append("time : ");
//        sb.append(location.getTime());
//        sb.append("\nerror code : ");
//        sb.append(location.getLocType());
//        sb.append("\nlatitude : ");
//        sb.append(location.getLatitude());
//        sb.append("\nlontitude : ");
//        sb.append(location.getLongitude());
//        sb.append("\nradius : ");
//        sb.append(location.getRadius());
//        if (location.getLocType() == BDLocation.TypeGpsLocation){
//            sb.append("\nspeed : ");
//            sb.append(location.getSpeed());
//            sb.append("\nsatellite : ");
//            sb.append(location.getSatelliteNumber());
//        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());
//        }

        GPS.latitude = location.getLatitude();
        GPS.longitude = location.getLongitude();
        if(GPS.latitude < 1 || GPS.longitude < 1){
            Tools.showToast(context, "定位失败！", Toast.LENGTH_SHORT);
        }

       // Log.i("kee",sb.toString());
        InitApplication.appLog.i("GPS:longtitude:"+GPS.longitude+" latitude:"+GPS.latitude);
    }
}
