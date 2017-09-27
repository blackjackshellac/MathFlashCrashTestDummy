package com.oneguycoding.mathflashcrashtestdummy;

import android.provider.BaseColumns;

/**
 * Created by steeve on 27/09/17.
 *
 *
 CREATE TABLE "perf_stats" (
 `name`	TEXT NOT NULL,
 `op`	TEXT CHECK(op in ( '+' , '-' , '*' , '/' )),
 `runtime`	INTEGER,
 `num`	INTEGER,
 `correct`	INTEGER,
 `wrong`	INTEGER,
 `id`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE
 )
 */

public class PerformanceStatsSchema {
	// prevent instantiation
	private PerformanceStatsSchema() {}

	public static class StatsSchema implements BaseColumns {
		public static final String TABLE_NAME = "perf_stats";
		public static final String COL_NAME_NAME = "name";
		public static final String COL_NAME_OPERATION = "op";
		public static final String COL_NAME_RUNTIME = "runtime";
		public static final String COL_NAME_DURATION = "duration";
		public static final String COL_NAME_NUM = "num";
		public static final String COL_NAME_CORRECT = "correct";
		public static final String COL_NAME_WRONG = "wrong";
		public static final String COL_NAME_ID = "id";
		public static final String CHECK_OP = String.format("CHECK(%s in (%s))", COL_NAME_OPERATION, Operation.CHARS_LIST);
	}

	public static String SQL_CREATE_TABLE = String.format(
			"CREATE TABLE \"%s\" (\n" +
			" `%s`\tTEXT NOT NULL,\n" +
			" `%s`\tTEXT %s,\n" +
			" `%s`\tINTEGER,\n" +   // runtime
			" `%s`\tINTEGER,\n" +   // duration
			" `%s`\tINTEGER,\n" +
			" `%s`\tINTEGER,\n" +
			" `%s`\tINTEGER,\n" +
			" `%s`\tINTEGER,\n" +
			" `%s`\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE\n" +
			" )",
				StatsSchema.TABLE_NAME,
				StatsSchema.COL_NAME_NAME,
				StatsSchema.COL_NAME_OPERATION,	StatsSchema.CHECK_OP,
				StatsSchema.COL_NAME_RUNTIME,
				StatsSchema.COL_NAME_DURATION,
				StatsSchema.COL_NAME_NUM,
				StatsSchema.COL_NAME_CORRECT,
				StatsSchema.COL_NAME_WRONG,
				StatsSchema.COL_NAME_ID);

	public static String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + StatsSchema.TABLE_NAME;
}
