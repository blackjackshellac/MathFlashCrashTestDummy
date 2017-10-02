package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

/**
 * Container to hold all users' UserData as well as the current and default user
 *
 * Created by steeve on 13/09/17.
 */

class UserDataMap implements Serializable {
//	@SerializedName("VERSION_USERDATAMAP")
	private static final String VERSION_USERDATAMAP = "20170914";
//	@SerializedName("curUser")
	private String curUser;
//	@SerializedName("userDataMap")
	private final HashMap<String,UserData> userDataMap;
	private final String defaultUser;

	UserDataMap(String name) {
		Log.d("UserDataMap", VERSION_USERDATAMAP);

		userDataMap = new HashMap<>();
		defaultUser = name;
		UserData userData = new UserData(defaultUser, "");
		addUserData(userData);
	}

	String getCurUser() {
		return curUser;
	}

	/**
	 * Set the current user
	 *
	 * @param name user to set as current user
	 *
	 */
	UserData setCurUser(String name) {
		if (this.hasUser(name)) {
			this.curUser = name;
			return getUserData();
		}
		throw new RuntimeException(String.format("Unknown user: %s", name));

	}

	UserData getUserData() throws IllegalArgumentException {
		UserData userData = userDataMap.get(curUser);
		if (userData == null) {
			throw new IllegalArgumentException("userData for curUser should never be null");
		}
		return userData;
	}

	/**
	 * Add new userData object for given user, replace if it already exists and set curUser to the new user
	 *
	 * @param userData - UserData object to insert into map
	 */
	void addUserData(UserData userData) {
		String name = userData.getName();
		if (name.isEmpty()) {
			return;
		}
		userDataMap.put(name, userData);
		curUser = name;
	}

	/**
	 * Load the given json data file and parse to create a UserDataMap
	 *
	 * @param activity - activity
	 * @param jsonFilename - json file name to load
	 *
	 * @return UserDataMap object if file is found, null otherwise
	 */
	static UserDataMap loadJson(Activity activity, String jsonFilename) {
		try {
			FileInputStream inputStream = activity.openFileInput(jsonFilename);
			byte[] data = new byte[inputStream.available()];
			if (inputStream.read(data) == -1) {
				Log.d("JSON", "No more data to read");
			}
			inputStream.close();

			String json = new String(data, "UTF-8");

			Gson gson = new Gson();
			Type type = new TypeToken<UserDataMap>(){}.getType();
			UserDataMap udm = gson.fromJson(json, type);
			if (udm.getCurUser() == null) {
				return null;
			}
			return udm;
		} catch (FileNotFoundException e) {
			Log.e("JSON", "json not found file="+jsonFilename);
		} catch (IOException e) {
			Log.e("JSON", "IO error loading file="+jsonFilename, e);
		} catch (JsonParseException e) {
			Log.e("JSON", "JSON parse error loading file="+jsonFilename, e);
		}
		return null;
	}

	private String toJson() {
		GsonBuilder gbuilder = new GsonBuilder();
		gbuilder.serializeNulls();
		gbuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		Gson gson = gbuilder.create();
		//Type type = new TypeToken<UserDataMap>(){}.getType();
		return gson.toJson(this, UserDataMap.class);
	}

	void saveJson(Activity mainActivity, String jsonFilename) {
		FileOutputStream outputStream;
		try {
			// don't save stats saved in userResults
			getUserData().results.clearStats(null, getCurUser());

			String json = toJson();
			outputStream = mainActivity.openFileOutput(jsonFilename, Context.MODE_PRIVATE);
			outputStream.write(json.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			Log.e("JSON", "File not found: "+jsonFilename, e);
		} catch (IOException e) {
			Log.e("JSON", "IO exception reading file: "+jsonFilename, e);
		}
	}


	Set<String> users() {
		return userDataMap.keySet();
	}

	boolean hasUser(String name) {
		return userDataMap.containsKey(name);
	}

	UserData deleteUser(String name) {
		if (name.equals(defaultUser)) {
			throw new RuntimeException(String.format("Attempting to delete defaultUser: %s", defaultUser));
		}
		if (hasUser(name) && hasUser(defaultUser)) {
			userDataMap.remove(name);
			setCurUser(defaultUser);
			return getUserData();
		}
		throw new RuntimeException(String.format("userDataMap is missing name (%s) or defaultUser (%s)", name, defaultUser));
	}
}
