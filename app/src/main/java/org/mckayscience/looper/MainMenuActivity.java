package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

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
        }


        if(!isGuest) {

            FacebookSdk.sdkInitialize(getApplicationContext());

            Button b = (Button)findViewById(R.id.guest_logout);
            b.setEnabled(false);
            b.setVisibility(View.GONE);

            //Track the token so the Activity knows when a user has logged out of Facebook
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(
                        AccessToken oldAccessToken,
                        AccessToken currentAccessToken) {
                    sharedPreferences = getSharedPreferences(
                            getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                    sharedPreferences
                            .edit()
                            .putString("CurrentUser", "")
                            .apply();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("EXIT", true);
                    startActivity(i);

                    //TODO this is not erasing backstack, why?
                    finish();
                }
            };
        } else {
            Button b = (Button)findViewById(R.id.logout_button);
            b.setEnabled(false);
            b.setVisibility(View.GONE);
        }

    }

    public void guestLogout(View v) {
        
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
        if(!isGuest)
            accessTokenTracker.stopTracking();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //resume tracking the token
        if(!isGuest)
            accessTokenTracker.startTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop the tracker when activity is destroyed
        if(!isGuest)
            accessTokenTracker.stopTracking();
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
