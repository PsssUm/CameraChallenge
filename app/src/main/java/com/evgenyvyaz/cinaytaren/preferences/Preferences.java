package com.evgenyvyaz.cinaytaren.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class Preferences {
	private static final String MY_ID = "myId";
    private static final String USER_NAME = "USER_NAME";

    public static String getMyId(Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(MY_ID, null);
    }

    public static void setMyId(Context context, String id) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(MY_ID, id);
        ed.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(USER_NAME, null);
    }

    public static void setUserName(Context context, String id) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(USER_NAME, id);
        ed.apply();
    }

    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LONG";

    public static String getMyLat(Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(MY_LAT, null);
    }

    public static void setMyLat(Context context, String lat) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(MY_LAT, lat);
        ed.apply();
    }
    public static String getMyLong (Context context) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sPref.getString(MY_LNG, null);
    }

    public static void setMyLong(Context context, String lng) {
        SharedPreferences sPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(MY_LNG, lng);
        ed.apply();
    }
}
