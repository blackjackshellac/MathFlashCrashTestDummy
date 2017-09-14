package com.oneguycoding.mathflashcrashtestdummy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {
	public static final String EXTRA_OPS = "ops";
	public static final String EXTRA_USERDATA = "userdata";
	public static final int RESULT_OPS = 100;
	public static final int RESULT_USERDATA = 101;

	private static final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    private NumberOperation numberOperation;
	private OperationsClass ops = new OperationsClass();
    private LongPair  numberPair;
	private TextView num1, num2, num3;
	private TextView message;
    private ImageView response;
	private ProgressBar progressBar;

	private UserDataMap userDataMap;
	//private UserData userData;
	//private String curUser;
	private UserResults userResults;
	private String jsonFilename;

	public MainActivity() {
		jsonFilename = "MathFlashCrashTestDummy.json";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (userDataMap == null) {
			userDataMap = new UserDataMap((String) getText(R.string.user_default));
		}
		if (userDataMap.isEmpty()) {
			UserDataMap udm = UserDataMap.loadJson(this, jsonFilename);
			if (udm == null) {
				File file = new File(getFilesDir(), jsonFilename);
				if (file != null) {
					file.delete();
				}
				// first time invocation
				userDataMap.createNewUser((String) getText(R.string.user_default), "");
				userDataMap.saveJson(this, jsonFilename);
			} else {
				userDataMap = udm;
			}
		}

		num1 = (TextView) findViewById(R.id.number1);
		num2 = (TextView) findViewById(R.id.number2);
		num3 = (TextView) findViewById(R.id.number3);
		message = (TextView) findViewById(R.id.text_message);
		response = (ImageView) findViewById(R.id.image_response);

		num1.setEnabled(false);
		num2.setEnabled(false);

		num3.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				if(  keyCode == EditorInfo.IME_ACTION_SEND ||
						(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
					sendAnswer(findViewById(R.id.sendAnswer));
					return true;
				}
				return false;
			}

		});

		// OnClickListener for the operation image (+, -, x, /)
		findViewById(R.id.imageOperation).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// open operationSelector view
					selectOperation(view);
				}
			}
		);

		userResults = new UserResults();

		setupNumbers();
		setupUser(userDataMap.getCurUser());

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setMax(50);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the main_menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		for (String name : userDataMap.users()) {
			if (name.equals(getText(R.string.user_default))) {
				continue;
			}
			boolean found = false;
			for (int i = 0; i < menu.size(); i++) {
				MenuItem item = menu.getItem(i);
				if (item.getTitle().toString().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				menu.add(name);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		String name = null;
		switch(item.getItemId()) {
			case R.id.reset_user:
				resetUser();
				break;
			case R.id.create_user:
				createUser();
				break;
			case R.id.default_user:
				name = (String) getText(R.string.user_default);
				break;
			default:
				name = item.getTitle().toString();
				break;
		}
		if (name != null) {
			setupUser(name);
		}

		return true;
	}

	protected void setupOperation(Operation op) {
	    ImageView imageOperation = (ImageView) findViewById(R.id.imageOperation);
	    int drawable;
	    switch(op) {
		    default:
		    case PLUS:
			    drawable = R.drawable.ic_action_plus;
			    break;
		    case MINUS:
		    	drawable = R.drawable.ic_action_minus;
			    break;
		    case MULTIPLY:
		    	drawable = R.drawable.ic_action_multiply;
			    break;
		    case DIVIDE:
		    	drawable = R.drawable.ic_action_divide;
			    break;
	    }
	    imageOperation.setImageResource(drawable);
    }

    protected void setupFocus() {
	    num3.setText("");
	    num3.setFocusableInTouchMode(true);
	    num3.requestFocus();
    }

	protected void setupUser(String name) {
		if (name.isEmpty()) {
			return;
		}
		if (userDataMap.hasUser(name)) {
			//userData = userDataMap.getUserData(name);
			userDataMap.setCurUser(name);
			setupNumbers();
			message.setText(getResources().getString(R.string.hello_name, name));
		}
	}

	protected void setupNumbers() {
	    // TODO for now just getUserData the top operation from the list
	    Operation op = ops.getNextOp();

	    setupOperation(op);

		UserData userData = userDataMap.getUserData();
	    numberOperation = userData.operationData.getOp(op);

        numberPair = numberOperation.randomize();

        num1.setText(getResources().getString(R.string.msg_number_long, numberPair.l1));
        num2.setText(getResources().getString(R.string.msg_number_long, numberPair.l2));

	    setupFocus();

	    message.setText("");
    }

    public void resetUser() {
	    progressBar.setProgress(0);
	    userResults.reset();
    }

    public void createUser() {
	    Intent intent = new Intent(this, CreateUserActivity.class);

	    UserData userData = new UserData();

	    try {
		    intent.putExtra(EXTRA_USERDATA, userData);
		    startActivityForResult(intent, RESULT_USERDATA);
	    } catch (RuntimeException e) {
		    AndroidUtil.showToast(this, e.toString());
	    }
    }

	public void selectOperation(View view) {
		Intent intent = new Intent(this, OperationSelector.class);
		/*
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_OPS, ops);
		intent.putExtras(b);
		*/
		intent.putExtra(EXTRA_OPS, ops);
		startActivityForResult(intent, RESULT_OPS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
			case RESULT_OPS:
				if (resultCode == RESULT_OK) {
					ops = (OperationsClass) intent.getSerializableExtra(EXTRA_OPS);
					setupNumbers();
				}
				break;
			case RESULT_USERDATA:
				if (resultCode == RESULT_OK) {
					UserData ud = (UserData) intent.getSerializableExtra(EXTRA_USERDATA);
					String name = ud.getName();
					if (!name.isEmpty()) {
						userDataMap.addUserData(ud);
						userDataMap.saveJson(this, this.jsonFilename);

						/*
						Gson gson = new Gson();
						Type type = new TypeToken<UserDataMap>(){}.getType();
						String json = gson.toJson(userDataMap);
						UserDataMap uMap = gson.fromJson(json, type);

						//File file = new File(this.getFilesDir(), jsonFilename);
						try {
							ApplicationInfo info = this.getApplicationInfo();
							System.out.printf("%s\n", info.toString());

							FileOutputStream outputStream = openFileOutput(jsonFilename, Context.MODE_PRIVATE);
							outputStream.write(json.getBytes());
							outputStream.close();

;
						} catch (Exception e) {
							e.printStackTrace();
						}
						*/

						setupUser(userDataMap.getCurUser());
					}
				}
				break;
			default:
				// shouldn't happen
				throw new RuntimeException("Unexpected activity result");
		}
	}

    public void sendAnswer(View view) {
        TextView tvans = (TextView) findViewById(R.id.number3);
	    String txt = tvans.getText().toString().replaceAll("\\s+", "");
        long nanswer;

	    if (txt.isEmpty()) {
		    return;
	    }

	    try {
		    nanswer = Long.parseLong(txt);
	    } catch (NumberFormatException e) {
		    Log.d("ERROR", "Failed to parse txt: "+txt);
		    setupFocus();
		    return;
	    }

        boolean b = numberOperation.isAnswer(nanswer);
        if (b) {
            response.setImageResource(R.drawable.mushroom_good);
            setupNumbers();
	        message.setText(getResources().getString(R.string.msg_correct, nanswer));

	        userResults.correct();
	        progressBar.setProgress(userResults.getnCorrect());
        } else {
	        try {
		        userResults.wrong(numberOperation.op, numberOperation.nums());
		        message.setText(getResources().getString(R.string.msg_incorrect, nanswer));
                response.setImageResource(R.drawable.mushroom_wrong);
		        setupFocus();
		        tg.startTone(ToneGenerator.TONE_SUP_ERROR);
		        Thread.sleep(1000);
		        tg.stopTone();
	        } catch (Exception e) {
		        // ignore
	        }
        }
    }
}
