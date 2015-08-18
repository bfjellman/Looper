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

    private boolean isSaved;

    //Record/Play Buttons
    private Button btn0;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    //Delete buttons
    private Button btn0Delete;
    private Button btn1Delete;
    private Button btn2Delete;
    private Button btn3Delete;
    private Button btn4Delete;


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
        btn0 = (Button)findViewById(R.id.btn0);
        btn0Delete = (Button)findViewById(R.id.btn0_delete);

        btn1 = (Button)findViewById(R.id.btn1);
        btn1Delete = (Button)findViewById(R.id.btn1_delete);

        btn2 = (Button)findViewById(R.id.btn2);
        btn2Delete = (Button)findViewById(R.id.btn2_delete);

        btn3 = (Button)findViewById(R.id.btn3);
        btn3Delete = (Button)findViewById(R.id.btn3_delete);

        btn4 = (Button)findViewById(R.id.btn4);
        btn4Delete = (Button)findViewById(R.id.btn4_delete);

        //disable delete buttons
        btn0Delete.setEnabled(false);
        btn1Delete.setEnabled(false);
        btn2Delete.setEnabled(false);
        btn3Delete.setEnabled(false);
        btn4Delete.setEnabled(false);

        //set listeners for all buttons

        //BUTTON ZERO
        btn0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 0;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if (!tracks[0].hasRecording) {
                    if(recordBool && !tracks[0].isRecording) {
                        return;
                    }
                    try {
                        record(btn0);
                        tracks[0].isRecording = !tracks[0].isRecording;

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
                isSaved = false;
                currentTrack = 0;
                if(tracks[0].isPlaying) {
                    stopPlayback();
                }
                tracks[0].reset();
                btn0.setText("Press to Record");
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
                    if(recordBool && !tracks[1].isRecording) {
                        return;
                    }

                    try {
                        record(btn1);
                        tracks[1].isRecording = !tracks[1].isRecording;

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
        //Button one delete
        btn1Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSaved = false;
                currentTrack = 1;
                if(tracks[1].isPlaying) {
                    stopPlayback();
                }
                tracks[1].reset();
                btn1.setText("Press to Record");
                btn1Delete.setEnabled(false);
            }
        });

        //BUTTON TWO
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 2;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if (!tracks[2].hasRecording) {
                    if(recordBool && !tracks[2].isRecording) {
                        return;
                    }

                    try {
                        record(btn2);
                        tracks[2].isRecording = !tracks[2].isRecording;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        play(btn2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Button two delete
        btn2Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSaved = false;
                currentTrack = 2;
                if(tracks[2].isPlaying) {
                    stopPlayback();
                }
                tracks[2].reset();
                btn2.setText("Press to Record");
                btn2Delete.setEnabled(false);
            }
        });

        //BUTTON THREE
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 3;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if (!tracks[3].hasRecording) {
                    if(recordBool && !tracks[3].isRecording) {
                        return;
                    }

                    try {
                        record(btn3);
                        tracks[3].isRecording = !tracks[3].isRecording;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        play(btn3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Button three delete
        btn3Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSaved = false;
                currentTrack = 3;
                if(tracks[3].isPlaying) {
                    stopPlayback();
                }
                tracks[3].reset();
                btn3.setText("Press to Record");
                btn3Delete.setEnabled(false);
            }
        });

        //BUTTON FOUR
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTrack = 4;
                OUTPUT_FILE = setOutputFile(Integer.toString(currentTrack));

                if (!tracks[4].hasRecording) {
                    if(recordBool && !tracks[4].isRecording) {
                        return;
                    }

                    try {
                        record(btn4);
                        tracks[4].isRecording = !tracks[4].isRecording;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        play(btn4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Button four delete
        btn4Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSaved = false;
                currentTrack = 4;
                if(tracks[4].isPlaying) {
                    stopPlayback();
                }
                tracks[4].reset();
                btn4.setText("PRESS TO RECORD");
                btn4Delete.setEnabled(false);
            }
        });

        //Check if the activity was opened from the load activity.
        //If so, then load the song information.
        if(sharedPreferences.getBoolean("loadSong", false)) {
            isSaved = true;
            UserSongsDb db = new UserSongsDb(getApplicationContext());
            List<UserInfo> users = db.selectUser(sharedPreferences.getString("CurrentUser", null));
            for(int i = 0; i < users.size(); i++) {
                if(sharedPreferences.getString("currentSong", null).equals(users.get(i).getSong())) {

                    if(users.get(i).getTrack0().equals("true")) {
                        tracks[0].hasRecording = true;
                        btn0.setText("Play");
                        btn0Delete.setEnabled(true);
                    }
                    if(users.get(i).getTrack1().equals("true")) {
                        tracks[1].hasRecording = true;
                        btn1.setText("Play");
                        btn1Delete.setEnabled(true);
                    }
                    if(users.get(i).getTrack2().equals("true")) {
                        tracks[2].hasRecording = true;
                        btn2.setText("Play");
                        btn2Delete.setEnabled(true);
                    }
                    if(users.get(i).getTrack3().equals("true")) {
                        tracks[3].hasRecording = true;
                        btn3.setText("Play");
                        btn3Delete.setEnabled(true);
                    }
                    if(users.get(i).getTrack4().equals("true")) {
                        tracks[4].hasRecording = true;
                        btn4.setText("Play");
                        btn4Delete.setEnabled(true);
                    }
                }
            }

            db.closeDB();
            //reset load song field
            sharedPreferences
                    .edit()
                    .putBoolean("loadSong", false)
                    .apply();

        }
    }

    /**
     * Creates a destination string to set the output file.
     *
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
        return Environment.getExternalStorageDirectory()+ "/AndroidLooper/" + userId + "/" + songString + track + ".mp4";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stop recording and playing when destroyed
        if (recordBool) {
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

        //stop all playback
        for(int i = 0; i < TOTAL_TRACKS; i++) {
            if(tracks[i].isPlaying) {
                currentTrack = i;

                //change button text
                switch(i) {
                    case 0:
                        btn0.performClick();
                        break;
                    case 1:
                        btn1.performClick();
                        break;
                    case 2:
                        btn2.performClick();
                        break;
                    case 3:
                        btn3.performClick();
                        break;
                    case 4:
                        btn4.performClick();
                        break;
                }
            }
        }
        //If saved to to menu
        if(isSaved) {
            Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        } else { //not saved, dialog them
            SaveDialogFragment dialog = new SaveDialogFragment();
            dialog.show(getFragmentManager(), "Menu");
        }
    }

    /**
     * Onclick method for button click to Save the current song.
     * @param v View object associated with the button.
     */
    public void save_OnClick(View v) {
        isSaved = true;
        boolean hasTracks = false;
        for(int i = 0; i < TOTAL_TRACKS; i++) {
            if(tracks[i].hasRecording)
                hasTracks = true;
        }
        if(!hasTracks) {
            Toast.makeText(LooperActivity.this, "No tracks recorded, not saved.", Toast.LENGTH_SHORT).show();
            return;
        }

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

        if (!isGuest) {

            //remove previous song entries
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SongTracks");
            query.whereEqualTo("userName", sharedPreferences.getString("CurrentUser", null));
            query.whereEqualTo("songName", songName.getText().toString());
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
            for (int i = 0; i < 5; i++) {

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
                    ParseFile file = new ParseFile(songString + i + ".mp4", bFile);
                    file.saveInBackground();
                    ParseObject jobApplication = new ParseObject("SongTracks");
                    jobApplication.put("userName", sharedPreferences.getString("CurrentUser", null));
                    jobApplication.put("songName", songName.getText().toString());
                    jobApplication.put("track", i);
                    jobApplication.put("userSongTrack", file);
                    jobApplication.saveInBackground();
                }
            }
        }
        Toast.makeText(LooperActivity.this, "Song Saved!", Toast.LENGTH_SHORT).show();


    }

    /**
     * Checks if the Recorder is currently recording.  If it is, it still stop the recording.
     * If it is not, it will start the recorder.
     *
     * @param b Button that called the method.
     * @throws IOException Throws an IOException.
     */
    public void record(Button b) throws IOException {

        isSaved = false;

        //Check if there is something recording, if not, then record.
        if(!recordBool) {
            startRecording();
            Toast.makeText(this, "recording", Toast.LENGTH_SHORT).show();
            b.setText("Stop");

        } else {

            if(stopRecording() == 1) {
                b.setText("Press to Record");
                return;
            }
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
                case 2:
                    btn2Delete.setEnabled(true);
                    break;
                case 3:
                    btn3Delete.setEnabled(true);
                    break;
                case 4:
                    btn4Delete.setEnabled(true);
                    break;

            }
        }
    }

    /**
     * Plays the recording attached for the current tracks mediarecorder.
     *
     * @param b The button that called the method.
     * @throws IOException Throws IOException.
     */
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

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION); //Set audio source. (Example...Camera, phone conversation, microphone)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //Set format to MPEG_4
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Encoding in advanced audio codec because it sounded cool
        recorder.setAudioEncodingBitRate(96000);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(OUTPUT_FILE); //location
        recorder.prepare(); //Prepares the recorder to begin capturing and encoding data.
        recorder.start(); //Begins capturing and encoding data to the file specified with setOutputFile().
        recordBool = true;

    }

    /**
     * Stops the current recording
     *
     * @return Returns a 0 if successful and a 1 if not.
     */
    private int stopRecording(){
        recordBool = false;
        try {
            recorder.stop();
            return 0;
        }catch(RuntimeException stopException) {
            //User pressed stop too fast after start.
            Toast.makeText(LooperActivity.this, "Record failed, pressed stop too quickly after record.", Toast.LENGTH_SHORT).show();
            return 1;

        }
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
     * Method to stop the current playback.
     */
    private void stopPlayback(){

        mediaPlayer[currentTrack].stop();
        tracks[currentTrack].isPlaying = false;

    }

    /**
     * Method to release the resources associated with the MediaPlayer
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
     * Method to release the resources associated with the MediaRecorder
     */
    private void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();//Releases resources associated with this MediaRecorder object.
    }

    @Override
    public void onBackPressed() {
        toMenu_OnClick(null);
    }

}
