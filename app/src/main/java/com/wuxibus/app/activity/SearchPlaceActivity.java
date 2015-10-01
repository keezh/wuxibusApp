package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.AroundStopAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.StopNearby;
import com.wuxibus.app.volley.VolleyManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhongkee on 15/7/26.
 */
public class SearchPlaceActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{

    ListView listView;
    TextView titleTextView;
    ImageView backImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        listView = (ListView) findViewById(R.id.around_stops_listview);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        String stopName = this.getIntent().getExtras().getString("stopName");
        titleTextView.setText(stopName);
        listView.setOnItemClickListener(this);
        backImageView.setOnClickListener(this);
        String longitude = this.getIntent().getExtras().getString("longitude");

        String latitude = this.getIntent().getExtras().getString("latitude");
        queryAroundStops(longitude,latitude);
    }

//    intent.putExtra("stopName",stopName);
//    intent.putExtra("longitude",viewHolder.lng);
//    intent.putExtra("latitude",viewHolder.lat);

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == listView){
            AroundStopAdapter.ViewHolder viewHolder = (AroundStopAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(this, SearchStopResultActivity.class);
            intent.putExtra("stopName",viewHolder.stopNameTextView.getText().toString());
            startActivity(intent);

        }
    }

    public void queryAroundStops(String longitude,String latitude){
//radius=500&longitude=120.303283&latitude=31.569154
        Map<String,String> params = new HashMap<String,String>();
        params.put("m","stop_nearby");
        params.put("radius","500");
//        params.put("longitude","120.303283");
//        params.put("latitude","31.569154");
        params.put("longitude",longitude);
        params.put("latitude",latitude);


        VolleyManager.getJson(AllConstants.ServerUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getString("error").equals("1")) {
                        List<StopNearby> list = JSON.parseArray(jsonObject.getString("result"), StopNearby.class);
                        AroundStopAdapter aroundStopAdapter = new AroundStopAdapter(SearchPlaceActivity.this, list);
                        listView.setAdapter(aroundStopAdapter);
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

    @Override
    public void onClick(View view) {
        if(view == backImageView){
            this.finish();
        }
    }
}
