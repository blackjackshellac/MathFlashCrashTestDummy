package com.oneguycoding.mathflashcrashtestdummy;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.drive.Drive;

import java.io.File;

public class MainActivity extends AppCompatActivity /* implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener */ {
	//public static final String EXTRA_OPS = "ops";
	public static final String EXTRA_USERDATA = "userdata";
	static final String EXTRA_OPERATION = "operation";
	public static final int RESULT_OPS = 100;
	public static final int RESULT_USERDATA = 101;
	private static final int RESULT_STATS = 102;
	//public static final int RESOLVE_CONNECTION_REQUEST_CODE = 102;

	private static final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

	private NumberOperation numberOperation;
	//private OperationsClass ops = new OperationsClass();
	private TextView num1, num2, num3;
	private TextView message;
    private ImageView response;
	private ProgressBar progressBar;

	private UserDataMap userDataMap;
	//private UserData userData;
	//private String curUser;
	//private UserResults results;
	public static final String jsonFilename = "MathFlashCrashTestDummy.json";
	private Menu menu;
	private SQLiteDatabase perfStatsDb;

	public MainActivity() {	}
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

		File file = new File(getFilesDir(), jsonFilename);
		if (file.exists()) {
			Log.d("DEBUG", "json file exists: "+jsonFilename);
		}

/*
		if (file.exists()) {
			boolean r = true; // file.delete(); // false; //file.delete();
			if (!r) {
				AndroidUtil.showToast(this, String.format("File %s not deleted", jsonFilename));
			}
		}
*/
		UserDataMap udm = UserDataMap.loadJson(this, jsonFilename);
		if (udm == null) {
			// first time invocation
			userDataMap = new UserDataMap((String) getText(R.string.user_default));
			userDataMap.saveJson(this, jsonFilename);
		} else {
			userDataMap = udm;
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
					try {
						sendAnswer(findViewById(R.id.sendAnswer));
					} catch(Exception e) {
						Log.d("ERROR", "Exception from sendAnswer", e);
					}
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

		//setupNumbers();
		setupUser(userDataMap.getCurUser());

		// reset database here
		//Log.d("SQL", "deleting database table: "+PerformanceStatsSchema.SQL_DROP_TABLE);
		//perfStatsDb.execSQL(PerformanceStatsSchema.SQL_DROP_TABLE);
		//this.deleteDatabase(PerformanceStatsHelper.DATABASE_NAME);

		PerformanceStatsHelper perfStatsHelper = new PerformanceStatsHelper(this);
		perfStatsDb = perfStatsHelper.getWritableDatabase();

/*
		TODO see also http://www.androidauthority.com/how-to-store-data-locally-in-android-app-717190/

		StorageManager sm = (StorageManager)getSystemService(Context.STORAGE_SERVICE);
		StorageVolume volume = sm.getPrimaryStorageVolume();
		Intent intent = volume.createAccessIntent(Environment.DIRECTORY_DOCUMENTS);
		startActivityForResult(intent, request_code);
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the main_menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		this.menu = menu;
		return true;
	}

	public MenuItem findMenuItemByName(Menu menu, String name) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			if (item.getTitle().toString().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public boolean onPreparePopupMenu(PopupMenu popupMenu) {
		Menu menu = popupMenu.getMenu();
		for (String name : userDataMap.users()) {
			if (name.equals(getText(R.string.user_default))) {
				continue;
			}
			MenuItem item = findMenuItemByName(menu, name);
			if (item == null) {
				menu.add(name);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		String name;
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
				}
				break;
			case R.id.menu_about:
				showAbout();
				break;
			case R.id.menu_popup_users:
				View menuItemView = findViewById(R.id.menu_popup_users); // SAME ID AS MENU ID
				PopupMenu popupMenu = new PopupMenu(this, menuItemView);
				popupMenu.inflate(R.menu.popup_users_menu);
				// ...
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						//AndroidUtil.showToast(MainActivity.this, "You clicked: "+item.getTitle());
						String name = item.getTitle().toString();
						if (!name.equals(userDataMap.getCurUser())) {
							userDataMap.setCurUser(name);
							selectOperation();
						}
						return true;
					}
				});

				onPreparePopupMenu(popupMenu);

				popupMenu.show();
				break;
			default:
				break;
		}

		return true;
	}

	protected PackageInfo getPackageInfo() {
		String pn = this.getPackageName();

		PackageInfo pInfo = null;
		try {
			PackageManager pm = this.getPackageManager();
			pInfo = pm.getPackageInfo(pn, 0);
		} catch (PackageManager.NameNotFoundException e) {
			Log.e("MainActivity", "Failed to get PackageInfo for PackageName="+pn, e);
		}
		return pInfo;
	}

	protected void showAbout() {
		// Inflate the about message contents
		View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

		// When linking text, force to always use default color. This works
		// around a pressed color state bug.
		TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
		int defaultColor = textView.getTextColors().getDefaultColor();
		textView.setTextColor(defaultColor);

		String versionName;
		int versionCode;
		PackageInfo pInfo = getPackageInfo();
		if (pInfo != null) {
			versionName = pInfo.versionName;
			versionCode = pInfo.versionCode;
		} else {
			versionName = "<failed to get versionName>";
			versionCode = -1;
		}
		String versionString = AndroidUtil.stringFormatter("%s (%d)", versionName, versionCode);
		TextView aboutVersion = (TextView) messageView.findViewById(R.id.about_version);
		aboutVersion.setText(versionString);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.mathflash);
		builder.setTitle(R.string.app_name);
		builder.setView(messageView);
		builder.create();
		builder.show();
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
			progressBar.setMax(userData.results.getNum());
		} else {
			throw new RuntimeException(String.format("UserDataMap does not contain user %s", name));
		}
	}

	protected void setupNumbers() {
		UserData userData = userDataMap.getUserData();
		// TODO need a better way to handle multiple ops
	    numberOperation = userData.ops.getNextOp();

	    setupOperation(numberOperation.op);

		int loops = 0;
		int maxLoops = 10;
		LongPair numberPair = numberOperation.randomize();
		while (userData.results.limitOperationNumbers(numberOperation.op, numberPair)) {
			numberPair = numberOperation.randomize();
			loops += 1;
			if (loops >= maxLoops) {
				break;
			}
		}

        num1.setText(getResources().getString(R.string.msg_number_long, numberPair.l1));
        num2.setText(getResources().getString(R.string.msg_number_long, numberPair.l2));

	    setupFocus();

	    message.setText("");
    }

    public void resetUser() {
	    progressBar.setProgress(0);
	    final UserResults userResults = userDataMap.getUserData().results;
	    userResults.reset(userResults.getNum());

	    final String name = userDataMap.getCurUser();
	    if (name.equals(getText(R.string.user_default))) {
		    return;
	    }
	    final MainActivity activity = this;

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle(getText(R.string.title_clear_user));
	    builder.setMessage(getString(R.string.text_clear_user_prompt, name));

	    builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int which) {
			    userResults.clearStats(perfStatsDb, name);

			    setupUser(activity.userDataMap.getCurUser());
			    userDataMap.saveJson(activity, MainActivity.jsonFilename);

			    dialog.dismiss();
		    }
	    });

	    builder.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
		    }
	    });

	    AlertDialog alert = builder.create();
	    alert.show();

    }

	/**
	 * https://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application
	 *
	 * For sending attachments see here,
	 *
	 * https://developer.android.com/guide/components/intents-common.html#Email
	 *
	 */
	private void sendEmail(Uri attachment) {

	    UserData userData = userDataMap.getUserData();
	    String userEmail = userData.getEmail();
	    if (userEmail.isEmpty()) {
		    return;
	    }

	    if (attachment == null) {
		    File jsonFile = new File(getFilesDir(), jsonFilename);
		    if (jsonFile.exists()) {
			    Log.d("DEBUG", "json file exists: "+jsonFilename);
			    attachment = Uri.fromFile(jsonFile);
		    }
	    }

		String[] addresses = {userEmail};

	    Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("message/rfc822");
	    intent.putExtra(Intent.EXTRA_EMAIL  , addresses);
	    intent.putExtra(Intent.EXTRA_SUBJECT, jsonFilename);
	    intent.putExtra(Intent.EXTRA_TEXT   , "See attached json file: "+jsonFilename);
		intent.putExtra(Intent.EXTRA_STREAM, attachment);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

		try {
			Intent chooser = Intent.createChooser(intent, "Send mail...");
		    startActivity(chooser);
	    } catch (ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
	    } catch (Exception e) {
			Log.e("email", "failed to attached "+jsonFilename, e);
		}
    }

	private void deleteUser() {
		// make final to allow OnClickListeners access
		final String name = userDataMap.getCurUser();
		if (name.equals(getText(R.string.user_default))) {
			return;
		}
		final MainActivity activity = this;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getText(R.string.title_delete_user));
		builder.setMessage(getString(R.string.text_delete_user_prompt, name));

		builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				userDataMap.deleteUser(name);
				MenuItem mitem = activity.findMenuItemByName(menu, name);
				menu.removeItem(mitem.getItemId());
				activity.invalidateOptionsMenu();
				setupUser(activity.userDataMap.getCurUser());
				userDataMap.saveJson(activity, MainActivity.jsonFilename);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
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
		intent.putExtras(b);
		startActivityForResult(intent, RESULT_OPS);
	}

