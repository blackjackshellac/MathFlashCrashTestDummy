package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
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

	public static void clearEditTextString(Activity activity, int id) {
		EditText editText = (EditText) activity.findViewById(id);
		editText.setText("");
	}

	public static void showToast(Activity activity, String s) {
		Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
		toast.show();
	}
}
