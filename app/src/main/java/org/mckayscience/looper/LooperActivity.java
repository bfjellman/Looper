package org.mckayscience.looper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class LooperActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;
    private boolean blah;
    private boolean blah2;
    private Button recordBtn;
    private Button playBtn;

    //http://developer.android.com/reference/android/media/MediaPlayer.html
    //http://developer.android.com/reference/android/media/MediaRecorder.html



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);
        OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/audiorecorder.3gpp"; //Setting directory string
        blah = true;
        blah2 = true;
    }

    public void toMenu_OnClick(View v) {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    public void save_OnClick(View v) {

    }

    public void record_OnClick(View v) throws IOException {
        recordBtn = (Button) findViewById(R.id.record_Btn_ID);

        if(blah) {
            startRecording();
            Toast.makeText(this, "recording", Toast.LENGTH_SHORT).show();
            recordBtn.setText("Stop");

        }
        if(!blah){
            stopRecording();
            Toast.makeText(this, "stopping", Toast.LENGTH_SHORT).show();
            recordBtn.setText("Record");

        }
        blah = !blah;
    }

    public void play_OnClick(View v) throws IOException {
        playBtn = (Button) findViewById(R.id.play_Btn_ID);

        if(blah2) {
            playRecording();
            playBtn.setText("Stop");
        }
        if(!blah2){
            stopPlayback();
            playBtn.setText("Play");
        }
        blah2 = !blah2;
    }


    private void startRecording() throws IOException {
        ditchMediaRecorder(); //Method to Releases resources associated with this MediaRecorder object.
        //MEOW!!
        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists())
            outFile.delete();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Set audio source. (Example...Camera, phone conversation, microphone)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Set format to 3gpp
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Encoding in advanced audio codec because it sounded cool
        recorder.setOutputFile(OUTPUT_FILE); //location
        recorder.prepare(); //Prepares the recorder to begin capturing and encoding data.
        recorder.start(); //Begins capturing and encoding data to the file specified with setOutputFile().

        System.out.println("Finished StartRecording Method");

    }
    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
        }
        System.out.println("Finished Stop Recording Method");

    }
    private void playRecording() throws IOException {
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare(); //Prepares the recorder to begin capturing and encoding data. Must always prepare before starting, bleh
        mediaPlayer.start();

        System.out.println("Finished Play Recording Method");

    }
    private void stopPlayback(){
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }

    private void ditchMediaPlayer() {
        if(mediaPlayer != null)
            try {
                mediaPlayer.release();//Releases resources associated with this MediaPlayer object.
            }
            catch(Exception e){
                e.printStackTrace();
            }
    }

    private void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();//Releases resources associated with this MediaRecorder object.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_looper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
