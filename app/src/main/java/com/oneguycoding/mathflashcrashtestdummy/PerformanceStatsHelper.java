package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * SQLiteOpenHelper class
 *
 * Created by steeve on 27/09/17.
 *  <br/>
 *  Reference: https://developer.android.com/training/basics/data-storage/databases.html
 */

class PerformanceStatsHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "MathFlashCrashTestDummy.db";

	PerformanceStatsHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String sql = PerformanceStatsSchema.SQL_CREATE_TABLE;
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.d("SQL", "Failed to create database ", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(PerformanceStatsSchema.SQL_DROP_TABLE);
			onCreate(db);
		} catch (Exception e) {
			Log.d("SQL", "Failed to upgrade database ", e);
		}
	}

}
