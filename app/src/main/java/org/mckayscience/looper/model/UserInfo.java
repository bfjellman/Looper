package org.mckayscience.looper.model;

/**
 * Created by B on 7/25/2015.
 */
public class UserInfo {


    private String mSong;
    private String mTrack0;
    private String mTrack1;


    private String mTrack2;
    private String mTrack3;
    private String mTrack4;
    private String mDest;

    public UserInfo(String song, String track0, String track1, String track2,
                    String track3, String track4) {
        mSong = song;
        mTrack0 = track0;
        mTrack1 = track1;
        mTrack2 = track2;
        mTrack3 = track3;
        mTrack4 = track4;

    }

    public String getSong() {
        return mSong;
    }

    public void setSong(String mSong) {
        this.mSong = mSong;
    }

    public String getTrack0() {
        return mTrack0;
    }

    public void setTrack0(String mTrack0) {
        this.mTrack0 = mTrack0;
    }

    public String getTrack1() {
        return mTrack1;
    }

    public void setTrack1(String mTrack1) {
        this.mTrack1 = mTrack1;
    }

    public String getTrack2() {
        return mTrack2;
    }

    public void setTrack2(String mTrack2) {
        this.mTrack2 = mTrack2;
    }

    public String getTrack3() {
        return mTrack3;
    }

    public void setTrack3(String mTrack3) {
        this.mTrack3 = mTrack3;
    }

    public String getTrack4() {
        return mTrack4;
    }

    public void setTrack4(String mTrack4) {
        this.mTrack4 = mTrack4;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserInfo{");
        sb.append("Song: " + mSong + " | ");
        sb.append("Track0: " + mTrack0 + " | ");
        sb.append("Track1: " + mTrack1 + " | ");
        sb.append("Track2: " + mTrack2 + " | ");
        sb.append("Track3: " + mTrack3 + " | ");
        sb.append("Track4: " + mTrack4 + " | ");
        sb.append("}");
        return sb.toString();
    }
}
