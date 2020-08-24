package cz.covid19cz.smartcough;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cz.covid19cz.erouska.R;
import pl.droidsonroids.gif.GifImageView;

public class DetectionActivity extends AppCompatActivity {

    public GifImageView gifResult;
    public TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        setUI();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        gifResult.setImageResource(R.drawable.positive_result);
                        tvResult.setText("Congratulations!!! You are safe.");
                        tvResult.setTextColor(getResources().getColor(R.color.positive));
                    }
                }, 5000);
    }

    public void setUI() {
        gifResult = findViewById(R.id.gif_result);
        tvResult = findViewById(R.id.tv_result);
    }
}
