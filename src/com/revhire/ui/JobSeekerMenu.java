package com.revhire.ui;

import com.revhire.model.*;
import com.revhire.service.JobSeekerService;
import com.revhire.service.AuthService;
import com.revhire.service.NotificationService;

import java.sql.SQLException;
import java.util.List;

public class JobSeekerMenu {
    private JobSeekerService service;
    private AuthService authService;
    private User user;
    private JobSeeker profile;
    private Resume resume;

    public JobSeekerMenu(JobSeekerService service, User user) {
        this.service = service;
        this.authService = new AuthService();
        this.user = user;
        loadProfile();
    }

    private void loadProfile() {
        try {
            this.profile = service.getJobSeekerProfile(user.getId());
            this.resume = service.getResume(user.getId());
        } catch (SQLException e) {
            System.out.println("Error loading profile: " + e.getMessage());
        }
    }

    public void show() {
        while (true) {
            try {
                int unreadCount = service.getUnreadNotificationCount(user.getId());
                String notificationBadge = unreadCount > 0 ? " (" + unreadCount + " unread)" : "";
                
                ConsoleUtils.printHeader("Job Seeker Dashboard - " + user.getName());
                System.out.println("1. View/Update Profile");
                System.out.println("2. Manage Resume");
                System.out.println("3. Search Jobs");
                System.out.println("4. View All Jobs");
                System.out.println("5. My Applications");
                System.out.println("6. Notifications" + notificationBadge);
                System.out.println("7. Change Password");
                System.out.println("8. Logout");
                ConsoleUtils.printLine(60);

                int choice = ConsoleUtils.readInt("Enter your choice: ");

                switch (choice) {
                    case 1:
                        manageProfile();
                        break;
                    case 2:
                        manageResume();
                        break;
                    case 3:
                        searchJobs();
                        break;
                    case 4:
                        viewAllJobs();
                        break;
                    case 5:
                        viewApplications();
                        break;
                    case 6:
                        viewNotifications();
                        break;
                    case 7:
                        changePassword();
                        break;
                    case 8:
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
        ConsoleUtils.printHeader("My Profile");
        
        System.out.println("Current Profile Information:");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Location: " + user.getLocation());
        if (profile != null) {
            System.out.println("Objective: " + profile.getObjective());
            System.out.println("Skills: " + profile.getSkills());
            System.out.println("Certifications: " + profile.getCertifications());
        }
        
        System.out.println("\n1. Update Personal Information");
        System.out.println("2. Update Professional Information");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        switch (choice) {
            case 1:
                updatePersonalInfo();
                break;
            case 2:
                updateProfessionalInfo();
                break;
            case 3:
                return;
        }
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

    private void updatePersonalInfo() throws SQLException {
        ConsoleUtils.printHeader("Update Personal Information");
        
        String name = ConsoleUtils.readString("Name [" + user.getName() + "]: ");
        String phone = ConsoleUtils.readString("Phone [" + user.getPhone() + "]: ");
        String location = ConsoleUtils.readString("Location [" + user.getLocation() + "]: ");
        
        if (!name.isEmpty()) user.setName(name);
        if (!phone.isEmpty()) user.setPhone(phone);
        if (!location.isEmpty()) user.setLocation(location);
        
        // Update in AuthService
        com.revhire.service.AuthService authService = new com.revhire.service.AuthService();
        boolean success = authService.updateProfile(user.getId(), user.getName(), user.getPhone(), user.getLocation());
        
        if (success) {
            System.out.println("Personal information updated successfully!");
        } else {
            System.out.println("Failed to update personal information!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void updateProfessionalInfo() throws SQLException {
        ConsoleUtils.printHeader("Update Professional Information");
        
        String objective = ConsoleUtils.readString("Career Objective [" + (profile != null ? profile.getObjective() : "") + "]: ");
        String skills = ConsoleUtils.readString("Skills [" + (profile != null ? profile.getSkills() : "") + "]: ");
        String certifications = ConsoleUtils.readString("Certifications [" + (profile != null ? profile.getCertifications() : "") + "]: ");
        
        if (profile == null) {
            profile = new JobSeeker();
            profile.setUserId(user.getId());
        }
        
        if (!objective.isEmpty()) profile.setObjective(objective);
        if (!skills.isEmpty()) profile.setSkills(skills);
        if (!certifications.isEmpty()) profile.setCertifications(certifications);
        
        boolean success = service.updateJobSeekerProfile(user.getId(), 
            profile.getObjective(), profile.getSkills(), profile.getCertifications());
        
        if (success) {
            System.out.println("Professional information updated successfully!");
        } else {
            System.out.println("Failed to update professional information!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void manageResume() throws SQLException {
        ConsoleUtils.printHeader("Manage Resume");
        
        if (resume == null) {
            System.out.println("No resume found. Create one now? (y/n): ");
            String choice = ConsoleUtils.readString("");
            if (choice.equalsIgnoreCase("y")) {
                createResume();
            }
            return;
        }
        
        System.out.println("Current Resume:");
        System.out.println("Education: " + resume.getEducation());
        System.out.println("Experience: " + resume.getExperience());
        System.out.println("Projects: " + resume.getProjects());
        
        System.out.println("\n1. Update Resume");
        System.out.println("2. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            updateResume();
        }
    }

    private void createResume() throws SQLException {
        ConsoleUtils.printHeader("Create Resume");
        
        String education = ConsoleUtils.readString("Education: ");
        String experience = ConsoleUtils.readString("Experience: ");
        String projects = ConsoleUtils.readString("Projects: ");
        
        int resumeId = service.createResume(user.getId(), education, experience, projects);
        if (resumeId > 0) {
            resume = new Resume(resumeId, user.getId(), education, experience, projects);
            System.out.println("Resume created successfully!");
        } else {
            System.out.println("Failed to create resume!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void updateResume() throws SQLException {
        ConsoleUtils.printHeader("Update Resume");
        
        String education = ConsoleUtils.readString("Education [" + resume.getEducation() + "]: ");
        String experience = ConsoleUtils.readString("Experience [" + resume.getExperience() + "]: ");
        String projects = ConsoleUtils.readString("Projects [" + resume.getProjects() + "]: ");
        
        if (!education.isEmpty()) resume.setEducation(education);
        if (!experience.isEmpty()) resume.setExperience(experience);
        if (!projects.isEmpty()) resume.setProjects(projects);
        
        boolean success = service.updateResume(resume.getId(), resume.getEducation(), resume.getExperience(), resume.getProjects());
        
        if (success) {
            System.out.println("Resume updated successfully!");
        } else {
            System.out.println("Failed to update resume!");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void searchJobs() throws SQLException {
        ConsoleUtils.printHeader("Search Jobs");
        
        String keyword = ConsoleUtils.readString("Keyword (title/description/skills): ");
        String location = ConsoleUtils.readString("Location (press Enter for any): ");
        String jobType = ConsoleUtils.readString("Job Type (fulltime/parttime/contract/internship): ");
        Double minSalary = null;
        
        String salaryInput = ConsoleUtils.readString("Minimum Salary (press Enter for any): ");
        if (!salaryInput.isEmpty()) {
            minSalary = Double.parseDouble(salaryInput);
        }
        
        List<Job> jobs = service.searchJobs(keyword, location, jobType.isEmpty() ? null : jobType, minSalary);
        
        if (jobs.isEmpty()) {
            System.out.println("No jobs found matching your criteria.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("\nFound " + jobs.size() + " job(s):");
        ConsoleUtils.printLine(100);
        System.out.printf("%-5s %-30s %-20s %-15s %-15s\n", "ID", "Title", "Company", "Location", "Salary");
        ConsoleUtils.printLine(100);
        
        for (Job job : jobs) {
            System.out.printf("%-5d %-30s %-20s %-15s $%-14.2f\n", 
                job.getId(), 
                job.getTitle().length() > 28 ? job.getTitle().substring(0, 28) + ".." : job.getTitle(),
                "TechCorp", // In real app, get company name from DB
                job.getLocation().length() > 13 ? job.getLocation().substring(0, 13) : job.getLocation(),
                job.getSalaryMax());
        }
        
        System.out.println("\n1. View Job Details");
        System.out.println("2. Apply for Job");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
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
                } else {
                    applyForJob(selectedJob);
                }
            } else {
                System.out.println("Invalid Job ID!");
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }

    private void viewAllJobs() throws SQLException {
        ConsoleUtils.printHeader("All Available Jobs");
        
        List<Job> jobs = service.getAllJobs();
        
        if (jobs.isEmpty()) {
            System.out.println("No jobs available at the moment.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Total Jobs: " + jobs.size());
        ConsoleUtils.printLine(100);
        System.out.printf("%-5s %-30s %-20s %-15s %-15s %-10s\n", "ID", "Title", "Company", "Location", "Salary", "Type");
        ConsoleUtils.printLine(100);
        
        for (Job job : jobs) {
            System.out.printf("%-5d %-30s %-20s %-15s $%-14.2f %-10s\n", 
                job.getId(), 
                job.getTitle().length() > 28 ? job.getTitle().substring(0, 28) + ".." : job.getTitle(),
                "TechCorp",
                job.getLocation().length() > 13 ? job.getLocation().substring(0, 13) : job.getLocation(),
                job.getSalaryMax(),
                job.getJobType());
        }
        
        System.out.println("\n1. View Job Details");
        System.out.println("2. Apply for Job");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
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
                } else {
                    applyForJob(selectedJob);
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
        
        System.out.println("\n1. Apply for this Job");
        System.out.println("2. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            applyForJob(job);
        }
    }

    private void applyForJob(Job job) {
        try {
            if (resume == null) {
                System.out.println("You need to create a resume first!");
                ConsoleUtils.pressEnterToContinue();
                return;
            }
            
            ConsoleUtils.printHeader("Apply for Job - " + job.getTitle());
            
            System.out.println("Your Resume:");
            System.out.println("Education: " + resume.getEducation());
            System.out.println("Experience: " + resume.getExperience());
            System.out.println("Projects: " + resume.getProjects());
            
            String coverLetter = ConsoleUtils.readString("\nCover Letter (optional): ");
            
            int applicationId = service.applyForJob(user.getId(), job.getId(), resume.getId(), coverLetter);
            if (applicationId > 0) {
                System.out.println("Application submitted successfully!");
                service.sendNotification(user.getId(), "You have successfully applied for: " + job.getTitle());
            } else {
                System.out.println("Failed to submit application!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private void viewApplications() throws SQLException {
        ConsoleUtils.printHeader("My Applications");
        
        List<Application> applications = service.getMyApplications(user.getId());
        
        if (applications.isEmpty()) {
            System.out.println("You haven't applied for any jobs yet.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        
        System.out.println("Total Applications: " + applications.size());
        ConsoleUtils.printLine(120);
        System.out.printf("%-5s %-30s %-20s %-15s %-15s %-20s\n", "ID", "Job Title", "Applied Date", "Status", "Cover Letter", "Reason");
        ConsoleUtils.printLine(120);
        
        for (Application app : applications) {
            String coverLetterPreview = app.getCoverLetter() != null && app.getCoverLetter().length() > 15 ? 
                app.getCoverLetter().substring(0, 15) + "..." : app.getCoverLetter();
            String reasonPreview = app.getReason() != null && app.getReason().length() > 15 ? 
                app.getReason().substring(0, 15) + "..." : app.getReason();
            
            System.out.printf("%-5d %-30s %-20s %-15s %-15s %-20s\n", 
                app.getId(), 
                "Job " + app.getJobId(), // In real app, get job title from DB
                app.getAppliedAt().toString(),
                app.getStatus(),
                coverLetterPreview != null ? coverLetterPreview : "N/A",
                reasonPreview != null ? reasonPreview : "N/A");
        }
        
        System.out.println("\n1. View Application Details");
        System.out.println("2. Withdraw Application");
        System.out.println("3. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1 || choice == 2) {
            int appId = ConsoleUtils.readInt("Enter Application ID: ");
            
            if (choice == 2) {
                withdrawApplication(appId);
            }
        }
    }

    private void withdrawApplication(int applicationId) throws SQLException {
        ConsoleUtils.printHeader("Withdraw Application");
        
        String reason = ConsoleUtils.readString("Reason for withdrawal (optional): ");
        
        boolean success = service.withdrawApplication(applicationId);
        if (success) {
            System.out.println("Application withdrawn successfully!");
        } else {
            System.out.println("Failed to withdraw application!");
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
        
        System.out.println("Total Notifications: " + notifications.size());
        ConsoleUtils.printLine(80);
        
        for (Notification notification : notifications) {
            String readStatus = notification.getIsRead() == 1 ? "[Read]" : "[NEW]";
            System.out.println(readStatus + " " + notification.getCreatedAt() + ": " + notification.getMessage());
        }
        
        System.out.println("\n1. Mark all as read");
        System.out.println("2. Back");
        
        int choice = ConsoleUtils.readInt("Enter choice: ");
        
        if (choice == 1) {
            // Mark all as read
            // Implementation depends on your NotificationDAO
            //System.out.println("All notifications marked as read!");
        	NotificationService notificationService = new NotificationService();
            boolean markedAll = notificationService.markAllAsRead(user.getId());
            if (markedAll) {
                System.out.println("All notifications marked as read!");
            } else {
                System.out.println("Failed to mark notifications as read!");
            }
        }
        
        ConsoleUtils.pressEnterToContinue();
    }
}