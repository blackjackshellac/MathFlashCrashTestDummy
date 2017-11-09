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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/**
	 * Create new UserResults object
	 *
	 * @param num number of operations to test, default is DEFAULT_NUM
	 */
	UserResults(int num) {
		setNum(num);
		resetCounters();
		retryMap = new HashMap<>();
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

	/**
	 * Reset results, keep current value of num
	 */
	void reset() {
		reset(0);
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

	boolean noneAnswered() {
		return getNumAnswered() == 0;
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

	static class SqlResult implements Serializable {
		final long id;
		final long runtime;
		final long duration;
		final int  num;
		final int correct;
		final float percentage_correct;
		final String name;
		final String operation;

		SqlResult(Cursor cursor) {
			id =  cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_ID));
			runtime = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_RUNTIME));
			duration = cursor.getLong(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_DURATION));
			num = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NUM));
			correct = cursor.getInt(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_CORRECT));
			name = cursor.getString(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_NAME));
			operation = cursor.getString(cursor.getColumnIndexOrThrow(PerformanceStatsSchema.StatsSchema.COL_OPERATION));

			percentage_correct = UserResults.getPercentage(num, correct);

			Log.d("SQL", AndroidUtil.stringFormatter("id,name,num,correct,percent=%d,%s,%d,%d,%.2f", id, name, num, correct, percentage_correct));
		}

		SqlResult(Map<String,String> map) {
			id = Long.parseLong(map.get(PerformanceStatsSchema.StatsSchema.COL_ID));
			runtime = Long.parseLong(map.get(PerformanceStatsSchema.StatsSchema.COL_RUNTIME));
			duration = Long.parseLong(map.get(PerformanceStatsSchema.StatsSchema.COL_DURATION));
			num = Integer.parseInt(map.get(PerformanceStatsSchema.StatsSchema.COL_NUM));
			correct = Integer.parseInt(map.get(PerformanceStatsSchema.StatsSchema.COL_CORRECT));
			name = map.get(PerformanceStatsSchema.StatsSchema.COL_NAME);
			operation = map.get(PerformanceStatsSchema.StatsSchema.COL_OPERATION);

			percentage_correct = UserResults.getPercentage(num, correct);
		}

		static final String  COMMA_ESCAPE = "%%COMMA%%";
		static final String  COMMA_REGEX   = "[,]";
		static final Pattern COMMA_PATTERN = Pattern.compile(COMMA_REGEX);

		String dump_csv() {
			StringBuilder csv=new StringBuilder();
			String[] columns = getRawCols();
			for (String column : columns) {
				Matcher m = COMMA_PATTERN.matcher(column);
				column = m.replaceAll(COMMA_ESCAPE);
				csv.append(column).append(",");
			}
			if (csv.length() > 0) {
				csv.deleteCharAt(csv.length()-1);
			}
			return csv.toString();
		}

		/**
		 * convert this SqlResult into a map, ignore values that are computed (percent, etc)
		 *
		 * @return map holding the result
		 */
		public Map<String, String> toMap() {
			Map<String, String> map = new HashMap<>();

			SqlColumn[] raw_columns = SqlColumn.raw_values();
			for (SqlColumn column : raw_columns) {
				String raw_name = column.raw_name();
				map.put(raw_name, getRawCol(column));
			}

			return map;
		}

		public ContentValues getContentValues() {
			ContentValues values = new ContentValues();

			values.put(PerformanceStatsSchema.StatsSchema.COL_NAME, name);
			values.put(PerformanceStatsSchema.StatsSchema.COL_OPERATION, operation);
			values.put(PerformanceStatsSchema.StatsSchema.COL_RUNTIME, runtime);
			values.put(PerformanceStatsSchema.StatsSchema.COL_DURATION, duration);
			values.put(PerformanceStatsSchema.StatsSchema.COL_NUM, num);
			values.put(PerformanceStatsSchema.StatsSchema.COL_CORRECT, correct);
			values.put(PerformanceStatsSchema.StatsSchema.COL_WRONG, num-correct);

			return values;
		}

		/*
			id
			runtime
			duration
			num
			correct
			name
			operation
		 */
		enum SqlColumn {
			RUNTIME     (true),
			NUM         (true),
			CORRECT     (true),
			PERCENT     (false),
			ID          (true),
			NAME        (true),
			DURATION    (true),
			RATE        (false),
			OPERATION   (true);

			final boolean raw;

			SqlColumn(boolean raw) {
				this.raw = raw;
			}

			public static SqlColumn[] raw_values() {
				List<SqlColumn> columns = new ArrayList<>();
				for (SqlColumn column : values()) {
					if (column.raw) {
						columns.add(column);
					}
				}
				SqlColumn[] array = new SqlColumn[columns.size()];
				return columns.toArray(array); // fill the array
			}

			public String raw_name() {
				if (raw) {
					return this.name().toLowerCase();
				}
				return null;
			}

		}

		/**
		 *
		 * @return
		 */
		String[] getRawCols() {
			SqlColumn[] columns = SqlColumn.values();
			String[] values = new String[columns.length];
			for (int i=0; i < columns.length; i++) {
				String value = getCol(columns[i]);
				if (value == null) {
					// ignore this column
					continue;
				}
				values[i] = value;
			}
			return values;
		}

		/**
		 * See PerformanceStatsSchema.StatsSchema for columns
		 *
		 * @param c - SqlColumn to get raw value
		 *
		 * @return value of raw column as string or null if not a valid raw column
 		 */
		String getRawCol(SqlColumn c) {
			switch(c) {
				case RUNTIME: // runtime
					return ""+runtime;
				case NUM: // num
					return ""+num;
				case CORRECT: // correct
					return ""+correct;
				case PERCENT:
					// this column is not raw
					break;
				case ID: // id
					return ""+id;
				case NAME: // name
					return ""+name;
				case DURATION: // duration in seconds
					return ""+duration;
				case RATE: // num per second
					// this column is not raw
					break;
				case OPERATION:
					return ""+operation;
				default:
					Log.e("SQL", "Unknown column index in SqlResult.getCol()");
			}
			throw new RuntimeException("Not a supported raw column: "+c.toString());
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
				case OPERATION:
					return getOperation();
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

		String getOperation() {
			return operation;
		}

		static String getDate(long rt_secs) {
			return DateFormat.getDateTimeInstance().format(rt_secs*1000L);
		}

		String[] getCols() {
			return getCols(null);
		}

		String[] getCols(SqlColumn[] columns) {
			if (columns==null) {
				columns = SqlColumn.values();
			}
			String[] values = new String[columns.length];
			for (int i=0; i < columns.length; i++) {
				values[i] = getCol(columns[i]);
			}
			return values;
		}
	}

	static String[] getStatsAverages(ArrayList<SqlResult> stats, SqlResult.SqlColumn[] resultColumns) {
		String[] averages = new String[6];
		String aveOp = ".";
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
		// avoid divide by 0
		if (stats.size() > 0) {
			aveNum /= stats.size();
			aveCorrect /= stats.size();
			avePercentCorrect /= stats.size();
			aveDuration /= stats.size();
		}

		for (int i=0; i < resultColumns.length; i++) {
			SqlResult.SqlColumn col = resultColumns[i];
			String ave = "";
			switch (col) {
				case RUNTIME:
					ave = SqlResult.getDate(System.currentTimeMillis()/1000L);
					break;
				case OPERATION:
					ave = aveOp;
					break;
				case NUM:
					ave = ""+aveNum;
					break;
				case CORRECT:
					ave = ""+aveCorrect;
					break;
				case PERCENT:
					ave = AndroidUtil.stringFormatter("%.2f", avePercentCorrect);
					break;
				case RATE:
					ave = ""+aveDuration;
					break;
			}
			averages[i] = ave;
		}
/*
		averages[0]=SqlResult.getDate(System.currentTimeMillis()/1000L);
		averages[1]=""+aveOp;
		averages[2]=""+aveNum;
		averages[3]=""+aveCorrect;
		averages[4]=AndroidUtil.stringFormatter("%.2f", avePercentCorrect);
		averages[5]=""+aveDuration;
*/

		return averages;
	}

	/**
	 *
	 * @param perfStatsDb - sqlite database handle
	 * @param ops - set of operations to query
	 * @param name - name to filter
	 *
	 * @return cursor to get SqlResults, <b>be sure to close the cursor when done</b>
	 */
	static Cursor getStatsQueryCursor(SQLiteDatabase perfStatsDb, Set<Operation> ops, String name) {
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				PerformanceStatsSchema.StatsSchema.COL_ID,
				PerformanceStatsSchema.StatsSchema.COL_RUNTIME,
				PerformanceStatsSchema.StatsSchema.COL_OPERATION,
				PerformanceStatsSchema.StatsSchema.COL_DURATION,
				PerformanceStatsSchema.StatsSchema.COL_NAME,
				PerformanceStatsSchema.StatsSchema.COL_NUM,
				PerformanceStatsSchema.StatsSchema.COL_CORRECT
		};

		/* if ops is empty, get stats for all operations */
		if (ops.isEmpty()) {
			ops = new HashSet<Operation>(Arrays.asList(Operation.values()));
			Log.d("SQL", "dump all operations: "+ops.toString());
		}
		// Filter results
		// String selection = PerformanceStatsSchema.StatsSchema.COL_NAME + " = ?" + " AND " + PerformanceStatsSchema.StatsSchema.COL_OPERATION + " = ?";
		//String selection = AndroidUtil.stringFormatter("%s = ? AND %s = ?", PerformanceStatsSchema.StatsSchema.COL_NAME, PerformanceStatsSchema.StatsSchema.COL_OPERATION);
		StringBuilder sb = new StringBuilder();
		sb.append(PerformanceStatsSchema.StatsSchema.COL_NAME).append(" = ? AND ( ");
		String[] selectionArgs = new String[ops.size()+1];
		selectionArgs[0] = name;
		int i = 1;

		// name = ? AND (op = ? OR op = ? )
		for (Operation op : ops) {
			selectionArgs[i] = op.toChar();
			sb.append(PerformanceStatsSchema.StatsSchema.COL_OPERATION).append(" = ? ");
			if (i < ops.size()) {
				sb.append(" OR ");
			} else {
				sb.append(")");
			}
			i++;
		}

		String selection = sb.toString();

		// How you want the results sorted in the resulting Cursor
		String sortOrder =
				PerformanceStatsSchema.StatsSchema.COL_RUNTIME + " ASC";

		return perfStatsDb.query(
				PerformanceStatsSchema.StatsSchema.TABLE_NAME,  // The table to query
				projection,         // The columns to return
				selection,          // The columns for the WHERE clause
				selectionArgs,      // The values for the WHERE clause
				null,       // don't group the rows
				null,        // don't filter by row groups
				sortOrder           // The sort order
		);
	}

	static Cursor getStatsQuery(SQLiteDatabase perfStatsDb, String table_name, String whereClause, String[] whereArgs) {
		return perfStatsDb.query(
				table_name,  // The table to query
				null,       // The columns to return
				whereClause,        // The columns for the WHERE clause
				whereArgs,          // The values for the WHERE clause
				null,       // don't group the rows
				null,        // don't filter by row groups
				null        // The sort order
		);	}

	@SuppressWarnings("unused")
	static ArrayList<SqlResult> loadStats(Cursor cursor) {
		// https://developer.android.com/training/basics/data-storage/databases.html

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
		values.put(PerformanceStatsSchema.StatsSchema.COL_NAME, name);
		values.put(PerformanceStatsSchema.StatsSchema.COL_OPERATION, op.toChar());
		values.put(PerformanceStatsSchema.StatsSchema.COL_RUNTIME, runtime);
		values.put(PerformanceStatsSchema.StatsSchema.COL_DURATION, duration);
		values.put(PerformanceStatsSchema.StatsSchema.COL_NUM, this.getNum());
		values.put(PerformanceStatsSchema.StatsSchema.COL_CORRECT, this.getnCorrect());
		values.put(PerformanceStatsSchema.StatsSchema.COL_WRONG, this.getnWrong());
		long newRowId = perfStatsDb.insert(PerformanceStatsSchema.StatsSchema.TABLE_NAME, null, values);
		Log.d("SQL", "newRowId="+newRowId);
	}

	static void saveStats(SQLiteDatabase perfStatsDb, Map<String,String> map) {
		SqlResult sqlResult = new SqlResult(map);
		ContentValues values = sqlResult.getContentValues();

		long newRowId = perfStatsDb.insert(PerformanceStatsSchema.StatsSchema.TABLE_NAME, null, values);
		Log.d("SQL", "newRowId="+newRowId);
	}

	void clearStats(SQLiteDatabase perfStatsDb, String name) {
		if (perfStatsDb != null) {
			try {
				String sql = PerformanceStatsSchema.getSqlDeleteUserResults(name);
				perfStatsDb.execSQL(sql);
			} catch (SQLException e) {
				Log.e("SQL", "Failed to delete user results for " + name, e);
			}
		}
	}

	static void mergeStats(SQLiteDatabase perfStatsDb, ArrayList<Map<String,String>> arrayMap) {

		for (Map<String,String> map : arrayMap) {
			String runtime = map.get(PerformanceStatsSchema.StatsSchema.COL_RUNTIME);
			if (runtime == null) {
				Log.e("SQL", "runtime entry not found in map");
				continue;
			}

			String name = map.get(PerformanceStatsSchema.StatsSchema.COL_NAME);
			if (name == null) {
				Log.e("SQL", "Name not found in map");
				continue;
			}

			// SELECT * FROM %s WHERE runtime = %s AND name = %s

			String[] whereArgs = { runtime, name };

			// select * from TABLE where runtime = +runtime.toString() and map.get(
			boolean found=false;
			Cursor cursor = UserResults.getStatsQuery(perfStatsDb,
					PerformanceStatsSchema.StatsSchema.TABLE_NAME,
					PerformanceStatsSchema.SQL_WHERE_RUNTIME_NAME,
					whereArgs);
			while (cursor.moveToNext()) {
				Log.d("SQL", "Found entry for runtime="+runtime+" name="+name);
				found=true;
				break;
			}
			cursor.close();

			if (!found) {
				Log.d("SQL", "Merge stats from map to sqlite database");
				UserResults.saveStats(perfStatsDb, map);
			}

		}
	}

}
