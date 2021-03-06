package com.wuxibus.app.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "line.db";
	//每次修改需要更改该参数,当用户是通过更新apk,时也必须要重新初始化数据库
	private static final int DATABASE_VERSION = 10;
	
	public DBHelper(Context context) {
		//CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		initTables(db);
//		db.execSQL("create table if not exists search_station_history(id integer primary key autoincrement,title varchar," +
//				"type integer,create_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
	}

	private void initTables(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS line_fav" +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT, line_id varchar,title varchar,name varchar,direction varchar, start_stop varchar, end_stop" +
				" varchar,start_end_time varchar)");
		db.execSQL("create table if not exists search_history(id integer primary key autoincrement,line_name varchar,title varchar,start_stop varchar," +
				"end_stop varchar,type integer,update_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
		//换乘搜索历史记录表,baidu接口的记录，只记录起点或者终点
		db.execSQL("create table if not exists interchange_search_history(id integer primary key autoincrement,name varchar,latitude varchar,longitude varchar," +
				"update_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
		//首页的过去查询的记录，该表记录下用户的起点，终点，
		db.execSQL("create table if not exists interchange_search_full_history(id integer primary key autoincrement,source_name varchar," +
				"source_latitude varchar,source_longitude varchar,destination_name varchar,destination_latitude varchar," +
				"destination_longitude varchar,update_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
	}

	//如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
		initTables(db);
	}
}
