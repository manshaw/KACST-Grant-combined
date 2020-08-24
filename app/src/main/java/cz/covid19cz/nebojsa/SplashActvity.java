package cz.covid19cz.nebojsa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActvity extends AppCompatActivity {

    private static final String PACAKGE_NAME = "cz.covid19cz.nebojsa";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("saved", false).apply();
        if (sharedPreferences.getInt("FavPlacesNum", 0) > 0) {
            startRiskActivity();
        } else {
            startMainActivity();
        }
    }

    public void startRiskActivity() {
        Intent intent = new Intent(this, RiskActivity.class);
        startActivity(intent);
        finish();
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
