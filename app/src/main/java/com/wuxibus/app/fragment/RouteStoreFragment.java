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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.wuxibus.app.R;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.adapter.RouteStoreAdapter;
import com.wuxibus.app.entity.FavoriteRoute;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.sqlite.DBManager;

import java.util.List;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;

/**
 * Created by zhongkee on 15/6/17.
 */
public class RouteStoreFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView;
    private BaseAdapter routeStoreAdapter;

    ImageView tipFavImageView;

    public RouteStoreFragment(){

    }

//    public RouteStoreFragment(Handler myHandler) {
//
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        //queryLineFavList();

        queryLineFavBySqlite();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_store,null);
        listView = (ListView) view.findViewById(R.id.route_store_listview);
        tipFavImageView = (ImageView) view.findViewById(R.id.no_store_line);
        listView.setOnItemClickListener(this);
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

    public void queryLineFavBySqlite(){
        DBManager dbManager = new DBManager(this.getActivity());
        List<Route> routes = dbManager.queryRoute();
        if(routes == null || routes.size() == 0){
            tipFavImageView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

        }else {
            tipFavImageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        List<FavoriteRoute>favList = dbManager.queryFavRoute();
        routeStoreAdapter = new RouteStoreAdapter(RouteStoreFragment.this.getActivity(),favList);
        listView.setAdapter(routeStoreAdapter);


    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String lineId = ((RouteStoreAdapter.ViewHolder)view.getTag()).lineNameTextView.getText().toString();
        FavoriteRoute favoriteRoute = ((RouteStoreAdapter.ViewHolder)view.getTag()).favoriteRoute;

        Bundle bundle = new Bundle();
        bundle.putString("lineName",favoriteRoute.getLineTitle());
        Intent intent = new Intent(this.getActivity(),SearchLineResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
