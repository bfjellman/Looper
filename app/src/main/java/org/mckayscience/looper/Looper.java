package org.mckayscience.looper;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by B on 7/28/2015.
 */
public class Looper extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "RS7Eb3jg4hzhw7VaSq3pHIxtSHpi8l1bVliTtfnA", "n6VvvsgD00yxKv9tg0W5wvKdLGB3FqMscMIJGsJz");

    }
}
