package com.wuxibus.app.util;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.wuxibus.app.activity.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhongkee on 15/12/7.
 */
public class JSBridge {
    WebViewActivity activity;

    public JSBridge(WebViewActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void bus_show_share_button(String jsonString){

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String text = jsonObject.getString("text");
            String title = jsonObject.getString("title");
            String imageUrl = jsonObject.getString("image_url");
            Log.d("webview",text);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void bus_show_share_button(String text,String title,String imageUrl,String link){

        Log.d("webview", text + " " + title + " " + imageUrl + " " + link);
        activity.showShareButton(text, title, imageUrl, link);

    }

    @JavascriptInterface
    public void bus_hide_share_button(){
        activity.hideShareButton();

    }

    /**
     * program_name: '无锡智慧公交', // 应用名
     program_version: '1.0.1', // 应用版本
     types: 'HM 1SW', // 设备类型
     system: 'Android 5.1', // 设备系统
     device_token: 'ebbcd6a7 0f1dcb40 b22904b0 417dfdea 31649861 93474e85 d39eabea b60e16f0' // 设备识别号
     * @return
     */
    @JavascriptInterface
    public String getDeviceInfo(){
        String program_name = "无锡智慧公交";
        String pkName = activity.getPackageName();
        String versionName = "";
        int versionCode = 0;
        try {
            versionName = activity.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            versionCode = activity.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String program_version = versionName;
        String types = Build.MODEL;
        String system = Build.VERSION.RELEASE + " "+Build.VERSION.SDK;
        String device_token = DeviceTools.getDeviceIMEI(activity);

        String deviceInfo = "{ program_name:'"+program_name+"',program_version:'"+program_version+"',"
                +"types:'"+types+"',system:'"+system+"',device_token:'"+device_token+"'}";

        return deviceInfo;
    }

    private String getHandSetInfo(){
        String handSetInfo=
                "手机型号:" + android.os.Build.MODEL +
                        ",SDK版本:" + android.os.Build.VERSION.SDK +
                        ",系统版本:" + android.os.Build.VERSION.RELEASE;
        return handSetInfo;

    }

    @JavascriptInterface
    public void testBridge(String str){
        Log.d("webview",str.toString());
    }
}
