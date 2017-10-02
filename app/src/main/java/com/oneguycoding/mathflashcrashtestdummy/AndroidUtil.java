package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.IllegalFormatConversionException;

/**
 *
 * AndroidUtil is just a bunch of helper utilities
 *
 * Created by steeve on 12/09/17.
 */

class AndroidUtil {

	static String getEditTextString(Activity activity, int id) {
		EditText editText = (EditText) activity.findViewById(id);
		return editText.getText().toString();
	}

	static void setEditTextString(Activity activity, int id, String text) {
		EditText editText = (EditText) activity.findViewById(id);
		editText.setText(text);
	}

	static void showToast(Activity activity, String s) {
		showToast(activity, s, Toast.LENGTH_LONG);
	}

	static void showToast(Activity activity, String s, int duration) {
		Toast toast = Toast.makeText(activity, s, duration);
		toast.show();
	}

	static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	static String stringFormatter(String format, Object... arguments) {
		try {
			return String.format(format, arguments);
		} catch (IllegalFormatConversionException e) {
			Log.e("AndroidUtil", "Failed to convert string: ", e);
			throw e;
		}
	}

	static long now_secs() {
		return System.currentTimeMillis()/1000L;
	}
}
