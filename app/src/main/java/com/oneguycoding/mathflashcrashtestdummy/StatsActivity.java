package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class StatsActivity extends AppCompatActivity {

	private static final UserResults.SqlResult.SqlColumn[] RESULT_COLUMNS ={
			UserResults.SqlResult.SqlColumn.RUNTIME,
			UserResults.SqlResult.SqlColumn.OPERATION,
			UserResults.SqlResult.SqlColumn.CORRECT,
			UserResults.SqlResult.SqlColumn.NUM,
			UserResults.SqlResult.SqlColumn.PERCENT,
			UserResults.SqlResult.SqlColumn.RATE
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		// to force LANDSCAPE
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		UserDataMap userDataMap = (UserDataMap) b.getSerializable(MainActivity.EXTRA_USERDATA);
		if (userDataMap == null) throw new IllegalArgumentException("UserDataMap should never be null here");

		UserData userData = userDataMap.getUserData();
		Set<Operation> ops = userData.ops.opNumbers.keySet();

		TableLayout statsTable = (TableLayout) findViewById(R.id.statsTable);
		TableRow statsRowHeader = (TableRow) findViewById(R.id.statsRowHeader);
		int rows = statsRowHeader.getChildCount();
		for (int i = 0; i < rows; i++) {
			TextView tv = (TextView) statsRowHeader.getChildAt(i);
			tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.START : Gravity.END));
		}

		// save stats for averaging later
		ArrayList<UserResults.SqlResult> stats = new ArrayList<>();

		PerformanceStatsHelper perfStatsHelper = new PerformanceStatsHelper(this);
		SQLiteDatabase perfStatsDb = perfStatsHelper.getReadableDatabase();
		Cursor cursor = UserResults.getStatsQueryCursor(perfStatsDb, ops, userDataMap.getCurUser());
		while (cursor.moveToNext()) {
			UserResults.SqlResult result = new UserResults.SqlResult(cursor);
			stats.add(result);
			String[] values = result.getCols(RESULT_COLUMNS);
			statsTable.addView(addRowStats(values));
		}
		cursor.close();

		String[] subheader={"Averages","","",""};
		statsTable.addView(addRowStats(subheader));

		// calculate averages on the fly
		String[] averages = UserResults.getStatsAverages(stats, RESULT_COLUMNS);
		statsTable.addView(addRowStats(averages));
	}

	public TableRow addRowStats(String [] values) {
		TableRow tr = new TableRow(this);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
		for (int i = 0; i < values.length; i++) {
			TableRow.LayoutParams tvlp;
			tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

			TextView tv = new TextView(this);
			tv.setLayoutParams(tvlp);
			tv.setText(values[i]);
			tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.START : Gravity.END));
			tr.addView(tv, i);
		}

		tr.setLayoutParams(lp);

		return tr;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_cancel:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();

/*
		// don't need to return anything
		Bundle b = new Bundle();
		b.putSerializable(MainActivity.EXTRA_USERDATA, null);
		intent.putExtras(b);
*/

		setResult(RESULT_OK, intent);

		finish();

		super.onBackPressed();
	}


}
