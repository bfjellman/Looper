package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;


import com.facebook.AccessToken;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.mckayscience.looper.data.UserSongsDb;
import org.mckayscience.looper.model.UserInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Looper activity is responsible for recording tracks to go into songs.
 */
public class LooperActivity extends Activity {
    /** The mediaplayer object to use for playing */
    private MediaPlayer mediaPlayer;
    /** MediaRecorder object for recording audio */
    private MediaRecorder recorder;
    /** Location to save the tracks */
    private String OUTPUT_FILE;
    //Variables to track recording/playing
    private boolean recordBool;
    private boolean playBool;
    private Button recordBtn;
    private Button playBtn;
    /** TextView that holds the song name */
    private TextView songName;
    /** Track the current track number for adding new tracks */
    private int currentTrack;
    /** Song name */
    private String mSong;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        //Grab TextView for song name
        songName = (TextView)findViewById(R.id.looper_song);

        //Get the song name from shared preferences
        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        //Set the song name in the TextView
        songName.setText(sharedPreferences.getString("currentSong", null));
        mSong = sharedPreferences.getString("currentSong", null);

        currentTrack = 0;
        //Set the output file.
        OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));
        recordBool = true;
        playBool = true;
    }

    /**
     * Creates a destination string to set the output file
     * @param track The track number for creating file name.
     * @return A string value for the desintation and filename of the current track.
     */
    private String setOutputFile(String track) {

        String userId = sharedPreferences.getString("CurrentUser", null);
        //String userId = AccessToken.getCurrentAccessToken().getUserId();

        //check if Android org.mckayscience.looper.Looper directory exists, if not, create one
        boolean isDir = (new File(Environment.getExternalStorageDirectory() + "/AndroidLooper")).exists();
        if(!isDir) {
            new File(Environment.getExternalStorageDirectory() + "/AndroidLooper").mkdir();
        }
        //check if userID directory exists, if not, create one
        isDir = (new File(Environment.getExternalStorageDirectory() + "/AndroidLooper/" + userId)).exists();
        if(!isDir) {
            new File(Environment.getExternalStorageDirectory() + "/AndroidLooper/" + userId).mkdir();
        }
        String songString = songName.getText().toString();
        //remove all white space from song name and all letters lowercase
        songString = songString
                .replaceAll("\\s+", "")
                .toLowerCase();

        //return path for new track
        return Environment.getExternalStorageDirectory()+ "/AndroidLooper/" + userId + "/" + songString + track + ".3gpp";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * OnClick method for button click to take the user back to the Menu.
     *
     * @param v View object associated with the button.
     */
    public void toMenu_OnClick(View v) {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    /**
     * Onclick method for button click to Save the current song.
     * @param v View object associated with the button.
     */
    public void save_OnClick(View v) {
        //Testing TextView to display all songs from the user in the Song window.
        TextView test = (TextView)findViewById(R.id.test);
        //Open database
        UserSongsDb userDB = new UserSongsDb(getApplicationContext());
        //Insert the song into the database
        userDB.insertSong(sharedPreferences.getString("CurrentUser", null), songName.getText().toString(), OUTPUT_FILE, "", "", "", "");
        //Populate the textView with songs from this user
        List<UserInfo> mList = userDB.selectUsers(sharedPreferences.getString("CurrentUser", null));
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mList.size(); i++) {
            sb.append(mList.get(i));
            sb.append("\n");
        }
        test.setText(sb.toString());
        //Close the database
        userDB.closeDB();


        //Changes Track into BYTE[] Form.
        File songTrack = new File(OUTPUT_FILE);

        byte[] bFile = new byte[(int) songTrack.length()];
        FileInputStream fileInputStream = null;

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(songTrack);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        //create a parsefile to send to cloud server
        ParseFile file = new ParseFile(mSong + currentTrack + ".3gpp", bFile);
        file.saveInBackground();
        ParseObject jobApplication = new ParseObject("JobApplication");
        jobApplication.put("applicantName", sharedPreferences.getString("CurrentUser", null));
        jobApplication.put("applicantResumeFile", file);
        jobApplication.saveInBackground();

    }

    /**
     * Onclick method for record button.
     * @param v The View for this button.
     * @throws IOException Exception handling.
     */
    public void record_OnClick(View v) throws IOException {
        recordBtn = (Button) findViewById(R.id.record_Btn_ID);

        //Check if there is something recording, if not, then record.
        if(recordBool) {
            startRecording();
            Toast.makeText(this, "recording", Toast.LENGTH_SHORT).show();
            recordBtn.setText("Stop");

        }
        if(!recordBool){
            stopRecording();
            Toast.makeText(this, "stopping", Toast.LENGTH_SHORT).show();
            recordBtn.setText("Record");

        }
        recordBool = !recordBool;
    }

    /**
     * Onclick method for the play button.
     * @param v The View for the button.
     * @throws IOException Can throw IOException.
     */
    public void play_OnClick(View v) throws IOException {
        playBtn = (Button) findViewById(R.id.play_Btn_ID);

        File outFile = new File(OUTPUT_FILE);
        if(!outFile.exists()) {
            return;
        }

        if(playBool) {
            playBtn.setText("Stop");
            playRecording();
        }
        if(!playBool){
            playBtn.setText("Play");
            stopPlayback();
        }
        playBool = !playBool;
    }

    /**
     * Method to begin recording.
     *
     * @throws IOException Throws IOException
     */
    private void startRecording() throws IOException {
        ditchMediaRecorder(); //Method to Releases resources associated with this MediaRecorder object.
        //create output file
        File outFile = new File(OUTPUT_FILE);

        //if the output file already exists, delete it.
        if(outFile.exists())
            outFile.delete();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Set audio source. (Example...Camera, phone conversation, microphone)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Set format to 3gpp
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Encoding in advanced audio codec because it sounded cool
        recorder.setOutputFile(OUTPUT_FILE); //location
        recorder.prepare(); //Prepares the recorder to begin capturing and encoding data.
        recorder.start(); //Begins capturing and encoding data to the file specified with setOutputFile().

    }

    /**
     * Method to stop the recording.
     */
    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
        }

    }

    /**
     * Method to play the recording.
     *
     * @throws IOException Throws IOException.
     */
    private void playRecording() throws IOException {
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare(); //Prepares the recorder to begin capturing and encoding data. Must always prepare before starting, bleh
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


    }

    /**
     * Method to stop the curent playback.
     */
    private void stopPlayback(){
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }

    /**
     * Method to release the resources assocaited with the MediaPlayer
     */
    private void ditchMediaPlayer() {
        if(mediaPlayer != null)
            try {
                mediaPlayer.release();//Releases resources associated with this MediaPlayer object.
            }
            catch(Exception e){
                e.printStackTrace();
            }
    }

    /**
     * Method to release the recources associated with the MediaRecorder
     */
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
