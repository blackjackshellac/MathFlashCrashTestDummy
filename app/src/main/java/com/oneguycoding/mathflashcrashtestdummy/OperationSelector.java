package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class OperationSelector extends AppCompatActivity {
	private UserDataMap userDataMap;
	private UserData userData;
	private UserResults results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation_selector);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		userDataMap = (UserDataMap) b.getSerializable(MainActivity.EXTRA_USERDATA);
		userData = userDataMap.getUserData();
		results = userData.results;

		setupOperations();
	}

	void setupOperations() {
		int id;

		CheckBox cb = (CheckBox) findViewById(R.id.checkMultiple);
		OperationsClass ops = userData.ops;
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
					topRange = userData.ops.getOp(op).getTopRange();
				}
				if (bottomRange == null) {
					bottomRange = userData.ops.getOp(op).getBottomRange();
				}
			}
		}

		// none of the operations are checked
		if (topRange == null || bottomRange == null) {
			cb = (CheckBox) findViewById(R.id.checkPlus);
			cb.setChecked(true);
			topRange = userData.ops.getOp(Operation.PLUS).getTopRange();
			bottomRange = userData.ops.getOp(Operation.PLUS).getBottomRange();
		}

		AndroidUtil.setEditTextString(this, R.id.numTopMax, topRange.l2.toString());
		AndroidUtil.setEditTextString(this, R.id.numBotMax, bottomRange.l2.toString());
		AndroidUtil.setEditTextString(this, R.id.numTestMax, Integer.toString(results.getNum()));
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

	private void updateRanges() {
		TextView textView;
		Long max;
		OperationsClass ops = userData.ops;
		try {
			textView = (TextView) findViewById(R.id.numTopMax);
			max = Long.parseLong(textView.getText().toString());
			ops.updateTop(null, max);
		} catch(NumberFormatException e) {
			// ignore
		}

		try {
			textView = (TextView) findViewById(R.id.numBotMax);
			max = Long.parseLong(textView.getText().toString());
			ops.updateBottom(null, max);
		} catch(NumberFormatException e) {
			// ignore
		}

		try {
			textView = (TextView) findViewById(R.id.numTestMax);
			int num = Integer.parseInt(textView.getText().toString());
			if (results.getNum() != num && num >= 0) {
				results.reset(num);
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	@Override
	public void onBackPressed() {

		updateRanges();

		Intent intent = new Intent();

		Bundle b = new Bundle();
		b.putSerializable(MainActivity.EXTRA_USERDATA, userDataMap);
		intent.putExtras(b);

		setResult(RESULT_OK, intent);

		finish();

		super.onBackPressed();
		return;
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		//boolean allowMultiple = ((CheckBox) view).isChecked();

		OperationsClass ops = userData.ops;
		Operation op = null;
		// Check which checkbox was clicked

		CheckBox cb = (CheckBox) view;
		int id = cb.getId();
		boolean checked = cb.isChecked();

		switch(id) {
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
			ops.setOperation(op, checked);
		}
		setupOperations();

	}

}
