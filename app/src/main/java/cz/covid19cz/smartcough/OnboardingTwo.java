package cz.covid19cz.smartcough;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cz.covid19cz.erouska.R;
import cz.covid19cz.smartcough.utils.AppLanguageManager;

public class OnboardingTwo extends AppCompatActivity {
    private static final String PACAKGE_NAME = "ncra.org.pk.audiocovid";
    FloatingActionButton btnTwo;
    SharedPreferences prefs = null;
    TextView text1, text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            setContentView(R.layout.onboarding_two_ar);
            prefs.edit().putBoolean("firstTime", false).commit();
            btnTwo = findViewById(R.id.btnTwo);
            text1 = findViewById(R.id.text1);
            text2 = findViewById(R.id.text2);
            text1.setMovementMethod(new ScrollingMovementMethod());
            text2.setMovementMethod(new ScrollingMovementMethod());
            prefs = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
            btnTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.edit().putBoolean("SignedIn", true).commit();
                    startNextActivity();
                }
            });
        } else {
            setContentView(R.layout.onboarding_two);
            prefs.edit().putBoolean("firstTime", false).commit();
            btnTwo = findViewById(R.id.btnTwo);
            text1 = findViewById(R.id.text1);
            text2 = findViewById(R.id.text2);
            text1.setMovementMethod(new ScrollingMovementMethod());
            text2.setMovementMethod(new ScrollingMovementMethod());
            prefs = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
            btnTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.edit().putBoolean("SignedIn", true).commit();
                    startNextActivity();
                }
            });
        }
    }

    public void startNextActivity() {
        Intent nextActivity = new Intent(this, MainActivity.class);
        nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextActivity);
        finish();
    }
}
