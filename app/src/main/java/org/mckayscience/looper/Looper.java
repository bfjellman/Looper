package org.mckayscience.looper;

import android.app.Application;
import com.parse.Parse;

/**
 * This class allows the initialization of the Parse cloud service before activites begin so that
 * any return to the login activity will not crash the service.
 */
public class Looper extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "RS7Eb3jg4hzhw7VaSq3pHIxtSHpi8l1bVliTtfnA", "n6VvvsgD00yxKv9tg0W5wvKdLGB3FqMscMIJGsJz");

    }
}
