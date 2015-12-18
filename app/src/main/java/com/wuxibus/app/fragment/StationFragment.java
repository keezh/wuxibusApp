package com.wuxibus.app.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchStopActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.adapter.AroundStopAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.adapter.StationViewPageAdapter;
import com.wuxibus.app.adapter.StringStationAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.PagerSlidingTabStrip;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.entity.StopNearby;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DBUtil;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;
import github.chenupt.dragtoplayout.DragTopLayout;

/**
 * Created by zhongkee on 15/6/16.
 * ViewPager pager = (ViewPager) findViewById(R.id.pager);
 pager.setAdapter(new TestAdapter(getSupportFragmentManager()));

 //向ViewPager绑定PagerSlidingTabStrip
 PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
 tabs.setViewPager(pager);
 *
 */
public class StationFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{

    private String[] titles = new String[]{"附近站台", "查询历史"};
    private ViewPager advertiseViewPager;
    PagerSlidingTabStrip slidingTabLayout;
//    SmartTabLayout smartTabLayout;

    TextView search_button;
    ListView around_stops_listview;
    ListView history_listview;
//    RadioButton aroundBtn;
//    RadioButton historyBtn;
    private DragTopLayout dragLayout;
    List<AdvItem> list;
//    ScrollSwipeRefreshLayout scrollSwipeRefreshLayout;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_fragment_tab,null);
        search_button = (TextView) view.findViewById(R.id.search_button);
//        aroundBtn = (RadioButton) view.findViewById(R.id.around_btn);
//        historyBtn = (RadioButton) view.findViewById(R.id.history_btn);
        advertiseViewPager = (ViewPager) view.findViewById(R.id.adv_viewpage);

//        around_stops_listview = (ListView) view.findViewById(R.id.around_stops_listview);
//        history_listview = (ListView) view.findViewById(R.id.history_stops_listview);
//        history_listview.setOnItemClickListener(this);
//        around_stops_listview.setOnItemClickListener(this);
        search_button.setOnClickListener(this);
//        aroundBtn.setOnClickListener(this);
//        historyBtn.setOnClickListener(this);
        dragLayout = (DragTopLayout) view.findViewById(R.id.drag_layout);
        dragLayout.setOverDrag(false);
        dragLayout.openTopView(true);

//        scrollSwipeRefreshLayout = (ScrollSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);

        //tab
        viewPager = (ViewPager)view.findViewById(R.id.vp_main_view);
        slidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.sliding_tabs);
//        smartTabLayout = (SmartTabLayout) view.findViewById(R.id.viewpagertab);


