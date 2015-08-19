package org.mckayscience.looper.model;

/**
 * Track class holds information about the track to help the LooperActivity with identifying
 * status of tracks.
 */
public class Track {
    /** Holds the track value. **/
    public int trackNum;
    /** Boolean to tell whether the track is playing. **/
    public boolean isPlaying;
    /** Tell if the track has a recording. **/
    public boolean hasRecording;
    /** Tell if the track is currently recording. **/
    public boolean isRecording;

    /**
     * Constructor for Track class
     *
     * @param number The track number.
     */
    public Track(int number) {

        trackNum = number;
        isPlaying = false;
        hasRecording = false;
        isRecording = false;
    }

    /**
     * Reset the track values.
     */
    public void reset() {
        isPlaying = false;
        hasRecording = false;
        isRecording = false;
    }
}
