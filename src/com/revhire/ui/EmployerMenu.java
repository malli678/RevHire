package com.revhire.ui;

import com.revhire.model.*;
import com.revhire.service.EmployerService;
import com.revhire.service.AuthService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EmployerMenu {
    private EmployerService service;
    private AuthService authService;
    private User user;
    private Employer profile;

    public EmployerMenu(EmployerService service, User user) {
        this.service = service;
        this.authService = new AuthService();
        this.user = user;
        loadProfile();
    }

    private void loadProfile() {
        try {
            this.profile = service.getEmployerProfile(user.getId());
        } catch (SQLException e) {
            System.out.println("Error loading profile: " + e.getMessage());
        }
    }

    public void show() {
        while (true) {
            try {
                int unreadCount = service.getUnreadNotificationCount(user.getId());
                String notificationBadge = unreadCount > 0 ? " (" + unreadCount + " unread)" : "";
                
                ConsoleUtils.printHeader("Employer Dashboard - " + (profile != null ? profile.getCompanyName() : user.getName()));
                System.out.println("1. View/Update Profile");
                System.out.println("2. Post New Job");
                System.out.println("3. Manage Jobs");
                System.out.println("4. View Applications");
                System.out.println("5. Notifications" + notificationBadge);
                System.out.println("6. Change Password");
                System.out.println("7. Logout");
                ConsoleUtils.printLine(60);

                int choice = ConsoleUtils.readInt("Enter your choice: ");

                switch (choice) {
                    case 1:
                        manageProfile();
                        break;
                    case 2:
                        postNewJob();
                        break;
                    case 3:
                        manageJobs();
                        break;
                    case 4:
                        viewApplications();
                        break;
                    case 5:
                        viewNotifications();
                        break;
                    case 6:
                        changePassword();
                        break;
                    case 7:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }
    
    // NEW: Change Password Method (same as JobSeekerMenu)
    private void changePassword() {
        ConsoleUtils.printHeader("Change Password");
        
        String currentPassword = ConsoleUtils.readString("Current Password: ");
        String newPassword = ConsoleUtils.readString("New Password: ");
        String confirmPassword = ConsoleUtils.readString("Confirm New Password: ");
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match!");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        if (newPassword.length() < 6) {
            System.out.println("Password must be at least 6 characters long!");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        try {
            boolean success = authService.changePassword(user.getId(), currentPassword, newPassword);
            if (success) {
                System.out.println("Password changed successfully!");
                service.sendNotification(user.getId(), "Your password was changed successfully.");
            } else {
                System.out.println("Failed to change password. Please check your current password.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void manageProfile() throws SQLException {
        ConsoleUtils.printHeader("Company Profile");
        
        if (profile == null) {
            System.out.println("No profile found!");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Company Name: " + profile.getCompanyName());
        System.out.println("Industry: " + profile.getIndustry());
        System.out.println("Size: " + profile.getSize());
        System.out.println("Description: " + profile.getDescription());
        System.out.println("Website: " + profile.getWebsite());
        System.out.println("\nContact Person: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Location: " + user.getLocation());
        
        System.out.println("\n1. Update Profile");
        System.out.println("2. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            updateProfile();
        }
    }

    private void updateProfile() throws SQLException {
        ConsoleUtils.printHeader("Update Profile");
        
        String companyName = ConsoleUtils.readString("Company Name [" + profile.getCompanyName() + "]: ");
        String industry = ConsoleUtils.readString("Industry [" + profile.getIndustry() + "]: ");
        String size = ConsoleUtils.readString("Size [" + profile.getSize() + "]: ");
        String description = ConsoleUtils.readString("Description [" + profile.getDescription() + "]: ");
        String website = ConsoleUtils.readString("Website [" + profile.getWebsite() + "]: ");
        String contactName = ConsoleUtils.readString("Contact Person [" + user.getName() + "]: ");
        String phone = ConsoleUtils.readString("Phone [" + user.getPhone() + "]: ");
        String location = ConsoleUtils.readString("Location [" + user.getLocation() + "]: ");
        
        if (!companyName.isEmpty()) profile.setCompanyName(companyName);
        if (!industry.isEmpty()) profile.setIndustry(industry);
        if (!size.isEmpty()) profile.setSize(size);
        if (!description.isEmpty()) profile.setDescription(description);
        if (!website.isEmpty()) profile.setWebsite(website);
        if (!contactName.isEmpty()) user.setName(contactName);
        if (!phone.isEmpty()) user.setPhone(phone);
        if (!location.isEmpty()) user.setLocation(location);
        
        // Update employer profile
        boolean empSuccess = service.updateEmployerProfile(user.getId(), 
            profile.getCompanyName(), profile.getIndustry(), profile.getSize(), 
            profile.getDescription(), profile.getWebsite());
        
        // Update user info
        com.revhire.service.AuthService authService = new com.revhire.service.AuthService();
        boolean userSuccess = authService.updateProfile(user.getId(), user.getName(), user.getPhone(), user.getLocation());
        
        if (empSuccess && userSuccess) {
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Profile update partially completed!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void postNewJob() throws SQLException {
        ConsoleUtils.printHeader("Post New Job");
        
        String title = ConsoleUtils.readString("Job Title: ");
        String description = ConsoleUtils.readString("Job Description: ");
        String skills = ConsoleUtils.readString("Required Skills (comma separated): ");
        int expYears = ConsoleUtils.readInt("Years of Experience Required: ");
        String education = ConsoleUtils.readString("Education Requirements: ");
        String location = ConsoleUtils.readString("Job Location: ");
        double salaryMin = ConsoleUtils.readDouble("Minimum Salary: ");
        double salaryMax = ConsoleUtils.readDouble("Maximum Salary: ");
        String jobType = ConsoleUtils.readString("Job Type (fulltime/parttime/contract/internship): ");
        Date deadline = ConsoleUtils.readDate("Application Deadline: ");
        
        int jobId = service.createJob(user.getId(), title, description, skills, expYears, 
                                      education, location, salaryMin, salaryMax, jobType, deadline);
        
        if (jobId > 0) {
            System.out.println("Job posted successfully! Job ID: " + jobId);
            service.sendNotification(user.getId(), "New job posted: " + title);
        } else {
            System.out.println("Failed to post job!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void manageJobs() throws SQLException {
        ConsoleUtils.printHeader("Manage Jobs");
        
        List<Job> jobs = service.getMyJobs(user.getId());
        
        if (jobs.isEmpty()) {
            System.out.println("No jobs posted yet.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Your Posted Jobs:");
        printLine(100);
        System.out.printf("%-5s %-30s %-15s %-15s %-10s %-10s\n", "ID", "Title", "Location", "Salary", "Type", "Status");
        printLine(100);
        
        for (Job job : jobs) {
            System.out.printf("%-5d %-30s %-15s $%-14.2f %-10s %-10s\n", 
                job.getId(), 
                truncateString(job.getTitle(), 28),
                truncateString(job.getLocation(), 13),
                job.getSalaryMax(),
                job.getJobType(),
                job.getStatus());
        }
        
        System.out.println("\n1. View Job Details");
        System.out.println("2. Close/Reopen Job");
        System.out.println("3. View Applications for Job");
        System.out.println("4. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2 || choice == 3) {
            int jobId = ConsoleUtils.readInt("Enter Job ID: ");
            Job selectedJob = null;
            for (Job job : jobs) {
                if (job.getId() == jobId) {
                    selectedJob = job;
                    break;
                }
            }
            
            if (selectedJob != null) {
                if (choice == 1) {
                    viewJobDetails(selectedJob);
                } else if (choice == 2) {
                    toggleJobStatus(selectedJob);
                } else if (choice == 3) {
                    viewJobApplications(selectedJob);
                }
            } else {
                System.out.println("Invalid Job ID!");
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }

    private void viewJobDetails(Job job) {
        ConsoleUtils.printHeader("Job Details - " + job.getTitle());
        
        System.out.println("Title: " + job.getTitle());
        System.out.println("Description: " + job.getDescription());
        System.out.println("Required Skills: " + job.getSkills());
        System.out.println("Experience Required: " + job.getExperienceYears() + " years");
        System.out.println("Education: " + job.getEducation());
        System.out.println("Location: " + job.getLocation());
        System.out.println("Salary Range: $" + job.getSalaryMin() + " - $" + job.getSalaryMax());
        System.out.println("Job Type: " + job.getJobType());
        System.out.println("Application Deadline: " + job.getDeadline());
        System.out.println("Status: " + job.getStatus());
        System.out.println("Posted On: " + job.getCreatedAt());
        
        ConsoleUtils.pressEnterToContinue();
    }

    private void toggleJobStatus(Job job) throws SQLException {
        ConsoleUtils.printHeader("Change Job Status - " + job.getTitle());
        
        String newStatus = job.getStatus().equals("open") ? "closed" : "open";
        System.out.println("Current Status: " + job.getStatus());
        System.out.println("New Status: " + newStatus);
        
        String confirm = ConsoleUtils.readString("Confirm change? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            boolean success = service.updateJobStatus(job.getId(), newStatus);
            if (success) {
                System.out.println("Job status updated successfully!");
                service.sendNotification(user.getId(), "Job status changed: " + job.getTitle() + " is now " + newStatus);
            } else {
                System.out.println("Failed to update job status!");
            }
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void viewJobApplications(Job job) throws SQLException {
        ConsoleUtils.printHeader("Applications for - " + job.getTitle());
        
        List<Application> applications = service.getApplicationsForJob(job.getId());
        
        if (applications.isEmpty()) {
            System.out.println("No applications for this job yet.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Total Applications: " + applications.size());
        printLine(100);
        System.out.printf("%-5s %-20s %-15s %-20s %-20s\n", "ID", "Applicant", "Applied Date", "Status", "Cover Letter");
        printLine(100);
        
        for (Application app : applications) {
            String coverLetterPreview = app.getCoverLetter() != null && app.getCoverLetter().length() > 15 ? 
                app.getCoverLetter().substring(0, 15) + "..." : app.getCoverLetter();
            
            System.out.printf("%-5d %-20s %-15s %-20s %-20s\n", 
                app.getId(), 
                "Applicant " + app.getJobSeekerId(), // In real app, get name from DB
                app.getAppliedAt().toString(),
                app.getStatus(),
                coverLetterPreview != null ? coverLetterPreview : "N/A");
        }
        
        System.out.println("\n1. View Application Details");
        System.out.println("2. Update Application Status");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
            int appId = ConsoleUtils.readInt("Enter Application ID: ");
            
            if (choice == 1) {
                viewApplicationDetails(appId);
            } else {
                updateApplicationStatus(appId);
            }
        }
    }

    private void viewApplicationDetails(int applicationId) throws SQLException {
        // Implementation for viewing full application details
        ConsoleUtils.printHeader("Application Details");
        System.out.println("Application ID: " + applicationId);
        // Add more details here
        ConsoleUtils.pressEnterToContinue();
    }

    private void updateApplicationStatus(int applicationId) throws SQLException {
        ConsoleUtils.printHeader("Update Application Status");
        
        System.out.println("1. Shortlist");
        System.out.println("2. Reject");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
            String status = choice == 1 ? "shortlisted" : "rejected";
            String reason = ConsoleUtils.readString("Reason/Comments (optional): ");
            
            boolean success = service.updateApplicationStatus(applicationId, status, reason);
            if (success) {
                System.out.println("Application status updated successfully!");
            } else {
                System.out.println("Failed to update application status!");
            }
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void viewApplications() throws SQLException {
        ConsoleUtils.printHeader("All Applications");
        
        List<Job> jobs = service.getMyJobs(user.getId());
        
        if (jobs.isEmpty()) {
            System.out.println("No jobs posted yet.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        int totalApplications = 0;
        for (Job job : jobs) {
            List<Application> applications = service.getApplicationsForJob(job.getId());
            totalApplications += applications.size();
        }
        
        System.out.println("Total Jobs Posted: " + jobs.size());
        System.out.println("Total Applications Received: " + totalApplications);
        
        System.out.println("\n1. View applications by job");
        System.out.println("2. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            manageJobs(); // Redirect to manage jobs to select job
        }
    }

    private void viewNotifications() throws SQLException {
        ConsoleUtils.printHeader("Notifications");
        
        List<Notification> notifications = service.getNotifications(user.getId());
        
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Total Notifications: " + notifications.size());
        printLine(80);
        
        for (Notification notification : notifications) {
            String readStatus = notification.getIsRead() == 1 ? "[Read]" : "[NEW]";
            System.out.println(readStatus + " " + notification.getCreatedAt() + ": " + notification.getMessage());
        }
        
        ConsoleUtils.pressEnterToContinue();
    }
    
    // Helper methods for Java 1.7 compatibility
    private void printLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("=");
        }
        System.out.println();
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "..";
        }
        return str;
    }
}