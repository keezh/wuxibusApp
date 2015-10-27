package com.wuxibus.app.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.HomeCompanyLocation;
import com.wuxibus.app.entity.InterchangeSearch;

/**
 * Created by zhongkee on 15/10/24.
 */
public class StorageUtil {

    public static void saveBindDivice(Activity activity){
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= activity.getSharedPreferences(AllConstants.HomeCompanyXml,
                Activity.MODE_PRIVATE);
//实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
//用putString的方法保存数据
        if(InterchangeSearch.homePoiInfo != null){
            String homeAddress = InterchangeSearch.homePoiInfo.name;
            String homeLatitude = InterchangeSearch.homePoiInfo.location.latitude+"";
            String homeLongitude = InterchangeSearch.homePoiInfo.location.longitude+"";
            editor.putString("homeAddress", homeAddress);
            editor.putString("homeLatitude", homeLatitude);
            editor.putString("homeLongitude", homeLongitude);
        }

        if(InterchangeSearch.companyInfo != null){
            String companyAddress = InterchangeSearch.companyInfo.name;
            String companyLatitude = InterchangeSearch.companyInfo.location.latitude+"";
            String companyLongitude = InterchangeSearch.companyInfo.location.longitude+"";

            editor.putString("companyAddress", companyAddress);
            editor.putString("companyLatitude", companyLatitude);
            editor.putString("companyLongitude", companyLongitude);
        }





//提交当前数据
        editor.commit();
    }

    public static  void readBindDevice(Activity activity){

        SharedPreferences sharedPreferences= activity.getSharedPreferences(AllConstants.HomeCompanyXml,
                Activity.MODE_PRIVATE);
// 使用getString方法获得value，注意第2个参数是value的默认值

        String homeAddress =sharedPreferences.getString("homeAddress","");
        String homeLatitude =sharedPreferences.getString("homeLatitude","");
        String homeLongitude =sharedPreferences.getString("homeLongitude","");
        String companyAddress =sharedPreferences.getString("companyAddress","");
        String companyLatitude =sharedPreferences.getString("companyLatitude","");
        String companyLongitude =sharedPreferences.getString("companyLongitude","");
        if(homeAddress.equals("") || companyAddress.equals("")){
            InterchangeSearch.homePoiInfo = null;
            InterchangeSearch.companyInfo = null;
            return;
        }

        PoiInfo poiInfo = new PoiInfo();
        poiInfo.location = new LatLng(Double.parseDouble(homeLatitude),Double.parseDouble(homeLongitude));
        poiInfo.name = homeAddress;
        InterchangeSearch.homePoiInfo = poiInfo;

        PoiInfo companyPoiInfo = new PoiInfo();
        companyPoiInfo.location = new LatLng(Double.parseDouble(companyLatitude),Double.parseDouble(companyLongitude));
        companyPoiInfo.name = companyAddress;
        InterchangeSearch.companyInfo = companyPoiInfo;

    }
}
