package cz.covid19cz.nebojsa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;
import cz.covid19cz.nebojsa.utility.LanguageCheck;

public class introSlide1 extends AppCompatActivity {
    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    SharedPreferences sharedPreferences;
    TextView button, disclaimer_head, textView;
    Button start;
    float x1, y1, x2, y2;
    CheckBox checkBox;
    Toast mToast = null;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        mToast = new Toast(this);

        if (sharedPreferences.getBoolean("firstTime", true)) {
            if (!LanguageCheck.isAlreadyCalled) {
                selectLanguageDialog();
            }
            setContentView(R.layout.disclaimer_dialog);
            textView = findViewById(R.id.disclaimer);
            disclaimer_head = findViewById(R.id.header);
            checkBox = findViewById(R.id.checkbox);
            start = findViewById(R.id.start);
            textView.setMovementMethod(new ScrollingMovementMethod());
            if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
                disclaimer_head.setText(getString(R.string.disclaimer_title_cz));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text_cz));
                start.setText(getString(R.string.disclaimer_button_text_cz));
                textView.setText(getString(R.string.disclaimer_text_cz));
            } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
                disclaimer_head.setText(getString(R.string.disclaimer_title_ar));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text_ar));
                start.setText(getString(R.string.disclaimer_button_text_ar));
                textView.setText(getString(R.string.disclaimer_text_ar));
            } else {
                disclaimer_head.setText(getString(R.string.disclaimer_title));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text));
                start.setText(getString(R.string.disclaimer_button_text));
                textView.setText(getString(R.string.disclaimer_text));
            }
            start.setBackgroundColor(Color.GRAY);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked())
                        start.setBackgroundColor(Color.parseColor("#F59662"));
                    else
                        start.setBackgroundColor(Color.GRAY);
                }
            });
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        if (new AppLanguageManager(introSlide1.this).getAppLanguage().equals("cz")) {
                            setContentView(R.layout.intro_1_cz);
                        } else if (new AppLanguageManager(introSlide1.this).getAppLanguage().equals("ar")) {
                            setContentView(R.layout.intro_1_ar);
                        } else {
                            setContentView(R.layout.intro_1);
                        }
                        button = findViewById(R.id.button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startNextSlide();
                            }
                        });
                    } else {
                        if (new AppLanguageManager(introSlide1.this).getAppLanguage().equals("cz")) {
                            showAToast(getString(R.string.disclaimer_button_tooltip_cz));
                        } else if (new AppLanguageManager(introSlide1.this).getAppLanguage().equals("ar")) {
                            showAToast(getString(R.string.disclaimer_button_tooltip_ar));
                        } else {
                            showAToast(getString(R.string.disclaimer_button_tooltip));
                        }
                    }
                }
            });

        } else {
            startMainActivity();
        }
    }

    public void showAToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, SplashActvity.class);
        startActivity(intent);
    }

    public void startNextSlide() {
        Intent intent = new Intent(this, introSlide2.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN): {
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case (MotionEvent.ACTION_UP): {
                x2 = event.getX();
                y2 = event.getY();

                if ((x1 - x2) > 50) {
                    if (checkBox.isChecked()) {
                        startNextSlide();
                    }
                }
            }
            break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    private void selectLanguageDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        String message = "";
        promptsView = li.inflate(R.layout.dialog_select_language, null);
        if (new AppLanguageManager(getApplicationContext()).getAppLanguage().equals("ar")) {
            message = "زبان منتخب کریں";
        } else {
            message = "Please Select Language";
        }
        TextView tvHeading = promptsView.findViewById(R.id.tvHeading);
        LinearLayout llLanguageEnglish = promptsView.findViewById(R.id.llLanguageEnglish);
        LinearLayout llLanguageUrdu = promptsView.findViewById(R.id.llLanguageCzech);
        LinearLayout llLanguageArabic = promptsView.findViewById(R.id.llLanguageArabic);
        tvHeading.setText(message);
        llLanguageEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppLanguageManager(introSlide1.this).setAppLanguage("en");
                alertDialog.dismiss();
                disclaimer_head.setText(getString(R.string.disclaimer_title));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text));
                start.setText(getString(R.string.disclaimer_button_text));
                textView.setText(getString(R.string.disclaimer_text));
            }
        });

        llLanguageUrdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppLanguageManager(introSlide1.this).setAppLanguage("cz");
                alertDialog.dismiss();
                disclaimer_head.setText(getString(R.string.disclaimer_title_cz));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text_cz));
                start.setText(getString(R.string.disclaimer_button_text_cz));
                textView.setText(getString(R.string.disclaimer_text_cz));
            }
        });
        llLanguageArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppLanguageManager(introSlide1.this).setAppLanguage("ar");
                alertDialog.dismiss();
                disclaimer_head.setText(getString(R.string.disclaimer_title_ar));
                checkBox.setText(getString(R.string.disclaimer_checkbox_text_ar));
                start.setText(getString(R.string.disclaimer_button_text_ar));
                textView.setText(getString(R.string.disclaimer_text_ar));
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
        Intent selfActivityIntent = new Intent(this, introSlide1.class);
        startActivity(selfActivityIntent);
        finish();
    }
}
