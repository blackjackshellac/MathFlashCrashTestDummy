package com.oneguycoding.mathflashcrashtestdummy;

import android.app.Activity;
import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
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
 * Created by steeve on 13/09/17.
 */

public class UserDataMap implements Serializable {
//	@SerializedName("VERSION_USERDATAMAP")
	public static final String VERSION_USERDATAMAP = "20170914";
//	@SerializedName("curUser")
	private String curUser;
//	@SerializedName("userDataMap")
	private final HashMap<String,UserData> userDataMap;
	private final String defaultUser;

	UserDataMap(String name) {
		userDataMap = new HashMap<String,UserData>();
		defaultUser = new String(name);
		UserData userData = new UserData(defaultUser, "");
		addUserData(userData);
	}

	public String getCurUser() {
		return curUser;
	}

	/**
	 * Set the current user
	 *
	 * @param name user to set as current user
	 *
	 */
	public UserData setCurUser(String name) {
		if (this.hasUser(name)) {
			this.curUser = name;
			return getUserData();
		}
		throw new RuntimeException(String.format("Unknown user: %s", name));

	}

	public UserData getUserData() {
		return userDataMap.get(curUser);
	}

	/**
	 * Add new userData object for given user, replace if it already exists and set curUser to the new user
	 * @param userData
	 */
	public void addUserData(UserData userData) {
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
	 * @param activity
	 * @param jsonFilename
	 *
	 * @return UserDataMap object if file is found, null otherwise
	 */
	public static UserDataMap loadJson(Activity activity, String jsonFilename) {
		try {
			FileInputStream inputStream = activity.openFileInput(jsonFilename);
			byte[] data = new byte[(int) inputStream.available()];
			inputStream.read(data);
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
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
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

	public void saveJson(Activity mainActivity, String jsonFilename) {
		FileOutputStream outputStream = null;
		try {
			String json = toJson();
			outputStream = mainActivity.openFileOutput(jsonFilename, Context.MODE_PRIVATE);
			outputStream.write(json.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Set<String> users() {
		return userDataMap.keySet();
	}

	public boolean hasUser(String name) {
		return userDataMap.containsKey(name);
	}

	public boolean isEmpty() {
		return userDataMap.isEmpty();
	}

	public UserData deleteUser(String name) {
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
