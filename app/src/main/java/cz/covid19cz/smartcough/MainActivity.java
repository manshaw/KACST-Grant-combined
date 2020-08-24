package cz.covid19cz.smartcough;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import cz.covid19cz.erouska.R;
import cz.covid19cz.smartcough.utils.AppLanguageManager;
import pl.droidsonroids.gif.GifImageView;

// version 1.0.2

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    public MediaRecorder recorder = null;
    GifImageView coughing;
    TextView one, two;
    long lastDown;
    long lastDuration;
    Boolean fever, tired, diarreah, headache, taste;
    Boolean lung, diabetes, heart, obesity;
    int[] permissionResults;
    private AudioRecordTest.RecordButton recordButton = null;
    private AudioRecordTest.PlayButton playButton = null;
    private MediaPlayer player = null;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private Button record;
    private StorageReference mStorageRef;
    private AlertDialog alertDialog;
    private Integer age;
    private Boolean smoke, close_contact, dry_cough, breath;
    private String gender, medical_condition;


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted) {
//            //finish();
//            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        //abar.setCustomView(viewActionBar, params);
        //abar.setDisplayShowCustomEnabled(true);
        //abar.setDisplayShowTitleEnabled(false);
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            textviewTitle.setText(getResources().getString(R.string.app_name_ar));
            setContentView(R.layout.activity_record_ar);
        } else {
            textviewTitle.setText(getResources().getString(R.string.app_name));
            setContentView(R.layout.activity_record);
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("smartcoughandroid")
                .setApiKey(" AIzaSyBl2kK5O4ARkHNRbbGFheuT8vZ1lHHdC1k ")
                .setDatabaseUrl("https://smartcoughandroid.firebaseio.com/")
                .setStorageBucket("smartcoughandroid.appspot.com")
                .build();
        FirebaseApp.initializeApp(this, options, "smartcough");
        FirebaseApp mMySecondApp = FirebaseApp.getInstance("smartcough");
        mStorageRef = FirebaseStorage.getInstance(mMySecondApp).getReference();
//        initRecorder();
        record = findViewById(R.id.record);
        coughing = findViewById(R.id.coughing);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                } else {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        lastDown = System.currentTimeMillis();
                        if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast3_ar), Toast.LENGTH_SHORT).show();
                            record.setText(getResources().getString(R.string.record_screen_button_status5_ar));
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast3), Toast.LENGTH_SHORT).show();
                            record.setText(getResources().getString(R.string.record_screen_button_status5));
                        }
                        coughing.setImageResource(R.drawable.coughing);
                        one.setTextColor(getResources().getColor(R.color.colorInactive));
                        two.setTextColor(getResources().getColor(R.color.colorInactive));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        lastDuration = System.currentTimeMillis() - lastDown;
                        Log.d(LOG_TAG, "Duration: " + lastDuration);
                        if (lastDuration < 1000) {
                            if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast1_ar), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast1), Toast.LENGTH_LONG).show();
                            }
                            try {
                                stopRecording();
                                //   initRecorder();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                                record.setText(getResources().getString(R.string.record_screen_button_status1_ar));
                            } else {
                                record.setText(getResources().getString(R.string.record_screen_button_status1));
                            }
                            one.setTextColor(Color.BLACK);
                            two.setTextColor(Color.RED);
                            coughing.setImageResource(R.drawable.cough);
                        } else {
                            stopRecording();
                            startPlaying();
                            if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                                record.setText(getResources().getString(R.string.record_screen_button_status2_ar));
                            } else {
                                record.setText(getResources().getString(R.string.record_screen_button_status2));
                            }
                            coughing.setImageResource(R.drawable.cough);
                            one.setTextColor(Color.BLACK);
                            two.setTextColor(Color.BLACK);
                            consent();
                        }
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void consent() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_consent_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_consent, null);
        }
        Button play = promptsView.findViewById(R.id.btnPlay);
        Button yes = promptsView.findViewById(R.id.btnYes);
        Button no = promptsView.findViewById(R.id.btnNo);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                enterInfo();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                    record.setText(getResources().getString(R.string.record_screen_button_status1_ar));
                } else {
                    record.setText(getResources().getString(R.string.record_screen_button_status1));
                }
                return;
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void enterInfo() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_enter_info_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_enter_info, null);
        }
        final EditText tvAge = promptsView.findViewById(R.id.age);
        ArrayAdapter<String> adapter;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gender_array_ar));
        } else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gender_array));
        }
        final BetterSpinner tvGender = promptsView.findViewById(R.id.gender);
        tvGender.setAdapter(adapter);
        Button next = promptsView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvAge.getText())) {
                    tvAge.setError("Age is required!");
                } else if (TextUtils.isEmpty(tvGender.getText())) {
                    tvGender.setError("Gender is required!");
                } else {
                    age = Integer.parseInt(tvAge.getText().toString());
                    if (tvGender.getText().toString().equals("الذكر") || tvGender.getText().toString().equals("Male")) {
                        gender = "Male";
                    } else if (tvGender.getText().toString().equals("أنثى") || tvGender.getText().toString().equals("Female")) {
                        gender = "Female";
                    }
                    alertDialog.dismiss();
                    medicalCondition();
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void medicalCondition() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_medical_condition_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_medical_condition, null);
        }
        final TextView healthy = promptsView.findViewById(R.id.healthy);
        TextView maybe = promptsView.findViewById(R.id.maybe);
        TextView patient = promptsView.findViewById(R.id.patient);
        TextView recovered = promptsView.findViewById(R.id.recovered);
        healthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medical_condition = "Healthy";
                alertDialog.dismiss();
                smoke();
            }
        });
        maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medical_condition = "Might Have COVID";
                alertDialog.dismiss();
                smoke();
            }
        });
        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medical_condition = "Patient of COVID";
                alertDialog.dismiss();
                submit();
            }
        });
        recovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medical_condition = "Survivour of COVID";
                alertDialog.dismiss();
                smoke();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void smoke() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_smoke_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_smoke, null);
        }
        TextView yes = promptsView.findViewById(R.id.yes);
        TextView no = promptsView.findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoke = Boolean.TRUE;
                alertDialog.dismiss();
                close_contact();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoke = Boolean.FALSE;
                alertDialog.dismiss();
                close_contact();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void close_contact() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_close_contact_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_close_contact, null);
        }
        TextView yes = promptsView.findViewById(R.id.yes);
        TextView no = promptsView.findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_contact = Boolean.TRUE;
                alertDialog.dismiss();
                disease();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_contact = Boolean.FALSE;
                alertDialog.dismiss();
                disease();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void disease() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_disease_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_disease, null);
        }
        final CheckBox tvLung = promptsView.findViewById(R.id.lung);
        final CheckBox tvDiabetes = promptsView.findViewById(R.id.diabetes);
        final CheckBox tvHeart = promptsView.findViewById(R.id.heart);
        final CheckBox tvObesity = promptsView.findViewById(R.id.obesity);
        TextView none = promptsView.findViewById(R.id.none);
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lung = tvLung.isChecked();
                diabetes = tvDiabetes.isChecked();
                heart = tvHeart.isChecked();
                obesity = tvObesity.isChecked();
                alertDialog.dismiss();
                dry_cough();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void dry_cough() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_dry_cough_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_dry_cough, null);
        }
        TextView yes = promptsView.findViewById(R.id.yes);
        TextView no = promptsView.findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dry_cough = Boolean.TRUE;
                alertDialog.dismiss();
                breath();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dry_cough = Boolean.FALSE;
                alertDialog.dismiss();
                breath();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void breath() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_breath_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_breath, null);
        }
        TextView yes = promptsView.findViewById(R.id.yes);
        TextView no = promptsView.findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breath = Boolean.TRUE;
                alertDialog.dismiss();
                symptoms();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breath = Boolean.FALSE;
                alertDialog.dismiss();
                symptoms();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void symptoms() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_symptoms_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_symptoms, null);
        }
        final CheckBox cbFever = promptsView.findViewById(R.id.fever);
        final CheckBox cbTired = promptsView.findViewById(R.id.tired);
        final CheckBox cbDiarreah = promptsView.findViewById(R.id.diarreah);
        final CheckBox cbHeadache = promptsView.findViewById(R.id.headache);
        final CheckBox cbTaste = promptsView.findViewById(R.id.taste);
        TextView none = promptsView.findViewById(R.id.no);
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fever = cbFever.isChecked();
                tired = cbTired.isChecked();
                diarreah = cbDiarreah.isChecked();
                headache = cbHeadache.isChecked();
                taste = cbTaste.isChecked();
                alertDialog.dismiss();
                submit();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void submit() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            promptsView = li.inflate(R.layout.dialog_submit_ar, null);
        } else {
            promptsView = li.inflate(R.layout.dialog_submit, null);
        }
        TextView submit = promptsView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                    record.setText(getResources().getString(R.string.record_screen_button_status4_ar));
                } else {
                    record.setText(getResources().getString(R.string.record_screen_button_status4));
                }
                final Date date = new Date();
                uploadData(date.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Uri file = Uri.fromFile(new File(fileName));
                        StorageReference riversRef = mStorageRef.child("recordings/" + date.toString() + ".3gp");
                        riversRef.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content555
                                        if (new AppLanguageManager(MainActivity.this).getAppLanguage().equals("ar")) {
                                            record.setText(getResources().getString(R.string.record_screen_button_status1_ar));
                                            Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast2_ar), Toast.LENGTH_SHORT).show();
                                        } else {
                                            record.setText(getResources().getString(R.string.record_screen_button_status1));
                                            Toast.makeText(MainActivity.this, getResources().getString(R.string.record_screen_toast2), Toast.LENGTH_SHORT).show();
                                            new Handler().postDelayed(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startDetectionActivity();
                                                        }
                                                    }, 2000);
                                        }
                                        //initRecorder();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Toast.makeText(MainActivity.this, "Uploading Failed" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void uploadData(String date) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("smartcoughandroid")
                .setApiKey(" AIzaSyBl2kK5O4ARkHNRbbGFheuT8vZ1lHHdC1k ")
                .setDatabaseUrl("https://smartcoughandroid.firebaseio.com/")
                .build();
        FirebaseApp mMySecondApp = FirebaseApp.getInstance("smartcough");
