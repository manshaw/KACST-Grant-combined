package cz.covid19cz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import cz.covid19cz.erouska.ui.main.MainActivity;
import cz.covid19cz.nebojsa.introSlide1;
import cz.covid19cz.smartcough.SplashActivityCough;

public class SelectionActivity extends AppCompatActivity {

    ImageView nebojsa, smartcough, covidtrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        nebojsa = findViewById(R.id.nebojsa_app);
        smartcough = findViewById(R.id.smartcough_app);
        covidtrace = findViewById(R.id.covidtrace_app);
        nebojsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNebojsaApp();
            }
        });
        smartcough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSmartcoughApp();
            }
        });
        covidtrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProximApp();
            }
        });
    }

    private void startNebojsaApp() {
        Intent selfActivityIntent = new Intent(this, introSlide1.class);
        startActivity(selfActivityIntent);
        finish();
    }

    private void startSmartcoughApp() {
        Intent selfActivityIntent = new Intent(this, SplashActivityCough.class);
        startActivity(selfActivityIntent);
        finish();
    }

    private void startProximApp() {
        Intent selfActivityIntent = new Intent(this, MainActivity.class);
        startActivity(selfActivityIntent);
        finish();
    }
}
