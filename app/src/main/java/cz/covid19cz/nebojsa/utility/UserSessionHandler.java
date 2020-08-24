package cz.covid19cz.nebojsa.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionHandler {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;


    private String FILE_NAME = "session";
    private String KEY_IS_USER_LOGGED_IN = "isLoggedIn";
    private int MODE = 0;

    public UserSessionHandler(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(FILE_NAME, MODE);
        editor = preferences.edit();
    }

    public Boolean getState() {
        Boolean isUserLoggedIn = preferences.getBoolean(KEY_IS_USER_LOGGED_IN, false);
        return isUserLoggedIn;
    }

    public void setState(Boolean isUserLoggedIn) {
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, isUserLoggedIn);
        editor.commit();
    }

}
