package com.kutirakushala.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREF_NAME     = "settings";
    private static final String PREF_LANGUAGE = "language";

    /**
     * Call this from every Activity's attachBaseContext.
     */
    public static Context onAttach(Context context) {
        String language = getLanguage(context);
        return setLocale(context, language);
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_LANGUAGE, "en"); // default English
    }

    public static Context setLocale(Context context, String language) {
        saveLanguage(context, language);
        return updateResources(context, language);
    }

    private static void saveLanguage(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_LANGUAGE, language).apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}