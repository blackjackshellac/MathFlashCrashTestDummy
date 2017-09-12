package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class OperationSelector extends AppCompatActivity {

	private OperationsClass ops;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation_selector);

		Intent intent = getIntent();
		//Bundle b = intent.getExtras();
		//ops = (OperationsClass) b.getSerializable(MainActivity.EXTRA_OPS);
		ops = (OperationsClass) intent.getSerializableExtra(MainActivity.EXTRA_OPS);
		setupOperations();
	}

	void setupOperations() {
		int id;

		CheckBox cb = (CheckBox) findViewById(R.id.checkMultiple);
		cb.setChecked(ops.isAllowMultiple());

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
			cb.setChecked(ops.isSet(op));
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
		intent.putExtra(MainActivity.EXTRA_OPS, ops);
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
				op = null;
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
