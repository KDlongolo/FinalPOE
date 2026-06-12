package testApplication;

import mainApplication.Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    public void setUp() {
        Message.clearAllData();
    }

    @Test
    public void testMessageLengthSuccess() {
        Message m = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(m.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "A".repeat(260);
        Message m = new Message("0012345678", 0, "+27718693002", longMessage);
        assertFalse(m.checkMessageLength());
    }

    @Test
    public void testRecipientCellSuccess() {
        Message m = new Message("0012345678", 0, "+27718693002", "Test message");
        assertEquals(1, m.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        Message m = new Message("0012345678", 0, "0812345678", "Test message");
        assertEquals(0, m.checkRecipientCell());
    }

    @Test
    public void testHashCreation() {
        Message m = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:0:HITONIGHT", m.getMessageHash());
    }

    @Test
    public void testHashCreationWithSingleWord() {
        Message m = new Message("0012345678", 1, "+27718693002", "Hello");
        assertEquals("00:1:HELLO", m.getMessageHash());
    }

    @Test
    public void testMessageIDCreation() {
        Message m = new Message("0012345678", 0, "+27718693002", "Hello");
        assertTrue(m.checkMessageID());
    }

    @Test
    public void testSendOption() {
        Message m = new Message("0012345678", 0, "+27718693002", "Send test");
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void testDisregardOption() {
        Message m = new Message("0012345678", 0, "+27718693002", "Delete test");
        assertEquals("Press 0 to delete message.", m.sentMessage(2));
    }

    @Test
    public void testStoreOption() {
        Message m = new Message("0012345678", 0, "+27718693002", "Store test");
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        Message.populateTestData();

        ArrayList<Message> sent = Message.getSentMessages();

        boolean foundCake = false;
        boolean foundDinner = false;

        for (Message m : sent) {
            if (m.getMessageText().equals("Did you get the cake?")) {
                foundCake = true;
            }
            if (m.getMessageText().equals("It is dinner time!")) {
                foundDinner = true;
            }
        }

        assertTrue(foundCake);
        assertTrue(foundDinner);
    }

    @Test
    public void testDisregardedMessagesArrayCorrectlyPopulated() {
        Message.populateTestData();

        ArrayList<Message> ignored = Message.getDisregardedMessages();

        assertEquals(1, ignored.size());
        assertEquals("Yohoooo, I am at your gate.", ignored.get(0).getMessageText());
    }

    @Test
    public void testStoredMessagesArrayCorrectlyPopulated() {
        Message.populateTestData();

        ArrayList<Message> stored = Message.getStoredMessages();

        assertEquals(2, stored.size());
    }

    @Test
    public void testDisplayLongestMessage() {
        Message.populateTestData();

        assertEquals("Where are you? You are late! I have asked you to be on time.", Message.displayLongestMessage());
    }

    @Test
    public void testSearchByMessageID() {
        Message.populateTestData();

        String result = Message.searchMessageID("0838884567");

        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        Message.populateTestData();

        ArrayList<String> results = Message.searchRecipientMessages("+27838884567");

        assertEquals(2, results.size());
        assertTrue(results.get(0).contains("Where are you?"));
        assertTrue(results.get(1).contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        Message m = new Message("2222222222", 2, "+27838884567", "Developer", "Where are you? You are late! I have asked you to be on time.");
        m.sentMessage(3);

        String result = Message.deleteMessage(m.getMessageHash());

        assertEquals("Message successfully deleted.", result);
    }

    @Test
    public void testMessageHashArrayPopulated() {
        Message.populateTestData();

        assertTrue(Message.getMessageHashes().size() >= 5);
    }

    @Test
    public void testMessageIDArrayPopulated() {
        Message.populateTestData();

        assertTrue(Message.getMessageIDs().size() >= 5);
    }

    @Test
    public void testSearchMessageIDNotFound() {
        Message.populateTestData();

        assertEquals("Message ID not found.", Message.searchMessageID("9999999999"));
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        Message.populateTestData();

        assertEquals("Message hash not found.", Message.deleteMessage("99:9:NOTFOUND"));
    }

    @Test
    public void testReturnTotalMessages() {
        Message.populateTestData();

        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testReturnTotalStored() {
        Message.populateTestData();

        assertEquals(2, Message.returnTotalStored());
    }

    @Test
    public void testReturnTotalDisregarded() {
        Message.populateTestData();

        assertEquals(1, Message.returnTotalDisregarded());
    }
}