	public void showStats() {
		Intent intent = new Intent(this, StatsActivity.class);
		/*
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_OPS, ops);
		intent.putExtras(b);
		*/
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_USERDATA, userDataMap);
		b.putSerializable(EXTRA_OPERATION, numberOperation.op);
		intent.putExtras(b);
		startActivityForResult(intent, RESULT_STATS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		boolean saveJson = false;
		switch(requestCode) {
/*
			case RESOLVE_CONNECTION_REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					mGoogleApiClient.connect();
				}
				break;
*/
			case RESULT_STATS:
				if (resultCode == RESULT_OK) {
					/*
					Nothing is changed in StatsActivity, we can ignore the bundle
					 */
					//Bundle b = intent.getExtras();
					//userDataMap = (UserDataMap) b.getSerializable(EXTRA_USERDATA);
					UserResults userResults = userDataMap.getUserData().results;
					userResults.reset(0);
				}
				break;
			case RESULT_OPS:
				if (resultCode == RESULT_OK) {
					Bundle b = intent.getExtras();
					userDataMap = (UserDataMap) b.getSerializable(EXTRA_USERDATA);
					saveJson = true;
					//setupNumbers();
				}
				break;
			case RESULT_USERDATA:
				if (resultCode == RESULT_OK) {
					UserData userData = (UserData) intent.getSerializableExtra(EXTRA_USERDATA);
					String name = userData.getName();
					if (!name.isEmpty()) {
						userDataMap.addUserData(userData);
						saveJson = true;
					}
				}
				break;
			default:
				// shouldn't happen
				throw new RuntimeException("Unexpected activity result");
		}
		if (saveJson) {
			userDataMap.saveJson(this, jsonFilename);
			// sendEmail(null);
		}
		//setupNumbers();
		setupUser(userDataMap.getCurUser());
	}

