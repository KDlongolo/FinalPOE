package testApplication;

import mainApplication.Login;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private final String SUCCESS_MSG = "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.";

    @Test
    public void testUsernameIsCorrectlyFormatted() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(SUCCESS_MSG, t.registerUser());
    }

    @Test
    public void testUsernameIsIncorrectlyFormatted() {
        Login t = new Login("Kyle", "Smith", "bad", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.", t.registerUser());
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(SUCCESS_MSG, t.registerUser());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "bad", "+27838968976");
        assertEquals("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", t.registerUser());
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals(SUCCESS_MSG, t.registerUser());
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "bad");
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.", t.registerUser());
    }

    @Test
    public void testLoginSuccessful() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(t.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(t.loginUser("wrong", "wrong"));
    }

    @Test
    public void testUsernameCorrectlyFormattedBoolean() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(t.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormattedBoolean() {
        Login t = new Login("Kyle", "Smith", "bad", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(t.checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexityRequirementsBoolean() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(t.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirementsBoolean() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "bad", "+27838968976");
        assertFalse(t.checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormattedBoolean() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(t.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormattedBoolean() {
        Login t = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "bad");
        assertFalse(t.checkCellPhoneNumber());
    }
}