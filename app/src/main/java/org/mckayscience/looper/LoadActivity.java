package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.mckayscience.looper.data.UserSongsDb;
import org.mckayscience.looper.model.UserInfo;

import java.util.List;

/**
 * LoadActivity is created from the Main Menu and is responsible for populating the ListView
 * with songs from the database on a per user basis.
 */
public class LoadActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        ListView userInfo = (ListView) findViewById(R.id.user_info);
        //Create a database object
        UserSongsDb userDB = new UserSongsDb(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        //Get a list of songs from the current user.
        List<UserInfo> list =  userDB.selectUsers(sharedPreferences.getString("CurrentUser", null));
        //close DB
        userDB.closeDB();
        //If there are songs to display
        if(list.size() != 0) {

            ArrayAdapter<UserInfo> adapter = new ArrayAdapter<UserInfo>(getApplicationContext(),
                    R.layout.darker_text, list);

            userInfo.setAdapter(adapter);

            userInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), "Yay this works", Toast.LENGTH_LONG).show();

                }
            });
        //If there are no songs to display
        } else {
            TextView noFile = (TextView)findViewById(R.id.load_no_songs);
            noFile.setText("No files found.");
        }

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
