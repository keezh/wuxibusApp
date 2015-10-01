package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVAnalytics;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.SearchAllAdapter;
import com.wuxibus.app.adapter.SearchLineAdapter;
import com.wuxibus.app.adapter.SearchPlaceAdapter;
import com.wuxibus.app.adapter.StringAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.entity.SearchPlace;
import com.wuxibus.app.util.AES;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DBUtil;
import com.wuxibus.app.util.StringUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/24.
 */
public class SearchAllActivity extends Activity implements SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener,View.OnClickListener {

    View historyContainer;
    View searchContainer;
    SearchView searchView;
    ListView lineListView;
    ListView stopListView;
    ListView placeListView;

    View lineTextView;
    View stopTextView;
    View placeTextView;
    ImageView backImageView;
    ListView historyListView;
    TextView clearHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        historyContainer = findViewById(R.id.history_container);
        searchContainer = findViewById(R.id.search_scrollview);
        historyListView = (ListView) findViewById(R.id.search_history);
        searchView = (SearchView)findViewById(R.id.search_view);
        lineListView = (ListView) findViewById(R.id.line_listview);
        stopListView = (ListView) findViewById(R.id.station_listview);
        placeListView = (ListView) findViewById(R.id.place_listview);

        lineTextView = findViewById(R.id.line_textview);
        stopTextView = findViewById(R.id.stop_textview);
        placeTextView = findViewById(R.id.place_textview);
        clearHistory = (TextView) findViewById(R.id.clear_history_textview);


        searchView.setOnQueryTextListener(this);
        lineListView.setOnItemClickListener(this);
        stopListView.setOnItemClickListener(this);
        placeListView.setOnItemClickListener(this);
        historyListView.setOnItemClickListener(this);

        backImageView.setOnClickListener(this);
        clearHistory.setOnClickListener(this);

