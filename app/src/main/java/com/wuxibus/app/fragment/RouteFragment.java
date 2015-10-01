package com.wuxibus.app.fragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.adapter.LineViewPagerAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.PagerSlidingTabStrip;
import com.wuxibus.app.activity.SearchLineActivity;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.util.AES7PaddingUtil;
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
import github.chenupt.dragtoplayout.DragTopLayout;

/**
 * Created by zhongkee on 15/6/16.
 */
public class RouteFragment extends Fragment {

    private String[] titles = new String[]{"收藏线路", "附近线路", "查询历史"};
    private PagerSlidingTabStrip slidingTabLayout;
    private ViewPager viewPager;
    private TextView search_button;
    ViewPager advertiseViewPager;
    List<AdvItem> list;
    private DragTopLayout dragLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route,null);
        initViews(view);
        initAdapte();
        setupListener();

        queryAdvList();
        return view;
    }

    public void initViews(View view){
        slidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager)view.findViewById(R.id.vp_main_view);
        search_button = (TextView) view.findViewById(R.id.search_button);
        advertiseViewPager = (ViewPager) view.findViewById(R.id.advertise_viewpage);
        dragLayout = (DragTopLayout) view.findViewById(R.id.drag_layout);
        dragLayout.setOverDrag(false);
        dragLayout.openTopView(true);

    }

    // Handle scroll event from fragments
    public void onEvent(Boolean b){
        dragLayout.setTouchMode(b);
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        AVAnalytics.onFragmentStart("route-fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        AVAnalytics.onFragmentEnd("route-fragment");
    }

    /**
     * 初始化模型数据
     */
    public void initAdapte(){
        LineViewPagerAdapter pagerAdapter = new LineViewPagerAdapter(this.getFragmentManager(),null,titles);


        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
//
        slidingTabLayout.setViewPager(viewPager);


        slidingTabLayout.notifyDataSetChanged();
    }

    public void setupListener(){
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteFragment.this.getActivity(), SearchLineActivity.class);
                RouteFragment.this.startActivity(intent);

            }
        });
    }

    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","line_search");
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
                    SmartImageView imageView = new SmartImageView(RouteFragment.this.getActivity());
                    imageList.add(imageView);
                    //imageView.setRatio(720.0f/200);
                    String imgUrl = list.get(i%list.size()).getIndex_pic();
                    //比例：16：9
                    String width = AllConstants.Width+"";
                    int height = DensityUtil.dip2px(RouteFragment.this.getActivity(),110);
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
                            Intent intent = new Intent(RouteFragment.this.getActivity(), WebViewActivity.class);
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
}
