package org.mckayscience.looper.model;

import junit.framework.TestCase;

/**
 * Created by bfjel on 8/17/2015.
 */
public class TestTrack extends TestCase {

    public void testConstructor() {
        Track testTrack = new Track(0);
        assertNotNull(testTrack);
    }

    public void testReset() {
        Track testTrack = new Track(1);
        testTrack.isRecording = true;
        testTrack.hasRecording = true;
        testTrack.isPlaying = true;
        testTrack.reset();
        assertFalse(testTrack.isRecording);
        assertFalse(testTrack.hasRecording);
        assertFalse(testTrack.isPlaying);
    }
}
