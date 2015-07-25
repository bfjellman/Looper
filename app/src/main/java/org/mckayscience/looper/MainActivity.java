package org.mckayscience.looper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.tv.TvInputService;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class MainActivity extends Activity {

    private TextView info;
    private TextView loggedIn;
    private LoginButton loginButton;

    private CallbackManager callbackManager;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        //check if user is already logged in
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            Intent i = new Intent(getApplicationContext(), MainMenu.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        info = (TextView)findViewById(R.id.info);

        loggedIn = (TextView)findViewById(R.id.login_value);
        loginButton = (LoginButton)findViewById(R.id.login_button);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {



            @Override
            public void onSuccess(LoginResult loginResult) {

                info.setText("User ID: " + loginResult.getAccessToken().getUserId()
                + "\n" +
                "Auth Token: " +
                loginResult.getAccessToken().getToken());

                SharedPreferences sharedPreferences = getSharedPreferences(
                                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                sharedPreferences
                        .edit()
                        .putString("CurrentUser", loginResult.getAccessToken().getUserId())
                        .putString("UserToken", loginResult.getAccessToken().getToken())
                        .commit();

                Intent i;
                i = new Intent(getApplicationContext(), MainMenu.class);


                startActivity(i);
                finish();

            }

            @Override
            public void onCancel() {

                info.setText("Login attempt canceled.");

            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt canceled");

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
