package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * MainActivity is the first activity called and handles all Login Activities using Facebook SDK
 * and a Guest bypass.
 */
public class MainActivity extends Activity {


    /** Info textview for debugging. */
    private TextView info;
    /** Facebook login button */
    private LoginButton loginButton;
    /** Callback manager for Facebook login */
    private CallbackManager callbackManager;
    /**AccessTokenTrack to track whether a user is already logged in */
    private AccessTokenTracker accessTokenTracker;
    /** Shared preferences for storing CurrentUser */
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //initiate Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        //initialize shared value
        sharedPreferences
                .edit()
                .putString("Shared", "")
                .apply();

        if(sharedPreferences.getString("CurrentUser", null) != null ) {

            if(!sharedPreferences.getString("CurrentUser", null).equals("")) {
                Intent i = new Intent(this, MainMenuActivity.class);
                startActivity(i);
                finish();
            }
        }

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        //check if user is already logged in
        //Just getting token does not help as getting a new token is done with Async
        //so have to track token changes for when Async is done.
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                //If already logged in, change currentuser in shared preferences
                sharedPreferences
                        .edit()
                        .putString("CurrentUser", currentAccessToken.getUserId())
                        .apply();

                //Open main menu
                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                accessTokenTracker.stopTracking();
            }
        };
        info = (TextView)findViewById(R.id.info);
        //Grab facebook login button.
        loginButton = (LoginButton)findViewById(R.id.login_button);

        //Check for Facebook Login, if successful, set the user in
        // SharedPreferences and move to new activity.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {


                sharedPreferences
                        .edit()
                        .putString("CurrentUser", loginResult.getAccessToken().getUserId())
                        .apply();


                Intent i;
                i = new Intent(getApplicationContext(), MainMenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //Stop tracking this token
                accessTokenTracker.stopTracking();
                finish();

            }

            @Override
            public void onCancel() {

                info.setText("Login attempt canceled.");

            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Error with Login attempt");

            }

        });
    }

    /**
     * Bypass Facebook login and log in as a guest.
     * @param v The View for this method.
     */
    public void loginAsGuest(View v) {

        accessTokenTracker.stopTracking();
        sharedPreferences
                .edit()
                .putString("CurrentUser", "Guest")
                .apply();

        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop the tracker when activity is destroyed
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
