/**
 * The main function for user authentication and banking operations.
 *
 * @param  args  the command-line arguments
 * @return       void
 */

package org.eb;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ATM {
    private static final String USER_DATA_FILE = "src/main/java/org/eb/userdata.txt";
    private static final Map<String, String> users = new HashMap<>();
    private static final Map<String, Double> balances = new HashMap<>();
    private static final Map<String, StringBuilder> transactionHistories = new HashMap<>();

    public static void main(String[] args) {
        loadDataFromFile();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String username;
        String pin;
        double balance;

        try {
            System.out.print("Enter your username: ");
            username = reader.readLine();

            System.out.print("Enter your PIN: ");
            pin = reader.readLine();

            if (authenticateUser(username, pin)) {
                balance = getBalance(username);
                System.out.println("Welcome, " + username + "! Your balance is: $" + balance);

                System.out.println("1. Withdraw");
                System.out.println("2. Deposit");
                System.out.print("Choose an option: ");
                int option = Integer.parseInt(reader.readLine());

                if (option == 1) {
                    System.out.print("Enter the amount to withdraw: ");
                    double amount = Double.parseDouble(reader.readLine());
                    if (withdraw(username, amount)) {
                        System.out.println("Withdrawal successful!");
                    } else {
                        System.out.println("Insufficient funds!");
                    }
                } else if (option == 2) {
                    System.out.print("Enter the amount to deposit: ");
                    double amount = Double.parseDouble(reader.readLine());
                    deposit(username, amount);
                    System.out.println("Deposit successful!");
                }

                balance = getBalance(username);
                System.out.println("Your updated balance is: $" + balance);
                System.out.println("Transaction history:");
                System.out.println(transactionHistories.get(username));
            } else {
                System.out.println("Invalid username or PIN! Please try again.");
            }

            reader.close();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    private static void loadDataFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String pin = parts[1];
                double balance = Double.parseDouble(parts[2]);
                addUser(username, pin, balance);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading data from file: " + e.getMessage());
        }
    }

    private static void saveDataToFile() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String username = entry.getKey();
                String pin = entry.getValue();
                double balance = balances.get(username);
                fileWriter.write(username + "," + pin + "," + balance + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    private static void addUser(String username, String pin, double initialBalance) {
        users.put(username, pin);
        balances.put(username, initialBalance);
        transactionHistories.put(username, new StringBuilder("Transaction history:\n"));
        saveDataToFile();
    }

    private static boolean authenticateUser(String username, String pin) {
        String storedPin = users.get(username);
        return storedPin != null && storedPin.equals(pin);
    }

    private static double getBalance(String username) {
        return balances.get(username);
    }

    private static boolean withdraw(String username, double amount) {
        double currentBalance = balances.get(username);
        if (amount > currentBalance) {
            return false;
        }
        balances.put(username, currentBalance - amount);
        transactionHistories.get(username).append("Withdraw: -$").append(amount).append("\n");
        saveDataToFile();
        return true;
    }

    private static void deposit(String username, double amount) {
        double currentBalance = balances.get(username);
        balances.put(username, currentBalance + amount);
        transactionHistories.get(username).append("Deposit: +$").append(amount).append("\n");
        saveDataToFile();
    }
}
