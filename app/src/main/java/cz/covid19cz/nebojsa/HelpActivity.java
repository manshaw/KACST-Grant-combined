package cz.covid19cz.nebojsa;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import cz.covid19cz.erouska.R;


public class HelpActivity extends AppCompatActivity {

    EditText age, condition;
    Button submit, Download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        age = findViewById(R.id.age);
        condition = findViewById(R.id.condition);
        submit = findViewById(R.id.submit);
        Download = findViewById(R.id.download);

        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.chronorobotics.fel.cvut.cz/static/explorer.apk"));
                startActivity(browserIntent);
//                if(haveStoragePermission())
//                {
//                    String myurl = "https://app.chronorobotics.fel.cvut.cz/static/explorer.apk";
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myurl));
//                    request.setTitle("Explorer.apk");
//                    request.setDescription("Data gathering application");
//                    request.allowScanningByMediaScanner();
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    String filename = URLUtil.guessFileName(myurl, null, MimeTypeMap.getFileExtensionFromUrl(myurl));
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
//                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    manager.enqueue(request);
//                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Write a message to the database
                age.setText("");
                condition.setText("");
                Toast.makeText(HelpActivity.this, "Thank you for your support", Toast.LENGTH_LONG).show();
            }
        });


    }

    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //you have the permission now.
            String myurl = "https://app.chronorobotics.fel.cvut.cz/static/explorer.apk";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myurl));
            request.setTitle("Explorer.apk");
            request.setDescription("Data gathering application");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String filename = URLUtil.guessFileName(myurl, null, MimeTypeMap.getFileExtensionFromUrl(myurl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startMapsActivity(String category) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void startRiskActivity() {
        Intent intent = new Intent(this, RiskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
