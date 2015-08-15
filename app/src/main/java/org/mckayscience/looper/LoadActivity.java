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
    private String userId;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        noFile = (TextView)findViewById(R.id.load_no_songs);
        db = new UserSongsDb(getApplicationContext());



        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

        userId = sharedPreferences.getString("CurrentUser", null);


    //TODO Only for Facebook login - load normally for guest

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

    public void loadFromParse() throws ParseException {

        final String userId = sharedPreferences.getString("CurrentUser", null);

        noFile.setText("Please wait...");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("SongTracks");
        query.whereEqualTo("userName", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Toast.makeText(LoadActivity.this, "Loaded songs", Toast.LENGTH_SHORT).show();

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
             /*       ParseFile loadSong = list.get(0).getParseFile("userSongTrack");
                    //MediaPlayer mp = new MediaPlayer();
                    FileOutputStream outputStream;


                    String outputlocation = setOutput(userId, "hello"); //TODO

                    try {
                        outputStream = new FileOutputStream(new File(outputlocation));
                        outputStream.write(loadSong.getData());
                        outputStream.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }*/
                 /*   try {
                        mp.setDataSource(outputlocation);
                        mp.prepare(); //Prepares the recorder to begin capturing and encoding data. Must always prepare before starting, bleh
                        mp.start();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }*/

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

    private void load(UserSongsDb db, TextView noFile) {
        ListView userInfo = (ListView) findViewById(R.id.user_info);
        //Create a database object

        final SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
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
                    Toast.makeText(getApplicationContext(), Integer.toString(i), Toast.LENGTH_LONG).show();
                    sharedPreferences
                            .edit()
                            .putBoolean("loadSong", true)
                            .putString("currentSong", songs.get(i))
                            .apply();

                    Intent intent = new Intent(getApplicationContext(), LooperActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

           //If there are no songs to display
        } else {
            noFile.setEnabled(true);
            noFile.setVisibility(View.VISIBLE);
            noFile.setText("No files found.");
        }
    }

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
        return Environment.getExternalStorageDirectory()+ "/AndroidLooper/" + userId + "/" + songName + track + ".3gpp";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load, menu);
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
