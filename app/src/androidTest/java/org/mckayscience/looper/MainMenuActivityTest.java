package org.mckayscience.looper;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Random;

/**
 * Created by bfjel on 8/17/2015.
 */
public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

    private Solo solo;

    public MainMenuActivityTest() {
        super(MainMenuActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testLogging() {
        if(solo.searchText("Android Looper")) {
            solo.clickOnButton("Logout");
        } else {
            solo.clickOnButton("Log in with Facebook");
            solo.enterText(0, "android_gnzfypj_user@tfbnw.net");
            solo.enterText(1, "testandroid");
            solo.clickOnButton("Log In");
        }
    }

    public void testCreateSongButtons() {
        solo.clickOnButton("Create Song");
        boolean textFound = solo.searchText("Please enter a name for the song.");
        assertTrue("Create song failed.", textFound);
    }

    public void testShareButton() {
        boolean isGuest = solo.searchText("Guest");
        if(isGuest) {
            return;
        }
        solo.clickOnButton("Share Song");
        boolean textFound = solo.searchText("Please enter the ID");
        assertTrue("Share failed", textFound);
    }

    //test rest of buttons
    public void testCreateSong() {
        solo.clickOnText("Create Song");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song creation failed", textFound);
    }

    public void testSaveSong() {
        solo.clickOnText("Create Song");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        solo.clickOnButton("Press to Record");
        solo.clickOnButton("Stop");
        solo.clickOnButton("Save");
        solo.clickOnButton("Menu");
        solo.clickOnButton(("Load Song"));
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song not found", textFound);
    }

    public void testLoadSong() {
        solo.clickOnButton("Create Song");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testLoad" + number);
        solo.clickOnButton("Create");
        solo.clickOnButton("Press to Record");
        solo.clickOnButton("Stop");
        solo.clickOnButton("Save");
        solo.clickOnButton("Menu");
        solo.clickOnButton("Load Song");
        solo.clickOnText("testLoad" + number);
        boolean textFound = solo.searchText("Press to Record");
        assertTrue("Load Song failed", textFound);
    }

    public void testLogout() {
        solo.clickOnButton("Logout");
        boolean textFound = solo.searchText("Guest");
        assertTrue("Logout failed", textFound);
    }
}
