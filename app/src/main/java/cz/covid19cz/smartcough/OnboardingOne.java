package cz.covid19cz.smartcough;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cz.covid19cz.erouska.R;
import cz.covid19cz.smartcough.utils.AppLanguageManager;
import cz.covid19cz.smartcough.utils.LanguageCheck;

public class OnboardingOne extends AppCompatActivity {
    private static final String PACAKGE_NAME = "ncra.org.pk.audiocovid";
    FloatingActionButton btnOne;
    SharedPreferences sharedPreferences;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("firstTime", true)) {
            if (!LanguageCheck.isAlreadyCalled) {
                selectLanguageDialog();
            }
            if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
                setContentView(R.layout.onboarding_one_ar);
                btnOne = findViewById(R.id.btnOne);
                btnOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startNextActivity();
                    }
                });
            } else {
                setContentView(R.layout.onboarding_one);
                btnOne = findViewById(R.id.btnOne);
                btnOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startNextActivity();
                    }
                });
            }
        } else {
            startMainActivity();
        }
    }

    public void startNextActivity() {
        Intent nextActivity = new Intent(this, OnboardingTwo.class);
        startActivity(nextActivity);
        finish();
    }

    public void startMainActivity() {
        Intent nextActivity = new Intent(this, MainActivity.class);
        startActivity(nextActivity);
        finish();
    }


    private void selectLanguageDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_select_language, null);
        LinearLayout llLanguageEnglish = promptsView.findViewById(R.id.llLanguageEnglish);
        LinearLayout llLanguageUrdu = promptsView.findViewById(R.id.llLanguageCzech);
        llLanguageEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppLanguageManager(OnboardingOne.this).setAppLanguage("en");
                alertDialog.dismiss();
                startSelfActivity();
            }
        });

        llLanguageUrdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppLanguageManager(OnboardingOne.this).setAppLanguage("ar");
                alertDialog.dismiss();
                startSelfActivity();
            }
        });
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        LanguageCheck.isAlreadyCalled = true;
    }

    private void startSelfActivity() {
        Intent selfActivityIntent = new Intent(this, OnboardingOne.class);
        startActivity(selfActivityIntent);
        finish();
    }
}
