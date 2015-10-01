package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.adapter.RouteStoreAdapter;
import com.wuxibus.app.entity.FavoriteRoute;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.sqlite.DBManager;

import java.util.List;

/**
 * Created by zhongkee on 15/8/6.
 */
public class MyFavActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{

    ImageView backImageView;
    ImageView tipFavImageView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fav);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        listView = (ListView) findViewById(R.id.line_fav_listview);
        tipFavImageView = (ImageView) findViewById(R.id.tip_favor);
        backImageView.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        queryLineFavBySqlite();
    }

    public void queryLineFavBySqlite(){
        DBManager dbManager = new DBManager(this);
        List<Route> routes = dbManager.queryRoute();
        if(routes == null || routes.size() == 0){
            tipFavImageView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

        }else {
            tipFavImageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        List<FavoriteRoute>favList = dbManager.queryFavRoute();
        RouteStoreAdapter routeStoreAdapter = new RouteStoreAdapter(this,favList);
        listView.setAdapter(routeStoreAdapter);


    }

    @Override
    public void onClick(View view) {
        if(backImageView == view){
            this.finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == listView){
            String lineId = ((RouteStoreAdapter.ViewHolder)view.getTag()).lineNameTextView.getText().toString();
            FavoriteRoute favoriteRoute = ((RouteStoreAdapter.ViewHolder)view.getTag()).favoriteRoute;

            Bundle bundle = new Bundle();
            bundle.putString("lineName",lineId);
            Intent intent = new Intent(this,SearchLineResultActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }

    }
}
