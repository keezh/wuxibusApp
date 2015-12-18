package com.wuxibus.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchStopResultActivity;
import com.wuxibus.app.adapter.StringStationAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/7/23.
 * 暂时该类没有被使用
 */
public class StationHistoryFragment extends Fragment implements AdapterView.OnItemClickListener{

    ListView history_listview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.station_history_fragment, null);
        history_listview = (ListView) view.findViewById(R.id.history_stops_listview);
        history_listview.setOnItemClickListener(this);

        showHistory();

        return view;

    }

    private void showHistory() {
        DBUtil dbUtil = new DBUtil(this.getActivity());
        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_STATIION_TYPE);
        List<String> stationList = new ArrayList<String>();
        for (int i = 0; list != null && i < list.size(); i++) {
            stationList.add(list.get(i).getLineName());
        }

        StringStationAdapter adapter = new StringStationAdapter(this.getActivity(),stationList);
        history_listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == history_listview){
            StringStationAdapter.ViewHolder viewHolder = (StringStationAdapter.ViewHolder) view.getTag();
            Intent intent = new Intent(this.getActivity(), SearchStopResultActivity.class);
            intent.putExtra("stopName",viewHolder.resultText.getText().toString());
            startActivity(intent);
        }
    }
}
