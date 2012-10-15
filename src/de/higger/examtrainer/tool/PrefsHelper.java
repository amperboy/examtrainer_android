package de.higger.examtrainer.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
	public enum Preferences {
		PREF_WS_URI
	}

	public static final String PREF_FILE = "examdb";

	private Context context;

	public PrefsHelper(Context context) {
		this.context = context;
	}

	public void store(Preferences propertyKey, String propertyValue) {
		SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(propertyKey.toString(), propertyValue);
		editor.commit();
	}

	public String read(Preferences propertyKey) {
		SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
		String propertyValue = settings.getString(propertyKey.toString(), null);
		
		return propertyValue;
	}
}
