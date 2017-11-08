package com.oneguycoding.mathflashcrashtestdummy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.io.File;

/**
 *
 * Helpers for dealing with permissions
 *
 * Created by steeve on 17/10/17.
 */

class PermissionsUtil {

	/**
	 * Variables for requesting permissions, API 25+
	 */
	static final int BACKUP_WRITE_REQUEST_CODE = 1;
	static final int RESTORE_READ_REQUEST_CODE = 2;
	static private int permissionsRequestCode;
	private static final String[] permissions = new String[] {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE
	};
	private static final File filePublicStoragePath;

	static {
		filePublicStoragePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), MainActivity.appName);
	}

	/**
	 * Perform the backup. This should only be called after permissions have been granted in try_backup()
	 *
	 * @param mainActivity
	 * @param jsonFilename
	 * @param statsFilename
	 * @param jsonStats
	 */
	static void do_backup(MainActivity mainActivity, String jsonFilename, String statsFilename, String jsonStats) {
		if (!filePublicStoragePath.exists()) {
			if (!filePublicStoragePath.mkdirs()) {
				AndroidUtil.showToast(mainActivity, R.string.err_create_backup_directory, filePublicStoragePath.getAbsolutePath());
				return;
			}
			AndroidUtil.showToast(mainActivity, R.string.msg_created_backup_directory, filePublicStoragePath.getAbsolutePath());
		}
		File fileJson = new File(filePublicStoragePath, jsonFilename);
		if (mainActivity.getUserDataMap().saveUserDataMapAsJson(mainActivity, fileJson, jsonFilename)) {
			AndroidUtil.showToast(mainActivity, R.string.msg_successfully_backed_up_json, fileJson.getAbsolutePath());
		}
		if (jsonStats != null && !jsonStats.isEmpty()) {
			fileJson = new File(filePublicStoragePath, statsFilename);
			mainActivity.getUserDataMap().saveUserDataMapAsJson(mainActivity, fileJson, null, jsonStats);
		}
	}

	static void try_backup(MainActivity activity, String jsonFilename, String statsFilename, String jsonStats) {
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			permissionsRequestCode = BACKUP_WRITE_REQUEST_CODE;
			ActivityCompat.requestPermissions(activity, permissions, permissionsRequestCode);
		} else {
			do_backup(activity, jsonFilename, statsFilename, jsonStats);
		}

	}

	static UserDataMap do_restore(MainActivity mainActivity, String jsonFilename, String statsFilename, SQLiteDatabase perfStatsDb) {
		if (!filePublicStoragePath.exists()) {
			AndroidUtil.showToast(mainActivity, R.string.err_backup_directory_not_found, filePublicStoragePath.getAbsolutePath());
		} else {
			File fileJsonStats = new File(filePublicStoragePath, statsFilename);

			mainActivity.getUserDataMap().restoreStats(mainActivity, perfStatsDb, fileJsonStats);

		}


		File fileJson = new File(filePublicStoragePath, jsonFilename);
		return UserDataMap.loadJson(mainActivity, fileJson, jsonFilename);
	}

	static UserDataMap try_restore(MainActivity activity, String jsonFilename, String statsFilename, SQLiteDatabase perfStatsDb) {
		UserDataMap udm = null;
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			permissionsRequestCode = RESTORE_READ_REQUEST_CODE;
			ActivityCompat.requestPermissions(activity, permissions, permissionsRequestCode);
		} else {
			udm = do_restore(activity, jsonFilename, statsFilename, perfStatsDb);
		}
		return udm;
	}


}
