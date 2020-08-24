package com.userobtain25.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppPreferences {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;


    public static void setToken(Context ctx, String value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("Token", value);
        mPrefsEditor.commit();
    }

    public static String getToken(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("Token", null);
    }


    public static void setPackageId(Context ctx, String value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("PackageId", value);
        mPrefsEditor.commit();
    }

    public static String getPackageId(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("PackageId", null);
    }


    public static Set<String> getName(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getStringSet("nameList", null);
    }

    public static void setName(Context ctx, Set<String> value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        Set<String> set = new HashSet<>();
        set.addAll(value);
        mPrefsEditor.putStringSet("nameList", set);
        mPrefsEditor.commit();
    }


    public static void setId(Context ctx, ArrayList<String> value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        Set<String> set = new HashSet<>(value);
        mPrefsEditor.putStringSet("id", set);
        mPrefsEditor.apply();
    }

    public static Set<String> getId(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getStringSet("id", null);
    }


    public static void setTitle(Context ctx, String value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("Title", value);
        mPrefsEditor.commit();
    }

    public static String getTitle(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("Title", null);
    }


    public static void setLati(Context ctx, Double value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("lati", value + "");
        mPrefsEditor.commit();
    }

    public static String getLati(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("lati", null);
    }

    public static void setLongi(Context ctx, Double value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("longi", value + "");
        mPrefsEditor.commit();
    }

    public static String getLongi(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("longi", null);
    }


}