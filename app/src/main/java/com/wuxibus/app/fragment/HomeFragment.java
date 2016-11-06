package com.wuxibus.app.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.LineRealActivity;
import com.wuxibus.app.activity.MainActivity;
import com.wuxibus.app.activity.SearchAllActivity;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.adapter.HomeClassAdapter;
import com.wuxibus.app.adapter.HomeLineAdapter;
import com.wuxibus.app.adapter.HomeNewsAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.HorizontalListView;
import com.wuxibus.app.customerView.MyGridView;
import com.wuxibus.app.customerView.MyListView;
import com.wuxibus.app.customerView.MyScrollView;
import com.wuxibus.app.customerView.ScrollSwipeRefreshLayout;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AroundRoute;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.HomeAdvertise;
import com.wuxibus.app.entity.HomeClass;
import com.wuxibus.app.entity.HomeFavAroundRoute;
import com.wuxibus.app.entity.NewsDetail;
import com.wuxibus.app.entity.NewsTj;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.entity.Weather;
import com.wuxibus.app.sqlite.DBManager;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.StringUtil;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/6/16.
 */
public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener,AdapterView.OnItemClickListener,View.OnTouchListener,MyScrollView.ScrollViewListener
        ,HorizontalListView.OnScrollStateChangedListener{

    //下拉刷新控件
    ScrollSwipeRefreshLayout swipeRefreshLayout;
    MyScrollView scrollView;
    Toolbar toolbar;

    //头部广告列表
    ViewPager advertiseViewPager;
    //收藏线路与附近线路
    HorizontalListView routeListView;
    //分类图标
    MyGridView classGridView;
    //核心新闻广告位
    ViewPager newsViewPager;
    //新闻列表
    MyListView newsListView;

    // List<ImageView> imageViewList;
    //
    TextView weather_textview;
    //天气温度
    TextView temp_textview;
    //温度符号
    TextView temp_char;

    View homeTipGeo;

    //adversiste  pageControls容器，小白的
    ViewGroup viewGroup;
    //新闻小白点
    ArrayList<ImageView> adverPageControls = new ArrayList<ImageView>();
    ArrayList<ImageView> newsPageControlsList = new ArrayList<ImageView>();
//新闻小白的
    ViewGroup newsPageControls;

    //广告图片集合
    List<ImageView> list;
    //
    TextView searchTextView;
    Typeface customFont;

    TextView newsTitleTextView;

    boolean isRun = true;//线程是否在运行
    boolean isFlag = true;//线程只创建一个

    boolean isSpcialFirst = true;//bug 首次广告间隔是少于5妙，因为在广告也已经开始了

    //收藏线路
    List<Route> routeFavList;
    List<HomeFavAroundRoute> favList = new ArrayList<HomeFavAroundRoute>();
    List<HomeFavAroundRoute> aroundList = new ArrayList<HomeFavAroundRoute>();
    List<HomeFavAroundRoute> allList = new ArrayList<HomeFavAroundRoute>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        swipeRefreshLayout = (ScrollSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        scrollView = (MyScrollView) view.findViewById(R.id.scrollview);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        advertiseViewPager = (ViewPager) view.findViewById(R.id.advertise_viewpage);
        routeListView = (HorizontalListView) view.findViewById(R.id.route_listview);
        homeTipGeo = view.findViewById(R.id.home_tip_geo);
        classGridView = (MyGridView) view.findViewById(R.id.class_gridview);
        newsViewPager = (ViewPager) view.findViewById(R.id.news_viewpage);
        newsListView = (MyListView) view.findViewById(R.id.news_listview);
        weather_textview = (TextView) view.findViewById(R.id.weather_textview);
        temp_textview = (TextView) view.findViewById(R.id.temperature_textview);
        temp_char = (TextView) view.findViewById(R.id.temp_char);

        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
        newsPageControls = (ViewGroup) view.findViewById(R.id.newsPageControls);
        searchTextView = (TextView) view.findViewById(R.id.search_textview);
        newsTitleTextView = (TextView) view.findViewById(R.id.news_title_textview);
        //解决手势冲突问题
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.contentViewGroup);
        swipeRefreshLayout.setViewGroup(viewGroup);

        advertiseViewPager.setOffscreenPageLimit(0);

        searchTextView.setOnClickListener(this);
        routeListView.setOnItemClickListener(this);
        scrollView.setOnTouchListener(this);
        classGridView.setOnItemClickListener(this);
        newsListView.setOnItemClickListener(this);
        scrollView.setScrollViewListener(this);

        routeListView.setOnScrollStateChangedListener(this);
        //routeListView.setOnTouchListener(this);



        queryIndex();

        initView();

        //发送轮播消息，每隔5秒

        return view;

    }

   Handler handler =  new Handler(){

       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           if(msg.what == 101){
               int currentItem = advertiseViewPager.getCurrentItem();
               advertiseViewPager.setCurrentItem(currentItem+1);

           }
       }
   };

    public void initView(){//下拉刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                queryIndex();

                ((MainActivity)HomeFragment.this.getActivity()).updateBaiduGps();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        queryRouteAround();
                    }
                }, 500);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);


    }




        @Override
    public void onResume() {
        super.onResume();
            isRun = true;
        favList.clear();
            queryFavLine();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    //延后3秒执行，否则会得不到经纬度
                    queryRouteAround();

                }
            }, 2000);

            AVAnalytics.onFragmentStart("home-fragment-start");

    }

    public void startBannerPlay(){
        //开启一个线程，3秒发送一次轮播消息
        isRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRun) {//该线程不会结束
                    try {
                        //bug 首次进入home页面，广告也间隔少于5秒，因为页面在首页广告页时就开始计时了，
                        //当前人为的把第一次延后3秒，为8秒，其他为5妙轮转
                        if(isSpcialFirst){
                            Thread.sleep(8000);
                            isSpcialFirst = false;
                        }else{
                            Thread.sleep(5000);//
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 101;
                    handler.sendMessage(msg);
                }

            }
        }).start();
    }

    @Override
    public void onPause() {
        isRun = false;//停止，轮播发送消息线程
        super.onPause();
        AVAnalytics.onFragmentEnd("home-fragment-end");
    }

    public void showTipGeo(){
        //不存在线路的情况下
        if(allList.size() == 0){
            homeTipGeo.setVisibility(View.VISIBLE);
            routeListView.setVisibility(View.GONE);
        }else{
            homeTipGeo.setVisibility(View.GONE);
            routeListView.setVisibility(View.VISIBLE);
        }
    }

    //初始化收藏的线路
    public void queryFavLine() {
        DBManager dbManager = new DBManager(this.getActivity());
        routeFavList = dbManager.queryRoute();

        for (int i = 0; routeFavList !=null && i < routeFavList.size(); i++) {
            String lineTitle = routeFavList.get(i).getLine_title();
            String endStop = routeFavList.get(i).getStop_end();

            //Fav = 0收藏为0
            HomeFavAroundRoute route = new HomeFavAroundRoute(lineTitle,"",endStop,"","",AllConstants.FAV_FLAG);
            route.setLineId(routeFavList.get(i).getLine_id());
            route.setLineTitle(routeFavList.get(i).getLine_title());
            route.setLineCode(routeFavList.get(i).getLine_code());
            route.setLineName(routeFavList.get(i).getLine_name());
            route.setDirection(routeFavList.get(i).getDirection()+"");
            route.setStartStop(routeFavList.get(i).getStop_start());
            route.setEndStop(routeFavList.get(i).getStop_end());
            route.setStartEndTime(routeFavList.get(i).getTime_start_end());
            route.setStopName(routeFavList.get(i).getStopName());
            favList.add(route);

        }

    }

    public void initFavRoute(List<Route> routeList) {
       // HomeLineAdapter homeLineAdapter = new HomeLineAdapter(this.getActivity(), routeList);
       // routeListView.setAdapter(homeLineAdapter);
    }

    /**
     * 网络请求，这边线路
     * 周边的线路，radius 是半径，单位米
     http://api.wxbus.com.cn/api/?latitude=31.5611&longitude=120.277&m=line_nearby&radius=500
     latitude=31.5611&longitude=120.277&m=line_nearby&radius=500

     longitude=120.303283&latitude=31.569154

     */
    public void queryRouteAround(){
//        String url = AllConstants.ServerUrl;
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","line_nearby");
        params.put("radius","500");

        params.put(AllConstants.LatitudeBaidu, Double.toString(GPS.latitude));
        params.put(AllConstants.LongitudeBaidu,Double.toString(GPS.longitude));
//        params.put(AllConstants.LatitudeBaidu, "0");
//        params.put(AllConstants.LongitudeBaidu,"0");
        //api.wxbus.com.cn/api/?latitude_baidu=31.565559&longitude_baidu=120.363557&radius=500&m=line_nearby

        params = AES7PaddingUtil.toAES7Padding(params);

        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    //bug 重复数据，多次请求服务器后
                    aroundList.clear();
                    allList.clear();
                    if (jsonObject.getString("error").equals("1")) {//坐标正确有正确值返回

                        List<AroundRoute> aroundRouteList = JSON.parseArray(jsonObject.getString("result"), AroundRoute.class);

                        //end spacial
                        if (aroundRouteList != null && aroundRouteList.size() > 0) {

                            for (int i = 0; aroundRouteList != null && i < aroundRouteList.size(); i++) {
                                String lineTitle = aroundRouteList.get(i).getLine_title();
                                String endStop = aroundRouteList.get(i).getStop_list().get(0).getStop_end();
                                String lineCode = aroundRouteList.get(i).getStop_list().get(0).getLine_code();
                                String stopSeq = aroundRouteList.get(i).getStop_list().get(0).getStop_seq();
                                String stopName = aroundRouteList.get(i).getStop_list().get(0).getStop_name();
                                HomeFavAroundRoute route = new HomeFavAroundRoute(lineTitle, "", endStop, lineCode, stopSeq, AllConstants.AROUND_FLAG);//type = 1
                                route.setLineId(aroundRouteList.get(i).getLine_id());
                                route.setStartStop(aroundRouteList.get(i).getStop_list().get(0).getStop_start());
                                route.setDirection(aroundRouteList.get(i).getStop_list().get(0).getDirection());
                                route.setEndStop(aroundRouteList.get(i).getStop_list().get(0).getStop_end());
                                route.setStartEndTime(aroundRouteList.get(i).getStop_list().get(0).getTime_start_end());
                                route.setStopName(aroundRouteList.get(i).getStop_list().get(0).getStop_name());
                                int j = 0;
                                for (j = 0; j < favList.size(); j++) {
                                    if (lineTitle.equals(favList.get(j).getLineTitle())) {
                                        favList.get(j).setFavIsHere(true);
                                        favList.get(j).setLineCode(lineCode);
                                        favList.get(j).setStopSeq(stopSeq);
                                        favList.get(j).setStopName(stopName);
                                        //第i个路线以及被收藏了，去除重复数据
                                        // aroundRouteList.remove(i);
                                        break;
                                    }

                                }
                                //没有找到收藏元素
                                if (j == favList.size()) {
                                    aroundList.add(route);
                                }

                            }

                            //特殊处理，目前业务逻辑是，我的周围大于10条线路，就获取前十条
                            if(aroundList != null && aroundList.size() > 10){
                                for (int i = aroundList.size(); i > 10; i--) {
                                    aroundList.remove(i - 1);
                                }
                            }

                        }

                    }

                    Log.i("kee","around size = "+aroundList.size());

                    allList.addAll(favList);
                    allList.addAll(aroundList);


                    HomeLineAdapter adapter = new HomeLineAdapter(HomeFragment.this.getActivity(), allList,favList);
                    routeListView.setAdapter(adapter);
                    //网络请求更新数据
                    getDistanceStop(allList, adapter);

                    //不存在线路的情况下
                    showTipGeo();

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


    /**
     *  实时公交
     * @param list
     * @return
     */
    public void getDistanceStop(List<HomeFavAroundRoute> list,BaseAdapter adapter){
        //调用附件线路接口，查询还有多少站

        for (int i = 0; allList != null && i < allList.size(); i++) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("m","bus_live");
            map.put("line_code",list.get(i).getLineCode());
            map.put("stop_seq",list.get(i).getStopSeq());
            list.get(i).adapter = adapter;
            map = AES7PaddingUtil.toAES7Padding(map);

            //具体回调在模型HomeFavAroundRoute中
            //是收藏线路，并且不是附近的线路，则不用网络请求
            if(allList.get(i).getType() == AllConstants.FAV_FLAG && !allList.get(i).isFavIsHere())
                continue;

            VolleyManager.getJson(AllConstants.RealBusUrl, map, list.get(i), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

        }
    }

//15861681900
    //网络请求首页接口
    public void queryIndex() {
        //String url = "http://api.wxbus.com.cn/api/";
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","index");
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(AllConstants.HomeIndexUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Weather weather = JSON.parseObject(jsonObject.getString("weather"), Weather.class);
                    List<HomeAdvertise> homeAdvertiseList = JSON.parseArray(jsonObject.getString("index_ad"), HomeAdvertise.class);
                    List<HomeClass> homeClassList = JSON.parseArray(jsonObject.getString("index_class"), HomeClass.class);
                    List<NewsTj> newsTjList = JSON.parseArray(jsonObject.getString("news_tj"), NewsTj.class);
                    List<NewsDetail> newsDetailList = JSON.parseArray(jsonObject.getString("news_list"), NewsDetail.class);

                    //
                    initWeather(weather);
                    initHomeAdvertise(homeAdvertiseList);


                    initHomeClass(homeClassList);
                    initNewsTj(newsTjList);
                    initNewsList(newsDetailList);
                    if(isFlag){
                        startBannerPlay();
                        isFlag = false;
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


    public void initWeather(Weather weather) {
       // Typeface customFont = Typeface.createFromAsset(HomeFragment.this.getActivity().getAssets(), "weather.ttf");
        customFont = Typeface.createFromAsset(HomeFragment.this.getActivity().getAssets(), "weather.ttf");

        weather_textview.setText(StringUtil.decode(weather.getWeather_pic_ios()));
        weather_textview.setTextSize(18);
        weather_textview.setTypeface(customFont);
        temp_textview.setText(weather.getTemp());

        temp_textview.setTextSize(12);
        temp_textview.setTypeface(customFont);
        temp_char.setText("\uF03C");
        temp_char.setTextSize(12);

        temp_char.setTypeface(customFont);

    }

    //初始化广告位
    public void initHomeAdvertise(final List<HomeAdvertise> homeAdvertiseList) {
       List<ImageView> list = new ArrayList<ImageView>();
        adverPageControls.clear();

        int length = homeAdvertiseList.size();

        if(length == 2 || length == 3) {//解决向左滑动白屏现象
            length = length * 2;
        }
        for (int i = 0; i < length; i++) {
                //ImageView imageView = new ImageView(this.getActivity());
                // list.add(imageView);

                String imgUrl = homeAdvertiseList.get(i % homeAdvertiseList.size()).getIndex_pic();

                //涉及到图片的地方使用此类安装比例来布局图片
                SmartImageView imageView = new SmartImageView(this.getActivity());
                list.add(imageView);
                float ratio = 15.0f/8;
                imageView.setRatio(ratio);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(R.color.view_bg_color);

                //图片15：8
                imgUrl += "/"+AllConstants.Width+"x"+(int)(AllConstants.Width * 8 / 15.0f);
                Log.i("kee","imgUrl = "+imgUrl);
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
                final int tempI = i % homeAdvertiseList.size();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeFragment.this.getActivity(), WebViewActivity.class);
                        intent.putExtra("url",homeAdvertiseList.get(tempI).getUrl());
                        intent.putExtra("title","");
                        startActivity(intent);

                    }
                });

        }



        //初始化pageControlpageControls = new ArrayList<ImageView>();
        viewGroup.removeAllViews();

        for (int i = 0; i < homeAdvertiseList.size(); i++) {
            Log.i("kee","i="+i);
            ImageView imageView = new ImageView(this.getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));

            if (i == 0) {
                imageView.setImageResource(R.drawable.carousel_indicator_current);
            } else {
                imageView.setImageResource(R.drawable.carousel_indicator);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            viewGroup.addView(imageView, layoutParams);
            adverPageControls.add(imageView);

        }

        advertiseViewPager.setAdapter(new MyViewPagerAdapter(list));
        //设置监听，主要是设置点点的背景
        advertiseViewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        advertiseViewPager.setCurrentItem((homeAdvertiseList.size()) * 50);
    }

    //渲染分类UI,新闻分类
    public void initHomeClass(List<HomeClass> list) {

        HomeClassAdapter homeClassAdapter = new HomeClassAdapter(this.getActivity(), list);
        classGridView.setAdapter(homeClassAdapter);

    }

    //新闻图片banner,第二个banner
    public void initNewsTj(final List<NewsTj> newsTjList) {
        List<ImageView> list = new ArrayList<ImageView>();
        newsPageControlsList.clear();
        int length = newsTjList.size();
        if(length == 2 || length == 3) {//解决向左滑动白屏现象
            length = length * 2;
        }
        for (int i = 0; i < length; i++) {
            SmartImageView imageView = new SmartImageView(this.getActivity());
            list.add(imageView);
            imageView.setRatio(16.0f/9);
            String imgUrl = newsTjList.get(i % newsTjList.size()).getIndex_pic();
            //比例：16：9
            int height = (int)(AllConstants.Width * 9/16.0f);
            imgUrl += "/"+AllConstants.Width+"x"+height;
            //InitApplication.appLog.d("news-imgurl"+imgUrl);

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
           // VolleyManager.loadImage(imageView, imgUrl, R.drawable.advertise_test);
            final int tempI = i % newsTjList.size();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeFragment.this.getActivity(), WebViewActivity.class);
                    intent.putExtra("url",newsTjList.get(tempI).getUrl());
                    intent.putExtra("title","");//改标题服务端没有传递过来
                    startActivity(intent);

                }
            });

        }
        newsViewPager.setAdapter(new MyViewPagerAdapter(list));
        newsViewPager.setCurrentItem(list.size()*100);
        //pageControls
        if(newsTjList!= null && newsTjList.size()>0){//title 初始化为第一个
            String title = newsTjList.get(0).getTitle();
            newsTitleTextView.setText(title);
            newsTitleTextView.setTextColor(Color.WHITE);
        }


        newsPageControls.removeAllViews();

        for (int i = 0; i < newsTjList.size(); i++) {
            ImageView imageView = new ImageView(this.getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));

            if (i == 0) {
                imageView.setImageResource(R.drawable.carousel_indicator_current);
            } else {
                imageView.setImageResource(R.drawable.carousel_indicator);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            newsPageControls.addView(imageView, layoutParams);
            newsPageControlsList.add(imageView);
            newsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                }

                @Override
                public void onPageSelected(int position) {

                    for (int i = 0; newsPageControlsList != null && i < newsPageControlsList.size(); i++) {
                        if (i == position % newsPageControlsList.size()) {
                            newsPageControlsList.get(i).setImageResource(R.drawable.carousel_indicator_current);
                        } else {
                            newsPageControlsList.get(i).setImageResource(R.drawable.carousel_indicator);
                        }
                    }

                    //add title
                    if(newsPageControlsList != null && newsPageControlsList.size()>0){
                        int i = position % newsPageControlsList.size();
                        String title = newsTjList.get(i).getTitle();
                        newsTitleTextView.setText(title);

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }

    }

    //新闻列表
    public void initNewsList(List<NewsDetail> newsList) {

        HomeNewsAdapter homeNewsAdapter = new HomeNewsAdapter(this.getActivity(), newsList);
        newsListView.setAdapter(homeNewsAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(adverPageControls.size() > 0)
        setImageBackground(position % adverPageControls.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; adverPageControls != null && i < adverPageControls.size(); i++) {
            if (i == selectItems) {
                adverPageControls.get(i).setImageResource(R.drawable.carousel_indicator_current);
            } else {
                adverPageControls.get(i).setImageResource(R.drawable.carousel_indicator);
            }
        }
    }



    @Override
    public void onClick(View view) {
        if(view == searchTextView){
            Intent intent = new Intent(this.getActivity(), SearchAllActivity.class);
            this.getActivity().startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == routeListView){
            if(favList.size() == 0 && i == allList.size()){//最后一个提示图片，点击nullpointer
                return;
            }
            HomeLineAdapter.ViewHolder viewHolder = (HomeLineAdapter.ViewHolder)view.getTag();
            HomeFavAroundRoute route = viewHolder.route;

            //add by kee，是收藏线路时，先跳转到单程列表
            if(route.getType() == AllConstants.FAV_FLAG){
                Intent intent = new Intent(this.getActivity(),SearchLineResultActivity.class);
                intent.putExtra("lineName",route.getLineTitle());
                startActivity(intent);
                return;
            }


            Intent intent = new Intent(this.getActivity(), LineRealActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("line_id",route.getLineId());
            //bundle.putString("stopName",);
            bundle.putString("direction",route.getDirection()+"");
            bundle.putString("line_name",route.getLineTitle());
            bundle.putString("line_code",route.getLineCode());
            bundle.putString("stop_seq",route.getStopSeq());
            bundle.putString("stop_start",route.getStartStop());
            bundle.putString("stop_end",route.getEndStop());
            bundle.putString("time_start_end", route.getStartEndTime());
            bundle.putString("stopName",route.getStopName());
            intent.putExtras(bundle);
            startActivity(intent);
        }else if(adapterView == classGridView){
            HomeClassAdapter.ViewHolder viewHolder = (HomeClassAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(HomeFragment.this.getActivity(), WebViewActivity.class);
            intent.putExtra("url",viewHolder.url);
            intent.putExtra("title",viewHolder.titleTextView.getText().toString());
            startActivity(intent);
        }else if(adapterView == newsListView){
            HomeNewsAdapter.ViewHolder viewHolder = (HomeNewsAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(HomeFragment.this.getActivity(), WebViewActivity.class);
            intent.putExtra("url",viewHolder.url);
            intent.putExtra("title",viewHolder.classTextView.getText().toString());
            startActivity(intent);

        }
    }

    /**
     * 解决事件冲突，只有在顶部
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(scrollView == view){
            if(view.getScrollY() < 1){//判读是否在顶部scrollview
                swipeRefreshLayout.setEnabled(true);

            }else{
                swipeRefreshLayout.setEnabled(false);//下拉刷新无效

            }


            int alpha = measureAlpha(view.getScrollY(),(int)view.getTranslationY());
            toolbar.setBackgroundColor(Color.argb(alpha,220,69,69));
        }

        return false;
    }

    /**
     * 动态计算透明度
     * @param scrollY
     * @return
     */
    public int measureAlpha(int scrollY,int translateY){
        int alpha = 0;
        if(scrollY <= 160 && translateY <=160){
            alpha = (int)(scrollY / 180.0f * 255);
            return alpha;
        }
        return (int)((160)/180.0f * 255);
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(y <= 0){
            toolbar.setBackgroundColor(Color.argb(0,220,69,69));
        }
    }

    //优化左右滑动事件，与下拉刷新事件相冲突问题
    @Override
    public void onScrollStateChanged(ScrollState scrollState) {

        if(scrollState == ScrollState.SCROLL_STATE_TOUCH_SCROLL || scrollState == ScrollState.SCROLL_STATE_FLING){
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.setRefreshing(false);//当滚动线路组件时，屏蔽下拉刷新
        }else if(scrollState == ScrollState.SCROLL_STATE_IDLE){
            //swipeRefreshLayout.setEnabled(true);
        }

    }
}

