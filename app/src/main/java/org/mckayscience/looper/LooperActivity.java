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


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.mckayscience.looper.data.UserSongsDb;
import org.mckayscience.looper.model.Track;
import org.mckayscience.looper.model.UserInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Looper activity is responsible for recording tracks to go into songs.
 */
public class LooperActivity extends Activity {

    private static final int TOTAL_TRACKS = 5;

    /** MediaRecorder object for recording audio */
    private MediaRecorder recorder;
    private MediaPlayer[] mediaPlayer;
    /** Location to save the tracks */
    private String OUTPUT_FILE;
    //Variables to track recording/playing
    private boolean recordBool;
    private boolean playBool;
    /** TextView that holds the song name */
    private TextView songName;
    /** Track the current track number for adding new tracks */
    private int currentTrack;
    /** Song name */
    private String songString;
    private SharedPreferences sharedPreferences;

    private boolean isGuest;

    //track whether recordings exist per track
    private Track[] tracks;

    //Delete buttons
    Button btn0Delete;
    Button btn1Delete;

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
        //set if guest or not
        if (sharedPreferences.getString("CurrentUser", null).equals("Guest")) {
            isGuest = true;
        } else {
            isGuest = false;
        }


        currentTrack = 0;
        mediaPlayer = new MediaPlayer[5];
        tracks = new Track[TOTAL_TRACKS];

        //Set the output file.
        recordBool = false;
        playBool = false;

        for(int i = 0; i < TOTAL_TRACKS; i++) {
            tracks[i] = new Track(i);

        }

        //find buttons
        final Button btn0 = (Button)findViewById(R.id.btn0);
        btn0Delete = (Button)findViewById(R.id.btn0_delete);

        final Button btn1 = (Button)findViewById(R.id.btn1);
        btn1Delete = (Button)findViewById(R.id.btn1_delete);

        btn0Delete.setEnabled(false);
        btn1Delete.setEnabled(false);


        //set listeners for all buttons

