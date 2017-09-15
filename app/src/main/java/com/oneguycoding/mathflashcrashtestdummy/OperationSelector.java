package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class OperationSelector extends AppCompatActivity {

	private OperationsClass ops;
	private UserDataMap userDataMap;
	private UserData userData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation_selector);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		ops = (OperationsClass) b.getSerializable(MainActivity.EXTRA_OPS);
		userDataMap = (UserDataMap) b.getSerializable(MainActivity.EXTRA_USERDATA);
		userData = userDataMap.getUserData();

		setupOperations();
	}

	LongPair getMaxima(Operation op, LongPair maxima) {
		if (maxima == null) {
			NumberOperation opd = userData.operationData.getOp(op);
			maxima = opd.getBottomRange();
		}
		return maxima;
	}

	void setupOperations() {
		int id;

		CheckBox cb = (CheckBox) findViewById(R.id.checkMultiple);
		cb.setChecked(ops.isAllowMultiple());

		LongPair topRange = null;
		LongPair bottomRange = null;
		for (Operation op : Operation.values()) {
			switch (op) {
				default:
				case PLUS:
					id = R.id.checkPlus;
					break;
				case MINUS:
					id = R.id.checkMinus;
					break;
				case MULTIPLY:
					id = R.id.checkMult;
					break;
				case DIVIDE:
					id = R.id.checkDivision;
					break;
			}
			cb = (CheckBox) findViewById(id);
			boolean checked = ops.isSet(op);
			cb.setChecked(checked);
			if (checked) {
				if (topRange == null) {
					topRange = userData.operationData.getOp(op).getTopRange();
				}
				if (bottomRange == null) {
					bottomRange = userData.operationData.getOp(op).getBottomRange();
				}
			}
		}

		if (topRange != null && bottomRange != null) {
			TextView hinter = (TextView) findViewById(R.id.numTopMax);
			hinter.setHint(getString(R.string.hint_top_max, topRange.l2));
			hinter = (TextView) findViewById(R.id.numBotMax);
			hinter.setHint((getString(R.string.hint_bot_max, bottomRange.l2)));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();

		Bundle b = new Bundle();
		b.putSerializable(MainActivity.EXTRA_OPS, ops);
		b.putSerializable(MainActivity.EXTRA_USERDATA, userDataMap);
		intent.putExtras(b);

		setResult(RESULT_OK, intent);

		finish();

		super.onBackPressed();
		return;
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		Operation op = null;
		// Check which checkbox was clicked
		switch(view.getId()) {
			case R.id.checkMultiple:
				ops.setAllowMultiple(checked);
				break;
			case R.id.checkPlus:
				op = Operation.PLUS;
				break;
			case R.id.checkMinus:
				op = Operation.MINUS;
				break;
			case R.id.checkMult:
				op = Operation.MULTIPLY;
				break;
			case R.id.checkDivision:
				op = Operation.DIVIDE;
				break;
		}

		if (op != null) {
			Long max;
			NumberOperation nop = userData.operationData.getOp(op);

			TextView textView = (TextView) findViewById(R.id.numTopMax);
			max = Long.parseLong(textView.getText().toString());
			nop.updateTop(null, max);

			textView = (TextView) findViewById(R.id.numBotMax);
			max = Long.parseLong(textView.getText().toString());
			nop.updateBottom(null, max);

			ops.setOperation(op, checked);
		}
		setupOperations();

	}

}
