package org.mckayscience.looper.model;

import junit.framework.TestCase;

/**
 * JUnit test for Track class.
 */
public class TestTrack extends TestCase {

    /**
     * Test constructor.
     */
    public void testConstructor() {
        Track testTrack = new Track(0);
        assertNotNull(testTrack);
    }

    /**
     * Test reset method.
     */
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
