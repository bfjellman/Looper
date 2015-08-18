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

    public void testMenuTest() {
        solo.unlockScreen();
        boolean textFound = solo.searchText("Android Looper");
        assertTrue("Text Found", textFound);
    }

    public void testCreateSongButtons() {
        solo.clickOnButton("Create Song");
        boolean textFound = solo.searchText("Please enter a name for the song.");
        assertTrue("Create Song button exists/works", textFound);
    }


    //test rest of buttons

    public void testCreateSong() {
        solo.clickOnText("Create Song");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song successfully created", textFound);
    }

    public void testSaveSong() {
        solo.clickOnText("Create Song");
        Random random = new Random();
        int number = random.nextInt(100000);
        solo.enterText(0, "testSong" + number);
        solo.clickOnButton("Create");
        solo.clickOnButton("Save");
        solo.clickOnButton("Menu");
        solo.clickOnButton(("Load Song"));
        boolean textFound = solo.searchText("testSong" + number);
        assertTrue("Song was saved", textFound);
    }







}
