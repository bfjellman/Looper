package org.mckayscience.looper;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Random;

/**
 * Automated Robotium tests.
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

    /**
     * Test logging in.
     */
    public void testLogging() {
        if(solo.searchText("Create")) {
            solo.clickOnButton("Logout");
        } else {
            solo.clickOnButton("Log in with Facebook");
            solo.enterText(0, "android_gnzfypj_user@tfbnw.net");
            solo.enterText(1, "testandroid");
            solo.clickOnButton("Log In");
        }
    }

    /**
     * Test Create song button.
     */
    public void testCreateSongButtons() {
        solo.clickOnButton("Create");
        boolean textFound = solo.searchText("Please");
        assertTrue("Create song failed.", textFound);
    }

    /**
     * Test Share button.
     */
    public void testShareButton() {
        boolean isGuest = solo.searchText("Guest");
        if(isGuest) {
            return;
        }
        solo.clickOnButton("Share");
        boolean textFound = solo.searchText("Please enter the ID");
        assertTrue("Share failed", textFound);
    }

    /**
     * Test creating a song.
     */
    public void testCreateSong() {
        solo.clickOnText("Create");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song creation failed", textFound);
    }

    /** Test saving a song.
     *
     */
    public void testSaveSong() {
        solo.clickOnText("Create");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        solo.clickOnText("Record");
        solo.sleep(15000);//give the emulator time to open MediaRecorder -- not necessary on Android phone.
        solo.clickOnText("Stop");
        solo.clickOnButton("Save");
        solo.clickOnButton("Menu");
        solo.clickOnButton(("Load"));
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song not found", textFound);
    }

    /**
     * Test loading a song.
     */
    public void testLoadSong() {
        solo.clickOnButton("Create");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testLoad" + number);
        solo.clickOnButton("Create");
        solo.clickOnButton("Press to Record");
        solo.sleep(15000); //give the emulator time to open MediaRecorder -- not necessary on Android phone.
        solo.clickOnText("Stop");
        solo.clickOnButton("Save");
        solo.clickOnButton("Menu");
        solo.clickOnButton("Load");
        solo.clickOnText("testLoad" + number);
        boolean textFound = solo.searchText("Press to Record");
        assertTrue("Load Song failed", textFound);
    }

    /**
     * Test logging out.
     */
    public void testLogout() {
        solo.clickOnButton("Logout");
        boolean textFound = solo.searchText("Guest");
        assertTrue("Logout failed", textFound);
    }
}
