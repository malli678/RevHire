package com.revhire.ui;

import com.revhire.model.*;
import com.revhire.service.EmployerService;
import com.revhire.service.AuthService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.revhire.dao.NotificationDAO;

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
        
        int result = service.createJob(user.getId(), title, description, skills, expYears, 
                                      education, location, salaryMin, salaryMax, jobType, deadline);
        
        if (result > 0) {
            System.out.println("Job posted successfully!");
            service.sendNotification(user.getId(), "New job posted: " + title);
        } else {
            System.out.println("Failed to post job!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void manageJobs() throws SQLException {
        while (true) {
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
            
            System.out.println("\n=== JOB STATISTICS ===");
            int openJobs = 0, closedJobs = 0, totalApplications = 0;
            for (Job job : jobs) {
                if ("open".equals(job.getStatus())) {
                    openJobs++;
                } else {
                    closedJobs++;
                }
                List<Application> apps = service.getApplicationsForJob(job.getId());
                totalApplications += apps.size();
            }
            System.out.printf("Open Jobs: %d | Closed Jobs: %d | Total Applications: %d\n", 
                openJobs, closedJobs, totalApplications);
            
            System.out.println("\n1. View Job Details");
            System.out.println("2. Edit Job");
            System.out.println("3. Close/Reopen Job");
            System.out.println("4. Delete Job");
            System.out.println("5. View Applications for Job");
            System.out.println("6. View Job Statistics");
            System.out.println("7. Back to Dashboard");
            
            int choice = ConsoleUtils.readInt("Enter choice: ");
            
            if (choice >= 1 && choice <= 6) {
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
                        editJob(selectedJob);
                    } else if (choice == 3) {
                        toggleJobStatus(selectedJob);
                    } else if (choice == 4) {
                        deleteJob(selectedJob);
                    } else if (choice == 5) {
                        viewJobApplications(selectedJob);
                    } else if (choice == 6) {
                        viewJobStatistics(selectedJob);
                    }
                } else {
                    System.out.println("Invalid Job ID!");
                    ConsoleUtils.pressEnterToContinue();
                }
            } else if (choice == 7) {
                return;
            }
        }
    }
    
    private void editJob(Job job) throws SQLException {
        ConsoleUtils.printHeader("Edit Job - " + job.getTitle());
        
        System.out.println("Leave field empty to keep current value.");
        System.out.println("Current Title: " + job.getTitle());
        String newTitle = ConsoleUtils.readString("New Title: ");
        
        System.out.println("\nCurrent Description: " + 
            (job.getDescription().length() > 50 ? job.getDescription().substring(0, 50) + "..." : job.getDescription()));
        String newDescription = ConsoleUtils.readString("New Description: ");
        
        System.out.println("\nCurrent Skills: " + job.getSkills());
        String newSkills = ConsoleUtils.readString("New Skills: ");
        
        System.out.println("\nCurrent Experience Required: " + job.getExperienceYears() + " years");
        String expStr = ConsoleUtils.readString("New Experience (years): ");
        
        System.out.println("\nCurrent Location: " + job.getLocation());
        String newLocation = ConsoleUtils.readString("New Location: ");
        
        System.out.println("\nCurrent Salary: $" + job.getSalaryMin() + " - $" + job.getSalaryMax());
        String minSalaryStr = ConsoleUtils.readString("New Minimum Salary: ");
        String maxSalaryStr = ConsoleUtils.readString("New Maximum Salary: ");
        
        // Update job object
        if (!newTitle.isEmpty()) job.setTitle(newTitle);
        if (!newDescription.isEmpty()) job.setDescription(newDescription);
        if (!newSkills.isEmpty()) job.setSkills(newSkills);
        if (!expStr.isEmpty()) job.setExperienceYears(Integer.parseInt(expStr));
        if (!newLocation.isEmpty()) job.setLocation(newLocation);
        if (!minSalaryStr.isEmpty()) job.setSalaryMin(Double.parseDouble(minSalaryStr));
        if (!maxSalaryStr.isEmpty()) job.setSalaryMax(Double.parseDouble(maxSalaryStr));
        
        System.out.println("\n>> NOTE: Job edit feature requires updateJob method in JobDAO");
        System.out.println("Job would be updated with new values.");
        ConsoleUtils.pressEnterToContinue();
    }
    
    private void deleteJob(Job job) throws SQLException {
        ConsoleUtils.printHeader("Delete Job - " + job.getTitle());
        
        System.out.println("WARNING: This will permanently delete the job and all associated applications!");
        System.out.println("Job Title: " + job.getTitle());
        System.out.println("Applications: " + service.getApplicationsForJob(job.getId()).size());
        
        System.out.print("\nAre you sure you want to delete this job? (y/n): ");
        String confirm = ConsoleUtils.readString("");
        
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            System.out.print("Type 'DELETE' to confirm: ");
            String finalConfirm = ConsoleUtils.readString("");
            
            if ("DELETE".equals(finalConfirm)) {
                boolean success = service.deleteJob(job.getId());
                if (success) {
                    System.out.println("\nJob deleted successfully!");
                    service.sendNotification(user.getId(), "Job deleted: " + job.getTitle());
                } else {
                    System.out.println("\nFailed to delete job!");
                }
            } else {
                System.out.println("\nDelete cancelled. Wrong confirmation word.");
            }
        } else {
            System.out.println("\nDelete cancelled.");
        }
        ConsoleUtils.pressEnterToContinue();
    }
    
    private void viewJobStatistics(Job job) throws SQLException {
        ConsoleUtils.printHeader("Job Statistics - " + job.getTitle());
        
        List<Application> applications = service.getApplicationsForJob(job.getId());
        
        System.out.println("=== JOB DETAILS ===");
        System.out.println("Title: " + job.getTitle());
        System.out.println("Status: " + job.getStatus());
        System.out.println("Posted: " + job.getCreatedAt());
        System.out.println("Deadline: " + job.getDeadline());
        
        System.out.println("\n=== APPLICATION STATISTICS ===");
        System.out.println("Total Applications: " + applications.size());
        
        int applied = 0, shortlisted = 0, rejected = 0, withdrawn = 0;
        for (Application app : applications) {
            String status = app.getStatus();
            if ("applied".equals(status)) {
                applied++;
            } else if ("shortlisted".equals(status)) {
                shortlisted++;
            } else if ("rejected".equals(status)) {
                rejected++;
            } else if ("withdrawn".equals(status)) {
                withdrawn++;
            }
        }
        
        System.out.println("Status Breakdown:");
        System.out.println("  Applied: " + applied);
        System.out.println("  Shortlisted: " + shortlisted);
        System.out.println("  Rejected: " + rejected);
        System.out.println("  Withdrawn: " + withdrawn);
        
        ConsoleUtils.pressEnterToContinue();
    }
    
    // SIMPLIFIED: View Applications with only required features
    private void viewApplications() throws SQLException {
        while (true) {
            ConsoleUtils.printHeader("Manage Applications");
            
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
            
            System.out.println("=== QUICK STATS ===");
            System.out.println("Total Jobs: " + jobs.size());
            System.out.println("Total Applications: " + totalApplications);
            
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. View all applications");
            System.out.println("2. Search applications (by status/date)");
            System.out.println("3. Bulk actions (shortlist/reject)");
            System.out.println("4. Back to Dashboard");
            
            int choice = ConsoleUtils.readInt("Enter choice: ");
            
            if (choice == 1) {
                viewAllApplicationsSimple(jobs);
            } else if (choice == 2) {
                simpleSearchApplications(jobs);
            } else if (choice == 3) {
                simpleBulkActions(jobs);
            } else if (choice == 4) {
                return;
            }
        }
    }

    // SIMPLIFIED: View all applications
    private void viewAllApplicationsSimple(List<Job> jobs) throws SQLException {
        ConsoleUtils.printHeader("All Applications");
        
        int totalCount = 0;
        for (Job job : jobs) {
            List<Application> applications = service.getApplicationsForJob(job.getId());
            if (!applications.isEmpty()) {
                System.out.println("\n=== " + job.getTitle() + " (" + applications.size() + " applications) ===");
                for (Application app : applications) {
                    System.out.printf("  ID: %d | Status: %-12s | Applied: %s\n", 
                        app.getId(), app.getStatus(), app.getAppliedAt());
                    totalCount++;
                }
            }
        }
        
        if (totalCount == 0) {
            System.out.println("No applications found.");
        }
        
        System.out.println("\n=== OPTIONS ===");
        System.out.println("1. Select applications for action");
        System.out.println("2. Back to menu");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            String selectedIds = ConsoleUtils.readString("Enter application IDs (comma separated): ");
            String[] idArray = selectedIds.split(",");
            List<Integer> appIds = new ArrayList<Integer>();
            for (String idStr : idArray) {
                try {
                    appIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid
                }
            }
            if (!appIds.isEmpty()) {
                showSimpleBulkActions(appIds);
            }
        }
    }

 // SIMPLIFIED: Search applications (only basic filters) - ALWAYS returns to main menu after action
    private void simpleSearchApplications(List<Job> jobs) throws SQLException {
        ConsoleUtils.printHeader("Search Applications");
        
        System.out.println("=== BASIC FILTERS ===");
        
        // Job filter
        System.out.println("\n1. Filter by Job (optional):");
        for (int i = 0; i < jobs.size(); i++) {
            System.out.printf("   %d. %s\n", i + 1, jobs.get(i).getTitle());
        }
        System.out.print("   Select job (number) or 0 for all: ");
        int jobChoice = ConsoleUtils.readInt("");
        int jobId = (jobChoice > 0 && jobChoice <= jobs.size()) ? jobs.get(jobChoice - 1).getId() : 0;
        
        // Status filter
        System.out.println("\n2. Application Status:");
        System.out.println("   [applied, shortlisted, rejected, withdrawn]");
        String status = ConsoleUtils.readString("   Status (or press Enter for all): ");
        
        // Date filter
        System.out.println("\n3. Application Date (optional):");
        System.out.print("   From date (yyyy-mm-dd): ");
        String fromDate = ConsoleUtils.readString("");
        System.out.print("   To date (yyyy-mm-dd): ");
        String toDate = ConsoleUtils.readString("");
        
        System.out.println("\n=== SEARCH ===");
        System.out.println("1. Search and show results");
        System.out.println("2. Back to main menu");
        
        int action = ConsoleUtils.readInt("Choice: ");
        
        if (action == 2) return; // Exit search
        
        if (action == 1) {
            try {
                // Create simple filters
                Map<String, Object> filters = new HashMap<String, Object>();
                if (jobId > 0) {
                    filters.put("jobId", jobId);
                }
                if (status != null && !status.isEmpty()) {
                    filters.put("status", status);
                }
                if (fromDate != null && !fromDate.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    filters.put("fromDate", sdf.parse(fromDate));
                }
                if (toDate != null && !toDate.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    filters.put("toDate", sdf.parse(toDate));
                }
                
                List<Application> applications = service.searchApplications(user.getId(), filters);
                displaySimpleResultsAndReturn(applications);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }

    // SIMPLIFIED: Display search results and always return to main menu
    private void displaySimpleResultsAndReturn(List<Application> applications) throws SQLException {
        ConsoleUtils.printHeader("Search Results");
        
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            ConsoleUtils.pressEnterToContinue();
            return; // Go back to main menu
        }
        
        System.out.println("Found " + applications.size() + " applications:\n");
        
        System.out.println("ID  Job Title               Applicant          Status       Applied Date");
        printLine(70);
        
        for (Application app : applications) {
            String applicantName = app.getSeekerName() != null ? app.getSeekerName() : "Applicant " + app.getJobSeekerId();
            System.out.printf("%-3d %-22s %-18s %-12s %s\n", 
                app.getId(),
                truncateString(app.getJobTitle() != null ? app.getJobTitle() : "Unknown", 20),
                truncateString(applicantName, 16),
                app.getStatus() != null ? app.getStatus() : "N/A",
                app.getAppliedAt().toString().substring(0, 10));
        }
        
        System.out.println("\n=== ACTIONS ===");
        System.out.println("1. Select applications for bulk action");
        System.out.println("2. Back to main menu");
        
        int choice = ConsoleUtils.readInt("Choice: ");
        
        if (choice == 1) {
            String selectedIds = ConsoleUtils.readString("Enter application IDs (comma separated): ");
            String[] idArray = selectedIds.split(",");
            List<Integer> appIds = new ArrayList<Integer>();
            for (String idStr : idArray) {
                try {
                    appIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid
                }
            }
            if (!appIds.isEmpty()) {
                showSimpleBulkActions(appIds);
            }
        }
        // Always returns to main menu after this
    }

    // SIMPLIFIED: Bulk actions
    private void simpleBulkActions(List<Job> jobs) throws SQLException {
        ConsoleUtils.printHeader("Bulk Actions");
        
        // Collect all applications
        List<Application> allApplications = new ArrayList<Application>();
        for (Job job : jobs) {
            allApplications.addAll(service.getApplicationsForJob(job.getId()));
        }
        
        if (allApplications.isEmpty()) {
            System.out.println("No applications found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Total Applications: " + allApplications.size());
        System.out.println("\nSelect applications to process:");
        System.out.println("1. Enter specific IDs");
        System.out.println("2. Select all applications");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Choice: ");
        
        List<Integer> appIds = new ArrayList<Integer>();
        
        if (choice == 1) {
            String idsInput = ConsoleUtils.readString("Enter Application IDs (comma separated): ");
            String[] idArray = idsInput.split(",");
            for (String idStr : idArray) {
                try {
                    appIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid ID: " + idStr);
                }
            }
        } else if (choice == 2) {
            for (Application app : allApplications) {
                appIds.add(app.getId());
            }
            System.out.println("Selected all " + appIds.size() + " applications.");
        } else {
            return;
        }
        
        if (!appIds.isEmpty()) {
            showSimpleBulkActions(appIds);
        }
    }

    // SIMPLIFIED: Show bulk action options
    private void showSimpleBulkActions(List<Integer> appIds) throws SQLException {
        ConsoleUtils.printHeader("Bulk Actions for " + appIds.size() + " Applications");
        
        System.out.println("=== SELECT ACTION ===");
        System.out.println("1. Shortlist selected");
        System.out.println("2. Reject selected");
        System.out.println("3. Add comments");
        System.out.println("4. Cancel");
        
        int choice = ConsoleUtils.readInt("Choice: ");
        
        if (choice == 4) return;
        
        switch (choice) {
            case 1:
                simpleBulkShortlist(appIds);
                break;
            case 2:
                simpleBulkReject(appIds);
                break;
            case 3:
                simpleAddComments(appIds);
                break;
        }
     // After any action, it will automatically return to main menu
    }

    // SIMPLIFIED: Bulk shortlist
    private void simpleBulkShortlist(List<Integer> appIds) throws SQLException {
        ConsoleUtils.printHeader("Shortlist Applications");
        
        System.out.println("Applications to shortlist: " + appIds.size());
        String comments = ConsoleUtils.readString("Add comments (optional): ");
        
        String confirm = ConsoleUtils.readString("Type 'YES' to shortlist " + appIds.size() + " applications: ");
        if ("YES".equalsIgnoreCase(confirm)) {
            boolean success = service.bulkUpdateApplications(appIds, "shortlisted", comments);
            if (success) {
                System.out.println("\nSuccessfully shortlisted " + appIds.size() + " applications!");
                
                // Send notifications
                for (int appId : appIds) {
                    service.sendNotification(user.getId(), "Application #" + appId + " has been shortlisted.");
                }
            } else {
                System.out.println("\nFailed to update some applications.");
            }
        } else {
            System.out.println("\nAction cancelled.");
        }
        
        ConsoleUtils.pressEnterToContinue();
    }

    // SIMPLIFIED: Bulk reject
    private void simpleBulkReject(List<Integer> appIds) throws SQLException {
        ConsoleUtils.printHeader("Reject Applications");
        
        System.out.println("WARNING: Rejecting " + appIds.size() + " applications");
        String reason = ConsoleUtils.readString("Rejection reason: ");
        
        String confirm = ConsoleUtils.readString("Type 'REJECT' to confirm: ");
        if ("REJECT".equalsIgnoreCase(confirm)) {
            boolean success = service.bulkUpdateApplications(appIds, "rejected", reason);
            if (success) {
                System.out.println("\nSuccessfully rejected " + appIds.size() + " applications!");
                
                // Send notifications
                for (int appId : appIds) {
                    service.sendNotification(user.getId(), "Application #" + appId + " has been rejected.");
                }
            } else {
                System.out.println("\nFailed to update some applications.");
            }
        } else {
            System.out.println("\nAction cancelled.");
        }
        
        ConsoleUtils.pressEnterToContinue();
    }

    // SIMPLIFIED: Add comments
    private void simpleAddComments(List<Integer> appIds) throws SQLException {
        ConsoleUtils.printHeader("Add Comments");
        
        System.out.println("Applications: " + appIds.size());
        String comments = ConsoleUtils.readString("Enter comments: ");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timestamp = sdf.format(new Date());
        String fullComment = "[Comment " + timestamp + "] " + comments + "\n";
        
        String confirm = ConsoleUtils.readString("Add comments to " + appIds.size() + " applications? (y/n): ");
        
        if ("y".equalsIgnoreCase(confirm)) {
            int successCount = 0;
            for (int appId : appIds) {
                // Pass empty string for status if you don't want to change status
                boolean success = service.addApplicationComment(appId, fullComment, "");
                if (success) {
                    successCount++;
                }
            }
            
            if (successCount == appIds.size()) {
                System.out.println("\nComments added to all applications.");
            } else {
                System.out.println("\nComments added to " + successCount + " out of " + appIds.size() + " applications.");
            }
        } else {
            System.out.println("\nAction cancelled.");
        }
        
        ConsoleUtils.pressEnterToContinue();
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
        
        String newStatus = "open".equals(job.getStatus()) ? "closed" : "open";
        System.out.println("Current Status: " + job.getStatus());
        System.out.println("New Status: " + newStatus);
        
        String confirm = ConsoleUtils.readString("Confirm change? (y/n): ");
        if ("y".equalsIgnoreCase(confirm)) {
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
                "Applicant " + app.getJobSeekerId(),
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
        ConsoleUtils.printHeader("Application Details #" + applicationId);
        
        System.out.println("=== APPLICATION INFORMATION ===");
        System.out.println("Application ID: " + applicationId);
        System.out.println("Status: [Would show actual status]");
        System.out.println("Applied Date: [Would show actual date]");
        System.out.println("Cover Letter: [Would show actual cover letter]");
        
        System.out.println("\n=== APPLICANT PROFILE ===");
        System.out.println("Name: [Applicant Name]");
        System.out.println("Email: [Applicant Email]");
        System.out.println("Phone: [Applicant Phone]");
        System.out.println("Location: [Applicant Location]");
        
        System.out.println("\n=== JOB DETAILS ===");
        System.out.println("Job Title: [Job Title]");
        System.out.println("Company: [Company Name]");
        System.out.println("Location: [Job Location]");
        System.out.println("Salary: [Salary Range]");
        
        System.out.println("\n=== APPLICATION HISTORY ===");
        System.out.println("1. View full resume");
        System.out.println("2. Contact applicant");
        System.out.println("3. Schedule interview");
        System.out.println("4. Add notes/comments");
        System.out.println("5. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        // Handle choices
        if (choice == 1) {
            System.out.println("\n>> Would display full resume here");
        } else if (choice == 2) {
            String message = ConsoleUtils.readString("Message to applicant: ");
            System.out.println(">> Message prepared: " + message);
        } else if (choice == 3) {
            System.out.println(">> Interview scheduling feature");
        } else if (choice == 4) {
            String notes = ConsoleUtils.readString("Add notes: ");
            System.out.println(">> Notes saved: " + notes);
        }
        
        if (choice != 5) {
            ConsoleUtils.pressEnterToContinue();
        }
    }

    private void updateApplicationStatus(int applicationId) throws SQLException {
        ConsoleUtils.printHeader("Update Application Status");
        
        System.out.println("1. Shortlist");
        System.out.println("2. Reject");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
            String status = (choice == 1) ? "shortlisted" : "rejected";
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

    private void viewNotifications() throws SQLException {
        ConsoleUtils.printHeader("Notifications");
        
        List<Notification> notifications = service.getNotifications(user.getId());
        
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        // Count unread notifications
        int unreadCount = service.getUnreadNotificationCount(user.getId());
        
        System.out.println("Total Notifications: " + notifications.size() + " (" + unreadCount + " unread)");
        ConsoleUtils.printLine(80);
        
        // Display all notifications with numbering
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            String readStatus = notification.getIsRead() == 1 ? "[Read]" : "[NEW]";
            System.out.println((i+1) + ". " + readStatus + " " + notification.getCreatedAt() + ": " + notification.getMessage());
        }
        
        ConsoleUtils.printLine(80);
        
        // Show action menu
        System.out.println("\nActions:");
        System.out.println("1. Mark all as read");
        System.out.println("2. Mark a specific notification as read");
        System.out.println("3. Delete a notification");
        System.out.println("4. Refresh");
        System.out.println("5. Back to dashboard");
        
        int action = ConsoleUtils.readInt("\nEnter action: ");
        
        // Create ONE NotificationDAO instance outside the switch
        NotificationDAO notificationDAO = new NotificationDAO();
        
        switch (action) {
        case 1: // Mark all as read
            try {
                // Collect all unread notification IDs
                List<Integer> unreadIds = new ArrayList<Integer>();
                for (Notification notification : notifications) {
                    if (notification.getIsRead() == 0) {
                        unreadIds.add(notification.getId());
                    }
                }
                
                if (!unreadIds.isEmpty()) {
                    // Use batch update
                    int markedCount = notificationDAO.markMultipleAsRead(unreadIds);
                    System.out.println(markedCount + " notifications marked as read.");
                } else {
                    System.out.println("No unread notifications to mark.");
                }
            } catch (SQLException e) {
                System.out.println("Error marking notifications as read: " + e.getMessage());
            }
            break;
                
            case 2: // Mark specific as read
                int noteNumber = ConsoleUtils.readInt("Enter notification number to mark as read: ");
                if (noteNumber > 0 && noteNumber <= notifications.size()) {
                    Notification selected = notifications.get(noteNumber - 1);
                    if (selected.getIsRead() == 0) {
                        try {
                            notificationDAO.markAsRead(selected.getId());
                            System.out.println("Notification marked as read.");
                        } catch (SQLException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Notification is already read.");
                    }
                } else {
                    System.out.println("Invalid notification number.");
                }
                break;
                
            case 3: // Delete specific
                int deleteNumber = ConsoleUtils.readInt("Enter notification number to delete: ");
                if (deleteNumber > 0 && deleteNumber <= notifications.size()) {
                    Notification toDelete = notifications.get(deleteNumber - 1);
                    System.out.print("Are you sure you want to delete this notification? (y/n): ");
                    // Simple input
                    String confirm = "";
                    try {
                        java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(System.in));
                        confirm = reader.readLine().trim().toLowerCase();
                    } catch (Exception e) {
                        confirm = "n";
                    }
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        try {
                            notificationDAO.deleteNotification(toDelete.getId());
                            System.out.println("Notification deleted.");
                        } catch (SQLException e) {
                            System.out.println("Error deleting notification: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Invalid notification number.");
                }
                break;
                
            case 4: // Refresh
                // Will show again on next loop
                break;
                
            case 5: // Back
                return;
                
            default:
                System.out.println("Invalid option.");
        }
        
        // IMPORTANT: Close the DAO connection when done
        try {
            // If your DAO has a close method, call it here
            // Or rely on the finally block in each DAO method
        } catch (Exception e) {
            // Ignore
        }
        
        ConsoleUtils.pressEnterToContinue();
    }
    
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
    
    // Helper methods
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