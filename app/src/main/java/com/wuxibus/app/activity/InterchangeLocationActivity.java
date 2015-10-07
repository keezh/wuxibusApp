package com.wuxibus.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeSearchAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/10/6.
 */
public class InterchangeLocationActivity extends Activity implements AdapterView.OnItemClickListener,
        View.OnClickListener,TextWatcher{
    public EditText locationEditText;

    PoiSearch poiSearch = PoiSearch.newInstance();
    List<PoiInfo> searchResultList = new ArrayList<PoiInfo>();
    ListView historyListView;
    ListView aroudnListView;
    ListView storeListView;
    MapView mapView;
    RadioButton historyBtn;
    RadioButton aroundBtn;
    RadioButton storeBtn;
    RadioButton mapviewBtn;

    ListView searchResultListView;
    View noSearchContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_location);
        locationEditText = (EditText) findViewById(R.id.location_et);
        historyListView = (ListView) findViewById(R.id.interchange_history_lv);
        aroudnListView = (ListView) findViewById(R.id.interchange_around_lv);
        storeListView = (ListView) findViewById(R.id.interchange_storage_lv);
        mapView = (MapView) findViewById(R.id.location_mapview);
        historyBtn = (RadioButton) findViewById(R.id.history_btn);
        aroundBtn = (RadioButton) findViewById(R.id.around_btn);
        storeBtn = (RadioButton) findViewById(R.id.store_btn);
        mapviewBtn = (RadioButton) findViewById(R.id.mapview_btn);

        searchResultListView = (ListView) findViewById(R.id.search_result_listview);
        noSearchContainer = findViewById(R.id.no_search_container);

        //locationEditText.setOnFocusChangeListener(this);
        locationEditText.addTextChangedListener(this);
        historyBtn.setOnClickListener(this);

        String locaiton = this.getIntent().getStringExtra("location");
        if(locaiton != null && locaiton.equals("origin")){
            locationEditText.setHint("请输入出发地");
        }else if(locaiton !=null && locaiton.equals("destination")){
            locationEditText.setHint("请输入目的地");
        }

        //poiSearch();

    }

    public void poiSearch(String keyword){

        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(AllConstants.City);// 城市：无锡
        citySearchOption.keyword(keyword);// 关键字
        citySearchOption.pageCapacity(30);// 默认每页10条
        //citySearchOption.pageNum(page);// 分页编号
        // 发起检索请求
        poiSearch.searchInCity(citySearchOption);
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                searchResultList.clear();//清空之前数据

                for (PoiInfo poi : poiResult.getAllPoi()) {
                    InitApplication.appLog.i(poi.name);

                    searchResultList.add(poi);

                }

                //更新listview视图
                InterchangeSearchAdapter adapter = new InterchangeSearchAdapter(searchResultList,
                        InterchangeLocationActivity.this);
                searchResultListView.setAdapter(adapter);



            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        if(v == historyBtn){
            historyListView.setVisibility(View.VISIBLE);
            aroudnListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);

        }else if(v == aroundBtn){
            historyListView.setVisibility(View.GONE);
            aroudnListView.setVisibility(View.VISIBLE);
            storeListView.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);

        }else if(v == storeBtn){
            historyListView.setVisibility(View.GONE);
            aroudnListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);

        }else if(v == mapviewBtn){
            historyListView.setVisibility(View.GONE);
            aroudnListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
       // if(v == locationEditText){
            String keyword = locationEditText.getText().toString().trim();
            if(keyword.equals("")){
                noSearchContainer.setVisibility(View.VISIBLE);
                searchResultListView.setVisibility(View.GONE);
            }else {
                noSearchContainer.setVisibility(View.GONE);
                searchResultListView.setVisibility(View.VISIBLE);
                poiSearch(keyword);
            }


       // }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == searchResultListView){
            InterchangeSearchAdapter.ViewHolder viewHolder = (InterchangeSearchAdapter.ViewHolder) view.getTag();

            PoiInfo poiInfo = viewHolder.poiInfo;
            DBUtil dbUtil = new DBUtil(this);
            dbUtil.insertInterchangeSearch(poiInfo.name,poiInfo.location.latitude+"",poiInfo.location.longitude+"");


        }
    }
}
