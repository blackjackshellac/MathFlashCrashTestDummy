package com.oneguycoding.mathflashcrashtestdummy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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


//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.drive.Drive;

import java.io.File;

public class MainActivity extends AppCompatActivity /* implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener */ {
	public static final String EXTRA_OPS = "ops";
	public static final String EXTRA_USERDATA = "userdata";
	public static final int RESULT_OPS = 100;
	public static final int RESULT_USERDATA = 101;
	//public static final int RESOLVE_CONNECTION_REQUEST_CODE = 102;

	private static final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    private NumberOperation numberOperation;
	private OperationsClass ops = new OperationsClass();
	private TextView num1, num2, num3;
	private TextView message;
    private ImageView response;
	private ProgressBar progressBar;

	private UserDataMap userDataMap;
	//private UserData userData;
	//private String curUser;
	//private UserResults userResults;
	private String jsonFilename;
/*
	private GoogleApiClient mGoogleApiClient;
	final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
			new ResultCallback<DriveApi.DriveContentsResult>() {
				@Override
				public void onResult(DriveApi.DriveContentsResult result) {
					if (!result.getStatus().isSuccess()) {
						Log.d("onResult", "Error while trying to create new file contents");
						return;
					}

					MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
							.setTitle("appconfig.txt")
							.setMimeType("text/plain")
							.build();
					Drive.DriveApi.getAppFolder(mGoogleApiClient)
							.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
							.setResultCallback(fileCallback);
				}
			};
	// [END drive_contents_callback]

	final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
			ResultCallback<DriveFolder.DriveFileResult>() {
				@Override
				public void onResult(DriveFolder.DriveFileResult result) {
					if (!result.getStatus().isSuccess()) {
						Log.d("onResult", "Error while trying to create the file");
						return;
					}
					Log.i("onResult", "Created a file in App Folder: "
							+ result.getDriveFile().getDriveId());
				}
			};
*/
	public MainActivity() {
		jsonFilename = "MathFlashCrashTestDummy.json";
	}

	@Override
	protected void onStart() {
		super.onStart();
		//mGoogleApiClient.connect();
	}

/*

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
			} catch (IntentSender.SendIntentException e) {
				// Unable to resolve, message user appropriately
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		//super.onConnected(bundle);
		// create new contents resource
		Drive.DriveApi.newDriveContents(mGoogleApiClient)
				.setResultCallback(driveContentsCallback);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}
*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

/*
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
*/

		if (userDataMap == null) {
			userDataMap = new UserDataMap((String) getText(R.string.user_default));
		}
		if (userDataMap.isEmpty()) {
			/*
			 * TODO Set udm to null to delete the existing jsonFilename file
			 */
			File file = new File(getFilesDir(), jsonFilename);
			if (file.exists()) {
				boolean r = false; //file.delete();
				if (!r) {
					AndroidUtil.showToast(this, String.format("File %s not deleted", jsonFilename));
				}
			}
			UserDataMap udm = UserDataMap.loadJson(this, jsonFilename);
			if (udm == null) {
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
					selectOperation();
				}
			}
		);

		setupNumbers();
		setupUser(userDataMap.getCurUser());



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
			case R.id.menu_delete_user:
				deleteUser();
				break;
			case R.id.menu_reset_user:
				resetUser();
				break;
			case R.id.menu_modify_user:
				name = userDataMap.getCurUser();
				if (!name.equals(getText(R.string.user_default))) {
					modifyUser(name, false);
				}
				break;
			case R.id.menu_create_user:
				name = userDataMap.getCurUser();
				modifyUser(name, true);
				break;
			case R.id.default_user:
				name = (String) getText(R.string.user_default);
				if (!name.equals(userDataMap.getCurUser())) {
					setupUser(name);
					selectOperation();
				}
				break;
			default:
				name = item.getTitle().toString();
				if (!name.equals(userDataMap.getCurUser())) {
					setupUser(name);
					selectOperation();
				}
				break;
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
		if (name.isEmpty())     {
			return;
		}
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		if (userDataMap.hasUser(name)) {
			//userData = userDataMap.getUserData(name);
			userDataMap.setCurUser(name);
			setupNumbers();
			message.setText(getResources().getString(R.string.hello_name, name));

			UserData userData = userDataMap.getUserData();
			progressBar.setMax(userData.userResults.getNum());
		} else {
			throw new RuntimeException(String.format("UserDataMap does not contain user %s", name));
		}
	}

	protected void setupNumbers() {
	    // TODO for now just getUserData the top operation from the list
	    Operation op = ops.getNextOp();

	    setupOperation(op);

		UserData userData = userDataMap.getUserData();
	    numberOperation = userData.operationData.getOp(op);

		LongPair numberPair = numberOperation.randomize();

        num1.setText(getResources().getString(R.string.msg_number_long, numberPair.l1));
        num2.setText(getResources().getString(R.string.msg_number_long, numberPair.l2));

	    setupFocus();

	    message.setText("");
    }

    public void resetUser() {
	    progressBar.setProgress(0);
	    UserResults userResults = userDataMap.getUserData().userResults;
	    userResults.reset(userResults.getNum());
    }

	private void deleteUser() {
		String name = userDataMap.getCurUser();
		if (name.equals(getText(R.string.user_default))) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Confirm");
		builder.setMessage("Are you sure?");

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				// Do nothing, but close the dialog
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Do nothing
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void modifyUser(String user, boolean create) {
	    Intent intent = new Intent(this, ModifyUserActivity.class);

	    UserData userData;
		if (create) {
			userData = new UserData();
		} else {
			if (userDataMap.hasUser(user)) {
				userDataMap.setCurUser(user);
				userData = userDataMap.getUserData();
			} else {
				AndroidUtil.showToast(this, "User doesn't exist: "+user);
				return;
			}
		}

	    try {
		    intent.putExtra(EXTRA_USERDATA, userData);
		    startActivityForResult(intent, RESULT_USERDATA);
	    } catch (RuntimeException e) {
		    AndroidUtil.showToast(this, e.toString());
	    }
    }

	public void selectOperation() {
		Intent intent = new Intent(this, OperationSelector.class);
		/*
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_OPS, ops);
		intent.putExtras(b);
		*/
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_USERDATA, userDataMap);
		b.putSerializable(EXTRA_OPS, ops);
		intent.putExtras(b);
		startActivityForResult(intent, RESULT_OPS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
/*
			case RESOLVE_CONNECTION_REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					mGoogleApiClient.connect();
				}
				break;
*/
			case RESULT_OPS:
				if (resultCode == RESULT_OK) {
					ops = (OperationsClass) intent.getSerializableExtra(EXTRA_OPS);
					setupNumbers();
				}
				break;
			case RESULT_USERDATA:
				if (resultCode == RESULT_OK) {
					UserData userData = (UserData) intent.getSerializableExtra(EXTRA_USERDATA);
					String name = userData.getName();
					if (!name.isEmpty()) {
						userDataMap.addUserData(userData);
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

	    //UserData userData = userDataMap.getUserData();
        boolean b = numberOperation.isAnswer(nanswer);
	    UserResults userResults = userDataMap.getUserData().userResults;
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
	    TextView textProgress = (TextView) findViewById(R.id.text_progress);
	    String progress = getString(R.string.text_progress, userResults.getnCorrect(), userResults.getNumAnswered(), userResults.getPercentage(), userResults.getRemaining());
	    textProgress.setText(progress);
    }
}
