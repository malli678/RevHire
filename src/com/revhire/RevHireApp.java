package com.revhire;

import com.revhire.ui.MainMenu;
import com.revhire.util.DBUtil;

public class RevHireApp {
    public static void main(String[] args) {
        System.out.println("Initializing RevHire Job Portal...");
        
        try {
            // Test database connection
            DBUtil.getConnection();
            System.out.println("Database connection successful!");
            
            // Start the application
            MainMenu mainMenu = new MainMenu();
            mainMenu.show();
            
        } catch (Exception e) {
            System.out.println("Failed to start RevHire: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection();
            System.out.println("\nThank you for using RevHire!");
        }
    }
}