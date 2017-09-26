package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by steeve on 12/09/17.
 */

public class AndroidUtil {

	public static String getEditTextString(Activity activity, int id) {
		EditText editText = (EditText) activity.findViewById(id);
		return editText.getText().toString();
	}

	public static void setEditTextString(Activity activity, int id, String text) {
		EditText editText = (EditText) activity.findViewById(id);
		editText.setText(text);
	}

	public static void showToast(Activity activity, String s) {
		showToast(activity, s, Toast.LENGTH_LONG);
	}

	public static void showToast(Activity activity, String s, int duration) {
		Toast toast = Toast.makeText(activity, s, duration);
		toast.show();
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
