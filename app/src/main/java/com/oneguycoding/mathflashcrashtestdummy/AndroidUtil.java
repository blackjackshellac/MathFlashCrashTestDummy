package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.os.Environment;
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

	/**
	 * Show toast popup for duration Toast.LENGTH_LONG
	 *
	 * @param activity - activity
	 * @param s - message
	 */
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

	/** Checks if external storage is available for read and write */
	@SuppressWarnings("unused")
	static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/** Checks if external storage is available to at least read */
	@SuppressWarnings("unused")
	static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
	}
}
