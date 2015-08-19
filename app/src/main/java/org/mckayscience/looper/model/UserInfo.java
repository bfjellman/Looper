package org.mckayscience.looper.model;

/**
 * UserInfo class for storing all database information in a single object.
 */
public class UserInfo {

    /** User ID **/
    private String mUserID;
    /** The song name **/
    private String mSong;
    /** Track 0 **/
    private String mTrack0;
    /** Track 1 **/
    private String mTrack1;
    /** Track 2 **/
    private String mTrack2;
    /** Track 3 **/
    private String mTrack3;
    /** Track 4 **/
    private String mTrack4;

    /**
     * Constructor for UserInfo
     *
     * @param userID The user ID.
     * @param song The song name.
     * @param track0 Track0's status.
     * @param track1 Track1's status.
     * @param track2 Track2's status.
     * @param track3 Track3's status.
     * @param track4 Track4's status.
     */
    public UserInfo(String userID, String song, String track0, String track1, String track2,
                    String track3, String track4) {
        mUserID = userID;
        mSong = song;
        mTrack0 = track0;
        mTrack1 = track1;
        mTrack2 = track2;
        mTrack3 = track3;
        mTrack4 = track4;

    }

    /**
     * Get UserID.
     *
     * @return The userID.
     */
    public String getUserID() { return mUserID;}

    /**
     * Sets the UserID.
     *
     * @param userID ID to be set.
     */
    public void setUserID(String userID) {mUserID = userID; }

    /** Gets the song name.
     *
     * @return The name of the song.
     */
    public String getSong() {
        return mSong;
    }

    /**
     * Sets the name of the song.
     *
     * @param mSong The name to be set.
     */
    public void setSong(String mSong) {
        this.mSong = mSong;
    }

    /** Gets the status of track0
     *
     * @return Status of track0
     */
    public String getTrack0() {
        return mTrack0;
    }

    /**
     * Sets track0 status.
     *
     * @param mTrack0 Status to bet set.
     */
    public void setTrack0(String mTrack0) {
        this.mTrack0 = mTrack0;
    }

    /**
     * Get track1's status.
     *
     * @return Status of track1.
     */
    public String getTrack1() {
        return mTrack1;
    }

    /**
     * Set the status for track1.
     * @param mTrack1 The status to be set.
     */
    public void setTrack1(String mTrack1) {
        this.mTrack1 = mTrack1;
    }

    /**
     * Get the status for track 2.
     *
     * @return Track2's status.
     */
    public String getTrack2() {
        return mTrack2;
    }

    /**
     * Set the status of track 2
     *
     * @param mTrack2 Status to be set.
     */
    public void setTrack2(String mTrack2) {
        this.mTrack2 = mTrack2;
    }

    /**
     * Get the status for track3.
     *
     * @return Track3's status.
     */
    public String getTrack3() {
        return mTrack3;
    }

    /**
     * Set the status for track3.
     *
     * @param mTrack3 Status to be set.
     */
    public void setTrack3(String mTrack3) {
        this.mTrack3 = mTrack3;
    }

    /**
     * Get track4's status.
     *
     * @return The status of track4.
     */
    public String getTrack4() {
        return mTrack4;
    }

    /**
     * Set the status of track4.
     *
     * @param mTrack4 Status to be set.
     */
    public void setTrack4(String mTrack4) {
        this.mTrack4 = mTrack4;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserInfo{");
        sb.append("UserID: " + mUserID + " | ");
        sb.append("Song: " + mSong + " | ");
        sb.append("Track0: " + mTrack0 + " | ");
        sb.append("Track1: " + mTrack1 + " | ");
        sb.append("Track2: " + mTrack2 + " | ");
        sb.append("Track3: " + mTrack3 + " | ");
        sb.append("Track4: " + mTrack4);
        sb.append("}");
        return sb.toString();
    }
}
