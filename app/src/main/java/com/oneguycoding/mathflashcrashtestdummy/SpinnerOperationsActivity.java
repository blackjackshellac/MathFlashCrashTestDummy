package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by steeve on 08/09/17.
 */

public class SpinnerOperationsActivity extends Activity implements AdapterView.OnItemSelectedListener {
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		Log.i("spinner", ""+i);
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}
}
