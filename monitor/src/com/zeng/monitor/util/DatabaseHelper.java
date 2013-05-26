package com.zeng.monitor.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zeng.monitor.R;
import com.zeng.monitor.config.MonitorParameter;

public class DatabaseHelper{

	
	static class Helper extends SQLiteOpenHelper {
		protected final static String TAG ="DatabaseHelper";
		private final static String DATABASE_NAME="MONITOR_CLIENT";
		private final static int DATABASE_VERSION = 1;
		private Context context;
		public Helper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);  
			this.context = context;
		}

		
		
		public void onCreate(SQLiteDatabase db) {
			try{
				String sql =context.getString(R.string.table_sql);
				db.execSQL(sql);
				Log.i(TAG, sql);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = context.getString(R.string.drop_sql);
			db.execSQL(sql);
			this.onCreate(db);
			
		}
		
	}

	private Context context;
	protected SQLiteDatabase db;
	public DatabaseHelper(Context context) {
		this.setContext(context);
		db = new Helper(context).getWritableDatabase();;
	}

	
	public static MonitorParameter query(Context context,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			String[] columns = new String[]{
				"name",	"ip","port","username","password","client_dir","connect_type"
			};
			
			
			System.out.println(columns);
			Cursor cursor =	db.query("tb_monitor_configs", columns, whereClause, whereArgs, null, null, null);
			if(cursor.getCount()==0){
				throw new Exception("没有找到ID"+id+"的数据");
			}
			cursor.moveToFirst();
			MonitorParameter param = new MonitorParameter();
			param.setId(id);
			param.setName(cursor.getString(0));
			param.setIp(cursor.getString(1));
			param.setPort(cursor.getInt(2));
			param.setUsername(cursor.getString(3));
			param.setPassword(cursor.getString(4));
			param.setLocal_dir(cursor.getString(5));
			return param;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	public static long testInsert(Context context){
		
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			ContentValues values = new ContentValues();
	//		values.put("_id", -1);
			values.put("name", "test1");
			values.put("port", 100);
			values.put("ip", "192.163.1");
			values.put("username", "888888");
			values.put("password", "888888");
			values.put("client_dir", "test");
			long num = db.insert("tb_monitor_configs", null, values);
			return num;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	

	public static long insert(Context context,String table,ContentValues values) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			long num = db.insert(table, null, values);
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public static long update(Context context,String table,ContentValues values,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			long num = db.update(table, values, whereClause, whereArgs);
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	
	
	public static void testDelete(Context context){
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();
			db.execSQL("delete from tb_monitor_configs;");
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void delete(Context context,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();
			String table = "tb_monitor_configs";  
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			db.delete(table, whereClause, whereArgs);
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void drop(Context context){
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String sql =context.getString(R.string.drop_sql);
			db.execSQL(sql);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static int getCount(Context context ,String table) throws Exception{
		SQLiteDatabase db =  null;
		try{
			db = new Helper(context).getReadableDatabase();
			Cursor cur = db.query(table, new String[]{"_id","name"}, null, null, null, null, null);
			return cur.getCount();
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public Cursor loadAllName() throws Exception{
		try{
			Cursor cur = db.query("tb_monitor_configs", new String[]{"_id","name"}, null, null, null, null, "_id DESC");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}
	
	
	public void close(){
		if(this.db!=null){
			this.db.close();
		}
	}
	
	
	public static List<String> loadName(Context context) throws Exception{
		SQLiteDatabase db =  null;
		List<String> rst = new ArrayList<String>();
		try{
			db = new Helper(context).getReadableDatabase();
			Cursor cur =db.query("tb_monitor_configs", new String[]{"_id","name"}, null, null, null, null, null);
			cur.moveToFirst();  
			for (int i = 0; i < cur.getCount(); i++) {  
				String s = cur.getString(1);  
				rst.add(s);
				cur.moveToNext();  
			}  
			return rst;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public Cursor query(int id) throws Exception{
		try{
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			Cursor cur = db.query("tb_monitor_configs", new String[]{"_id","name","ip","port","username","password","client_dir","connect_type"}, whereClause, whereArgs, null, null, "_id DESC");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}

/*
 * get set 为了消除警告*/
	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}
}
