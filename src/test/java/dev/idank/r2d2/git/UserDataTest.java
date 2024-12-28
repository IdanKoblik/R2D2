package dev.idank.r2d2.git;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTest {

    private UserData testUser = null;

    @BeforeEach
    public void setUp() {
        this.testUser = new UserData("TestUser", "TestToken", "TestURL", Platform.GITLAB);
    }

    @AfterEach
    public void tearDown() {
        this.testUser = null;
    }

    @Test
    public void testToString() {
        String expected = "UserData{username='TestUser', token='TestToken', apiURL='TestURL', platform=interface dev.idank.r2d2.git.Gitlab}";
        assertEquals(expected, testUser.toString());
    }

    @Test
    public void testEquals() {
        UserData expectedEqUser = new UserData("TestUser", "TestToken", "TestURL", Platform.GITLAB);
        UserData expectedNonEqUser = new UserData("TestUser", "TestToken", "TestURL", Platform.GITHUB);

        assertNotEquals(expectedNonEqUser, this.testUser);
        assertEquals(expectedEqUser, this.testUser);
    }

}
