package com.oneguycoding.mathflashcrashtestdummy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * Container for storing user results for a single operation
 *
 * Created by steeve on 13/09/17.
 *
 */

class UserResults implements Serializable {
	private static final int DEFAULT_NUM = 50;

	private int num;
	private int nCorrect;
	private int nWrong;
	private long runtime;
	private long duration;
	private Map<Operation, Stack<LongPair>> retryMap;

	private static final SparseIntArray limitCounts;
	private static final SparseIntArray MAX_PERCENTAGE;

	static {
		limitCounts = new SparseIntArray();
		limitCounts.put(0, 0);
		limitCounts.put(1, 0);
		limitCounts.put(10, 0);
		limitCounts.put(11, 0);

		MAX_PERCENTAGE = new SparseIntArray();
		MAX_PERCENTAGE.put(0, 2);
		MAX_PERCENTAGE.put(1, 2);
		MAX_PERCENTAGE.put(10, 2);
		MAX_PERCENTAGE.put(11, 2);
	}

	private ArrayList<SqlResult> stats;

	/**
	 * Create new UserResults object
	 *
	 * @param num number of operations to test, default is DEFAULT_NUM
	 */
	UserResults(int num) {
		setNum(num);
		resetCounters();
		retryMap = new HashMap<>();
		stats = new ArrayList<>();
	}

	private void startTimer() {
		runtime = AndroidUtil.now_secs();
		duration = runtime;
	}

	private void resetCounters() {
		nCorrect = 0;
		nWrong = 0;
		for (int ikey = 0; ikey < 4; ikey++) {
			limitCounts.put(limitCounts.keyAt(ikey), 0);
		}
		startTimer();
	}

	private void setRetry(Operation op, LongPair nums) {
		Stack<LongPair> retryStack = retryMap.get(op);
		if (retryStack == null) {
			retryStack = new Stack<>();
			retryMap.put(op, retryStack);
		}
		retryStack.push(nums);
	}

	void correct() {
		if (getNumAnswered() == 0) {
			startTimer();
		}
		nCorrect += 1;
	}

	void wrong(Operation op, LongPair nums) {
		nWrong += 1;
		setRetry(op, nums);
	}

	int getnCorrect() {
		return nCorrect;
	}

	private int getnWrong() {
		return nWrong;
	}

	private void setNum(int num) {
		this.num = num <= 0 ? DEFAULT_NUM : num;
	}

	/**
	 * Reset results with the given number of trials, and the retryMap for all ops
	 *
	 * @param num - if num > 0 reset its value, otherwise keep the current value
	 */
	void reset(int num) {
		if (num > 0) {
			setNum(num);
		}
		resetCounters();
		for (Operation op : retryMap.keySet()) {
			Stack<LongPair> stack = retryMap.get(op);
			stack.empty();
		}
	}

	int getNum() {
		return num;
	}

	float getPercentage() {
		return UserResults.getPercentage(num, nCorrect);
	}

	/**
	 * Calculate the percentage given the number answered and the number correct
	 *
	 * @param num_answered - number answered
	 * @param nCorrect - number correct
	 *
	 * @return percentage as a float
	 *
	 */
	private static float getPercentage(int num_answered, int nCorrect) {
		if (num_answered == 0) {
			throw new IllegalArgumentException("num_answered should never be zero");
		}
		float percentage_correct;
		try {
			percentage_correct = ((float) nCorrect / num_answered) * 100;
		} catch (Exception e) {
			Log.d("UserResults", "Failed to calculate percentage", e);
			percentage_correct = 0.0f;
		}
		return percentage_correct;
	}

	int getNumAnswered() {
		return nCorrect + nWrong;
	}

	int getRemaining() {
		return num - getNumAnswered();
	}

	boolean testDone() {
		return getNumAnswered() >= num;
	}

	private float limitPercent(int cnt) {
		if (cnt > num) {
			Log.e("Limit", "count greater than num!");
			return 100.0f;
		}
		return 100.0f*cnt / num;
	}

