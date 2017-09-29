package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		userDataMap = (UserDataMap) b.getSerializable(MainActivity.EXTRA_USERDATA);
		userData = userDataMap.getUserData();
		results = userData.results;

		TableLayout statsTable = (TableLayout) findViewById(R.id.statsTable);
		TableRow statsRowHeader = (TableRow) findViewById(R.id.statsRowHeader);
		int rows = statsRowHeader.getChildCount();
		for (int i = 0; i < rows; i++) {
			TextView child = (TextView) statsRowHeader.getChildAt(i);
			child.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		}

		ArrayList<UserResults.SqlResult> stats = results.getStats();
		if (stats != null) {
			Iterator<UserResults.SqlResult> it = stats.iterator();
			while (it.hasNext()) {
				TableRow tr = new TableRow(this);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

				tr.setLayoutParams(lp);

				UserResults.SqlResult result = it.next();
				for (int i = 0; i < 4; i++) {
					TableRow.LayoutParams tvlp;
					tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

					TextView tv = new TextView(this);
					tv.setLayoutParams(tvlp);
					tv.setText(result.getCol(i));
					tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.LEFT : Gravity.RIGHT));
					tr.addView(tv, i);
				}
				statsTable.addView(tr);
			}
			TableRow tr = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

			tr.setLayoutParams(lp);
			for (int i = 0; i < 4; i++) {
				TableRow.LayoutParams tvlp;
				tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

				TextView tv = new TextView(this);
				tv.setLayoutParams(tvlp);
				tv.setText("");
				tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.LEFT : Gravity.RIGHT));
				tr.addView(tv, i);
			}
			statsTable.addView(tr);

			tr = new TableRow(this);
			lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

			tr.setLayoutParams(lp);
			ArrayList<String> averages = UserResults.getStatsAverages(stats);
			for (int i = 0; i < 4; i++) {
				TableRow.LayoutParams tvlp;
				tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

				TextView tv = new TextView(this);
				tv.setLayoutParams(tvlp);
				tv.setText(averages.get(i));
				tv.setGravity(Gravity.CENTER_VERTICAL | (i==0 ? Gravity.LEFT : Gravity.RIGHT));
				tr.addView(tv, i);
			}
			statsTable.addView(tr);

		}
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
