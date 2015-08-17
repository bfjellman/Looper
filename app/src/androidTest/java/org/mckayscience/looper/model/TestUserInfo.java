package org.mckayscience.looper.model;

import junit.framework.TestCase;

/**
 * Created by bfjel on 8/17/2015.
 */
public class TestUserInfo extends TestCase {

    public void testConstructor() {
        UserInfo testUserInfo = new UserInfo("user", "song", "track0", "track1", "track2", "track3", "track4");
        assertNotNull(testUserInfo);
    }

    public void testToString() {
        UserInfo user = new UserInfo("test", "test", "test", "test", "test", "test", "test");
        assertEquals("UserInfo{UserID: test | Song: test | Track0: test | Track1: test | Track2: test | Track3: test | Track4: test}", user.toString());
    }




}
