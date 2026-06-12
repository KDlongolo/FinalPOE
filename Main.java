package mainApplication;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static String loggedInUser = "";

    public static void main(String[] args) {

        Scanner scanInput = new Scanner(System.in);

        System.out.println("===== REGISTRATION =====");

        System.out.print("Enter first name: ");
        String first = scanInput.nextLine();

        System.out.print("Enter last name: ");
        String last = scanInput.nextLine();

        System.out.print("Enter username: ");
        String username = scanInput.nextLine();

        System.out.print("Enter password: ");
        String password = scanInput.nextLine();

        System.out.print("Enter South African cell phone number (include +27): ");
        String phone = scanInput.nextLine();

        Login accountLogin = new Login(first, last, username, password, phone);

        System.out.println(accountLogin.registerUser());

        if (accountLogin.checkUserName()
                && accountLogin.checkPasswordComplexity()
                && accountLogin.checkCellPhoneNumber()) {

            System.out.println();
            System.out.println("===== LOGIN =====");

            System.out.print("Enter username: ");
            String loginUsername = scanInput.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanInput.nextLine();

            System.out.println(accountLogin.returnLoginStatus(loginUsername, loginPassword));

            if (accountLogin.loginUser(loginUsername, loginPassword)) {

                loggedInUser = first + " " + last;
                
                // Load existing stored messages from JSON file
                Message.readMessagesFromFile();

                System.out.println();
                System.out.println("Welcome to QuickChat.");

                int option = 0;

                while (option != 5) {

                    System.out.println();
                    System.out.println("===== QUICKCHAT =====");
                    System.out.println("1) Send Messages");
                    System.out.println("2) Show recently sent messages");
                    System.out.println("3) Stored Messages");
                    System.out.println("4) Message Reports");
                    System.out.println("5) Quit");
                    System.out.println("6) Populate Test Data");

                    System.out.print("Select option: ");

                    option = scanInput.nextInt();
                    scanInput.nextLine();

                    switch (option) {

                        case 1:
                            sendMessages(scanInput);
                            break;

                        case 2:
                            System.out.println("Coming Soon.");
                            break;

                        case 3:
                            storedMessagesMenu(scanInput);
                            break;

                        case 4:
                            Message.displayReport();
                            break;

                        case 5:
                            System.out.println("Exiting QuickChat.");
                            break;

                        case 6:
                            Message.populateTestData();
                            System.out.println("Test data populated successfully.");
                            break;

                        default:
                            System.out.println("Invalid menu option.");
                    }
                }
            }
        }

        scanInput.close();
    }

    public static void sendMessages(Scanner scanInput) {

        System.out.print("How many messages would you like to send? ");
        int totalMessages = scanInput.nextInt();
        scanInput.nextLine();

        for (int x = 1; x <= totalMessages; x++) {

            System.out.println();
            System.out.println("===== MESSAGE " + x + " =====");

            String generatedID = createMessageID();

            System.out.println("Message ID generated: " + generatedID);

            System.out.print("Enter recipient number (+27XXXXXXXXX): ");
            String recipient = scanInput.nextLine();

            System.out.print("Enter message: ");
            String text = scanInput.nextLine();

            Message msg = new Message(generatedID, x, recipient, loggedInUser, text);

            if (!msg.checkMessageID()) {
                System.out.println("Message ID invalid.");
                continue;
            }

            if (msg.checkRecipientCell() == 1) {
                System.out.println("Cell phone number successfully captured.");
            } else {
                System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                continue;
            }

            if (msg.checkMessageLength()) {
                System.out.println("Message ready to send.");
            } else {
                int exceeded = text.length() - 250;
                System.out.println("Message exceeds 250 characters by " + exceeded + ", please reduce the size.");
                continue;
            }

            System.out.println();
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message");

            System.out.print("Enter option: ");
            int sendChoice = scanInput.nextInt();
            scanInput.nextLine();

            System.out.println(msg.sentMessage(sendChoice));

            System.out.println();
            System.out.println(msg.printMessage());
        }

        System.out.println();
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println("Total messages stored: " + Message.returnTotalStored());
        System.out.println("Total messages disregarded: " + Message.returnTotalDisregarded());
    }

    public static void storedMessagesMenu(Scanner scanInput) {

        int option = 0;

        while (option != 7) {

            System.out.println();
            System.out.println("===== STORED MESSAGES MENU =====");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search message by Message ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete message by message hash");
            System.out.println("6) Display stored message report");
            System.out.println("7) Back to main menu");

            System.out.print("Select option: ");
            option = scanInput.nextInt();
            scanInput.nextLine();

            switch (option) {

                case 1:
                    Message.displayStoredSendersAndRecipients();
                    break;

                case 2:
                    System.out.println("Longest message: " + Message.displayLongestMessage());
                    break;

                case 3:
                    System.out.print("Enter Message ID: ");
                    String messageID = scanInput.nextLine();
                    System.out.println(Message.searchMessageID(messageID));
                    break;

                case 4:
                    System.out.print("Enter recipient number: ");
                    String recipient = scanInput.nextLine();
                    ArrayList<String> results = Message.searchRecipientMessages(recipient);

                    if (results.isEmpty()) {
                        System.out.println("No messages found.");
                    } else {
                        System.out.println("Messages found:");
                        for (String msg : results) {
                            System.out.println("- " + msg);
                        }
                    }
                    break;

                case 5:
                    System.out.print("Enter message hash: ");
                    String hash = scanInput.nextLine();
                    System.out.println(Message.deleteMessage(hash));
                    break;

                case 6:
                    Message.displayReport();
                    break;

                case 7:
                    System.out.println("Returning to main menu.");
                    break;

                default:
                    System.out.println("Invalid stored messages option.");
            }
        }
    }

    public static String createMessageID() {

        Random random = new Random();
        long generatedNumber = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(generatedNumber);
    }
}