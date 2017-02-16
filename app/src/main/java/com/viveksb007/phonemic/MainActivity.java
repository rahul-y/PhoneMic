package com.viveksb007.phonemic;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int AUDIO_RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = AudioRecord.getMinBufferSize(AUDIO_RECORDER_SAMPLE_RATE, RECORDER_CHANNEL, RECORDER_AUDIO_ENCODING);
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private Thread playBackThread;
    short[] audioData;
    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.btnStart);
        bufferSize += 2048;
        prepareRecorderAndPlayer();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playBackThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MicPlayBack();
                    }
                });
                playBackThread.start();
            }
        });
    }


    public void prepareRecorderAndPlayer() throws IllegalArgumentException {
        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    AUDIO_RECORDER_SAMPLE_RATE,
                    RECORDER_CHANNEL,
                    RECORDER_AUDIO_ENCODING,
                    bufferSize);


            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    AUDIO_RECORDER_SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    RECORDER_AUDIO_ENCODING,
                    bufferSize,
                    AudioTrack.MODE_STREAM);


            audioTrack.setPlaybackRate(AUDIO_RECORDER_SAMPLE_RATE);

        } catch (Throwable t) {
            audioRecord.startRecording();
            isRecording = true;
            audioTrack.play();
            Log.v(TAG, "Recording And Playing Started");
            Log.e(TAG, "Initialization Failed");
        }

    }

    public void MicPlayBack() {

        Log.v(TAG, "Reading And Writing Buffer Started");
        while (isRecording) {
            audioRecord.read(audioData, 0, bufferSize);
            audioTrack.write(audioData, 0, audioData.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecord.stop();
        audioRecord.release();
        audioTrack.stop();
        audioTrack.release();

    }
}
