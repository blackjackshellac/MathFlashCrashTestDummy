package com.oneguycoding.mathflashcrashtestdummy;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.drive.Drive;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity /* implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener */ {
	//public static final String EXTRA_OPS = "ops";
	public static final String EXTRA_USERDATA = "userdata";
	public static final int RESULT_OPS = 100;
	public static final int RESULT_USERDATA = 101;
	private static final int RESULT_STATS = 102;
	//public static final int RESOLVE_CONNECTION_REQUEST_CODE = 102;

	// private static final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

	private NumberOperation numberOperation;
	//private OperationsClass ops = new OperationsClass();
	private TextView num1, num2;
	private EditText num3;
	private TextView message;
    private ImageView response;
	private ProgressBar progressBar;
	private TextView progressText;

	private UserDataMap userDataMap;
	//private UserData userData;
	//private String curUser;
	//private UserResults results;
	public static final String appName = "MathFlashCrashTestDummy";
	public final String jsonFilename = appName +".json";
	public final String statsFilename = appName+"_stats.json";

	//public final File filePublicStoragePath;

	private Menu menu;
	private SQLiteDatabase perfStatsDb;
	private NumericKeyboard numericKeyboard;
	private Uri notification;
	private MediaPlayer mp;


	public MainActivity() {

	}

	UserDataMap getUserDataMap() {
		return userDataMap;
	}

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

		MainActivity mainActivity = this;
		notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mp = MediaPlayer.create(getApplicationContext(), notification);

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
			//file.delete();
		}

		UserDataMap udm = UserDataMap.loadJson(this, null, jsonFilename);
		if (udm == null) {
			// first time invocation
			userDataMap = new UserDataMap((String) getText(R.string.user_default));
			userDataMap.saveUserDataMapAsJson(this, null, jsonFilename);
		} else {
			userDataMap = udm;
		}

		num1 = (TextView) findViewById(R.id.number1);
		num2 = (TextView) findViewById(R.id.number2);
		num3 = (EditText) findViewById(R.id.number3);
		message = (TextView) findViewById(R.id.text_message);
		response = (ImageView) findViewById(R.id.image_response);
		progressText = (TextView) findViewById(R.id.text_progress);

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

		// prevent this edit from responding to touch events
		num3.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// pretend that we did something
				return true;
			}
		});

		final GridLayout keyboard_layout = (GridLayout) findViewById(R.id.keyboard_grid_layout);
		numericKeyboard = new NumericKeyboard(mainActivity, num3);

		for (int i=0; i < keyboard_layout.getChildCount(); i++) {
			Button keyboard_button = (Button) keyboard_layout.getChildAt(i);
			keyboard_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					numericKeyboard.keyboardClick(view);
				}
			});
			AndroidUtil.buttonEffect(keyboard_button);
		}

		/* attach keyboard listener to numeric keyboard layout */
		keyboard_layout.setOnKeyListener(numericKeyboard);
		keyboard_layout.setFocusableInTouchMode(true);
		keyboard_layout.requestFocus();
		MainActivity.sendKeyEvent(num3, null);

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
		see also http://www.androidauthority.com/how-to-store-data-locally-in-android-app-717190/

		StorageManager sm = (StorageManager)getSystemService(Context.STORAGE_SERVICE);
		StorageVolume volume = sm.getPrimaryStorageVolume();
		Intent intent = volume.createAccessIntent(Environment.DIRECTORY_DOCUMENTS);
		startActivityForResult(intent, request_code);
