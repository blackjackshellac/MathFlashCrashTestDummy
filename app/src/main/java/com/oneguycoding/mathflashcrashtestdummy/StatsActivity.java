package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class StatsActivity extends AppCompatActivity {
	private UserDataMap userDataMap;
	private UserData userData;
	private UserResults results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		// to force LANDSCAPE
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		userDataMap = (UserDataMap) b.getSerializable(MainActivity.EXTRA_USERDATA);
		userData = userDataMap.getUserData();
		results = userData.results;

		TableLayout statsTable = (TableLayout) findViewById(R.id.statsTable);
		TableRow statsRowHeader = (TableRow) findViewById(R.id.statsRowHeader);
		int rows = statsRowHeader.getChildCount();
		for (int i = 0; i < rows; i++) {
			TextView tv = (TextView) statsRowHeader.getChildAt(i);
			tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.LEFT : Gravity.RIGHT));
		}

		ArrayList<UserResults.SqlResult> stats = results.getStats();
		if (stats != null) {
			int[] indices={0,1,2,6,3};

			Iterator<UserResults.SqlResult> it = stats.iterator();
			while (it.hasNext()) {
				UserResults.SqlResult result = it.next();
				String[] columns = result.getCols(indices);
				statsTable.addView(addRowStats(columns));
			}

			String[] subheader={"Averages","","",""};
			statsTable.addView(addRowStats(subheader));

			String[] averages = UserResults.getStatsAverages(stats);
			statsTable.addView(addRowStats(averages));
		}

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
			tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.LEFT : Gravity.RIGHT));
			tr.addView(tv, i);
		}

		tr.setLayoutParams(lp);

		return tr;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();

		Bundle b = new Bundle();
		b.putSerializable(MainActivity.EXTRA_USERDATA, userDataMap);
		intent.putExtras(b);

		setResult(RESULT_OK, intent);

		finish();

		super.onBackPressed();
		return;
	}


}