package net.basilwang.utils;

import net.basilwang.dao.Preferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

	public static String getPreferSemester(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.CURRICULUM_TO_SHOW, "");
	}

	public static void modifyIntValueInPreferences(Context context, String key,
			int value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void modifyStrngValueInPreferences(Context context,
			String key, String value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}
}
