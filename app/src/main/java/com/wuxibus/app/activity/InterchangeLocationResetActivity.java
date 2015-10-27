package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeStoreAdapter;
import com.wuxibus.app.util.StorageUtil;

/**
 * Created by zhongkee on 15/10/23.
 */
public class InterchangeLocationResetActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{

    ListView locationListView;
    ImageView backImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interchange_location_reset);
        locationListView = (ListView) findViewById(R.id.interchange_storage_lv);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        backImageView.setOnClickListener(this);

        locationListView.setOnItemClickListener(this);

        //读入本地xml文件，地址信息，信息存在在InterchangeSearch  homeInfo,CompanyInfo
        StorageUtil.readBindDevice(this);
    }

    @Override
    public void onClick(View v) {
        if(v == backImageView){
            this.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        InterchangeStoreAdapter storeAdapter = new InterchangeStoreAdapter(this);
        locationListView.setAdapter(storeAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == locationListView){
            Intent intent = new Intent(this,MapLocationResetActivity.class);
            intent.putExtra("position",""+position);
            startActivity(intent);
        }
    }


}
