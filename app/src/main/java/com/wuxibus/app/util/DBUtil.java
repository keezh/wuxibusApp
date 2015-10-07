package com.wuxibus.app.util;

import android.content.Context;

import com.wuxibus.app.entity.SearchHistory;
import com.wuxibus.app.sqlite.DBManager;

import java.util.List;

/**
 * Created by zhongkee on 15/7/25.
 * "line_id": "35",
 "line_title": "35路",
 "line_code": "21874403",
 "line_name": "35路环形",
 "direction": 0,
 "stop_start": "新区分公司",
 "stop_end": "火车站",
 "time_start_end": "新区分公司5:30-23:00"
 *
 */
public class DBUtil {
    Context context;

    public DBUtil(Context context){
        this.context = context;
    }

    public  void saveDB(String lineName,String title,String startStop,String endStop,int type){
        DBManager dbManager = new DBManager(context);
        dbManager.insertSearchHistory(lineName,title,startStop,endStop,type);
        dbManager.closeDB();
    }

    public List<SearchHistory> querySearchHistory(int type){
        DBManager dbManager = new DBManager(context);
        List<SearchHistory> list = dbManager.querySearchHistory(type);
        dbManager.closeDB();
        return list;
    }


    public void clearSearchHistory(int type){
        DBManager dbManager = new DBManager(context);
        dbManager.clearSearchHistory(type);
        dbManager.closeDB();
    }

    /**
     * 保存换乘历史
     * @param name
     * @param latitude
     * @param longitude
     */
    public void insertInterchangeSearch(String name,String latitude,String longitude){
        DBManager dbManager = new DBManager(context);
        dbManager.insertInterchangeSearchHistory(name,latitude,longitude);
        dbManager.closeDB();
    }


}
