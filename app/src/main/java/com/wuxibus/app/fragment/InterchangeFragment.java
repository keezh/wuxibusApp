package com.wuxibus.app.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.InterchangeLocationActivity;
import com.wuxibus.app.activity.InterchangeLocationResetActivity;
import com.wuxibus.app.activity.InterchangeResultActivity;
import com.wuxibus.app.activity.MainActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.HomeCompanyLocation;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.util.StorageUtil;
import com.wuxibus.app.util.Tools;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/6/16.
 */
public class InterchangeFragment extends Fragment implements View.OnClickListener{

    /**
     * 起始位置
     */
    private String orign;

    private String destination;

    private TextView orignTextView;
    private TextView destinationTextView;
    private ImageView reverseImageView;
    private ImageView submitImageView;

    TextView locationTextView;
    ImageView locationImageView;

    ImageView homeImageView;
    TextView homeTextView;

    ImageView companyImageView;
    TextView  companyTextView;

    List<AdvItem> list;
    ViewPager advertiseViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interchange,null);
        orignTextView = (TextView) view.findViewById(R.id.orign_tv);
        destinationTextView = (TextView) view.findViewById(R.id.destination_tv);
        reverseImageView = (ImageView) view.findViewById(R.id.reverse_iv);
        submitImageView = (ImageView) view.findViewById(R.id.submit_iv);
        locationImageView = (ImageView) view.findViewById(R.id.location_reset_iv);
        locationTextView = (TextView) view.findViewById(R.id.location_reset_tv);
        homeImageView = (ImageView) view.findViewById(R.id.home_iv);
        homeTextView = (TextView) view.findViewById(R.id.home_tv);
        companyImageView = (ImageView) view.findViewById(R.id.company_iv);
        companyTextView = (TextView) view.findViewById(R.id.company_tv);
        advertiseViewPager = (ViewPager) view.findViewById(R.id.adv_viewpage);

        homeImageView.setOnClickListener(this);
        homeTextView.setOnClickListener(this);
        companyImageView.setOnClickListener(this);
        companyTextView.setOnClickListener(this);


        orignTextView.setOnClickListener(this);
        destinationTextView.setOnClickListener(this);
        reverseImageView.setOnClickListener(this);
        submitImageView.setOnClickListener(this);
        locationImageView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);

        //查询广告
        queryAdvList();

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        //重新获取GPS坐标，当前位置坐标为GPS class中
        ((MainActivity)InterchangeFragment.this.getActivity()).updateBaiduGps();

        if(InterchangeSearch.sourceInfo != null){
            orignTextView.setText(InterchangeSearch.sourceInfo.name);

        }

        if(InterchangeSearch.destinationInfo != null){
            destinationTextView.setText(InterchangeSearch.destinationInfo.name);
        }

        StorageUtil.readBindDevice(this.getActivity());


    }




    @Override
    public void onClick(View v) {
        if(v == orignTextView){
            Intent intent = new Intent(this.getActivity(), InterchangeLocationActivity.class);
            intent.putExtra("location","origin");//是否是出发地
            startActivity(intent);

        }else if(v == destinationTextView){
            Intent intent = new Intent(this.getActivity(), InterchangeLocationActivity.class);
            intent.putExtra("location","destination");//是否是目的地
            startActivity(intent);

        } else if(v == reverseImageView){

        }else if(v == submitImageView){
            String orignTemp = orignTextView.getText().toString().trim();
            String destinationTemp = destinationTextView.getText().toString().trim();
            if(orignTemp == null || orignTemp.equals("")){//没有输入则获取当前位置
                orign = GPS.latitude+","+GPS.longitude;
                orignTemp = orign;

            }
            if(destinationTemp == null || destinationTemp.trim().equals("")){
                Tools.showToast(this.getActivity(),"目的地不能为空", Toast.LENGTH_SHORT);
            }

            //Bundle bundle = new Bundle();
            Intent intent = new Intent(this.getActivity(), InterchangeResultActivity.class);
            intent.putExtra("orign", orignTemp);
            intent.putExtra("destination",destinationTemp);
            startActivity(intent);


        }else if(v == locationImageView || v == locationTextView){
            Intent intent = new Intent(this.getActivity(), InterchangeLocationResetActivity.class);
            startActivity(intent);

        }else if(v == homeImageView || v == homeTextView){
            if(InterchangeSearch.homePoiInfo == null){
                Intent intent = new Intent(this.getActivity(), InterchangeLocationResetActivity.class);
                startActivity(intent);
            }else{//是否跳转换乘查询页面

            }

        }else if(v == companyImageView || v == companyTextView){
            if(InterchangeSearch.companyInfo == null){
                Intent intent = new Intent(this.getActivity(), InterchangeLocationResetActivity.class);
                startActivity(intent);
            }else{//

            }

        }
    }

    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","transfer_search");

        //服务器端程序返回接口不统一
        paras = AES7PaddingUtil.toAES7Padding(paras);
        VolleyManager.getJsonArray(url, paras, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                list = JSON.parseArray(jsonArray.toString(), AdvItem.class);
                List<ImageView> imageList = new ArrayList<ImageView>();
                int length = 0;
                if (list != null) {
                    length = list.size();
                    if (length == 2 || length == 3) {
                        length = length * 2;
                    }
                }
                for (int i = 0; list != null && i < length; i++) {
                    SmartImageView imageView = new SmartImageView(InterchangeFragment.this.getActivity());
                    imageList.add(imageView);
                    //imageView.setRatio(720.0f/200);
                    String imgUrl = list.get(i % list.size()).getIndex_pic();
                    //比例：16：9
                    String width = AllConstants.Width + "";
                    int height = DensityUtil.dip2px(InterchangeFragment.this.getActivity(), 110);
                    //xml布局高度设定了
                    imgUrl += "/" + width + "x" + height;

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
                            Intent intent = new Intent(InterchangeFragment.this.getActivity(), WebViewActivity.class);
                            intent.putExtra("url", list.get(tempI).getUrl());
                            intent.putExtra("title", list.get(tempI).getTitle());
                            startActivity(intent);

                        }
                    });

                }
                if (imageList.size() > 0) {
                    advertiseViewPager.setVisibility(View.VISIBLE);
                    advertiseViewPager.setAdapter(new MyViewPagerAdapter(imageList));
                    advertiseViewPager.setCurrentItem(list.size() * 100);
                } else {
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
