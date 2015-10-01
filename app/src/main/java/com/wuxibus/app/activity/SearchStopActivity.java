package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.StringAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.util.AES7PaddingUtil;
import com.wuxibus.app.util.DBUtil;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/9.
 */
public class SearchStopActivity extends Activity implements View.OnClickListener
        ,SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {


    /* 返回按钮 */
    ImageView back_imageview;
    /* 查询历史 */
    TextView history_textview;
    /* 清除查询历史 */
    TextView clear_history_textview;
    /**/
    SearchView searchView;

    View container;
    ListView searchResult;
    ListView historyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stop);
        back_imageview = (ImageView) findViewById(R.id.back_imageview);
        history_textview = (TextView) findViewById(R.id.history_textview);
        clear_history_textview = (TextView)findViewById(R.id.clear_history_textview);
        searchView = (SearchView) findViewById(R.id.search_view);
        container = findViewById(R.id.container);
        searchResult = (ListView) findViewById(R.id.search_result_listview);
        historyListView = (ListView) findViewById(R.id.history_listview);


        back_imageview.setOnClickListener(this);
        history_textview.setOnClickListener(this);
        clear_history_textview.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        searchResult.setOnItemClickListener(this);
        historyListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSearchStopHistory();
    }

    public void initSearchStopHistory(){
        DBUtil dbUtil = new DBUtil(this);
        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<String> stopList = new ArrayList<String>();
        for (int i = 0; list != null && i < list.size(); i++) {
            stopList.add(list.get(i).getLineName());
        }
        StringAdapter adapter = new StringAdapter(this,stopList);
        historyListView.setAdapter(adapter);
    }

    public void clearSearchStopHistory(){
        DBUtil dbUtil = new DBUtil(this);
        dbUtil.clearSearchHistory(AllConstants.SEARCH_STATIION_TYPE);

        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<String> stopList = new ArrayList<String>();
        for (int i = 0; list != null && i < list.size(); i++) {
            stopList.add(list.get(i).getLineName());
        }
        StringAdapter adapter = new StringAdapter(this,stopList);
        historyListView.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        if(view == back_imageview){
            this.finish();
        }else if(clear_history_textview == view){
            clearSearchStopHistory();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String stopName = "";
//        if(adapterView == searchResult){
            stopName = ((StringAdapter.ViewHolder) view.getTag()).resultText.getText().toString();
//        }else if(adapterView == historyListView){
//
//        }

        DBUtil dbUtil = new DBUtil(this);
        dbUtil.saveDB(stopName,"","","",AllConstants.SEARCH_STATIION_TYPE);

        Intent intent = new Intent(this, SearchStopResultActivity.class);
        intent.putExtra("stopName",stopName);
        startActivity(intent);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s != null && !s.equals("")){
            searchResult.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }else{
            searchResult.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
        String url = AllConstants.ServerUrl;

        Map<String,String> params = new HashMap<String,String>();
        params.put("m","stop_search");
        params.put("k",s);
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    List<String> routeList = new ArrayList<String>();
                    if (jsonObject.getString("error").equals("1")) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i =0;i<array.length();i++){
                            String temp = array.getString(i);
                            routeList.add(temp);
                        }
                        StringAdapter stringAdapter = new StringAdapter(SearchStopActivity.this,routeList);
                        initSearchResultListview(stringAdapter);
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

    public void initSearchResultListview(StringAdapter stringAapter ){
        searchResult.setAdapter(stringAapter);
    }
}
