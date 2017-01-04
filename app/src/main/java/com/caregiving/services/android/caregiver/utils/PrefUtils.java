package com.caregiving.services.android.caregiver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by aloys on 30/9/2016.
 */
public class PrefUtils {
    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__";
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__";
    public static final String PREFS_ROLE_KEY = "_ROLE_";
    public static final String PREFS_USER_ID = "_USERID_";

    public static void saveStringToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveIntToPrefs(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveBooleanToPrefs(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void removeFromPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public static String getStringFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getIntFromPrefs(Context context, String key, int defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean getBooleanFromPrefs(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean resetLoginPrefs(Context context) {
        try {
            PrefUtils.removeFromPrefs(
                    context,
                    PrefUtils.PREFS_LOGIN_USERNAME_KEY);

            PrefUtils.removeFromPrefs(
                    context,
                    PrefUtils.PREFS_LOGIN_PASSWORD_KEY);

            PrefUtils.removeFromPrefs(
                    context,
                    PrefUtils.PREFS_ROLE_KEY);

            PrefUtils.removeFromPrefs(
                    context,
                    PrefUtils.PREFS_USER_ID);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserId(Context context) {
        return getIntFromPrefs(context, PREFS_USER_ID, 0);
    }
}
