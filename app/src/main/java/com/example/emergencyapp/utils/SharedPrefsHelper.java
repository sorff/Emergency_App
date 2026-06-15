package com.example.emergencyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    private static final String PREFS_NAME = "EmergencyPrefs";
    private static final String KEY_NOM = "nom";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_SE_RAPPELER = "se_rappeler";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_LOCALISATION = "localisation";
    private static final String KEY_BLOOD_TYPE = "blood_type";
    private static final String KEY_CONDITIONS = "conditions";

    private static SharedPreferences getPrefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveNom(Context ctx, String nom) {
        getPrefs(ctx).edit().putString(KEY_NOM, nom).apply();
    }

    public static String getNom(Context ctx) {
        return getPrefs(ctx).getString(KEY_NOM, "");
    }

    public static void saveTelephone(Context ctx, String telephone) {
        getPrefs(ctx).edit().putString(KEY_TELEPHONE, telephone).apply();
    }

    public static String getTelephone(Context ctx) {
        return getPrefs(ctx).getString(KEY_TELEPHONE, "");
    }

    public static void saveSeRappeler(Context ctx, boolean seRappeler) {
        getPrefs(ctx).edit().putBoolean(KEY_SE_RAPPELER, seRappeler).apply();
    }

    public static boolean isSeRappeler(Context ctx) {
        return getPrefs(ctx).getBoolean(KEY_SE_RAPPELER, false);
    }

    public static void saveNotifications(Context ctx, boolean active) {
        getPrefs(ctx).edit().putBoolean(KEY_NOTIFICATIONS, active).apply();
    }

    public static boolean isNotificationsActive(Context ctx) {
        return getPrefs(ctx).getBoolean(KEY_NOTIFICATIONS, true);
    }

    public static void saveLocalisation(Context ctx, boolean active) {
        getPrefs(ctx).edit().putBoolean(KEY_LOCALISATION, active).apply();
    }

    public static boolean isLocalisationActive(Context ctx) {
        return getPrefs(ctx).getBoolean(KEY_LOCALISATION, true);
    }

    public static void saveBloodType(Context ctx, String type) {
        getPrefs(ctx).edit().putString(KEY_BLOOD_TYPE, type).apply();
    }

    public static String getBloodType(Context ctx) {
        return getPrefs(ctx).getString(KEY_BLOOD_TYPE, "");
    }

    public static void saveConditions(Context ctx, String conditions) {
        getPrefs(ctx).edit().putString(KEY_CONDITIONS, conditions).apply();
    }

    public static String getConditions(Context ctx) {
        return getPrefs(ctx).getString(KEY_CONDITIONS, "");
    }
}
