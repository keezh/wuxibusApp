package com.wuxibus.app.activity;



import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.update.UmengUpdateAgent;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.fragment.HomeFragment;
import com.wuxibus.app.fragment.MyFragment;
import com.wuxibus.app.fragment.RouteFragment;
import com.wuxibus.app.fragment.StationFragment;
import com.wuxibus.app.util.AES;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DeviceTools;
import com.wuxibus.app.util.StringUtil;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener,BDLocationListener,View.OnClickListener {
    private FragmentManager fragmentManager;
    private RadioGroup rg_home_bottom;
    private HomeFragment homeFragment;
    private RouteFragment routeFragment;
    //private InterchangeFragment interchangeFragment;
    private StationFragment stationFragment;
    private MyFragment myFragment;

    View advContainer;
    View mainContentContainer;
    View tabContainer;
    SmartImageView advImageView;
    TextView jumpTextView;
    //广告标题
    String title;
    String duration = "3";
    int second = 3;
    //用户是否点击调过按钮
    boolean hasJump = false;
    String url;

    Handler myHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    int secondTemp = (int)msg.obj;
                    jumpTextView.setText(secondTemp + " 跳过");
                    if(secondTemp <= 0){
                        //duration 之后,广告页消失，显示首页
                        showHomeView();
                    }
                    break;
            }
        }
    };

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        //umeng
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);

        advImageView = (SmartImageView) findViewById(R.id.adv_imageview);
        jumpTextView = (TextView) findViewById(R.id.jump_textview);
        advImageView.setOnClickListener(this);
        jumpTextView.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);
        AllConstants.Width = dm.widthPixels;// 获取屏幕分辨率宽度
        AllConstants.Height = dm.heightPixels;
        InitApplication.appLog.i("phone width:"+AllConstants.Width+" height:"+AllConstants.Height);
        initBaiduGps();

        initAdvImageView();
        //showHomeView();
    }

    public void updateBaiduGps(){
        //if(!mLocationClient.isStarted()){
            mLocationClient.start();
            mLocationClient.requestLocation();
            mLocationClient.registerLocationListener(myListener);
        //}
    }

    public void initBaiduGps(){
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocation();
        mLocationClient.start();

        initView();
        initListeners();
        //启动的时候绑定，绑定状态应该保存在本地，保存成功后，不需要发网络请求了
        String result = readBindDevice();
        if(result != null && result.equals("yes") ){

        }else{//没有绑定则需要调用接口绑定
            bindDevice();
        }

    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，bd09ll
        int span=5000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        //option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
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

    private void initView(){
        advContainer = findViewById(R.id.adv_container);
        mainContentContainer = findViewById(R.id.fra_main_content);
        tabContainer = findViewById(R.id.tab_container);
        advImageView = (SmartImageView) findViewById(R.id.adv_imageview);

        fragmentManager = getSupportFragmentManager();
        rg_home_bottom = (RadioGroup)findViewById(R.id.rg_home_bottom);
        if(homeFragment == null){
            homeFragment = new HomeFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fra_main_content, homeFragment).commit();

    }


    public void initAdvImageView(){
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_start_pic");
        //paras.put("flag","index_pic");
        //加密处理
        paras = AES7PaddingUtil.toAES7Padding(paras);

        //宽：屏幕宽度
//        高：屏幕高度-顶部状态栏高-底部虚拟按键高-(屏幕宽度 * 29.0 / 75.0)

    //    {"duration":"5","title":"测试启动广告","def":"0","url":"http:\/\/www.wuxibus.com\/","index_pic":"http:\/\/img.wxbus.com.cn\/attachment\/2015\/08\/1438826839.jpg"}
        VolleyManager.getJson(AllConstants.ServerUrl, paras, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String jsonString = jsonObject.toString();


                String index_pic;
                try {
                    if (!jsonString.equals("{}")) {//具有广告图
                         duration = jsonObject.getString("duration");
                        title = jsonObject.getString("title");
                        String def = jsonObject.getString("def");//1:强制显示,0可跳过
                        if(def.equals("1")){
                            jumpTextView.setEnabled(false);
                        }else{
                            jumpTextView.setEnabled(true);
                        }
                         url = jsonObject.getString("url");
                         index_pic = jsonObject.getString("index_pic");
                        int height = (int)(AllConstants.Width * 946/720.0f);
                        height = AllConstants.Height - (int)(AllConstants.Width * 29.0/75.0) - 20;
                        String imgUrl = index_pic +"/"+AllConstants.Width+"x"+height;
                        boolean flag = BitmapCache.getInstern().getSDCardBitmap(imgUrl, advImageView, new BitmapCache.CallBackSDcardImg() {
                            @Override
                            public void setImgToView(Bitmap bitmap, ImageView imgView) {
                                new ObjectAnimator().ofFloat(imgView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                                imgView.setImageBitmap(bitmap);
                            }
                        });
                        if (!flag) {
                            VolleyManager.loadImage(advImageView, imgUrl, R.drawable.background_img);
                        }

                       // VolleyManager.loadImage(advImageView,imgUrl,R.drawable.background_img);
                        advImageView.setVisibility(View.VISIBLE);
                    }else if(jsonString.endsWith("{}")){//720x946
                        //advImageView.setRatio(720/946.0f);
                        int height = AllConstants.Height - (int)(AllConstants.Width * 29.0/75.0) - 20;
                        float ratio = (float) (AllConstants.Width*1.0/height);
                        advImageView.setRatio(ratio);


                        advImageView.setImageResource(R.drawable.launch_ad);
                        advImageView.setVisibility(View.VISIBLE);

                        advImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    jumpSeconds(duration);

                }catch (Exception e){

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //失败了返回首页
                showHomeView();
            }
        });

    }

    public void jumpSeconds(String duration){
        second = Integer.parseInt(duration);

       Runnable r =  new Runnable() {
            @Override
            public void run() {
                while(!hasJump && second >= 0){

                    try {

                        //jumpTextView.setText(second + " 跳过");//刷新按钮
                        Message msg = new Message();
                        msg.obj = second;
                        msg.what = 1;//标识消息类型,仅仅一个消息就使用1了


                        myHander.sendMessage(msg);
                        second--;
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        //启动线程
        Thread t = new Thread(r);
        t.start();

    }

    private void initListeners(){
        rg_home_bottom.setOnCheckedChangeListener(this);
    }

    /**
     * 底部tab监听事件
     * @param radioGroup
     * @param i
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        hideFragments(transaction);
        switch (i){
            case R.id.home_btn:
                if(homeFragment != null){
                    transaction.show(homeFragment);
                }else{
                    homeFragment = new HomeFragment();

                    transaction.add(R.id.fra_main_content,homeFragment);
                }
                break;
            case R.id.route_btn:
                if(routeFragment != null){
                    transaction.show(routeFragment);
                }else{
                    routeFragment = new RouteFragment();
                    transaction.add(R.id.fra_main_content,
                            routeFragment);

                }
                break;
            case R.id.station_btn:
                if(stationFragment != null){
                    transaction.show(stationFragment);
                }else{
                    stationFragment = new StationFragment();
                    transaction.add(R.id.fra_main_content,
                            stationFragment);
                }
                break;
//            case R.id.interchange_btn:
//                if(interchangeFragment != null){
//                    transaction.show(interchangeFragment);
//                }else{
//                    interchangeFragment = new InterchangeFragment();
//                    transaction.add(R.id.fra_main_content,
//                            interchangeFragment);
//                }
//                break;
            case R.id.my_btn:
                if(myFragment != null){
                    transaction.show(myFragment);
                }else{
                    myFragment = new MyFragment();
                    transaction.add(R.id.fra_main_content,
                            myFragment);
                }
                break;
        }
        transaction.commit();
    }

    public void saveBindDivice(){
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= getSharedPreferences("bind_xml",
                Activity.MODE_PRIVATE);
//实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
//用putString的方法保存数据
        editor.putString("bind", "yes");
//提交当前数据
        editor.commit();
    }

    public String readBindDevice(){

        SharedPreferences sharedPreferences= getSharedPreferences("bind_xml",
                Activity.MODE_PRIVATE);
// 使用getString方法获得value，注意第2个参数是value的默认值
        String str =sharedPreferences.getString("bind","");
        return str;

    }





    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (routeFragment != null) {
            transaction.hide(routeFragment);
        }
//        if (interchangeFragment != null) {
//            transaction.hide(interchangeFragment);
//        }
        if (stationFragment != null) {
            transaction.hide(stationFragment);
        }
        if (myFragment != null) {
            transaction.hide(myFragment);
        }

    }


    public void  bindDevice(){
        //http://api.wxbus.com.cn/api/?m=device_install&device_token=f0ef5d1facd0bb3409&device_type=iOS&device_name=iPhone%206

        String url = AllConstants.ServerUrl;
        //String geturl = url +  "/?m=device_install&device_token="+ DeviceTools.getDeviceIMEI(this)+"&device_type=Android&device_name=Android";
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","device_install");
        params.put("device_token",DeviceTools.getDeviceIMEI(this));
        params.put("device_type","Android");
        params.put("device_name","Android");
        InitApplication.appLog.i("deviceIMei:"+DeviceTools.getDeviceIMEI(this));
        params = AES7PaddingUtil.toAES7Padding(params);

        VolleyManager.getJson(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    //不管服务器是否绑定过，都要写入到本地数据xml中
                        saveBindDivice();

                    //}
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


//    @Override
//    public void onLocationChanged(Location location) {
//
//        if(location != null){
//            GPS.latitude = location.getLatitude();
//            GPS.longitude = location.getLongitude();
//            InitApplication.appLog.i("kee latitude:"+GPS.latitude + ": longititude:"+GPS.longitude);
//        }
//
//    }



    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }

    @Override
    public void onClick(View v) {
        if(v == advImageView){
            if(title != null){
                hasJump = true;//终止线程
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("url",url);
                startActivity(intent);
                showHomeView();

            }

        }else if(v == jumpTextView){
            hasJump = true;//终止线程
            showHomeView();
        }
    }

    public void showHomeView(){
//        View advContainer;
//        View mainContentContainer;
//        View tabContainer;
        advContainer.setVisibility(View.GONE);
        mainContentContainer.setVisibility(View.VISIBLE);
        tabContainer.setVisibility(View.VISIBLE);

    }
}
