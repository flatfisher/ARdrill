/** 2014 0313 Itnav Ishinomaki Kyohei Tsuda
 * 
 */
package jp.itnav.r4r.ardrill;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MySQLite {
	private Context mContext;
	public MySQLite(Context context) {
		this.mContext = context;
		DatabaseHelper dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
	}

	public void Insert(double latitude,double longitude 
			,double distance) {
		long ret = 0;
		ContentValues values = new ContentValues();
		DatabaseHelper dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		values.put("Latitude", latitude);
		values.put("Longitude", longitude);
		values.put("Distance", distance);
		try {
			ret = db.insert("MyTable", null, values);
		} finally {
			db.close();
			if (ret == -1) {
				Log.v("インサート失敗", "インサートに失敗しました。");
			} else {
				Log.v("インサート成功", "インサートに成功しました。");
			}
		}
	}

	public void Serch() {
		int ret = 0;
		Cursor cursor;
		String[] cols = { "Latitude", "Longitude", "Distance"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		DatabaseHelper dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.query("MyTable", cols, selection, selectionArgs,
					groupBy, having, orderBy);
			try {
				cursor.moveToFirst();
				int count = cursor.getCount();
				Log.v("cursor", String.valueOf(count));
				for (int i = 0; i < count; i++) {
					Log.v("cursor1",String.valueOf(cursor.getDouble(0)));
					Log.v("cursor2",String.valueOf(cursor.getDouble(1)));
					Log.v("cursor3",String.valueOf(cursor.getDouble(2)));
					cursor.moveToNext();
				}

			} catch (Exception e) {
				Log.v("Exception", String.valueOf(e));
			}

		} finally {
			db.close();
			if (ret == -1) {
				Log.v("サーチ失敗", "サーチに失敗しました。");
			} else {
				Log.v("サーチ成功", "サーチに成功しました。");
			}
		}
	
	}
	
	public double DistanceSerch() {
		int ret = 0;
		Cursor cursor;
		double distance = 0.0;
		String[] cols = { "Latitude", "Longitude", "Distance"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		DatabaseHelper dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.query("MyTable", cols, selection, selectionArgs,
					groupBy, having, orderBy);
			try {
				cursor.moveToFirst();
				int count = cursor.getCount();
				Log.v("cursor", String.valueOf(count));
				for (int i = 0; i < count; i++) {
					Log.v("cursor1",String.valueOf(cursor.getDouble(0)));
					Log.v("cursor2",String.valueOf(cursor.getDouble(1)));
					Log.v("cursor3",String.valueOf(cursor.getDouble(2)));
					distance += cursor.getDouble(2);
					cursor.moveToNext();
				}

			} catch (Exception e) {
				Log.v("Exception", String.valueOf(e));
			}

		} finally {
			db.close();
			if (ret == -1) {
				Log.v("サーチ失敗", "サーチに失敗しました。");
			} else {
				Log.v("サーチ成功", "サーチに成功しました。");
			}
		}
		return distance;
	
	}
	
	public ArrayList<LatLng> getLatLng(){
		int ret = 0;
		Cursor cursor;
		LatLng geo;
		ArrayList<LatLng> latlng = new ArrayList<LatLng>();
		String[] cols = { "Latitude", "Longitude", "Distance"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		DatabaseHelper dbHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.query("MyTable", cols, selection, selectionArgs,
					groupBy, having, orderBy);
			
			try {
				cursor.moveToFirst();
				int count = cursor.getCount();
				Log.v("cursor", String.valueOf(count));
				
			
				for (int i = 0; i < count; i++) {
					geo = new LatLng(cursor.getDouble(0),cursor.getDouble(1));
					latlng.add(geo); 
					cursor.moveToNext();
				}

			} catch (Exception e) {
				Log.v("Exception", String.valueOf(e));

			}

		} finally {
			db.close();
			if (ret == -1) {
				Log.v("サーチ失敗", "サーチに失敗しました。");
			} else {
				Log.v("サーチ成功", "サーチに成功しました。");
			}
		}
		return latlng;
	}
	
	public void Delete(Context context) {
		long ret = 0;
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] whereArgs = null;
		String selection = null;
		try {
			ret = db.delete("MyTable", selection, whereArgs);
		} finally {
			db.close();
			if (ret == -1) {
				Log.v("デリート失敗", "デリートに失敗しました。");
			} else {
				Log.v("デリート成功", "デリートに成功しました。");
			}
		}

	}
	
}
