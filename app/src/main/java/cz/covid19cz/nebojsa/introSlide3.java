package cz.covid19cz.nebojsa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;

public class introSlide3 extends AppCompatActivity {
    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    SharedPreferences sharedPreferences;
    TextView button;
    float x1, y1, x2, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("firstTime", true)) {
            if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
                setContentView(R.layout.intro_3_cz);
            } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
                setContentView(R.layout.intro_3_ar);
            } else {
                setContentView(R.layout.intro_3);
            }
            button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNextSlide();
                }
            });
        } else {
            startMainActivity();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case (MotionEvent.ACTION_DOWN): {
//                x1 = event.getX();
//                y1 = event.getY();
//                break;
//            }
//            case (MotionEvent.ACTION_UP): {
//                x2 = event.getX();
//                y2 = event.getY();
//
//                if ((x1 - x2) > 50) {
//                    startNextSlide();
//                }
//            }
//            break;
//            default:
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startNextSlide() {
        Intent intent = new Intent(introSlide3.this, introSlide4.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        finish();
    }
}
