package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CreateUserActivity extends AppCompatActivity {

	private UserData userData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);

		Intent data = getIntent();
		userData = (UserData) data.getSerializableExtra(MainActivity.EXTRA_USERDATA);

		ActionBar ab = getSupportActionBar();
		ab.setTitle(R.string.menu_user_create);

		NumberOperation nop = userData.operationData.getOp(Operation.PLUS);

		TextView hinter = (TextView) findViewById(R.id.numTopMax);
		hinter.setHint(getString(R.string.hint_top_max, nop.lp1.l2));
		hinter = (TextView) findViewById(R.id.numBotMax);
		hinter.setHint((getString(R.string.hint_bot_max, nop.lp2.l2)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_cancel:
				AndroidUtil.clearEditTextString(this, R.id.edit_user_name);
				AndroidUtil.clearEditTextString(this, R.id.edit_user_email);
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		userData.setName(AndroidUtil.getEditTextString(this, R.id.edit_user_name));
		userData.setEmail(AndroidUtil.getEditTextString(this, R.id.edit_user_email));

		Intent intent = new Intent();
		intent.putExtra(MainActivity.EXTRA_USERDATA, userData);
		setResult(RESULT_OK, intent);

		finish();

		super.onBackPressed();
		return;
	}


}
