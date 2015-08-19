package org.mckayscience.looper.model;

import junit.framework.TestCase;

/**
 * Test the userinfo model class.
 */
public class TestUserInfo extends TestCase {

    /**
     * Test constructor.
     */
    public void testConstructor() {
        UserInfo testUserInfo = new UserInfo("user", "song", "track0", "track1", "track2", "track3", "track4");
        assertNotNull(testUserInfo);
    }

    /**
     * Test to toString.
     */
    public void testToString() {
        UserInfo user = new UserInfo("test", "test", "test", "test", "test", "test", "test");
        assertEquals("UserInfo{UserID: test | Song: test | Track0: test | Track1: test | Track2: test | Track3: test | Track4: test}", user.toString());
    }




}
