package com.example.ashsandra.texttospeechdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    private Spinner mSpinner;
    private Button mButtondownload;
    private DownloadManager downloadManager;
    private long reference;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonSpeak = findViewById(R.id.Convert);
        mSpinner = findViewById(R.id.spinner1);
        mEditText = findViewById(R.id.edit_text);
        mButtondownload = findViewById(R.id.large_text);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toast = new Toast(this);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEditText.setEnabled(true);
                String item = adapterView.getItemAtPosition(i).toString();
                if (item == "Chinese"){
                    mTTS.setLanguage(Locale.SIMPLIFIED_CHINESE);
                }
                else if (item == "English"){
                    mTTS.setLanguage(Locale.ENGLISH);
                }
                else if (item == "French"){
                    mTTS.setLanguage(Locale.FRENCH);
                }
                else if (item == "German"){
                    mTTS.setLanguage(Locale.GERMAN);
                }
                else if (item == "Italian"){
                    mTTS.setLanguage(Locale.ITALIAN);
                }
                else if (item == "Japanese"){
                    mTTS.setLanguage(Locale.JAPANESE);
                }
                else if (item == "Spanish"){
                    mTTS.setLanguage(Locale.KOREAN);
                }
                else {
                    Log.e("language choice error","language not supported");
                }

            mButtondownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startdownload();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });

                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            DownloadManager.Query req_query = new DownloadManager.Query();
                            req_query.setFilterById(reference);
                            Cursor c = downloadManager.query(req_query);
                            if (c.moveToFirst()){
                                int column_index = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(column_index)){
                                    try {
                                        downloadManager.openDownloadedFile(reference);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.makeText(getApplicationContext(),"Download is Completed", Toast.LENGTH_LONG).show();




                                }

                            }




                        }
                    }

                };
                registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            }

            private void startdownload() throws FileNotFoundException {
                downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://d3bxy9euw4e147.cloudfront.net/oscms-prodcms/media/documents/Prealgebra-OP.pdf");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Textbook Download");
                request.setDescription("Textbook Downloading...");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                reference = downloadManager.enqueue(request);




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this,"Please select a language",Toast.LENGTH_SHORT).show();


            }
        });
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    mButtonSpeak.setEnabled(true);
                } else {
                    Log.e("Initialization", "Initialization failed");
                }

            }
        });

        mSeekBarPitch = findViewById(R.id.seekbarpitch);
        mSeekBarSpeed = findViewById(R.id.seekbarpitch);
        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditText.getText().toString();
                float pitch = (float) mSeekBarPitch.getProgress() / 50;
                if (pitch < 0.1) {
                    pitch = 0.1f;
                }
                float speed = (float) mSeekBarSpeed.getProgress() / 50;
                if (speed < 0.1) {
                    speed = 0.1f;
                }
                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}


