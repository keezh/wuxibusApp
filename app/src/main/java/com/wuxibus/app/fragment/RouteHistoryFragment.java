package com.wuxibus.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.adapter.RouteHistoryAdapter;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.util.DBUtil;

import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;

/**
 * Created by zhongkee on 15/6/17.
 */
public class RouteHistoryFragment extends Fragment implements AdapterView.OnItemClickListener{

    public ListView listView;
    ImageView bgImageView;
    public RouteHistoryFragment(){

    }
//    public RouteHistoryFragment(Handler myHandler) {
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(this.getActivity(), R.layout.route_history_fragment,null);

        listView = (ListView) view.findViewById(R.id.route_history_listview);
        listView.setOnItemClickListener(this);
        bgImageView = (ImageView) view.findViewById(R.id.route_history_none_bg);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DBUtil dbUtil = new DBUtil(this.getActivity());
        List<SearchHistory> list = dbUtil.querySearchHistory(AllConstants.SEARCH_LINE_TYPE);
        if(list != null && list.size() > 0){
            RouteHistoryAdapter adapter = new RouteHistoryAdapter(this.getActivity(),list);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            bgImageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            bgImageView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == listView){

            RouteHistoryAdapter.ViewHolder viewHolder = (RouteHistoryAdapter.ViewHolder) view.getTag();
            String lineName = viewHolder.titleTextView.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("lineName",lineName);
            Intent intent = new Intent(this.getActivity(),SearchLineResultActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }
}
