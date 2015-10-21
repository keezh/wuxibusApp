package com.wuxibus.app.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuxibus.app.entity.FavoriteRoute;
import com.wuxibus.app.entity.FavoriteStop;
import com.wuxibus.app.entity.InterchangeSearchHistory;
import com.wuxibus.app.entity.Route;
import com.wuxibus.app.entity.SearchHistory;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		helper = new DBHelper(context);
		//因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
		//所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	/**
	 * 插入搜索历史,该处理的业务处理逻辑如下：先查询线路或站的，如果查到数据，则更新该条记录的更新时间
	 * @param title
	 * @param type
	 */
	public void insertSearchHistory(String lineName,String title,String startStop,String endStop,int type){
		db.beginTransaction();
		try{
			//String sql = "select * from search_history";
			//Cursor c = db.rawQuery(sql,null);

			Cursor c = db.query("search_history", new String[]{"id","line_name"}, "type = ? and line_name= ?", new String[]{type + "", lineName}, null, null, null, null);
			if(c.moveToNext()){
				int id = c.getInt(c.getColumnIndex("id"));
				//ContentValues contentValues = new ContentValues();
				//contentValues.pu
				db.execSQL("update search_history set update_date = CURRENT_TIMESTAMP where id = ?",new Object[]{id});
			}else{
				db.execSQL("INSERT INTO search_history(line_name,title,start_stop,end_stop,type) VALUES(?,?,?,?,?)",
						new Object[]{lineName,title,startStop,endStop,type});
			}
			//kee bug 必须设置此函数，否则保存不会同步到数据库中
			db.setTransactionSuccessful();	//设置事务成功完成
			c.close();


		}catch (Exception e){
			e.printStackTrace();

		}finally {

			db.endTransaction();
		}
	}

	/**
	 * 换乘搜索历史
	 * @param name
	 * @param latitude
	 * @param longitude
	 */
	public void insertInterchangeSearchHistory(String name,String latitude,String longitude){
		db.beginTransaction();
		try{
			//String sql = "select * from search_history";
			//Cursor c = db.rawQuery(sql,null);

			Cursor c = db.query("interchange_search_history", new String[]{"id","name"}, "name= ?", new String[]{name}, null, null, null, null);
			if(c.moveToNext()){
				int id = c.getInt(c.getColumnIndex("id"));
				//ContentValues contentValues = new ContentValues();
				//contentValues.pu
				db.execSQL("update interchange_search_history set update_date = CURRENT_TIMESTAMP where id = ?",new Object[]{id});
			}else{
				db.execSQL("INSERT INTO interchange_search_history(name,latitude,longitude) VALUES(?,?,?)",
						new Object[]{name,latitude,longitude});
			}
			//kee bug 必须设置此函数，否则保存不会同步到数据库中
			db.setTransactionSuccessful();	//设置事务成功完成
			c.close();


		}catch (Exception e){
			e.printStackTrace();

		}finally {

			db.endTransaction();
		}
	}

	/**
	 * 全局搜索历史记录
	 * @return
	 */
	public List<SearchHistory> querySearchHistory(){
		Cursor c = db.rawQuery("SELECT * FROM search_history", null);
		List<SearchHistory> list = new ArrayList<SearchHistory>();
		while (c.moveToNext()){
			//int id = c.getInt(c.getColumnIndex("id"));
			String lineName = c.getString(c.getColumnIndex("line_name"));
			String title = c.getString(c.getColumnIndex("title"));
			int type = c.getInt(c.getColumnIndex("type"));
			String startStop = c.getString(c.getColumnIndex("start_stop"));
			String endStop = c.getString(c.getColumnIndex("end_stop"));
			SearchHistory searchHistory = new SearchHistory(lineName,title,type,startStop,endStop);
			list.add(searchHistory);

		}
		c.close();
		return list;

	}

	/**
	 * 根据线路，站台，地点查询历史
	 * @param typeSearch
	 * @return
	 */
	public List<SearchHistory> querySearchHistory(int typeSearch){
		Cursor c = db.rawQuery("SELECT * FROM search_history where type = ? order by update_date desc", new String[]{typeSearch+""});
		//Cursor c = db.query("search_history",new String[]{"line_id","title","start_stop","end_stop"},null,null,null,null,null,null);
		List<SearchHistory> list = new ArrayList<SearchHistory>();
		while (c.moveToNext()){
			//int id = c.getInt(c.getColumnIndex("id"));
			String lineId = c.getString(c.getColumnIndex("line_name"));

			String title = c.getString(c.getColumnIndex("title"));
			//int type = c.getInt(c.getColumnIndex("type"));
			String startStop = c.getString(c.getColumnIndex("start_stop"));
			String endStop = c.getString(c.getColumnIndex("end_stop"));
			SearchHistory searchHistory = new SearchHistory(lineId,title,typeSearch,startStop,endStop);
			list.add(searchHistory);

		}
		c.close();
		return list;
	}



	/**
	 * 清楚历史
	 */
	public void clearSearchHistory(int type){
		db.delete("search_history","type = ?",new String[]{type+""});
		db.close();
	}
	
	/**
	 * add persons
	 */
	public void add(String line_id,String title,String name,String direction,String startStop,String endStop,String startEndTime,String stopName) {
        db.beginTransaction();	//开始事务
        try {

        		db.execSQL("INSERT INTO line_fav(line_id,title,name,direction,start_stop,end_stop,start_end_time) VALUES(?,?,?, ?,?,?,?)",
						new Object[]{line_id,title, name, direction, startStop,endStop,startEndTime});


        	db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
        	db.endTransaction();	//结束事务
        }
	}

	//Cursor cursor = db.query("person", new String[]{"personid,name,age"},
	// "name like ?", new String[]{"%ljq%"}, null, null, "personid desc", "1,2");

	public boolean get(String line_id){
		Cursor cursor = db.query("line_fav",new String[]{"line_id,title"},"line_id = ?",new String[]{line_id},null,null,null,null);

		if(cursor.moveToNext()){
			return true;
		}
		return false;
	}
	
	/**
	 * update person's age
	 */
	public void updateAge() {
//		ContentValues cv = new ContentValues();
//		cv.put("age", person.age);
//		db.update("person", cv, "name = ?", new String[]{person.name});
	}
	
	/**
	 * delete old person
	 */
	public void deleteLine(String line_id) {
		db.delete("line_fav", "line_id = ?", new String[]{line_id});
	}
	
	/**
	 * query all persons, return list
	 * @return List<Person>
	 */
	public List<FavoriteRoute> queryFavRoute() {
		ArrayList<FavoriteRoute> routes = new ArrayList<FavoriteRoute>();
		Cursor c = queryTheCursor();
        while (c.moveToNext()) {
			FavoriteRoute favoriteRoute = new FavoriteRoute();

			String lineId = c.getString(c.getColumnIndex("line_id"));
			String title = c.getString(c.getColumnIndex("title"));
			String name = c.getString(c.getColumnIndex("name"));
			String direction = c.getString(c.getColumnIndex("direction"));
			String startStop = c.getString(c.getColumnIndex("start_stop"));
			String endStop = c.getString(c.getColumnIndex("end_stop"));
			String startEndTime = c.getString(c.getColumnIndex("start_end_time"));
			//String stopName = c.getString(c.getColumnIndex("stop_name"));
			favoriteRoute.setLine_id(lineId);
			favoriteRoute.setLine_name(name);
			favoriteRoute.setLineTitle(title);
			favoriteRoute.setDirection(direction);
			favoriteRoute.setStop_start(startStop);
			favoriteRoute.setStop_end(endStop);
			favoriteRoute.setStart_end_time(startEndTime);
			//favoriteRoute.setStopName(stopName);

//			FavoriteStop favoriteStop = new FavoriteStop();
//			favoriteStop.setStop_start(startStop);
//			favoriteStop.setStop_end(endStop);
//			List<FavoriteStop> list = new ArrayList<FavoriteStop>();
//			list.add(favoriteStop);


			routes.add(favoriteRoute);

//			route.setLine_name(name);
//			route.setLine_title(title);
//			route.setStop_start(startStop);
//			route.setStop_end(endStop);
//
//        	routes.add(route);
        }
        c.close();
//		closeDB();
        return routes;
	}

	public List<Route> queryRoute(){
		ArrayList<Route> routes = new ArrayList<Route>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()){
			Route route = new Route();
			String lineId = c.getString(c.getColumnIndex("line_id"));
			String title = c.getString(c.getColumnIndex("title"));
			String name = c.getString(c.getColumnIndex("name"));
			String startStop = c.getString(c.getColumnIndex("start_stop"));
			String endStop = c.getString(c.getColumnIndex("end_stop"));
			String startEndTime = c.getString(c.getColumnIndex("start_end_time"));
			//String stopName = c.getString(c.getColumnIndex("stop_name"));
			route.setLine_id(lineId);
			route.setLine_title(title);
			route.setLine_name(name);
			route.setStop_start(startStop);
			route.setStop_end(endStop);
			route.setTime_start_end(startEndTime);
			//route.setStopName(stopName);
			routes.add(route);
		}
//		closeDB();

		return routes;

	}
	
	/**
	 * query all persons, return cursor
	 * @return	Cursor
	 */
	public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM line_fav", null);
        return c;
	}
	
	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}

	public List<InterchangeSearchHistory> queryInterchangeSearchHistory() {
		List<InterchangeSearchHistory> list = new ArrayList<InterchangeSearchHistory>();

		Cursor c = db.rawQuery("SELECT * FROM interchange_search_history order by update_date desc", null);

		//Cursor c = db.query("search_history",new String[]{"line_id","title","start_stop","end_stop"},null,null,null,null,null,null);
		while (c.moveToNext()){
			//int id = c.getInt(c.getColumnIndex("id"));
			int id = c.getInt(c.getColumnIndex("id"));

			String name = c.getString(c.getColumnIndex("name"));
			String latitude = c.getString(c.getColumnIndex("latitude"));
			String longitude = c.getString(c.getColumnIndex("longitude"));
			InterchangeSearchHistory temp = new InterchangeSearchHistory(id,name,latitude,longitude);
			list.add(temp);

		}
		c.close();
		return list;


	}
}
