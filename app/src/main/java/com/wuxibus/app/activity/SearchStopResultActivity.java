package com.wuxibus.app.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.AroundShopAdapter;
import com.wuxibus.app.adapter.LineNameAdapter;
import com.wuxibus.app.adapter.MyViewPagerAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.customerView.SmartImageView;
import com.wuxibus.app.entity.AdvItem;
import com.wuxibus.app.entity.Shop;
import com.wuxibus.app.entity.StopInfoMap;
import com.wuxibus.app.entity.StopMapModel;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.volley.BitmapCache;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/10.
 */
public class SearchStopResultActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener {


    GridView stop_linews_gridview;
    ListView shopListView;

    ImageView backImageView;
    List<AdvItem> list;

    public ViewPager adViewPager;
    TextView mapTextView;
    String stopName;
    TextView aroundShopTitle;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_detail);
        stop_linews_gridview = (GridView) findViewById(R.id.stop_lines_gridview);
        shopListView = (ListView) findViewById(R.id.shop_listview);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        adViewPager = (ViewPager) findViewById(R.id.ad_viewpage);
        mapTextView = (TextView) findViewById(R.id.map_textview);
        TextView titleTextView = (TextView) findViewById(R.id.title_textview);
        aroundShopTitle = (TextView) findViewById(R.id.around_shop_title);
        stopName = this.getIntent().getExtras().getString("stopName");
        queryStopNearby(stopName);
        titleTextView.setText(stopName);

        backImageView.setOnClickListener(this);
        shopListView.setOnItemClickListener(this);
        mapTextView.setOnClickListener(this);
        queryAdvList();
    }

    public void queryAdvList(){
        //["m":"get_ad_list","flag":flag,"k":k]
        String url = AllConstants.ServerUrl;
        Map<String,String> paras = new HashMap<String,String>();
        paras.put("m","get_ad_list");
        paras.put("flag","stop_list");
        paras.put("k","");
        VolleyManager.getJsonArray(url, paras, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray jsonArray) {
                list = JSON.parseArray(jsonArray.toString(), AdvItem.class);
                List<ImageView> imageList = new ArrayList<ImageView>();
                for (int i = 0; list != null && i < list.size(); i++) {
                    SmartImageView imageView = new SmartImageView(SearchStopResultActivity.this);
                    imageList.add(imageView);
                    imageView.setRatio(720.0f / 200);
                    String imgUrl = list.get(i).getIndex_pic();
                    //比例：16：9
                    int height = (int)(AllConstants.Width * 200/720.0f);
                    imgUrl += "/"+AllConstants.Width+"x"+height;
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
                    final int tempI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchStopResultActivity.this, WebViewActivity.class);
                            intent.putExtra("url", list.get(tempI).getUrl());
                            intent.putExtra("title", list.get(tempI).getTitle());
                            startActivity(intent);

                        }
                    });

                }
                adViewPager.setAdapter(new MyViewPagerAdapter(imageList));
                adViewPager.setCurrentItem(list.size() * 100);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }


    public void queryStopNearby(final String stopName){
        Map<String,String> params = new HashMap<String,String>();
        params.put("k",stopName);
        params.put("m","stop_info");
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(jsonObject.getString("error").equals("1")){

                        String line_info = jsonObject.getJSONObject("result").getString("line_info");
                        String mapListStr = jsonObject.getJSONObject("result").getString("map_list");
                        List<StopInfoMap> list = JSON.parseArray(mapListStr,StopInfoMap.class);
                        StopMapModel.getInstance().setList(list);

                        List<String> lineList = JSON.parseArray(line_info,String.class);
                        LineNameAdapter lineNameAdapter = new LineNameAdapter(SearchStopResultActivity.this,lineList,stopName);
                        stop_linews_gridview.setAdapter(lineNameAdapter);

                        String shop_list = jsonObject.getJSONObject("result").getString("sj_list");
                        List<Shop> shopList = JSON.parseArray(shop_list,Shop.class);
                        AroundShopAdapter aroundShopAdapter = new AroundShopAdapter(SearchStopResultActivity.this,shopList);
                        shopListView.setAdapter(aroundShopAdapter);
                        if(shopList.size() == 0){
                            aroundShopTitle.setVisibility(View.GONE);
                        }else {
                            aroundShopTitle.setVisibility(View.VISIBLE);
                        }


                    }
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


    @Override
    public void onClick(View view) {
        if(view == backImageView){
            this.finish();
        }
        if(view == mapTextView){
            Intent intent = new Intent(this,StopMapActivity.class);
            intent.putExtra("stopName",stopName);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == shopListView){
            AroundShopAdapter.ViewHolder viewHolder = (AroundShopAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url",viewHolder.url);
            intent.putExtra("title",viewHolder.titleTextView.getText().toString());
            startActivity(intent);
        }
    }
}
