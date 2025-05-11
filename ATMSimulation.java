import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ATMSimulation simulates a simple ATM machine.
 * It provides options for balance inquiry, cash withdrawal, cash deposit, PIN change, and transaction history.
 */
public class ATMSimulation {
    private double accountBalance; // Stores the current account balance
    private String pin; // Stores the user's PIN
    private ArrayList<String> transactionHistory; // Stores the transaction history

    private static final String FILE_NAME = "atm_state.txt"; // File to save and load ATM state

    public static void main(String[] args) {
        ATMSimulation atm = new ATMSimulation(); // Create an instance of ATMSimulation
        atm.run(); // Start the ATM simulation
    }

    // Constructor to initialize the ATM state
    public ATMSimulation() {
        transactionHistory = new ArrayList<>();
        loadState(); // Load saved state (if available) or initialize defaults
    }

    // Loads ATM state from a file
    private void loadState() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            pin = reader.readLine(); // Read saved PIN
            accountBalance = Double.parseDouble(reader.readLine()); // Read saved account balance

            String transaction;
            while ((transaction = reader.readLine()) != null) {
                transactionHistory.add(transaction); // Load transaction history
            }
        } catch (IOException e) {
            // If no saved state is found, initialize default values
            System.out.println("No previous data found. Initializing default state.");
            pin = "1234"; // Default PIN
            accountBalance = 5000.00; // Default balance
            transactionHistory.add("Initial deposit: Rs. 5000.00"); // Default initial transaction
        }
    }

    // Saves ATM state to a file
    private void saveState() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(pin + "\n"); // Save the current PIN
            writer.write(accountBalance + "\n"); // Save the current account balance
            for (String transaction : transactionHistory) {
                writer.write(transaction + "\n"); // Save the transaction history
            }
        } catch (IOException e) {
            System.out.println("Error saving state to file.");
        }
    }

    // Main program logic
    public void run() {
        Scanner scanner = new Scanner(System.in); // Create scanner for user input
        int attempts = 0; // Track the number of incorrect PIN attempts

        // Allow the user 3 attempts to enter the correct PIN
        while (attempts < 3) {
            System.out.print("Enter your PIN: ");
            String inputPin = scanner.nextLine();

            if (validatePIN(inputPin)) {
                System.out.println("PIN Verified. Welcome!");
                mainMenu(scanner); // Show the main menu if the PIN is correct
                saveState(); // Save the ATM state before exiting
                scanner.close();
                return;
            } else {
                attempts++;
                System.out.println("Incorrect PIN. Attempts remaining: " + (3 - attempts));
            }
        }
        System.out.println("Too many incorrect attempts. Exiting...");
    }

    // Displays the ATM's main menu
    private void mainMenu(Scanner scanner) {
        boolean exit = false; // Flag to determine when to exit the menu

        while (!exit) {
            // Display the main menu options
            System.out.println("\nATM Machine Menu:");
            System.out.println("1. Balance Inquiry");
            System.out.println("2. Cash Withdrawal");
            System.out.println("3. Cash Deposit");
            System.out.println("4. Change PIN");
            System.out.println("5. Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt(); // Read the user's menu choice
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        balanceInquiry(); // Display account balance
                        break;
                    case 2:
                        cashWithdrawal(scanner); // Perform cash withdrawal
                        break;
                    case 3:
                        cashDeposit(scanner); // Perform cash deposit
                        break;
                    case 4:
                        changePIN(scanner); // Change the account PIN
                        break;
                    case 5:
                        showTransactionHistory(); // Display the transaction history
                        break;
                    case 6:
                        exit = true; // Exit the program
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose a valid menu option."); // Handle invalid input
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number."); // Handle non-numeric input
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Validates the user's PIN
    private boolean validatePIN(String inputPin) {
        return pin.equals(inputPin); // Compare the entered PIN with the stored PIN
    }

    // Displays the current account balance
    private void balanceInquiry() {
        System.out.printf("Your current balance is: Rs. %.2f%n", accountBalance);
        transactionHistory.add("Balance Inquiry: Rs. " + accountBalance); // Record the transaction
    }

    // Performs cash withdrawal
    private void cashWithdrawal(Scanner scanner) {
        System.out.print("Enter amount to withdraw: Rs. ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            if (amount > accountBalance) {
                System.out.println("Insufficient balance. Transaction cancelled."); // Handle insufficient funds
            } else if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value."); // Handle invalid input
            } else {
                System.out.printf("Are you sure you want to withdraw Rs. %.2f? (yes/no): ", amount);
                if (confirm(scanner)) {
                    accountBalance -= amount; // Deduct the amount from the balance
                    System.out.printf("Withdrawal successful. Amount withdrawn: Rs. %.2f%n", amount);
                    transactionHistory.add("Cash Withdrawal: Rs. " + amount); // Record the transaction
                } else {
                    System.out.println("Withdrawal cancelled.");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a numeric value.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Performs cash deposit
    private void cashDeposit(Scanner scanner) {
        System.out.print("Enter amount to deposit: Rs. ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value."); // Handle invalid input
            } else {
                System.out.printf("Are you sure you want to deposit Rs. %.2f? (yes/no): ", amount);
                if (confirm(scanner)) {
                    accountBalance += amount; // Add the amount to the balance
                    System.out.printf("Deposit successful. Amount deposited: Rs. %.2f%n", amount);
                    transactionHistory.add("Cash Deposit: Rs. " + amount); // Record the transaction
                } else {
                    System.out.println("Deposit cancelled.");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a numeric value.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Changes the account PIN
    private void changePIN(Scanner scanner) {
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();

        if (!validatePIN(currentPin)) {
            System.out.println("Incorrect current PIN."); // Handle incorrect PIN
            return;
        }

        System.out.print("Enter new PIN: ");
        String newPin = scanner.nextLine();

        if (newPin.length() < 4) {
            System.out.println("PIN must be at least 4 digits."); // Handle invalid PIN length
        } else {
            System.out.printf("Are you sure you want to change the PIN? (yes/no): ");
            if (confirm(scanner)) {
                pin = newPin; // Update the PIN
                System.out.println("PIN changed successfully.");
                transactionHistory.add("PIN Change"); // Record the transaction
            } else {
                System.out.println("PIN change cancelled.");
            }
        }
    }

    // Confirms the user's choice
    private boolean confirm(Scanner scanner) {
        String response = scanner.nextLine().toLowerCase();
        return response.equals("yes");
    }

    // Displays the transaction history
    private void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {
            System.out.println("\nTransaction History:");
            for (String transaction : transactionHistory) {
                System.out.println(transaction);
            }
        }
    }
}
