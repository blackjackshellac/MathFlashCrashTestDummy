package com.oneguycoding.mathflashcrashtestdummy;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * NumericKeyboard onKey listener
 *
 * Created by steeve on 31/10/17.
 */

public class NumericKeyboard implements View.OnKeyListener {
	/** map keyCodes to button ids */
	private static final SparseIntArray keyCodeIdMap;
	/** map button ids to keyCodes */
	private static final SparseIntArray idKeyCodeMap;

	static {
		keyCodeIdMap = new SparseIntArray();
		keyCodeIdMap.put(KeyEvent.KEYCODE_0, R.id.number0);
		keyCodeIdMap.put(KeyEvent.KEYCODE_1, R.id.number1);
		keyCodeIdMap.put(KeyEvent.KEYCODE_2, R.id.number2);
		keyCodeIdMap.put(KeyEvent.KEYCODE_3, R.id.number3);
		keyCodeIdMap.put(KeyEvent.KEYCODE_4, R.id.number4);
		keyCodeIdMap.put(KeyEvent.KEYCODE_5, R.id.number5);
		keyCodeIdMap.put(KeyEvent.KEYCODE_6, R.id.number6);
		keyCodeIdMap.put(KeyEvent.KEYCODE_7, R.id.number7);
		keyCodeIdMap.put(KeyEvent.KEYCODE_8, R.id.number8);
		keyCodeIdMap.put(KeyEvent.KEYCODE_9, R.id.number9);
		keyCodeIdMap.put(KeyEvent.KEYCODE_DEL, R.id.backspace);

		// invert keyCodeIdMap key,val pairs
		idKeyCodeMap = new SparseIntArray();
		for (int i=0 ; i < keyCodeIdMap.size(); i++) {
			int key = keyCodeIdMap.keyAt(i);
			int val = keyCodeIdMap.get(key);
			idKeyCodeMap.put(val, key);
		}
	}

	private final MainActivity mainActivity;
	private final EditText     editTarget;

	/**
	 * Key Listener for activity that contains numeric keyboard and edit control
	 *
	 * @param mainActivity - activity
	 * @param editTarget - edit control to forward keyboard events
	 */
	NumericKeyboard(MainActivity mainActivity, EditText editTarget) {
		this.mainActivity = mainActivity;
		this.editTarget   = editTarget;
	}

	/**
	 * Lookup button id from keyCode
	 *
	 * @param keyCode - keyCode to lookup
	 * @return return the button id, or -1 if not known/handled
	 */
	private int getButtonId(int keyCode) {
		return keyCodeIdMap.get(keyCode, -1) == -1 ? -1 : keyCodeIdMap.get(keyCode);
	}

	private int getButtonKeyCode(Button button) {
		return idKeyCodeMap.get(button.getId(), -1);
	}

	/**
	 * Respond to key down and key up and pass on result to editTarget
	 *
	 * @param view - view
	 * @param keyCode - key code
	 * @param keyEvent - key event object
	 *
	 * @return true if handled, otherwise false
	 */
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
		boolean retval = true;

		int id = getButtonId(keyCode);
		if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

			switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					Log.d("KeyEvent", "Caught key down for KeyEvent dpad left/right: "+keyCode);
					break;
				case KeyEvent.KEYCODE_ENTER:
					Log.d("KeyEvent", "Caught key down for KeyEvent ENTER");
					break;
				default:
					if (id == -1) {
						Log.d("KeyEvent", AndroidUtil.stringFormatter("Caught key down for code/ch = %d/[%s]", keyCode, (char) keyEvent.getUnicodeChar()));
						return false;
					}
					break;
			}
			if (id != -1) {
				View b = mainActivity.findViewById(id);
				if (b != null) {
					AndroidUtil.buttonPress(b);
				}
			}
			MainActivity.sendKeyEvent(editTarget, keyEvent);
		} else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
			if (id != -1) {
				View b = mainActivity.findViewById(id);
				if (b != null) {
					AndroidUtil.buttonRelease(b);
				}
			}
		} else {
			Log.d("KeyEvent", "Unhandled keyevent: "+keyEvent.toString());
			retval = false;
		}
		return retval;
	}

	void keyboardClick(View view) {
		Button b = (Button)view;
		String txt = b.getText().toString().trim();
		if (txt.isEmpty()) {
			// ignore button without txt
			return;
		}
		// AndroidUtil.showToast(this, "Someone clicked id="+ch, Toast.LENGTH_SHORT);

		int keyCode = getButtonKeyCode(b);
		if (keyCode == -1) {
				Log.e("keyboardClick", "Unknown button txt: ["+txt+"]");
				return;
		}
		KeyEvent keyEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0);
		MainActivity.sendKeyEvent(editTarget, keyEvent);
	}


}
