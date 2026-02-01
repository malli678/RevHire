package com.revhire.ui;

import com.revhire.model.User;
import com.revhire.service.AuthService;
import com.revhire.service.JobSeekerService;
import com.revhire.service.EmployerService;
import java.sql.SQLException;

public class MainMenu {
    private AuthService authService = new AuthService();
    private JobSeekerService jobSeekerService = new JobSeekerService();
    private EmployerService employerService = new EmployerService();

    public void show() {
        while (true) {
            ConsoleUtils.printHeader("RevHire Job Portal");
            System.out.println("1. Register as Job Seeker");
            System.out.println("2. Register as Employer");
            System.out.println("3. Login");
            System.out.println("4. Forgot Password");
            System.out.println("5. Exit");
            ConsoleUtils.printLine(60);

            int choice = ConsoleUtils.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    registerJobSeeker();
                    break;
                case 2:
                    registerEmployer();
                    break;
                case 3:
                    login();
                    break;
                case 4:
                    forgotPassword();
                    break;
                case 5:
                    System.out.println("Thank you for using RevHire!");
                    return;
                default:
                    System.out.println("Invalid choice!");
                    ConsoleUtils.pressEnterToContinue();
            }
        }
    }
    
    // UPDATED: registerJobSeeker with validation
    private void registerJobSeeker() {
        ConsoleUtils.printHeader("Job Seeker Registration");
        
        // Using new validation methods
        String email = ConsoleUtils.readEmail("Email: ");
        String password = ConsoleUtils.readPassword("Password: ");
        String name = ConsoleUtils.readString("Full Name: ");
        String phone = ConsoleUtils.readPhone("Phone (10 digits): ");
        String location = ConsoleUtils.readString("Location: ");
        String objective = ConsoleUtils.readString("Career Objective: ");
        String skills = ConsoleUtils.readString("Skills (comma separated): ");
        String certifications = ConsoleUtils.readString("Certifications: ");

        try {
            User user = authService.register(email, password, "jobseeker", name, phone, location);
            if (user != null) {
                boolean success = jobSeekerService.registerJobSeeker(user.getId(), objective, skills, certifications);
                if (success) {
                    System.out.println("\nRegistration successful! Please login.");
                } else {
                    System.out.println("\nRegistration completed partially. Please contact support.");
                }
            } else {
                System.out.println("\nRegistration failed!");
            }
        } catch (SQLException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }
    
    // UPDATED: registerEmployer with validation
    private void registerEmployer() {
        ConsoleUtils.printHeader("Employer Registration");
        
        // Using new validation methods
        String email = ConsoleUtils.readEmail("Email: ");
        String password = ConsoleUtils.readPassword("Password: ");
        String name = ConsoleUtils.readString("Contact Person Name: ");
        String phone = ConsoleUtils.readPhone("Phone (10 digits): ");
        String location = ConsoleUtils.readString("Location: ");
        String companyName = ConsoleUtils.readString("Company Name: ");
        String industry = ConsoleUtils.readString("Industry: ");
        String size = ConsoleUtils.readString("Company Size: ");
        String description = ConsoleUtils.readString("Company Description: ");
        String website = ConsoleUtils.readString("Website: ");

        try {
            User user = authService.register(email, password, "employer", name, phone, location);
            if (user != null) {
                boolean success = employerService.registerEmployer(user.getId(), companyName, industry, size, description, website);
                if (success) {
                    System.out.println("\nRegistration successful! Please login.");
                } else {
                    System.out.println("\nRegistration completed partially. Please contact support.");
                }
            } else {
                System.out.println("\nRegistration failed!");
            }
        } catch (SQLException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }
    
    // NEW: Forgot Password Feature
    private void forgotPassword() {
        ConsoleUtils.printHeader("Forgot Password Recovery");
        
        System.out.println("Recover password for:");
        System.out.println("1. Job Seeker Account");
        System.out.println("2. Employer Account");
        ConsoleUtils.printLine(60);
        
        int roleChoice = ConsoleUtils.readInt("Enter your choice: ");
        String expectedRole = (roleChoice == 1) ? "jobseeker" : "employer";
        
        System.out.println("\nSelected: " + (roleChoice == 1 ? "Job Seeker" : "Employer") + " Account");
        ConsoleUtils.printLine(60);
        
        String email = ConsoleUtils.readEmail("Enter your registered email: ");
        
        try {
            User user = authService.getUserByEmail(email);
            if (user == null) {
                System.out.println("\n>> ERROR: Email not found in our system.");
                ConsoleUtils.pressEnterToContinue();
                return;
            }
            
            // Check if user role matches selected role
            if (!expectedRole.equals(user.getRole())) {
                System.out.println("\n>> ERROR: This email is registered as a " + 
                                 user.getRole().toUpperCase() + ", not as a " + 
                                 expectedRole.toUpperCase() + "!");
                System.out.println(">> Please select the correct account type.");
                ConsoleUtils.pressEnterToContinue();
                return;
            }
            
            System.out.println("\nSecurity Question: " + authService.generateSecurityQuestion(user.getId()));
            String answer = ConsoleUtils.readString("Your answer: ");
            
            if (authService.verifySecurityAnswer(user.getId(), answer)) {
                String tempPassword = authService.generateTemporaryPassword();
                System.out.println("\n>> SUCCESS: Password reset initiated!");
                System.out.println(">> Temporary Password: " + tempPassword);
                System.out.println(">> Please use this to login and change your password immediately.");
                
                // Reset password with temporary password
                authService.resetPassword(user.getId(), tempPassword);
                System.out.println("\n>> NOTE: In a real system, this temporary password would be sent to your email.");
            } else {
                System.out.println("\n>> ERROR: Security answer incorrect. Password reset failed.");
            }
        } catch (SQLException e) {
            System.out.println("\n>> ERROR: " + e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void login() {
        ConsoleUtils.printHeader("Login");
        
        System.out.println("Login as:");
        System.out.println("1. Job Seeker");
        System.out.println("2. Employer");
        ConsoleUtils.printLine(60);
        
        int roleChoice = ConsoleUtils.readInt("Enter your choice: ");
        String expectedRole = (roleChoice == 1) ? "jobseeker" : "employer";
        
        System.out.println("\nSelected Role: " + (roleChoice == 1 ? "Job Seeker" : "Employer"));
        ConsoleUtils.printLine(60);
        
        String email = ConsoleUtils.readEmail("Email: ");
        String password = ConsoleUtils.readPassword("Password: ");
        
        
        try {
            User user = authService.login(email, password);
            if (user != null) {
                // Check if user role matches selected role
                if (!expectedRole.equals(user.getRole())) {
                    System.out.println("\nError: You are registered as a " + 
                                     user.getRole().toUpperCase() + ", not as a " + 
                                     expectedRole.toUpperCase() + "!");
                    System.out.println("Please select the correct login option.");
                    ConsoleUtils.pressEnterToContinue();
                    return;
                }
                
                System.out.println("\nLogin successful! Welcome, " + user.getName());
                ConsoleUtils.pressEnterToContinue();
                
                if ("jobseeker".equals(user.getRole())) {
                    JobSeekerMenu jobSeekerMenu = new JobSeekerMenu(jobSeekerService, user);
                    jobSeekerMenu.show();
                } else {
                    EmployerMenu employerMenu = new EmployerMenu(employerService, user);
                    employerMenu.show();
                }
            } else {
                System.out.println("\nInvalid email or password!");
                ConsoleUtils.pressEnterToContinue();
            }
        } catch (SQLException e) {
            System.out.println("\nError: " + e.getMessage());
            ConsoleUtils.pressEnterToContinue();
        }
    }
}