        initSearchView();

    }

    public void initSearchView(){
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBUtil dbUtil = new DBUtil(this);
        List<SearchHistory>lineList = dbUtil.querySearchHistory(AllConstants.SEARCH_LINE_TYPE);
        List<SearchHistory>stationList = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<SearchHistory> placeList = dbUtil.querySearchHistory(AllConstants.SEARCH_PLACE_TYPE);

        List<SearchHistory> myHistory = new ArrayList<SearchHistory>();
        myHistory.addAll(lineList);
        myHistory.addAll(stationList);
        myHistory.addAll(placeList);

        SearchAllAdapter adapter = new SearchAllAdapter(this,myHistory);
        historyListView.setAdapter(adapter);

        AVAnalytics.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s != null && !s.equals("")){
            searchContainer.setVisibility(View.VISIBLE);
            historyContainer.setVisibility(View.GONE);
        }else{
            searchContainer.setVisibility(View.GONE);
            historyContainer.setVisibility(View.VISIBLE);
        }

        Map<String,String> params = new HashMap<String,String>();

        Map<String,String> tempParams = new HashMap<String,String>();
        tempParams.put("m","all_search");
        tempParams.put("k",s);
        tempParams = AES7PaddingUtil.toAES7Padding(tempParams);
        System.out.println(tempParams.get("b"));

        VolleyManager.getJson(AllConstants.ServerUrl, tempParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    List<String> lines = JSON.parseArray(jsonObject.getString("line"), String.class);
                    List<String> stops = JSON.parseArray(jsonObject.getString("stop"),String.class);
                    List<SearchPlace> places = JSON.parseArray(jsonObject.getString("place"),SearchPlace.class);

                    if(lines != null && lines.size() >0){
                        StringAdapter adapter = new StringAdapter(SearchAllActivity.this,lines);
                        lineListView.setAdapter(adapter);
                    }else {
                        lineTextView.setVisibility(View.GONE);
                        lineListView.setVisibility(View.GONE);
                    }

                    if(stops != null && stops.size() > 0){
                        StringAdapter adapter = new StringAdapter(SearchAllActivity.this,stops);
                        stopListView.setAdapter(adapter);
                    }else{
                        stopTextView.setVisibility(View.GONE);
                        stopListView.setVisibility(View.GONE);
                    }

                    if(places != null && places.size()>0){
                        SearchPlaceAdapter adapter = new SearchPlaceAdapter(SearchAllActivity.this,places);
                        placeListView.setAdapter(adapter);
                    }else{
                        placeListView.setVisibility(View.GONE);
                        placeTextView.setVisibility(View.GONE);
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


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == lineListView){
            String lineName = ((StringAdapter.ViewHolder) view.getTag()).resultText.getText().toString();
            Intent intent = new Intent(this,SearchLineResultActivity.class);
            intent.putExtra("lineName",lineName);
            startActivity(intent);

        }else if(adapterView == stopListView){
            String stopName = ((StringAdapter.ViewHolder) view.getTag()).resultText.getText().toString();
            //保存都本地数据库中
            DBUtil dbUtil = new DBUtil(this);
            dbUtil.saveDB(stopName,"","","",AllConstants.SEARCH_STATIION_TYPE);

            Intent intent = new Intent(this,SearchStopResultActivity.class);
            intent.putExtra("stopName",stopName);
            startActivity(intent);

        }else if(adapterView == placeListView){

            SearchPlaceAdapter.ViewHolder viewHolder = (SearchPlaceAdapter.ViewHolder)view.getTag();
            String stopName = viewHolder.titleTextView.getText().toString();
            //String longitude = viewHolder.lng;
            //String latitude = viewHolder.lat;
            //保存到sqlite数据记录
            DBUtil dbUtil = new DBUtil(this);
            //经纬度保存在数据表的start_stop,end_stop字段
            dbUtil.saveDB(stopName,"",viewHolder.lng,viewHolder.lat,AllConstants.SEARCH_PLACE_TYPE);

            Intent intent = new Intent(this,SearchPlaceActivity.class);
            intent.putExtra("stopName",stopName);
            intent.putExtra("longitude",viewHolder.lng);
            intent.putExtra("latitude",viewHolder.lat);
            startActivity(intent);


        }else if(adapterView == historyListView){
            SearchAllAdapter.ViewHolder viewHolder = (SearchAllAdapter.ViewHolder)view.getTag();
            String name = viewHolder.titleTextView.getText().toString();
            int type = viewHolder.type;
            if(type == AllConstants.SEARCH_LINE_TYPE){
                Intent intent = new Intent(this,SearchLineResultActivity.class);
                intent.putExtra("lineName",name);
                startActivity(intent);
            }else if(type == AllConstants.SEARCH_STATIION_TYPE){
                Intent intent = new Intent(this,SearchStopResultActivity.class);
                intent.putExtra("stopName",name);
                startActivity(intent);
            }else if(type == AllConstants.SEARCH_PLACE_TYPE){
                Intent intent = new Intent(this,SearchPlaceActivity.class);
                intent.putExtra("stopName",name);
                intent.putExtra("longitude",viewHolder.longitude);
                intent.putExtra("latitude",viewHolder.latitude);
                startActivity(intent);

            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backImageView){
            this.finish();
        }else if(view == clearHistory){
            DBUtil dbUtil = new DBUtil(this);
            dbUtil.clearSearchHistory(AllConstants.SEARCH_STATIION_TYPE);
            dbUtil.clearSearchHistory(AllConstants.SEARCH_LINE_TYPE);
            dbUtil.clearSearchHistory(AllConstants.SEARCH_PLACE_TYPE);
            //设置一个空的数组，自动刷新界面
            List<SearchHistory> myHistory = new ArrayList<SearchHistory>();
            SearchAllAdapter adapter = new SearchAllAdapter(this,myHistory);
            historyListView.setAdapter(adapter);

//            searchContainer.setVisibility(View.VISIBLE);
//            historyContainer.setVisibility(View.GONE);


        }
    }
}