        //BUTTON ZERO
        btn0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 0;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if (!tracks[0].hasRecording) {
                    try {
                        record(btn0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        play(btn0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Button zero delete
        btn0Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 0;
                if(tracks[0].isPlaying) {
                    stopPlayback();
                }
                tracks[0].reset();
                btn0.setText("Press button to record");
                btn0Delete.setEnabled(false);
            }
        });

        //BUTTON ONE
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 1;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if(!tracks[1].hasRecording) {
                    try {
                        record(btn1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        play(btn1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Button zero delete
        btn1Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 1;
                if(tracks[1].isPlaying) {
                    stopPlayback();
                }
                tracks[1].reset();
                btn1.setText("Press button to record");
                btn1Delete.setEnabled(false);
            }
        });

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
        songString = songName.getText().toString();
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

        //stop recording and playing when destroyed
        if(recordBool) {
            stopRecording();
        }
        if(playBool){
            stopPlayback();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        //stop recording and playing when paused
        if(recordBool) {
            stopRecording();
        }
        if(playBool){
            stopPlayback();
        }
    }

    /**
     * OnClick method for button click to take the user back to the Menu.
     *
     * @param v View object associated with the button.
     */
    public void toMenu_OnClick(View v) {
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
    }

    /**
     * Onclick method for button click to Save the current song.
     * @param v View object associated with the button.
     */
    public void save_OnClick(View v) {
        //Testing TextView to display all songs from the user in the Song window.
        //TextView test = (TextView)findViewById(R.id.test);
        //Open database
        UserSongsDb userDB = new UserSongsDb(getApplicationContext());
        //Insert the song into the database
        userDB.insertSong(sharedPreferences.getString("CurrentUser", null),
                songName.getText().toString(),
                Boolean.toString(tracks[0].hasRecording),
                Boolean.toString(tracks[1].hasRecording),
                Boolean.toString(tracks[2].hasRecording),
                Boolean.toString(tracks[3].hasRecording),
                Boolean.toString(tracks[4].hasRecording));


        userDB.closeDB();

        if(!isGuest) {

            //remove previous song entries
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SongTracks");
            query.whereEqualTo("userName", sharedPreferences.getString("CurrentUser", null));
            query.whereEqualTo("songName", songString);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        //Toast.makeText(LooperActivity.this, "Found Song", Toast.LENGTH_SHORT).show();
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).deleteInBackground();
                            }
                        }


                    } else {
                        Toast.makeText(LooperActivity.this, "Error finding song", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //add new song entries

            //TODO Fix this to 5
            for (int i = 0; i < 2; i++) {

                if (tracks[i].hasRecording) {

                    //Changes Track into BYTE[] Form.
                    File songTrack = new File(setOutputFile(Integer.toString(i)));

                    byte[] bFile = new byte[(int) songTrack.length()];
                    FileInputStream fileInputStream = null;

                    try {
                        //convert file into array of bytes
                        fileInputStream = new FileInputStream(songTrack);
                        fileInputStream.read(bFile);
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //create a parsefile to send to cloud server
                    ParseFile file = new ParseFile(songString + i + ".3gpp", bFile);
                    file.saveInBackground();
                    ParseObject jobApplication = new ParseObject("SongTracks");
                    jobApplication.put("userName", sharedPreferences.getString("CurrentUser", null));
                    jobApplication.put("songName", songString);
                    jobApplication.put("track", i);
                    jobApplication.put("userSongTrack", file);
                    jobApplication.saveInBackground();
                }
            }
            Toast.makeText(LooperActivity.this, "Song Saved!", Toast.LENGTH_SHORT).show();
        }

    }


    public void record(Button b) throws IOException {

        //Check if there is something recording, if not, then record.
        if(!recordBool) {
            startRecording();
            Toast.makeText(this, "recording", Toast.LENGTH_SHORT).show();
            b.setText("Stop");

        } else {

            stopRecording();
            Toast.makeText(this, "stopping", Toast.LENGTH_SHORT).show();
            b.setText("Play");

            tracks[currentTrack].hasRecording = true;

            switch(currentTrack) {
                case 0:
                    btn0Delete.setEnabled(true);
                    break;
                case 1:
                    btn1Delete.setEnabled(true);
                    break;

            }

        }
    }


    public void play(Button b) throws IOException {


        File outFile = new File(OUTPUT_FILE);
        if (!outFile.exists()) {
            return;
        }

        if (!tracks[currentTrack].isPlaying) {
            b.setText("Stop");
            playRecording();
        } else {
            b.setText("Play");
            stopPlayback();
        }
    }

    /**
     * Method to begin recording.
     *
     * @throws IOException Throws IOException
     */
    private void startRecording() throws IOException {
        ditchMediaRecorder(); //Method to Releases resources associated with this MediaRecorder object.
        //create output file
        //File outFile = new File(OUTPUT_FILE);

//        //if the output file already exists, delete it.
//        if(outFile.exists())
//            outFile.delete();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Set audio source. (Example...Camera, phone conversation, microphone)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Set format to 3gpp
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Encoding in advanced audio codec because it sounded cool
        recorder.setOutputFile(OUTPUT_FILE); //location
        recorder.prepare(); //Prepares the recorder to begin capturing and encoding data.
        recorder.start(); //Begins capturing and encoding data to the file specified with setOutputFile().
        recordBool = true;

    }

    /**
     * Method to stop the recording.
     */
    private void stopRecording(){
        recorder.stop();
        recordBool = false;

    }

    /**
     * Method to play the recording.
     *
     * @throws IOException Throws IOException.
     */
    private void playRecording() throws IOException {
        ditchMediaPlayer();
        mediaPlayer[currentTrack] = new MediaPlayer();
        mediaPlayer[currentTrack].setDataSource(OUTPUT_FILE);
        mediaPlayer[currentTrack].prepare(); //Prepares the recorder to begin capturing and encoding data. Must always prepare before starting, bleh
        mediaPlayer[currentTrack].setLooping(true);
        mediaPlayer[currentTrack].start();
        tracks[currentTrack].isPlaying = true;

    }

    /**
     * Method to stop the curent playback.
     */
    private void stopPlayback(){

        mediaPlayer[currentTrack].stop();
        tracks[currentTrack].isPlaying = false;

    }

    /**
     * Method to release the resources assocaited with the MediaPlayer
     */
    private void ditchMediaPlayer() {

            try {
                mediaPlayer[currentTrack].release();//Releases resources associated with this MediaPlayer object.
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
