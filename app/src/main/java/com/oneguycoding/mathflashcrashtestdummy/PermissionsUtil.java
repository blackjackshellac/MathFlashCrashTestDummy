package com.oneguycoding.mathflashcrashtestdummy;

import android.Manifest;
import android.content.pm.PackageManager;
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

	static void do_backup(MainActivity mainActivity, String jsonFilename) {
		if (!filePublicStoragePath.exists()) {
			if (!filePublicStoragePath.mkdirs()) {
				AndroidUtil.showToast(mainActivity, "Failed to create backup directory: "+filePublicStoragePath.getAbsolutePath());
				return;
			}
			AndroidUtil.showToast(mainActivity, "Created backup directory: "+filePublicStoragePath.getAbsolutePath());
		}
		File fileJson = new File(filePublicStoragePath, jsonFilename);
		if (mainActivity.getUserDataMap().saveJson(mainActivity, fileJson, jsonFilename)) {
			AndroidUtil.showToast(mainActivity, "Successfullly backed up json: "+fileJson.getAbsolutePath());
		}
	}

	static void backup(MainActivity activity, String jsonFilename) {
/*
		if (AndroidUtil.isExternalStorageWritable()) {
			AndroidUtil.showToast(activity, "External storage is not writable!");
		}
*/

		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			permissionsRequestCode = BACKUP_WRITE_REQUEST_CODE;
			ActivityCompat.requestPermissions(activity, permissions, permissionsRequestCode);
		} else {
			do_backup(activity, jsonFilename);
		}

	}

	static UserDataMap do_restore(MainActivity mainActivity, String jsonFilename) {
		if (!filePublicStoragePath.exists()) {
			AndroidUtil.showToast(mainActivity, "Backup directory not found: "+filePublicStoragePath.getAbsolutePath());
		}
		File fileJson = new File(filePublicStoragePath, jsonFilename);
		return UserDataMap.loadJson(mainActivity, fileJson, jsonFilename);
	}

	static UserDataMap restore(MainActivity activity, String jsonFilename) {
		UserDataMap udm = null;
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			permissionsRequestCode = RESTORE_READ_REQUEST_CODE;
			ActivityCompat.requestPermissions(activity, permissions, permissionsRequestCode);
		} else {
			udm = do_restore(activity, jsonFilename);
		}
		return udm;
	}


}
