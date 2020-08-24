package cz.covid19cz.nebojsa.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppLanguageManager {


    private final static String TAG = "Language";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;


    private String FILE_NAME = "pk.com.pakzarzameen.c19places";
    private String KEY_APP_LANGUAGE = "app_language";
    private int MODE = 0;

    public AppLanguageManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(FILE_NAME, MODE);
        editor = preferences.edit();
    }

    public String getAppLanguage() {
        String langugae = null;
        langugae = preferences.getString(KEY_APP_LANGUAGE, "en");
        Log.v(TAG, "Setting getAppLanguage: " + langugae);
        return langugae;
    }

    public void setAppLanguage(String language) {
        editor.putString(KEY_APP_LANGUAGE, language);
        editor.commit();
    }

}