	private boolean isLimited(float percent, int maxPercentage) {
		return percent >= maxPercentage;
	}

	private boolean limitValue(int n, LongPair numberPair) {
		if (numberPair.l1 == n || numberPair.l2 == n) {
			int maxPercent = MAX_PERCENTAGE.get(n);
			int cnt = limitCounts.get(n);
			// increment limit counter
			limitCounts.put(n, cnt + 1);
			float percent = limitPercent(cnt);
			boolean limited = isLimited(percent, maxPercent);
			Log.d("Limit", AndroidUtil.stringFormatter("%d (%s): %d of %d (%.1f%%), limit is %d%%", n, (limited ? "true" : "false"), cnt, num, percent, maxPercent));
			if (isLimited(percent, maxPercent)) {
				return true;
			}
		}
		return false;
	}

	private boolean limitZerosAndOnes(LongPair numberPair) {
		for (int i = 0; i <= 1; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}

	private boolean limitTensAndElevens(LongPair numberPair) {
		for (int i = 10; i <= 11; i++) {
			if (limitValue(i, numberPair)) {
				return true;
			}
		}
		return false;
	}

	boolean limitOperationNumbers(Operation op, LongPair numberPair) {
		if (op == Operation.MULTIPLY || op == Operation.DIVIDE) {
			if (limitTensAndElevens(numberPair)) {
				return true;
			}
		}
		return limitZerosAndOnes(numberPair);
	}



	void setStats(ArrayList<SqlResult> stats) {
		this.stats = stats;
	}

	ArrayList<SqlResult> getStats() {
		return stats;
	}

	static class SqlResult implements Serializable {
		final long id;
		final long runtime;
		final long duration;
		final int  num;
		final int correct;
		final float percentage_correct;
		final String name;

		SqlResult(Cursor cursor) {
			id =  cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_ID));
			runtime = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME));
			duration = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_DURATION));
			num = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_NUM));
			correct = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_CORRECT));
			name = cursor.getString(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME_NAME));

			percentage_correct = UserResults.getPercentage(num, correct);

			Log.d("SQL", AndroidUtil.stringFormatter("id,name,num,correct,percent=%d,%s,%d,%d,%.2f", id, name, num, correct, percentage_correct));
		}

		enum SqlColumn {
			RUNTIME,
			NUM,
			CORRECT,
			PERCENT,
			ID,
			NAME,
			DURATION,
			RATE
		}
		/**
		 * <br>0 - runtime</br>
		 * <br>1 - num</br>
		 * <br>2 - correct</br>
		 * <br>3 - percent</br>
		 * <br>4 - id</br>
		 * <br>5 - name</br>
		 * <br>6 - duration (secs)</br>
		 *
		 * @param c index to retrieve
		 * @return value of column as string for given index or empty string if unknown
		 */
		String getCol(SqlColumn c) {
			switch(c) {
				case RUNTIME: // runtime
					return getRuntime();
				case NUM: // num
					return getNum();
				case CORRECT: // correct
					return getCorrect();
				case PERCENT:
					return getPercentage_correct();
				case ID: // id
					return getId();
				case NAME: // name
					return getName();
				case DURATION: // duration in seconds
					return getDuration();
				case RATE: // num per second
					return getRate();
				default:
					Log.e("SQL", "Unknown column index in SqlResult.getCol()");
			}
			return "";
		}

		public String getId() {
			return ""+id;
		}

		String getRuntime() {
			return getDate(runtime);
		}

		long getSecs() {
			return duration-runtime;
		}

		String getDuration() {
			return ""+getSecs();
		}

		String getNum() {
			return ""+num;
		}

		String getCorrect() {
			return ""+correct;
		}

		String getPercentage_correct() {
			return AndroidUtil.stringFormatter("%.2f", percentage_correct);
		}

		String getName() {
			return name;
		}

		String getRate() {
			long d = getSecs();
			return AndroidUtil.stringFormatter("%.1f", ((float)num/(d == 0 ? 1 : d)));
		}

		static String getDate(long rt_secs) {
			return DateFormat.getDateTimeInstance().format(rt_secs*1000L);
		}

		String[] getCols(SqlColumn[] columns) {
			String[] values = new String[columns.length];
			for (int i=0; i < columns.length; i++) {
				values[i] = getCol(columns[i]);
			}
			return values;
		}
	}

	static String[] getStatsAverages(ArrayList<SqlResult> stats) {
		String[] averages = new String[5];
		long aveNum = 0;
		long aveCorrect = 0;
		long aveDuration = 0;
		float avePercentCorrect = 0f;
		for (int i = 0; i < stats.size(); i++) {
			SqlResult stat = stats.get(i);
			aveNum += stat.num;
			aveCorrect += stat.correct;
			aveDuration += stat.getSecs();
			avePercentCorrect += stat.percentage_correct;
		}
		aveNum /= stats.size();
		aveCorrect /= stats.size();
		avePercentCorrect /= stats.size();
		aveDuration /= stats.size();

		averages[0]=SqlResult.getDate(System.currentTimeMillis()/1000L);
		averages[1]=""+aveNum;
		averages[2]=""+aveCorrect;
		averages[3]=""+aveDuration;
		averages[4]=AndroidUtil.stringFormatter("%.2f", avePercentCorrect);

		return averages;
	}

	/**
	 *
	 * @param perfStatsDb - sqlite database handle
	 * @param op - operation
	 * @param name - name to filter
	 *
	 * @return cursor to get SqlResults, be sure to close the cursor when done
	 */
	private static Cursor getStatsQueryCursor(SQLiteDatabase perfStatsDb, Operation op, String name) {
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

		// Filter results
		// String selection = PerformanceStatsSchema.StatsSchema.COL_NAME_NAME + " = ?" + " AND " + PerformanceStatsSchema.StatsSchema.COL_NAME_OPERATION + " = ?";
		String selection = AndroidUtil.stringFormatter("%s = ? AND %s = ?", PerformanceStatsSchema.StatsSchema.COL_NAME_NAME, PerformanceStatsSchema.StatsSchema.COL_NAME_OPERATION);
		String[] selectionArgs = { name, op.toChar() };

		// How you want the results sorted in the resulting Cursor
		String sortOrder =
				PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME + " ASC";

		return perfStatsDb.query(
				PerformanceStatsSchema.StatsSchema.TABLE_NAME,                     // The table to query
				projection,                               // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);
	}

	static ArrayList<SqlResult> loadStats(SQLiteDatabase perfStatsDb, Operation op, String name) {
		// https://developer.android.com/training/basics/data-storage/databases.html

		Cursor cursor = getStatsQueryCursor(perfStatsDb, op, name);

		ArrayList<SqlResult> results = new ArrayList<>();
		while (cursor.moveToNext()) {
			SqlResult result = new SqlResult(cursor);
			results.add(result);

		}

		cursor.close();

		return results;
	}

	void saveStats(SQLiteDatabase perfStatsDb, Operation op, String name) {
		// https://developer.android.com/training/basics/data-storage/databases.html

		duration = AndroidUtil.now_secs();

		ContentValues values = new ContentValues();
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_NAME, name);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_OPERATION, op.toChar());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_RUNTIME, runtime);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_DURATION, duration);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_NUM, this.getNum());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_CORRECT, this.getnCorrect());
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME_WRONG, this.getnWrong());
		long newRowId = perfStatsDb.insert(PerformanceStatsSchema.StatsSchema.TABLE_NAME, null, values);
		Log.d("SQL", "newRowId="+newRowId);
	}

	void clearStats(SQLiteDatabase perfStatsDb, String name) {
		if (stats != null) {
			stats.clear();
		}
		if (perfStatsDb != null) {
			try {
				String sql = PerformanceStatsSchema.getSqlDeleteUserResults(name);
				perfStatsDb.execSQL(sql);
			} catch (SQLException e) {
				Log.e("SQL", "Failed to delete user results for " + name, e);
			}
		}

	}
}
