package cz.covid19cz.nebojsa;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;

public class CustomName extends AppCompatActivity {

    public String placeName, placeAddress, customName;
    public TextView tv_placeName, tv_placeAddress, tv_addPlace;
    public EditText et_customName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            setContentView(R.layout.activity_custom_name_cz);
        } else {
            setContentView(R.layout.activity_custom_name);
        }
        placeName = getIntent().getStringExtra("placeName");
        placeAddress = getIntent().getStringExtra("placeAddress");
        initUI();
        initFunction();
    }

    public void initUI() {
        tv_placeName = findViewById(R.id.placeName);
        tv_placeAddress = findViewById(R.id.placeAddress);
        et_customName = findViewById(R.id.customName);
        tv_addPlace = findViewById(R.id.tv_AddPlace);
    }

    public void initFunction() {
        tv_placeName.setText(placeName);
        tv_placeAddress.setText(placeAddress);
        tv_addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customName = et_customName.getText().toString().trim();
            }
        });
    }
}
