package com.wheeltest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DqxxUtils {
	
	/**先检测应用程序data目录下面是否已经存在该文件。不存在则
	 * 复制assert目录下文件到应用程序data目录下面
	 * @param context
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyDB(Context context, String fileName) throws IOException {
		String filePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
		if (new File(filePath).exists()) {
			return;
		}
		FileOutputStream fos = new FileOutputStream(new File(filePath));
		InputStream is = context.getResources().getAssets().open(fileName);
		byte[] buffer = new byte[1024*500];
		int count = 0;
		while((count = is.read(buffer)) > 0){
			fos.write(buffer, 0, count);
		}
		fos.close();
		is.close();
	}
	
	public static Map<String, Integer>  getProvince(SQLiteDatabase db, String tableName){
		Map<String, Integer> provinceMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"DQX_DQXX01","DQXX02"}, "DQXX03=?", new String[]{"1"}, null, null, "DQX_DQXX01 ASC");
		if (cursor != null ) {
			while(cursor.moveToNext()) {
				provinceMap.put(cursor.getString(1), cursor.getInt(0));
			}
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return provinceMap;
	}
	
	/**
	 * 
	 * @param db
	 * @param tableName
	 * @param dqx_dqxx01
	 * @param Municipalities  是否是直辖市
	 * @return
	 */
	public static Map<String, Integer> getCity(SQLiteDatabase db, String tableName, int dqx_dqxx01, boolean municipalities) {
		Map<String, Integer> cityMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"DQXX02", "DQXX01"}, "DQX_DQXX01=?", new String[]{""+dqx_dqxx01}, null, null, "DQXX01 ASC");
		if (cursor != null) {
			if (municipalities) {
				cursor.moveToNext();
			}
			while(cursor.moveToNext()) {
				cityMap.put(cursor.getString(0), cursor.getInt(1));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return cityMap;
	}
	
	public static Map<String, Integer> getArea(SQLiteDatabase db, String tableName, int dqx_dqxx01) {
		Map<String, Integer> areaMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"DQXX02", "DQXX01"}, "DQX_DQXX01=?", new String[]{""+dqx_dqxx01}, null, null, "DQXX01 ASC");
		if (cursor != null ) {
			while(cursor.moveToNext()) {
				areaMap.put(cursor.getString(0), cursor.getInt(1));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return areaMap;
	}
	
	public static int findPrimaryKey(SQLiteDatabase db, String tableName, String address) {
		int key = -1;
		Cursor cursor = db.query(tableName, new String[]{"DQXX01"}, "DQXX05=?", new String[]{address}, null, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				key = cursor.getInt(0);
			}
		}
		return key;
	}
}
