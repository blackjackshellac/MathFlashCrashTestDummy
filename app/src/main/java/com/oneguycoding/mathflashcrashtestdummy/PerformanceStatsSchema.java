package com.oneguycoding.mathflashcrashtestdummy;

import android.provider.BaseColumns;

/**
 *
 * Table schema column names and table creation and deletion sql strings
 *
 * Created by steeve on 27/09/17.
 * <br/>
 *  Reference: https://developer.android.com/training/basics/data-storage/databases.html
 */
class PerformanceStatsSchema {
	// prevent instantiation
	private PerformanceStatsSchema() {}

	static class StatsSchema implements BaseColumns {
		static final String TABLE_NAME = "perf_stats";
		static final String COL_NAME = "name";
		static final String COL_OPERATION = "op";
		static final String COL_RUNTIME = "runtime";
		static final String COL_DURATION = "duration";
		static final String COL_NUM = "num";
		static final String COL_CORRECT = "correct";
		static final String COL_WRONG = "wrong";
		static final String COL_ID = "id";
		static final String CHECK_OP = String.format("CHECK(%s in (%s))", COL_OPERATION, Operation.CHARS_LIST);
	}

	static final String SQL_CREATE_TABLE = String.format(
			"CREATE TABLE \"%s\" (\n" + // table name
			" `%s`\tTEXT NOT NULL,\n" + // user name
			" `%s`\tTEXT %s,\n" +       // operation with check_op
			" `%s`\tINTEGER,\n" +       // runtime
			" `%s`\tINTEGER,\n" +       // duration
			" `%s`\tINTEGER,\n" +       // num
			" `%s`\tINTEGER,\n" +       // correct
			" `%s`\tINTEGER,\n" +       // wrong
			" `%s`\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE\n" +   // id
			" )",
				StatsSchema.TABLE_NAME,
				StatsSchema.COL_NAME,
				StatsSchema.COL_OPERATION,	StatsSchema.CHECK_OP,
				StatsSchema.COL_RUNTIME,
				StatsSchema.COL_DURATION,
				StatsSchema.COL_NUM,
				StatsSchema.COL_CORRECT,
				StatsSchema.COL_WRONG,
				StatsSchema.COL_ID);

	static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + StatsSchema.TABLE_NAME;
	// DELETE FROM perf_stats WHERE name LIKE %s
	private static final String SQL_DELETE_USER_RESULTS = "DELETE FROM "+StatsSchema.TABLE_NAME+" WHERE "+StatsSchema.COL_NAME +" LIKE '%s'";

	static final String SQL_WHERE_RUNTIME_NAME = String.format(" %s = ? AND %s = ? ", StatsSchema.COL_RUNTIME, StatsSchema.COL_NAME);

	static String getSqlDeleteUserResults(String name) {
		return AndroidUtil.stringFormatter(SQL_DELETE_USER_RESULTS, name);
	}
}