//        scrollSwipeRefreshLayout.setViewGroup(viewGroup);
//        scrollSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ((MainActivity)StationFragment.this.getActivity()).updateBaiduGps();
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollSwipeRefreshLayout.setRefreshing(false);
//                        queryAroundStops();
//                    }
//                }, 500);
//            }
//        });
//        scrollSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
     //           android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

      //  handleDownEvent();

        //广告接口
        queryAdvList();
        initAdapter();


        return view;
    }

    /**
     * 解决下拉事件冲突问题，
     */
    public void handleDownEvent(){
        around_stops_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (around_stops_listview.getLastVisiblePosition() == (around_stops_listview.getCount() - 1)) {
                        }
                        // 判断滚动到顶部

//                        if(around_stops_listview.getFirstVisiblePosition() == 0){
//                            scrollSwipeRefreshLayout.setEnabled(true);
//                        }else{
//                            scrollSwipeRefreshLayout.setEnabled(false);
//                        }

                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));

            }
        });

        history_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));
            }
        });

    }

    // Handle scroll event from fragments
    public void onEvent(Boolean b){
        dragLayout.setTouchMode(b);
    }

    public void initAdapter(){
        viewPager.setAdapter(new StationViewPageAdapter(this.getFragmentManager(), titles));
        viewPager.setCurrentItem(0);

//        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
//                this.getChildFragmentManager(), FragmentPagerItems.with(this.getActivity())
//                .add(titles[0], StationAroundFragment.class)
//                .add(titles[1], StationHistoryFragment.class)
//                .create());

        //ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        viewPager.setAdapter(adapter);

//        smartTabLayout.setViewPager(viewPager);

        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        AVAnalytics.onFragmentStart("station-frgment-start");
//        queryAroundStops();




    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        AVAnalytics.onFragmentEnd("station-fragment-end");
    }

    @Override
    public void onClick(View view) {
        if(view == search_button){
            Intent intent = new Intent(this.getActivity(), SearchStopActivity.class);
            startActivity(intent);
        }

//        if(view == aroundBtn){
//            around_stops_listview.setVisibility(View.VISIBLE);
//            history_listview.setVisibility(View.GONE);
//
//        }else if(view == historyBtn){
//            around_stops_listview.setVisibility(View.GONE);
//            history_listview.setVisibility(View.VISIBLE);
//        }
    }

    private void showHistory() {
        DBUtil dbUtil = new DBUtil(this.getActivity());
        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<String> stationList = new ArrayList<String>();
        for (int i = 0; list != null && i < list.size(); i++) {
            stationList.add(list.get(i).getLineName());
        }

        StringStationAdapter adapter = new StringStationAdapter(this.getActivity(),stationList);
//        history_listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 周边站台
     */
    public void queryAroundStops(){
        Map<String,String>params = new HashMap<String,String>();
        params.put("m","stop_nearby");
        params.put("radius","500");
        params.put(AllConstants.LongitudeBaidu, GPS.longitude+"");
        params.put(AllConstants.LatitudeBaidu,GPS.latitude+"");

        params = AES7PaddingUtil.toAES7Padding(params);

        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(jsonObject.getString("error").equals("1")){
                        List<StopNearby> list = JSON.parseArray(jsonObject.getString("result"),StopNearby.class);
                        AroundStopAdapter aroundStopAdapter = new AroundStopAdapter(StationFragment.this.getActivity(),list);
                        around_stops_listview.setAdapter(aroundStopAdapter);
                    }

                    //test
                    //
                    // showHistory();

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

    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","stop_search");
        //paras.put("flag","stop_list");
        paras.put("k","");
        //服务器端程序返回接口不统一
        paras = AES7PaddingUtil.toAES7Padding(paras);
        VolleyManager.getJsonArray(url, paras, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                list = JSON.parseArray(jsonArray.toString(), AdvItem.class);
                List<ImageView> imageList = new ArrayList<ImageView>();
                int length = 0;
                if(list != null){
                    length = list.size();
                    if(length == 2 || length == 3){
                        length = length * 2;
                    }
                }
                for (int i = 0; list != null && i < length; i++) {
                    SmartImageView imageView = new SmartImageView(StationFragment.this.getActivity());
                    imageList.add(imageView);
                    //imageView.setRatio(720.0f/200);
                    String imgUrl = list.get(i % list.size()).getIndex_pic();
                    //比例：16：9
                    String width = AllConstants.Width+"";
                    int height = DensityUtil.dip2px(StationFragment.this.getActivity(), 110);
                    //xml布局高度设定了
                    imgUrl += "/"+width+"x"+height;

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
                    final int tempI = i % list.size();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StationFragment.this.getActivity(), WebViewActivity.class);
                            intent.putExtra("url",list.get(tempI).getUrl());
                            intent.putExtra("title",list.get(tempI).getTitle());
                            startActivity(intent);

                        }
                    });

                }
                if(imageList.size() > 0){
                    advertiseViewPager.setVisibility(View.VISIBLE);
                    advertiseViewPager.setAdapter(new MyViewPagerAdapter(imageList));
                    advertiseViewPager.setCurrentItem(list.size()*100);
                }else{
                    advertiseViewPager.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(adapterView == around_stops_listview){
//            AroundStopAdapter.ViewHolder viewHolder = (AroundStopAdapter.ViewHolder) view.getTag();
//            Intent intent = new Intent(this.getActivity(), SearchStopResultActivity.class);
//            intent.putExtra("stopName",viewHolder.stopNameTextView.getText().toString());
//            startActivity(intent);
//        }else if(adapterView == history_listview){
//
//            StringStationAdapter.ViewHolder viewHolder = (StringStationAdapter.ViewHolder) view.getTag();
//            Intent intent = new Intent(this.getActivity(), SearchStopResultActivity.class);
//            intent.putExtra("stopName",viewHolder.resultText.getText().toString());
//            startActivity(intent);
//
//        }
    }
}
