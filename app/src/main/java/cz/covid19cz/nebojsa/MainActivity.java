package cz.covid19cz.nebojsa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;


public class MainActivity extends AppCompatActivity {

    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    String category = "pharmacy";
    TextView bpharmacy, bpark, bgrocery;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
//        if (sharedPreferences.getBoolean("firstTime", true)) {
//            sharedPreferences.edit().putInt("FavPlacesNum", 0).apply();
        sharedPreferences.edit().putBoolean("firstTime", false).apply();
//        } else {
//            if (sharedPreferences.getInt("FavPlacesNum", 0) > 0 && sharedPreferences.getBoolean("fromStart", true))
//                startRiskActivity();
//        }
//        sharedPreferences.edit().putBoolean("fromStart", true).apply();
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            setContentView(R.layout.activity_main_cz);
        } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            setContentView(R.layout.activity_main_ar);
        } else {
            setContentView(R.layout.activity_main_nebosja);
        }
        bpharmacy = findViewById(R.id.bpharmacy);
        bpark = findViewById(R.id.bpark);
        bgrocery = findViewById(R.id.bgrocery);

//        maps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MapActivity.class);
//                int selectedId = radioGroupCategory.getCheckedRadioButtonId();
//                RadioButton button = (RadioButton) findViewById(selectedId);
//                category = button.getText().toString();
//                if(category.equals("Grocery Store"))
//                    category = "grocery_or_supermarket";
//                intent.putExtra("category",category);
//                startActivity(intent);
//            }
//        });
        bpharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapsActivity("pharmacy");
            }
        });
        bpark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapsActivity("park");
            }
        });
        bgrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapsActivity("shop");
            }
        });
    }

    public void startMapsActivity(String category) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sharedPreferences.getInt("FavPlacesNum", 0) == 0) {
            finishAffinity();
        }
    }

    public void startHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void startRiskActivity() {
        Intent intent = new Intent(this, RiskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
    }
}