*/
/*
		if (AndroidUtil.isExternalStorageWritable()) {
			File public_storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
			File external_storage = Environment.getExternalStorageDirectory();

			Log.i("STORAGE", "External storage is writable: "+external_storage.getAbsolutePath());
			Log.i("STORAGE", "Public storage is writable: "+public_storage.getAbsolutePath());
		}
*/
	}

	public static void sendKeyEvent(EditText editText, KeyEvent keyEvent) {
		boolean bke = (keyEvent != null);
		if (bke) {
			editText.dispatchKeyEvent(keyEvent);
		}
		editText.setCursorVisible(true);
		editText.setActivated(bke);
		editText.setPressed(bke);
	}

	@Override // android recommended class to handle permissions
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String permissions[],
	                                       @NonNull int[] grantResults) {
		switch (requestCode) {
			case PermissionsUtil.RESTORE_READ_REQUEST_CODE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("permission", "granted");
					UserDataMap udm = PermissionsUtil.do_restore(this, jsonFilename, statsFilename, perfStatsDb);
					if (udm != null) {
						userDataMap = udm;
					}
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.uujm
					AndroidUtil.showToast(this, "Permission denied to read from your External storage", Toast.LENGTH_LONG);
				}
				break;
			case PermissionsUtil.BACKUP_WRITE_REQUEST_CODE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("permission", "granted");
					String jsonStats = userDataMap.dumpStatsAsJson(perfStatsDb, userDataMap);
					PermissionsUtil.do_backup(this, jsonFilename, statsFilename, jsonStats);
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.uujm
					AndroidUtil.showToast(this, "Permission denied to write your External storage", Toast.LENGTH_LONG);
				}
				break;
			default:
				AndroidUtil.showToast(this, "Unknown permissions request code: "+requestCode);
				break;
		}
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

	public void onPreparePopupMenu(PopupMenu popupMenu) {
		Menu menu = popupMenu.getMenu();
		for (String name : userDataMap.users()) {
			boolean isCurUser = userDataMap.isCurrentUser(name);

			MenuItem item = findMenuItemByName(menu, name);
			if (item == null) {
				if (!isCurUser) {
					menu.add(name);
				}
			} else {
				if (isCurUser) {
					menu.removeItem(item.getItemId());
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_backup:
				String jsonStats = userDataMap.dumpStatsAsJson(perfStatsDb, userDataMap);
				if (jsonStats == null) {
					AndroidUtil.showToast(this, getString(R.string.msg_failed_backup_stats));
				}
				PermissionsUtil.try_backup(this, jsonFilename, statsFilename, jsonStats);
 				break;
			case R.id.menu_restore:
				UserDataMap udm = PermissionsUtil.try_restore(this, jsonFilename, statsFilename, perfStatsDb);
				if (udm != null) {
					userDataMap = udm;
					AndroidUtil.showToast(this, R.string.msg_successfully_restored_user, userDataMap.getCurUser());
					selectOperation();
				}
				break;
			case R.id.menu_delete_user:
				deleteUser();
				break;
			case R.id.menu_reset_user:
				resetUser();
				AndroidUtil.showToast(this, R.string.msg_user_progress_reset, userDataMap.getCurUser());
				break;
			case R.id.menu_clear_user:
				clearUser();
				break;
			case R.id.menu_modify_user:
				if (!userDataMap.isDefaultUser()) {
					modifyUser(userDataMap.getCurUser());
				}
				break;
			case R.id.menu_create_user:
				modifyUser(null);
				break;
			case R.id.menu_show_user_stats:
				showStats();
				break;
			case R.id.default_user:
				if (!userDataMap.isDefaultUser()) {
					setupUser(userDataMap.getCurUser());
				}
				break;
			case R.id.menu_about:
				showAbout();
				break;
			case R.id.menu_quit:
				finishAndRemoveTask();
				break;
			case R.id.menu_popup_users:
				View menuItemView = findViewById(R.id.menu_popup_users); // SAME ID AS MENU ID
				PopupMenu popupMenu = new PopupMenu(this, menuItemView);
				popupMenu.inflate(R.menu.popup_users_menu);
				// ...
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						//AndroidUtil.showToast(MainActivity.this, "You clicked: "+item.getTitle());
						String new_name = item.getTitle().toString();
						if (!userDataMap.getUserData().results.testDone()) {
							selectOperation(new_name);
						} else if (!new_name.equals(userDataMap.getCurUser())) {
							userDataMap.setCurUser(new_name);
							resetUser();
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
		final ViewGroup nullParent = null; // <-- stupid hack to get rid of warning about null ViewGroup
		View messageView = getLayoutInflater().inflate(R.layout.about, nullParent, false);

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

		// display build date
		Date buildDate = new Date(BuildConfig.TIMESTAMP);
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		TextView aboutBuildDate = (TextView) messageView.findViewById(R.id.about_build_date);
		aboutBuildDate.setText(dt.format(buildDate));

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
	    //num3.setFocusableInTouchMode(true);
	    //num3.requestFocus();
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

	/**
	 * Check the given number pair for limiting (based on op and longPair value)
	 *
	 * @param op - operation
	 * @param longPair - value to be checked for duplicates or if limited
	 * @return return true if limited or duplicate, otherwise false
	 */
	private boolean checkNumbers(Operation op, LongPair longPair) {
		UserData userData = userDataMap.getUserData();

		boolean dupe = userData.getLongPairRecorder().isRecorded(op, longPair);
		if (dupe) {
			Log.d("CHECK", "found duplicate numberPair "+longPair.toString());
			return true;
		}

		boolean limited = userDataMap.getUserData().results.limitOperationNumbers(op, longPair);
		if (limited) {
			Log.d("CHECK", "found limited numberPair "+longPair.toString());
			return true;
		}
		return false;
	}

	protected void setupNumbers() {
		UserData userData = userDataMap.getUserData();
		// TODO need a better way to handle multiple ops
	    numberOperation = userData.ops.getNextOp();

	    setupOperation(numberOperation.op);

		int loops = 0;
		int maxLoops = 10;
		LongPair numberPair;
		while (true) {
			//numberPair = numberOperation.sameTestNumbers();
			numberPair = numberOperation.randomize();
			if (!checkNumbers(numberOperation.op, numberPair)) {
				break;
			}
			loops += 1;
			if (loops >= maxLoops) {
				break;
			}
		}

        num1.setText(getResources().getString(R.string.msg_number_long, numberPair.l1));
        num2.setText(getResources().getString(R.string.msg_number_long, numberPair.l2));

	    setupFocus();

		// this wipes out the correct/incorrect message in sendAnswer
	    //message.setText("");
    }

	/**
	 * Reset user progress
	 */
	private void resetUser() {
	    UserData userData = userDataMap.getUserData();
		userData.getLongPairRecorder().clear();

	    UserResults userResults = userData.results;
	    userResults.reset();
	    progressBar.setProgress(0);
	    progressBar.setMax(userResults.getNum());
		progressText.setText("");
    }

	/**
	 * Clear all user data completely
	 */
	private void clearUser() {
		if (userDataMap.isDefaultUser()) {
			AndroidUtil.showToast(this, getText(R.string.msg_refusing_to_delete_default_user).toString());
			return;
		}

	    final MainActivity activity = this;

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle(getText(R.string.title_clear_user));
	    builder.setMessage(getString(R.string.text_clear_user_prompt, userDataMap.getCurUser()));

	    builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int which) {
			    UserData userData = activity.userDataMap.getUserData();
			    UserResults userResults = userData.results;
			    String name = activity.userDataMap.getCurUser();

			    resetUser();

			    userResults.clearStats(perfStatsDb, name);

			    setupUser(name);

			    userDataMap.saveUserDataMapAsJson(activity, null, jsonFilename);

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
	@SuppressWarnings("unused")
	void sendEmail(Uri attachment) {

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
			AndroidUtil.showToast(this, R.string.err_no_email_clients_installed);
	    } catch (Exception e) {
			Log.e("email", "failed to attached "+jsonFilename, e);
		}
    }

	private void deleteUser() {
		// make final to allow OnClickListeners access
		if (userDataMap.isDefaultUser()) {
			AndroidUtil.showToast(this, getString(R.string.msg_refusing_to_delete_default_user));
			return;
		}

		// pass this activity to the onClickListener
		final MainActivity activity = this;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getText(R.string.title_delete_user));
		builder.setMessage(getString(R.string.text_delete_user_prompt, userDataMap.getCurUser()));

		builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String name = activity.userDataMap.getCurUser();
				UserData ud = userDataMap.deleteUser(name);
				if (ud == null) {
					AndroidUtil.showToast(activity, getString(R.string.msg_failed_to_delete_current_user, name));
					activity.userDataMap.setCurUser();
				}
				MenuItem mitem = activity.findMenuItemByName(menu, name);
				menu.removeItem(mitem.getItemId());
				activity.invalidateOptionsMenu();
				setupUser(activity.userDataMap.getCurUser());
				userDataMap.saveUserDataMapAsJson(activity, null, jsonFilename);
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
	 * modify or create user
	 *
	 * @param user - user to modify, or create if null
	 */
	public void modifyUser(String user) {
	    Intent intent = new Intent(this, ModifyUserActivity.class);

	    UserData userData;
		if (user == null) {
			userData = new UserData();
		} else {
			if (userDataMap.hasUser(user)) {
				userDataMap.setCurUser(user);
				userData = userDataMap.getUserData();
			} else {
				AndroidUtil.showToast(this, R.string.err_user_not_found, user);
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

    public void openSelectOperation() {
	    Intent intent = new Intent(this, OperationSelector.class);

	    Bundle b = new Bundle();
	    b.putSerializable(EXTRA_USERDATA, userDataMap);
	    intent.putExtras(b);
	    startActivityForResult(intent, RESULT_OPS);
    }

    public void selectOperation() {
		selectOperation(null);
    }

	public void selectOperation(String new_name) {
		final String _name = new_name;

		UserData userData = userDataMap.getUserData();
		if (userData.results.testDone()) {
			openSelectOperation();
		} else {
			// pass this activity to the onClickListener
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(getString(R.string.title_select_operation, userDataMap.getCurUser()));
			builder.setMessage(getText(R.string.msg_select_operation));

			builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (_name != null) {
						userDataMap.setCurUser(_name);
					}
					resetUser();
					openSelectOperation();
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
					resetUser();
				}
				break;
			case RESULT_OPS:
				if (resultCode == RESULT_OK) {
					Bundle b = intent.getExtras();
					if (b == null) {
						Log.e("OnActivityResult", "Bundle is null in RESULT_OPS");
					} else {
						userDataMap = (UserDataMap) b.getSerializable(EXTRA_USERDATA);
						saveJson = true;
					}
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
				Log.e("OnActivityResult", "Unexpected activity result");
				break;
		}
		// if the UserDataMap was serialized the LongPairRecorders for each UserData are lost
		userDataMap.createRecorders();
		if (saveJson) {
			userDataMap.saveUserDataMapAsJson(this, null, jsonFilename);
			// sendEmail(null);
		}
		//setupNumbers();
		setupUser(userDataMap.getCurUser());
	}

	private void validateNumbers() {
		LongPair numbers = numberOperation.getNumbers();
		Long n1=Long.parseLong(num1.getText().toString());
		Long n2=Long.parseLong(num2.getText().toString());

		if (numbers.l1.equals(n1)) {
			n1 = null;
		} else {
			Log.e("validateNumbers", AndroidUtil.stringFormatter("num1 is out of whack: %d != %d", n1, numbers.l1));
		}
		if (numbers.l2.equals(n2)) {
			n2 = null;
		} else {
			Log.e("validateNumbers", AndroidUtil.stringFormatter("num2 is out of whack: %d != %d", n2, numbers.l2));
		}
		if (n1 != null && n2 != null) {
			numberOperation.setNumbers(n1, n2);
		}
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
		    Log.e("sendAnswer", "Failed to parse txt: "+txt);
		    setupFocus();
		    return;
	    }

	    validateNumbers();

	    UserData userData = userDataMap.getUserData();
        boolean correct = numberOperation.isAnswer(nanswer);
	    UserResults userResults = userData.results;
	    if (correct) {
            response.setImageResource(R.drawable.mushroom_good);
		    userData.getLongPairRecorder().record(numberOperation.op, numberOperation.getNumbers());

		    // do this below but only if test is not complete
		    // setupNumbers();
	        message.setText(getResources().getString(R.string.msg_correct, nanswer));

	        userResults.correct();
	        progressBar.setProgress(userResults.getnCorrect());
        } else {
	        try {
		        userResults.wrong(numberOperation.op, numberOperation.getNumbers());
		        message.setText(getResources().getString(R.string.msg_incorrect, nanswer));
                response.setImageResource(R.drawable.mushroom_wrong);
		        setupFocus();

		        // play a notification
		        mp.start();
	        } catch (Exception e) {
		        // ignore
	        }
        }
		String progress;

	    if (userResults.testDone()) {

		    AndroidUtil.hideKeyboard(this);

		    userResults.saveStats(perfStatsDb, numberOperation.op, userDataMap.getCurUser());

		    progress = getString(R.string.text_progress_done, userDataMap.getCurUser(), userResults.getnCorrect(), userResults.getNumAnswered(), userResults.getPercentage());

		    showStats();

		    resetUser();

		    message.setText(getString(R.string.text_bravo, userDataMap.getCurUser()));

		    AndroidUtil.showToast(this, progress, 5);

	    } else {
		    progress = getString(R.string.text_progress, userResults.getnCorrect(), userResults.getNumAnswered(), userResults.getPercentage(), userResults.getRemaining());
		    progressText.setText(progress);
		    if (correct) {
			    setupNumbers();
		    }
	    }
    }

	@Override
    public void onDestroy() {
	    perfStatsDb.close();
	    mp.release();
	    super.onDestroy();
    }
}
