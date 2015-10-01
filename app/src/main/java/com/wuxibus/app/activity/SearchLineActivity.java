package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.SearchLineAdapter;
import com.wuxibus.app.adapter.StringAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.sqlite.DatabaseHelper;
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
 * Created by zhongkee on 15/6/19.
 * 查询线路
 */
public class SearchLineActivity extends Activity implements View.OnClickListener,SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {
//
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
        setContentView(R.layout.activity_search);
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
        initSearchHistory();

    }

    public void initSearchHistory(){
        DBUtil dbUtil = new DBUtil(this);
        List<SearchHistory> searchHistoryList = dbUtil.querySearchHistory(AllConstants.SEARCH_LINE_TYPE);
        SearchLineAdapter adapter = new SearchLineAdapter(this,searchHistoryList);
        historyListView.setAdapter(adapter);
    }

    public void clearLineHistory(){
        DBUtil dbUtil = new DBUtil(this);
        dbUtil.clearSearchHistory(AllConstants.SEARCH_LINE_TYPE);

        List<SearchHistory> searchHistoryList = dbUtil.querySearchHistory(AllConstants.SEARCH_LINE_TYPE);
        SearchLineAdapter adapter = new SearchLineAdapter(this,searchHistoryList);
        historyListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view == back_imageview){
            this.finish();
        }else if(view == clear_history_textview){
            clearLineHistory();

        }
    }

    /**
     *
     * @param s
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        if(s==null || s.equals(""))
//            return false;

        if(s != null && !s.equals("")){
            searchResult.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }else{
            searchResult.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }

        String url = AllConstants.ServerUrl;
        //url = "/?m=line_search&k=" +  s;

        //测试，post请求是没有问题的
        //String tempUrl = "http://192.168.0.103/post.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","line_search");
        params.put("k",s);
        //params.put("name","zhongke22");
        //测试使用get请求
        params = AES7PaddingUtil.toAES7Padding(params);
        VolleyManager.getJson(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //获取的JSON不为空的话
                if (!TextUtils.isEmpty(jsonObject.toString())) {
                    try{

                        String str = jsonObject.toString();
                        String error = jsonObject.getString("error");
                        List<String> routeList = new ArrayList<String>();
                        if(error.equals("1")){
                            JSONArray array = jsonObject.getJSONArray("result");
                            for (int i =0;i<array.length();i++){
                                String temp = array.getString(i);
                                routeList.add(temp);
                            }
                            StringAdapter stringAdapter = new StringAdapter(SearchLineActivity.this,routeList);
                            initSearchResultListview(stringAdapter);


                        }




                    }catch (Exception e){
                        e.printStackTrace();
                    }



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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String lineName = "";
        if(adapterView == searchResult){
             lineName = ((StringAdapter.ViewHolder) view.getTag()).resultText.getText().toString();

        }else if(adapterView == historyListView){
            lineName = ((SearchLineAdapter.ViewHolder)view.getTag()).lineName;
        }
        Bundle bundle = new Bundle();
        bundle.putString("lineName",lineName);
        Intent intent = new Intent(this,SearchLineResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
