package com.oneguycoding.mathflashcrashtestdummy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by steeve on 13/09/17.
 *
 */

public class UserResults implements Serializable {
	public static final int DEFAULT_NUM = 50;
	private int num;
	private int nCorrect;
	private int nWrong;
	private Map<Operation, Stack<LongPair>> retryMap;

	private static final SparseIntArray limitMap;
	private static final SparseIntArray MAX_PERCENTAGE;

	static {
		limitMap = new SparseIntArray();
		limitMap.put(0, 0);
		limitMap.put(1, 0);
		limitMap.put(10, 0);
		limitMap.put(11, 0);

		MAX_PERCENTAGE = new SparseIntArray();
		MAX_PERCENTAGE.put(0, 2);
		MAX_PERCENTAGE.put(1, 2);
		MAX_PERCENTAGE.put(10, 2);
		MAX_PERCENTAGE.put(11, 2);
	}

	/**
	 * Create new UserResults object
	 *
	 * @param num number of operations to test, default is DEFAULT_NUM
	 */
	UserResults(int num) {
		setNum(num);
		resetCounters();
		retryMap = new HashMap<Operation, Stack<LongPair>>();
	}

	private void resetCounters() {
		nCorrect = 0;
		nWrong = 0;
		for (int ikey=0; ikey < 4; ikey++) {
			limitMap.put(limitMap.keyAt(ikey), 0);
		}
	}

	private void setRetry(Operation op, LongPair nums) {
		Stack<LongPair> retryStack = retryMap.get(op);
		if (retryStack == null) {
			retryStack = new Stack<LongPair>();
			retryMap.put(op, retryStack);
		}
		retryStack.push(nums);
	}

	public void correct() {
		nCorrect += 1;
	}

	public void wrong(Operation op, LongPair nums) {
		nWrong += 1;
		setRetry(op, nums);
	}

	public boolean hasNext(Operation op) {
		return false;
	}

	public LongPair next(Operation op) {
		Stack<LongPair> retryStack = retryMap.get(op);
		return retryStack == null ? null : retryStack.pop();
	}

	public int getnCorrect() {
		return nCorrect;
	}

	public int getnWrong() {
		return nWrong;
	}

	private void setNum(int num) {
		this.num = num <=0 ? DEFAULT_NUM : num;
	}

	/**
	 * Reset results with the given number of trials, and the retryMap for all ops
	 *
	 * @param num - if num > 0 reset its value, otherwise keep the current value
	 */
	public void reset(int num) {
		if (num > 0) {
			setNum(num);
		}
		resetCounters();
		for (Operation op : retryMap.keySet()) {
			Stack<LongPair> stack = retryMap.get(op);
			stack.empty();
		}
	}

	public int getNum() {
		return num;
	}

	public float getPercentage() {
		int na = getNumAnswered();
		return ((float)nCorrect / na) * 100;
	}

	public int getNumAnswered() {
		return nCorrect+nWrong;
	}

	public int getRemaining() {
		return num-getNumAnswered();
	}

	public boolean testDone() {
		return getNumAnswered() >= num;
	}

	private boolean isLimited(int n, int maxPercentage) {
		float percent = (float) (n*100.0/num);
		return percent >= maxPercentage;
	}

	private boolean limitValue(int n, LongPair numberPair) {
		if (numberPair.l1 == n || numberPair.l2 == n) {
			int max = MAX_PERCENTAGE.get(n);
			int val = limitMap.get(n);
			if (isLimited(val, max)) {
				return true;
			}
			limitMap.put(n, val+1);
		}
		return false;
	}

	public boolean limitZerosAndOnes(LongPair numberPair) {
		for (int i=0; i <= 1; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}

	public boolean limitElevensAndTens(LongPair numberPair) {
		for (int i=10; i <= 11; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}

	public static class SqlResult {
		public final long id;
		public final long runtime;
		public final long duration;
		public final int  num;
		public final int correct;
		public final String name;

		SqlResult(Cursor cursor) {
			id =  cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_ID));
			runtime = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME));
			duration = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_DURATION));
			num = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_NUM));
			correct = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_CORRECT));
			name = cursor.getString(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_NAME));
			Log.d("SQL", String.format("id,name,num,correct=%d,%s,%d,%d", id, name, num, correct));
		}

	}

	public static ArrayList<SqlResult> loadStats(SQLiteDatabase perfStatsDb, Operation op, String name) {
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				PerformanceStatsSchema.StatsSchema.COL_NAME_ID,
				PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME,
				PerformanceStatsSchema.StatsSchema.COL_NAME_DURATION,
				PerformanceStatsSchema.StatsSchema.COL_NAME_NAME,
				PerformanceStatsSchema.StatsSchema.COL_NAME_NUM,
				PerformanceStatsSchema.StatsSchema.COL_NAME_CORRECT
		};

		// Filter results WHERE "title" = 'My Title'
		String selection = PerformanceStatsSchema.StatsSchema.COL_NAME_NAME + " = ?";
		String[] selectionArgs = { name };

		// How you want the results sorted in the resulting Cursor
		String sortOrder =
				PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME + " ASC";

		Cursor cursor = perfStatsDb.query(
				PerformanceStatsSchema.StatsSchema.TABLE_NAME,                     // The table to query
				projection,                               // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);

		ArrayList<SqlResult> results = new ArrayList<SqlResult>();
		while (cursor.moveToNext()) {
			SqlResult result = new SqlResult(cursor);
			results.add(result);
		}
		return results;
	}

	public void saveStats(SQLiteDatabase perfStatsDb, Operation op, String name) {
		ContentValues values = new ContentValues();
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_NAME, name);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_OPERATION, op.toChar());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME, System.currentTimeMillis()/1000L);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_DURATION, 0);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_NUM, this.getNum());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_CORRECT, this.getnCorrect());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_WRONG, this.getnWrong());
		long newRowId = perfStatsDb.insert(PerformanceStatsSchema.StatsSchema.TABLE_NAME, null, values);
		Log.d("SQL", "newRowId="+newRowId);
	}
}
