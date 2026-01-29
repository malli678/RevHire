package com.revhire.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUtils {
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Make these public for testing
    public static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    public static int readInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static double readDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static Date readDate(String prompt) {
        System.out.print(prompt + " (yyyy-MM-dd): ");
        while (true) {
            try {
                String dateStr = scanner.nextLine();
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.print("Please enter a valid date (yyyy-MM-dd): ");
            }
        }
    }
    
    // REDUCED clear screen
    public static void clearScreen() {
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }
    
    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    public static void printHeader(String title) {
        // Don't clear screen, just add a couple newlines
        System.out.println("\n\n" + repeatString("=", 60));
        centerText(title, 60);
        System.out.println(repeatString("=", 60));
    }
    
    // Helper method to repeat strings (Java 1.7 compatible)
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    // Helper method for printing lines
    public static void printLine(int length) {
        System.out.println(repeatString("=", length));
    }
    
    // Helper method for centered text
    public static void centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.println(repeatString(" ", padding) + text);
    }
    
    // Email validation
    public static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = scanner.nextLine().trim();
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            System.out.println("Invalid email format. Please enter a valid email (e.g., user@example.com)");
        }
    }
    
    // Phone validation
    public static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String phone = scanner.nextLine().trim();
            if (PHONE_PATTERN.matcher(phone).matches()) {
                return phone;
            }
            System.out.println("Invalid phone number. Please enter 10 digits.");
        }
    }
    
    // Password validation
    public static String readPassword(String prompt) {
        while (true) {
            System.out.print(prompt);
            String password = scanner.nextLine();
            if (password.length() >= 6) {
                return password;
            }
            System.out.println("Password must be at least 6 characters. Try again:");
        }
    }
}