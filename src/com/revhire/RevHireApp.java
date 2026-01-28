package com.revhire;

import com.revhire.ui.MainMenu;
import com.revhire.util.DBUtil;
import com.revhire.util.LoggerUtil;
import org.apache.log4j.Logger;

public class RevHireApp {

    private static final Logger logger = LoggerUtil.getLogger(RevHireApp.class);

    public static void main(String[] args) {

        logger.info("Initializing RevHire Job Portal...");
        System.out.println("Initializing RevHire Job Portal...");

        try {
            // Test database connection
            DBUtil.getConnection();
            logger.info("Database connection successful!");
            System.out.println("Database connection successful!");

            // Start the application
            MainMenu mainMenu = new MainMenu();
            mainMenu.show();

        } catch (Exception e) {
            logger.error("Failed to start RevHire", e);
            System.out.println("Failed to start RevHire: " + e.getMessage());
        } finally {
            DBUtil.closeConnection();
            logger.info("Application shutdown");
            System.out.println("\nThank you for using RevHire!");
        }
    }
}
