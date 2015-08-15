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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * This activity acts as the main menu for the application allowing the user to create a song,
 * load a song, share a song, or logout.
 */
public class MainMenuActivity extends Activity {
    /** Track the token for logout */
    private AccessTokenTracker accessTokenTracker;
    private SharedPreferences sharedPreferences;
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

        if(sharedPreferences.getString("CurrentUser", null).equals("Guest")) {
            isGuest = true;
        } else {
            isGuest = false;
            FacebookSdk.sdkInitialize(getApplicationContext());
            loadFromParse();
        }

    }

    public void loadFromParse() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("SongTracks");
        query.whereEqualTo("userName", sharedPreferences.getString("CurrentUser", null));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    Toast.makeText(MainMenuActivity.this, "Loaded songs", Toast.LENGTH_SHORT).show();
                    TextView test = (TextView)findViewById(R.id.testBox);
                    test.setText(list.get(0).getParseFile("userSongTrack").getName());
                    ParseFile loadSong = list.get(0).getParseFile("userSongTrack");
                    MediaPlayer mp = new MediaPlayer();
                    FileOutputStream outputStream;
                    String outputlocation = Environment.getExternalStorageDirectory() + "/" + loadSong.getName();

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
                    }
                    try {
                        mp.setDataSource(outputlocation);
                        mp.prepare(); //Prepares the recorder to begin capturing and encoding data. Must always prepare before starting, bleh
                        mp.start();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainMenuActivity.this, "Load song failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void guestLogout(View v) {

        if(!isGuest) {
            LoginManager.getInstance().logOut();
        }

        sharedPreferences
                .edit()
                .putString("CurrentUser", "")
                .apply();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        startActivity(i);
        finish();
    }

    /**
     * OnClick method for the create song button.
     * @param v View for the button.
     */
    public void createSong_onClick(View v) {
        Intent i = new Intent(getApplicationContext(), CreateSongActivity.class);
        startActivity(i);
    }

    /**
     * Onclick method for the load song button.
     * @param v View for the button.
     */
    public void loadSong_onClick(View v) {
        Intent i = new Intent(getApplicationContext(), LoadActivity.class);
        startActivity(i);

    }

    /**
     * Onclick method for the share song button.
     *
     * @param v View for the button.
     */
    public void shareSong_onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop tracking when activity is paused.
        //if(!isGuest)
            //accessTokenTracker.stopTracking();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //resume tracking the token
        //if(!isGuest)
            //accessTokenTracker.startTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop the tracker when activity is destroyed
        //if(!isGuest)
            //accessTokenTracker.stopTracking();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
