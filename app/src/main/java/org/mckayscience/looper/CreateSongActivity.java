package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.mckayscience.looper.data.UserSongsDb;
import org.mckayscience.looper.model.UserInfo;

import java.util.List;

/**
 * Activity for handling the creation of the song name.
 */
public class CreateSongActivity extends Activity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_name);
    }

    /**
     * OnClick method for the create button.
     * @param v View for the button.
     */
    public void newSong(View v) {


        EditText edit = (EditText)findViewById(R.id.song_name_edit);
        //check if the user entered anything
        if(edit.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a song name.", Toast.LENGTH_LONG).show();
            return;
        }
        if(edit.getText().toString().contains("-Shared")) {
            Toast.makeText(CreateSongActivity.this, "Song name cannot contain \"-Shared\"", Toast.LENGTH_SHORT).show();
        }

        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

        //check if song name is already used with this user
        UserSongsDb db = new UserSongsDb(getApplicationContext());
        List<UserInfo> list = db.selectUser(sharedPreferences.getString("CurrentUser", null));
        for(int i = 0; i < list.size(); i++) {
            if(edit.getText().toString().equals(list.get(i).getSong())) {
                Toast.makeText(CreateSongActivity.this, "Song already exists, please enter a new name.", Toast.LENGTH_SHORT).show();
                edit.setHint("Song Name");
                edit.setText("");
                db.closeDB();
                return;
            }
        }
        db.closeDB();

        sharedPreferences
                .edit()
                .putString("currentSong", edit.getText().toString())
                .apply();

        Intent i = new Intent(getApplicationContext(), LooperActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * OnClick method for the cancel button
     *
     * @param v View for the button.
     */
    public void cancelNew(View v) {
        //finishes this activity (goes back to last activity on stack)
        this.finish();

    }
}
