package com.wuxibus.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.adapter.StringStationAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/9/22.
 */
public class TestHistoryActivity extends Activity {

    ListView history_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_history);
        history_listview = (ListView) findViewById(R.id.history_stops_listview);

        showHistory();

    }

    private void showHistory() {
        DBUtil dbUtil = new DBUtil(this);
        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<String> stationList = new ArrayList<String>();
        for (int i = 0; list != null && i < list.size(); i++) {
            stationList.add(list.get(i).getLineName());
        }

//            stationList.add("test");
//            stationList.add("test2");
        StringStationAdapter adapter = new StringStationAdapter(this,stationList);
        history_listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
