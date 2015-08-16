package org.mckayscience.looper.model;

/**
 * Created by bfjel on 8/10/2015.
 */
public class Track {

    public int trackNum;
    public boolean isPlaying;
    public boolean hasRecording;
    public boolean isRecording;

    public Track(int number) {

        trackNum = number;
        isPlaying = false;
        hasRecording = false;
        isRecording = false;
    }

    public void reset() {
        isPlaying = false;
        hasRecording = false;
        isRecording = false;
    }
}
