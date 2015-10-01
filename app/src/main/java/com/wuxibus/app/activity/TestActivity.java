package com.wuxibus.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.wuxibus.app.R;

/**
 * Created by zhongkee on 15/7/17.
 */
public class TestActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_line_search);
        listView = (ListView) findViewById(R.id.line_name_listview);
    }
}
