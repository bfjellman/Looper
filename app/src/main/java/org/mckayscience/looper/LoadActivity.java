package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.mckayscience.looper.data.UserSongsDb;
import org.mckayscience.looper.model.UserInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LoadActivity is created from the Main Menu and is responsible for populating the ListView
 * with songs from the database on a per user basis.
 */
public class LoadActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private TextView noFile;
    private UserSongsDb db;
    private List<ParseObject> parseList;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        noFile = (TextView)findViewById(R.id.load_no_songs);
        db = new UserSongsDb(getApplicationContext());

        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);


        if(sharedPreferences.getString("CurrentUser", null).equals("Guest")) {
            load(db, noFile);

        } else {

            try {
                loadFromParse();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves all songs from the user that are stored on the cloud and populates internal memory
     * for all songs that did not exist.
     *
     * @throws ParseException Throws exception.
     */
    public void loadFromParse() throws ParseException {

        final String userId = sharedPreferences.getString("CurrentUser", null);

        noFile.setText("Please wait...");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("SongTracks");
        query.whereEqualTo("userName", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    parseList = list;

                    List<UserInfo> userSongs = db.selectUser(sharedPreferences.getString("CurrentUser", null));

                    List<String> missingSongs = new ArrayList<String>();
                    String currentSong = "";
                    boolean found = false;
                    //Iterate through Parse list to find a unique song name
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).getString("songName").equals(currentSong)) {
                            currentSong = list.get(i).getString("songName");
                            //Iterate through internal DB songs to check if the Parse song already exists
                            for (int j = 0; j < userSongs.size(); j++) {
                                if (userSongs.get(j).getSong().equals(currentSong)) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                missingSongs.add(currentSong);
                                found = false;
                            } else {
                                found = false;
                            }

                        } else { } //Song name already exists - find next unique song name
                    }//end for

                    //Add missing songs to DB.
                    for(int i = 0; i < missingSongs.size(); i++) {

                        int counter = 0;

                        for(int j = 0; j < list.size(); j++) {
                            if(missingSongs.get(i).equals(list.get(j).getString("songName"))) {

                                FileOutputStream outputStream;

                                String outputLoc = setOutput(userId, missingSongs.get(i), Integer.toString(counter));

                                try {
                                    outputStream = new FileOutputStream(new File(outputLoc));
                                    outputStream.write(list.get(j).getParseFile("userSongTrack").getData());
                                    outputStream.close();
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                counter++;

                            } else {

                                db.insertSong(sharedPreferences.getString("CurrentUser", null),
                                        missingSongs.get(i),
                                        Boolean.toString(counter == 0),
                                        Boolean.toString(counter == 1),
                                        Boolean.toString(counter == 2),
                                        Boolean.toString(counter == 3),
                                        Boolean.toString(counter == 4));

                                counter = 0;
                            }
                        }
                    }

                    //end
                    noFile.setEnabled(false);
                    noFile.setVisibility(View.INVISIBLE);

                    load(db, noFile);
                } else {
                    Toast.makeText(LoadActivity.this, "Load song failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Populates the load adapter to allow the user to select a song.
     *
     * @param db The SQLite database of internal memory songs.
     * @param noFile The TextView on top of Activity to display information.
     */
    private void load(UserSongsDb db, TextView noFile) {
        ListView userInfo = (ListView) findViewById(R.id.user_info);
        //Create a database object

        //Get a list of songs from the current user.
        List<UserInfo> list =  db.selectUser(sharedPreferences.getString("CurrentUser", null));
        //close DB
        db.closeDB();
        final List<String> songs = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++) {
            songs.add(list.get(i).getSong());
        }

        //If there are songs to display
        if(list.size() != 0) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.darker_text, songs);

            userInfo.setAdapter(adapter);

            userInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if(sharedPreferences.getString("Shared", null).equals("")) {
                        sharedPreferences
                                .edit()
                                .putBoolean("loadSong", true)
                                .putString("currentSong", songs.get(i))
                                .apply();

                        Intent intent = new Intent(getApplicationContext(), LooperActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        shareSong(songs, i);

                    }

                }
            });

           //If there are no songs to display
        } else {
            noFile.setEnabled(true);
            noFile.setVisibility(View.VISIBLE);
            noFile.setText("No files found.");
        }
    }

    /**
     * Method that will iterate through the list of songs from Parse, find the one wanting
     * to be shared and upload that song to the new user.
     *
     * @param songs List of songs on activity.
     * @param index Index of the song clicked to share.
     */
    private void shareSong(List<String> songs, int index) {

        String sharedSong = songs.get(index);

        for(int i = 0; i < parseList.size(); i++) {

            if(parseList.get(i).getString("songName").equals(sharedSong)) {

                ParseObject newParse = new ParseObject("SongTracks");
                newParse.put("userName", sharedPreferences.getString("Shared", null));
                newParse.put("songName", sharedSong + "-shared");
                newParse.put("track", parseList.get(i).getNumber("track"));
                newParse.put("userSongTrack", parseList.get(i).getParseFile("userSongTrack"));
                newParse.saveInBackground();
            }

        }
        //reset shared field
        sharedPreferences
                .edit()
                .putString("Shared", "")
                .apply();

        Toast.makeText(LoadActivity.this, "Song Shared!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Sets the output to save files to from cloud.
     *
     * @param userId User ID of the person to be saved.
     * @param songName Name of song to be saved.
     * @param track Track number for song.
     * @return String representation of the path to use for fileoutput.
     */
    private String setOutput(String userId, String songName, String track) {
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
        //remove all white space from song name and all letters lowercase
        songName = songName
                .replaceAll("\\s+", "")
                .toLowerCase();

        //return path for new track
        return Environment.getExternalStorageDirectory()+ "/AndroidLooper/" + userId + "/" + songName + track + ".mp4";
    }
}