    public void sendAnswer(View view) throws Exception {
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

	    if (BuildConfig.DEBUG) {
		    if (Long.parseLong(num1.getText().toString()) != numberOperation.nums().l1) {
			    throw new Exception("num1 is out of whack!");
		    }
		    if (Long.parseLong(num2.getText().toString()) != numberOperation.nums().l2) {
			    throw new Exception("num2 is out of whack!");
		    }
	    }

	    //UserData userData = userDataMap.getUserData();
        boolean b = numberOperation.isAnswer(nanswer);
	    UserResults userResults = userDataMap.getUserData().results;
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
		String progress;

	    if (userResults.testDone()) {

		    AndroidUtil.hideKeyboard(this);

		    userResults.saveStats(perfStatsDb, numberOperation.op, userDataMap.getCurUser());

/*
		    Cursor cursor = UserResults.getStatsQueryCursor(perfStatsDb, numberOperation.op, userDataMap.getCurUser());
		    ArrayList<UserResults.SqlResult> stats = UserResults.loadStats(cursor);
		    if (stats == null) {
			    Log.d("SQL", "failed to load results for user "+userDataMap.getCurUser());
		    }
		    userResults.setStats(stats);
*/

		    progress = getString(R.string.text_progress_done, userDataMap.getCurUser(), userResults.getnCorrect(), userResults.getNumAnswered(), userResults.getPercentage());
		    //textProgress.setText(progress);
/*

		    StringBuilder sb = new StringBuilder(progress);
		    float total = 0.0f;
		    Iterator<UserResults.SqlResult> it = stats.iterator();
		    while (it.hasNext()) {
			    UserResults.SqlResult entry = it.next();
			    total += entry.percentage_correct;
			    sb.append(AndroidUtil.stringFormatter("%s:[%d],%d,%d,%.2f\n", DateFormat.getDateTimeInstance().format(entry.runtime*1000L), entry.duration-entry.runtime, entry.correct, entry.num, entry.percentage_correct));
		    }
		    float ave = total / stats.size();
		    sb.append("\n").append(AndroidUtil.stringFormatter("Ave=%.2f", ave)).append("\n");
		    textProgress.setText(sb.toString());
*/
		    showStats();

		    //userResults.reset(0);

		    progressBar.setProgress(0);
		    progressBar.setMax(userResults.getNum());
		    message.setText(getString(R.string.text_bravo, userDataMap.getCurUser()));

		    AndroidUtil.showToast(this, progress, 5);

	    } else {
		    progress = getString(R.string.text_progress, userResults.getnCorrect(), userResults.getNumAnswered(), userResults.getPercentage(), userResults.getRemaining());
		    textProgress.setText(progress);
	    }
    }

    @Override
    public void onDestroy() {
	    perfStatsDb.close();
	    super.onDestroy();
    }
}