//        mSecondDBRef = FirebaseDatabase.getInstance(mMySecondApp).getReference();
        FirebaseDatabase database = FirebaseDatabase.getInstance(mMySecondApp);
        DatabaseReference myRef = database.getReference("Users/");
        myRef.child(date).child("Age").setValue(age);
        myRef.child(date).child("Gender").setValue(gender);
        myRef.child(date).child("Medical Condition").setValue(medical_condition);
        if (!medical_condition.equals("Patient of COVID")) {
            myRef.child(date).child("Smoker").setValue(smoke);
            myRef.child(date).child("Close Contact").setValue(close_contact);
            myRef.child(date).child("Dry Cough").setValue(dry_cough);
            myRef.child(date).child("Breath").setValue(breath);
            myRef.child(date).child("Disease").child("Chronic Lung Disease").setValue(lung);
            myRef.child(date).child("Disease").child("Diabetes").setValue(diabetes);
            myRef.child(date).child("Disease").child("Heart Disease").setValue(heart);
            myRef.child(date).child("Disease").child("Obesity or Adipositas").setValue(obesity);
            myRef.child(date).child("Symptoms").child("Fever").setValue(fever);
            myRef.child(date).child("Symptoms").child("Tired or Weakness").setValue(tired);
            myRef.child(date).child("Symptoms").child("Diarreah").setValue(diarreah);
            myRef.child(date).child("Symptoms").child("Headache").setValue(headache);
            myRef.child(date).child("Symptoms").child("Lost of taste and smell").setValue(taste);
        }
    }

    public void startDetectionActivity() {
        Intent nextActivity = new Intent(this, DetectionActivity.class);
        startActivity(nextActivity);
        finish();
    }

    class RecordButton extends AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends AppCompatButton {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
}
