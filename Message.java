package mainApplication;

import java.io.*;
import java.util.ArrayList;

public class Message {

    private String messageID;
    private int messageNumber;
    private String recipientNumber;
    private String senderName;
    private String messageText;
    private String messageHash;
    private String messageFlag;

    private static int totalSentMessages = 0;
    private static int totalStoredMessages = 0;
    private static int totalDisregardedMessages = 0;

    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();

    public Message(String messageID,
                   int messageNumber,
                   String recipientNumber,
                   String messageText) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipientNumber = recipientNumber;
        this.senderName = "Developer";
        this.messageText = messageText;
        this.messageFlag = "";

        messageHash = createMessageHash();
    }

    public Message(String messageID,
                   int messageNumber,
                   String recipientNumber,
                   String senderName,
                   String messageText) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipientNumber = recipientNumber;
        this.senderName = senderName;
        this.messageText = messageText;
        this.messageFlag = "";

        messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() == 10;
    }

    public int checkRecipientCell() {
        if (recipientNumber != null && recipientNumber.matches("^\\+27\\d{9}$")) {
            return 1;
        }
        return 0;
    }

    public boolean checkMessageLength() {
        return messageText != null && messageText.length() <= 250;
    }

    public String createMessageHash() {
        if (messageText == null || messageText.trim().isEmpty()) {
            String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
            return prefix + ":" + messageNumber + ":EMPTY";
        }

        String[] words = messageText.trim().split("\\s+");

        if (words.length == 1) {
            String singleWord = cleanWord(words[0]).toUpperCase();
            String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
            return prefix + ":" + messageNumber + ":" + singleWord;
        }

        String firstWord = cleanWord(words[0]).toUpperCase();
        String lastWord = cleanWord(words[words.length - 1]).toUpperCase();
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;

        return prefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^a-zA-Z0-9]$", "");
    }

    public String sentMessage(int choice) {

        switch (choice) {

            case 1:
                totalSentMessages++;
                messageFlag = "Sent";

                sentMessages.add(this);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                return "Message successfully sent.";

            case 2:
                totalDisregardedMessages++;
                messageFlag = "Disregarded";

                disregardedMessages.add(this);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                return "Press 0 to delete message.";

            case 3:
                messageFlag = "Stored";
                totalStoredMessages++;

                storedMessages.add(this);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);

                storeMessage();

                return "Message successfully stored.";

            default:
                return "Invalid option selected.";
        }
    }

    public String printMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nSender: " + senderName
                + "\nRecipient: " + recipientNumber
                + "\nMessage: " + messageText;
    }

    public void storeMessage() {
        try {
            ArrayList<String> existingMessages = loadMessagesFromJSON();

            String jsonMessage = "{\n";
            jsonMessage += "  \"messageID\": \"" + escapeJson(messageID) + "\",\n";
            jsonMessage += "  \"messageHash\": \"" + escapeJson(messageHash) + "\",\n";
            jsonMessage += "  \"sender\": \"" + escapeJson(senderName) + "\",\n";
            jsonMessage += "  \"recipient\": \"" + escapeJson(recipientNumber) + "\",\n";
            jsonMessage += "  \"messageText\": \"" + escapeJson(messageText) + "\",\n";
            jsonMessage += "  \"flag\": \"" + escapeJson(messageFlag) + "\"\n";
            jsonMessage += "}";

            existingMessages.add(jsonMessage);

            FileWriter writer = new FileWriter("messages.json");
            writer.write("[\n");

            for (int i = 0; i < existingMessages.size(); i++) {
                writer.write(existingMessages.get(i));
                if (i < existingMessages.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("]");
            writer.close();

        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public static ArrayList<String> loadMessagesFromJSON() {
        ArrayList<String> messages = new ArrayList<>();
        File file = new File("messages.json");

        if (!file.exists()) {
            return messages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String jsonContent = content.toString();
            int braceCount = 0;
            int start = -1;

            for (int i = 0; i < jsonContent.length(); i++) {
                char c = jsonContent.charAt(i);

                if (c == '{') {
                    if (braceCount == 0) {
                        start = i;
                    }
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0 && start != -1) {
                        String message = jsonContent.substring(start, i + 1);
                        messages.add(message);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading messages: " + e.getMessage());
        }

        return messages;
    }

    public static void readMessagesFromFile() {
        storedMessages.clear();

        ArrayList<String> jsonMessages = loadMessagesFromJSON();

        for (String jsonMsg : jsonMessages) {
            StoredMessageData sm = parseJSONToStoredMessage(jsonMsg);
            Message m = new Message(sm.messageID, 0, sm.recipient, sm.sender, sm.messageText);
            m.messageHash = sm.messageHash;
            m.messageFlag = sm.flag;
            storedMessages.add(m);
            messageHashes.add(sm.messageHash);
            messageIDs.add(sm.messageID);
        }
    }

    private static StoredMessageData parseJSONToStoredMessage(String jsonString) {
        StoredMessageData msg = new StoredMessageData();

        msg.messageID = extractJsonValue(jsonString, "messageID");
        msg.messageHash = extractJsonValue(jsonString, "messageHash");
        msg.sender = extractJsonValue(jsonString, "sender");
        msg.recipient = extractJsonValue(jsonString, "recipient");
        msg.messageText = extractJsonValue(jsonString, "messageText");
        msg.flag = extractJsonValue(jsonString, "flag");

        return msg;
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\": \"";
        int startIndex = json.indexOf(searchKey);

        if (startIndex == -1) {
            return "";
        }

        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);

        if (endIndex == -1) {
            return "";
        }

        return json.substring(startIndex, endIndex);
    }

    public static void displayStoredSendersAndRecipients() {

        readMessagesFromFile();

        System.out.println();
        System.out.println("===== STORED MESSAGE SENDERS AND RECIPIENTS =====");

        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }

        for (Message m : storedMessages) {
            System.out.println("Sender: " + m.senderName + " | Recipient: " + m.recipientNumber);
        }
    }

    public static String displayLongestMessage() {

        readMessagesFromFile();

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        Message longest = storedMessages.get(0);

        for (Message m : storedMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }

        return longest.messageText;
    }

    public static String searchMessageID(String searchID) {

        readMessagesFromFile();

        for (Message m : storedMessages) {
            if (m.messageID.equals(searchID)) {
                return "Recipient: " + m.recipientNumber + "\nMessage: " + m.messageText;
            }
        }

        for (Message m : sentMessages) {
            if (m.messageID.equals(searchID)) {
                return "Recipient: " + m.recipientNumber + "\nMessage: " + m.messageText;
            }
        }

        return "Message ID not found.";
    }

    public static ArrayList<String> searchRecipientMessages(String recipient) {

        readMessagesFromFile();

        ArrayList<String> results = new ArrayList<>();

        for (Message m : storedMessages) {
            if (m.recipientNumber.equals(recipient)) {
                results.add(m.messageText);
            }
        }

        for (Message m : sentMessages) {
            if (m.recipientNumber.equals(recipient)) {
                results.add(m.messageText);
            }
        }

        return results;
    }

    public static String deleteMessage(String hash) {

        readMessagesFromFile();

        boolean found = false;
        ArrayList<Message> newList = new ArrayList<>();

        for (Message m : storedMessages) {
            if (m.messageHash.equals(hash)) {
                found = true;
            } else {
                newList.add(m);
            }
        }

        if (!found) {
            return "Message hash not found.";
        }

        try {
            FileWriter writer = new FileWriter("messages.json");
            writer.write("[\n");

            for (int i = 0; i < newList.size(); i++) {
                Message m = newList.get(i);
                String jsonMessage = "{\n";
                jsonMessage += "  \"messageID\": \"" + escapeJsonStatic(m.messageID) + "\",\n";
                jsonMessage += "  \"messageHash\": \"" + escapeJsonStatic(m.messageHash) + "\",\n";
                jsonMessage += "  \"sender\": \"" + escapeJsonStatic(m.senderName) + "\",\n";
                jsonMessage += "  \"recipient\": \"" + escapeJsonStatic(m.recipientNumber) + "\",\n";
                jsonMessage += "  \"messageText\": \"" + escapeJsonStatic(m.messageText) + "\",\n";
                jsonMessage += "  \"flag\": \"" + escapeJsonStatic(m.messageFlag) + "\"\n";
                jsonMessage += "}";
                
                writer.write(jsonMessage);
                if (i < newList.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("]");
            writer.close();

            storedMessages = newList;

        } catch (IOException e) {
            return "Error deleting message.";
        }

        return "Message successfully deleted.";
    }

    private static String escapeJsonStatic(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public static void displayReport() {

        readMessagesFromFile();

        System.out.println();
        System.out.println("===== STORED MESSAGES REPORT =====");

        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }

        for (Message m : storedMessages) {
            System.out.println();
            System.out.println("Message Hash: " + m.messageHash);
            System.out.println("Recipient: " + m.recipientNumber);
            System.out.println("Message: " + m.messageText);
        }
    }

    public static void populateTestData() {

        clearAllData();

        Message m1 = new Message("1111111111", 1, "+27834557896", "Developer", "Did you get the cake?");
        Message m2 = new Message("2222222222", 2, "+27838884567", "Developer", "Where are you? You are late! I have asked you to be on time.");
        Message m3 = new Message("3333333333", 3, "+27834484567", "Developer", "Yohoooo, I am at your gate.");
        Message m4 = new Message("0838884567", 4, "0838884567", "Developer", "It is dinner time!");
        Message m5 = new Message("5555555555", 5, "+27838884567", "Developer", "Ok, I am leaving without you.");

        m1.sentMessage(1);
        m2.sentMessage(3);
        m3.sentMessage(2);
        m4.sentMessage(1);
        m5.sentMessage(3);
    }

    public static void clearAllData() {

        totalSentMessages = 0;
        totalStoredMessages = 0;
        totalDisregardedMessages = 0;

        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        try {
            FileWriter writer = new FileWriter("messages.json");
            writer.write("[]");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error clearing file.");
        }
    }

    public static int returnTotalMessages() {
        return totalSentMessages;
    }

    public static int returnTotalStored() {
        return totalStoredMessages;
    }

    public static int returnTotalDisregarded() {
        return totalDisregardedMessages;
    }

    public static ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public static ArrayList<Message> getDisregardedMessages() {
        return disregardedMessages;
    }

    public static ArrayList<Message> getStoredMessages() {
        readMessagesFromFile();
        return storedMessages;
    }

    public static ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public static ArrayList<String> getMessageIDs() {
        return messageIDs;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageFlag() {
        return messageFlag;
    }
}

class StoredMessageData {
    String messageID = "";
    String messageHash = "";
    String sender = "";
    String recipient = "";
    String messageText = "";
    String flag = "";
}